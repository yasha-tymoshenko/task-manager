package com.tymoshenko;

import com.tymoshenko.controller.TaskManager;
import com.tymoshenko.controller.exporting.Exporter;
import com.tymoshenko.controller.importing.Importer;
import com.tymoshenko.controller.task_monitor.comparator.MemoryUsedDescendingComparator;
import com.tymoshenko.controller.task_monitor.comparator.TaskDtoDiffLeftMemoryDescendingComparator;
import com.tymoshenko.controller.task_monitor.comparator.TaskDtoDiffRightMemoryDescendingComparator;
import com.tymoshenko.model.DiffSign;
import com.tymoshenko.model.TaskDto;
import com.tymoshenko.model.TaskDtoDiff;
import com.tymoshenko.view.MenuBarController;
import com.tymoshenko.view.TaskManagerController;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;

/**
 * @author Yakiv
 * @since 04.02.2016
 */
public class MainApp extends Application {

    private static final Logger LOG = LoggerFactory.getLogger(MainApp.class);

    private Stage primaryStage;
    private BorderPane rootLayout;
    private TaskManagerController taskManagerController;

    private ApplicationContext applicationContext;

    private TaskManager taskManager;
    private Exporter exporterXml;
    private Exporter exporterExcel;

    private ObservableList<TaskDto> taskList;
    private boolean taskDiffLeftSortMemoryAscending = false;
    private boolean taskDiffRightSortMemoryAscending = false;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Task Manager");

        setIcon();
        initSpringContext();

        taskList = FXCollections.observableArrayList(taskManager.taskList());

        initRootLayout();
        showTaskList();
    }

    public void refreshTaskList() {
        taskList.setAll(taskManager.taskList());
    }

    public void sortByMemory(Boolean ascending) {
        if (ascending) {
            taskList.sort(new MemoryUsedDescendingComparator().reversed());
        } else {
            taskList.sort(new MemoryUsedDescendingComparator());
        }
    }

    public void groupTasksByName() {
        refreshTaskList();
        List<TaskDto> taskList = taskManager.taskListCollapseDuplicates(this.taskList);
        this.taskList.setAll(taskList);
    }

    public void doExport(File exportTo) {
        if (exportTo != null) {
            try {
                List<TaskDto> tasksGroupedByName = taskManager.taskListCollapseDuplicates(this.taskList);
                exporterXml.export(tasksGroupedByName, exportTo);
            } catch (Exception e) {
                showExceptionDialog("Export to XML failed.", "File=" + exportTo.getPath(), e);
                LOG.error(String.format("Failed export to XML file=%s. Exception: %s", exportTo.getPath(), e.getMessage()));
            }
        }
    }

    public void doExportToExcel(File exportTo) {
        if (exportTo != null) {
            try {
                List<TaskDto> tasksGroupedByName = taskManager.taskListCollapseDuplicates(this.taskList);
                exporterExcel.export(tasksGroupedByName, exportTo);
            } catch (Exception e) {
                showExceptionDialog("Export to Excel failed.", "File=" + exportTo.getPath(), e);
                LOG.error(String.format("Failed export to Excel file=%s. Exception: %s", exportTo.getPath(), e.getMessage()));
            }
        }
    }

    public void doImport(File xmlFile) {
        if (xmlFile != null) {
            Tab newTab = new Tab(xmlFile.getPath(), createImportedTabContent(xmlFile));

            TabPane tabPane = taskManagerController.getTabPane();
            tabPane.getTabs().add(newTab);
            tabPane.getSelectionModel().select(newTab);
        }
    }

    private Node createImportedTabContent(File importedFile) {
        Importer importer = (Importer) applicationContext.getBean("xmlImporter");
        List<TaskDto> taskList = importer.doImport(importedFile);

        // Compare current running tasks and imported
        Map<String, TaskDto> leftMap = new HashMap<>();
        // FIXME maybe make it depend on checkbox "Group by name"
        List<TaskDto> tasksGroupedByName = taskManager.taskListCollapseDuplicates(this.taskList);
        for (TaskDto taskDto : tasksGroupedByName) {
            leftMap.put(taskDto.getName(), taskDto);
        }

        Map<String, TaskDto> rightMap = new HashMap<>();
        for (TaskDto taskDto : taskList) {
            rightMap.put(taskDto.getName(), taskDto);
        }

        Set<String> allTaskNames = new HashSet<>(leftMap.keySet());
        allTaskNames.addAll(rightMap.keySet());

        // Merge current and imported lists
        List<TaskDtoDiff> taskDtoDiffList = new ArrayList<>();
        TaskDto emptyTask = new TaskDto.Builder()
                .withName("-")
                .withPid(0)
                .withMemory(0)
                .build();
        for (String taskName : allTaskNames) {
            TaskDto left = emptyTask;
            TaskDto right = emptyTask;
            if (leftMap.containsKey(taskName)) {
                left = leftMap.get(taskName);
            }
            if (rightMap.containsKey(taskName)) {
                right = rightMap.get(taskName);
            }
            // Determine DiffSign
            DiffSign diffSign;
            if (left.equals(emptyTask)) {
                diffSign = DiffSign.REMOVED;
            } else if (right.equals(emptyTask)) {
                diffSign = DiffSign.ADDED;
            } else if (left.equals(right)) {
                diffSign = DiffSign.NO_CHANGES;
            } else {
                diffSign = DiffSign.CHANGED;
            }
            TaskDtoDiff taskDtoDiff = new TaskDtoDiff(left, diffSign, right);
            taskDtoDiffList.add(taskDtoDiff);
        }

        taskDtoDiffList.sort((first, second) -> {
            Long firstMaxMemory = Math.max(first.getLeft().getMemory(), first.getRight().getMemory());
            Long secondMaxMemory = Math.max(second.getLeft().getMemory(), second.getRight().getMemory());
            return secondMaxMemory.compareTo(firstMaxMemory);
        });
        ObservableList<TaskDtoDiff> taskDtoDiffListObservable = FXCollections.observableArrayList(taskDtoDiffList);
        TableView<TaskDtoDiff> table = new TableView<>(taskDtoDiffListObservable);

        // Display merged tasklist in table
        // Left task
        TableColumn<TaskDtoDiff, String> leftNameColumn = makeStringColumn("Name");
        TableColumn<TaskDtoDiff, Number> leftPidColumn = new TableColumn<>("PID");
        TableColumn<TaskDtoDiff, String> leftMemoryColumn = new TableColumn<>("Memory");
        leftNameColumn.setCellValueFactory(cellData -> cellData.getValue().getLeft().nameProperty());
        leftPidColumn.setCellValueFactory(cellData -> cellData.getValue().getLeft().pidProperty());
        leftMemoryColumn.setCellValueFactory(cellData -> cellData.getValue().getLeft().memoryHumanReadableProperty());
        leftMemoryColumn.sortTypeProperty().addListener((observable, oldValue, newValue) -> {
            if (taskDiffLeftSortMemoryAscending) {
                taskDtoDiffListObservable.sort(new TaskDtoDiffLeftMemoryDescendingComparator().reversed());
            } else {
                taskDtoDiffListObservable.sort(new TaskDtoDiffLeftMemoryDescendingComparator());
            }
            taskDiffLeftSortMemoryAscending = !taskDiffLeftSortMemoryAscending;
        });

        // Diff sign
        TableColumn<TaskDtoDiff, String> diffSignColumn = new TableColumn<>("Diff");
        diffSignColumn.setCellValueFactory(cellData -> cellData.getValue().getDiffSign().nameProperty());

        // Right task
        TableColumn<TaskDtoDiff, String> rightNameColumn = makeStringColumn("Name");
        TableColumn<TaskDtoDiff, Number> rightPidColumn = new TableColumn<>("PID");
        TableColumn<TaskDtoDiff, String> rightMemoryColumn = new TableColumn<>("Memory");
        rightNameColumn.setCellValueFactory(cellData -> cellData.getValue().getRight().nameProperty());
        rightPidColumn.setCellValueFactory(cellData -> cellData.getValue().getRight().pidProperty());
        rightMemoryColumn.setCellValueFactory(cellData -> cellData.getValue().getRight().memoryHumanReadableProperty());
        rightMemoryColumn.sortTypeProperty().addListener((observable, oldValue, newValue) -> {
            if (taskDiffRightSortMemoryAscending) {
                taskDtoDiffListObservable.sort(new TaskDtoDiffRightMemoryDescendingComparator().reversed());
            } else {
                taskDtoDiffListObservable.sort(new TaskDtoDiffRightMemoryDescendingComparator());
            }
            taskDiffRightSortMemoryAscending = !taskDiffRightSortMemoryAscending;
        });

        // Color highlight
        leftNameColumn.setCellFactory(colorHighlightCallback(DiffSign.ADDED));
        leftPidColumn.setCellFactory(colorHighlightCallback(DiffSign.ADDED));
        leftMemoryColumn.setCellFactory(colorHighlightCallback(DiffSign.ADDED, DiffSign.CHANGED));

        rightNameColumn.setCellFactory(colorHighlightCallback(DiffSign.REMOVED));
        rightPidColumn.setCellFactory(colorHighlightCallback(DiffSign.REMOVED));
        rightMemoryColumn.setCellFactory(colorHighlightCallback(DiffSign.REMOVED, DiffSign.CHANGED));

        table.getColumns().addAll(leftNameColumn, leftPidColumn, leftMemoryColumn,
                diffSignColumn, rightNameColumn, rightPidColumn, rightMemoryColumn);

        BorderPane tabRoot = new BorderPane();
        tabRoot.setCenter(table);
        return tabRoot;
    }

    private <T> Callback<TableColumn<TaskDtoDiff, T>, TableCell<TaskDtoDiff, T>> colorHighlightCallback(DiffSign... allowed) {
        return column -> new TableCell<TaskDtoDiff, T>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);

                TableRow<TaskDtoDiff> tableRow = getTableRow();
                // Reset styles (e.g. for sorting)
                setStyle("");
                if (tableRow != null) {
                    TaskDtoDiff diff = tableRow.getItem();
                    if (item != null && !empty) {
                        setText(item.toString());
                        if (Arrays.asList(allowed).contains(diff.getDiffSign())) {
                            switch (diff.getDiffSign()) {
                                case ADDED:
                                    setStyle("-fx-background-color: Aquamarine");
                                    break;
                                case REMOVED:
                                    setStyle("-fx-background-color: DarkSalmon");
                                    break;
                                case CHANGED:
                                    setStyle("-fx-background-color: Khaki");
                                    break;
                                default:
                                    setStyle("");
                            }
                        }
                    } else {
                        setStyle("");
                    }
                }
            }
        };
    }

    private TableColumn<TaskDtoDiff, String> makeStringColumn(String columnName) {
        TableColumn<TaskDtoDiff, String> column = new TableColumn<>(columnName);
        column.setPrefWidth(158);
        return column;
    }

    private void showExceptionDialog(String headerMessage, String mainMessage, Exception exception) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("ERROR");
        alert.setHeaderText(headerMessage);
        alert.setContentText(mainMessage);

        // Create expandable Exception.
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        exception.printStackTrace(pw);
        String exceptionText = sw.toString();

        Label label = new Label("The exception stacktrace was:");

        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        // Set expandable Exception into the dialog pane.
        alert.getDialogPane().setExpandableContent(expContent);

        alert.showAndWait();
    }

    private void setIcon() {
        String iconPath = "/resources/images/task-manager-icon.png";
        try {
            Image icon = new Image(iconPath);
            this.primaryStage.getIcons().add(icon);
        } catch (IllegalArgumentException e) {
            LOG.warn(String.format("Icon not found: %s", iconPath));
        }
    }

    private void initSpringContext() {
        applicationContext = new ClassPathXmlApplicationContext("/resources/beans.xml");
        taskManager = applicationContext.getBean(TaskManager.class);
        exporterXml = (Exporter) applicationContext.getBean("xmlExporter");
        exporterExcel = (Exporter) applicationContext.getBean("excelExporter");
    }

    private void initRootLayout() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("/resources/fxml/RootLayout.fxml"));
            rootLayout = loader.load();

            MenuBarController controller = loader.getController();
            controller.setMainApp(this);

            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            LOG.error(String.format("Failed to load root layout. %s", e.getMessage()));
        }
    }

    private void showTaskList() {
        try {
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/resources/fxml/TaskManager.fxml"));
            BorderPane taskListOverview = loader.load();
            rootLayout.setCenter(taskListOverview);

            TaskManagerController controller = loader.getController();
            controller.setMainApp(this);
        } catch (IOException e) {
            LOG.error(String.format("Failed to load TaskManager.fxml. %s", e.getMessage()));
        }
    }

    public ObservableList<TaskDto> getTaskList() {
        return taskList;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public void setTaskManagerController(TaskManagerController taskManagerController) {
        this.taskManagerController = taskManagerController;
    }
}
