package com.tymoshenko;

import com.tymoshenko.controller.TaskManager;
import com.tymoshenko.controller.exporting.Exporter;
import com.tymoshenko.controller.importing.Importer;
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
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
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
    private Exporter exporter;

    private ObservableList<TaskDto> taskList;

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

    public void groupTasksByName() {
        List<TaskDto> taskList = taskManager.taskListCollapseDuplicates(this.taskList);
        this.taskList.setAll(taskList);
    }

    public void doExport(File exportTo) {
        try {
            exporter.export(new ArrayList<>(taskList), exportTo);
        } catch (Exception e) {
            LOG.error(String.format("Failed export to XML file=%s. Exception: %s", exportTo.getPath(), e.getMessage()));
            // TODO show Dialog
        }
    }

    public void doExportToExcel(File exportTo) {
        try {
            ArrayList<TaskDto> taskList = new ArrayList<>(this.taskList);
            generateChart(taskList, exportTo);
        } catch (Exception e) {
            LOG.error(String.format("Failed export to Excel file=%s. Exception: %s", exportTo.getPath(), e.getMessage()));
            // TODO show Dialog
        }
    }

    private void generateChart(List<TaskDto> taskList, File excelFile) throws IOException, InvalidFormatException {
        // Load Excel template with chart
        File excelChartTemplate = new File(MainApp.class.getResource("/resources/excel/chart_template.xlt").getFile());
        FileInputStream fileInputStream = new FileInputStream(excelChartTemplate);
        // Using Excel 2003 format (*.xls)
        Workbook wb = new HSSFWorkbook(fileInputStream);
        Sheet sheet = wb.getSheet("Memory usage");
        final int _rows_number = taskList.size();

        // Copy taskList to Excel
        Row row;
        Cell cell;
        TaskDto taskDto;
        for (int rowIndex = 0; rowIndex < _rows_number; rowIndex++) {
            row = sheet.createRow(rowIndex);
            taskDto = taskList.get(rowIndex);

            // Name
            cell = row.createCell(0);
            cell.setCellValue(taskDto.getName());

            // PID
            cell = row.createCell(1);
            cell.setCellValue(taskDto.getPid());

            // Memory
            cell = row.createCell(2);
            cell.setCellValue(taskDto.getMemory());
        }

        // Write Excel content to a file
        try {
            FileOutputStream fileOut = new FileOutputStream(excelFile);
            wb.write(fileOut);
            wb.close();
            fileOut.flush();
            fileOut.close();
        } catch (IOException e) {
            LOG.error(String.format("Error writting excel content to a file: %s. Error: %s", excelFile.getPath(), e.getMessage()));
            // TODO show dialog
        }
    }

    public void doImport(File xmlFile) {
        Tab newTab = new Tab(xmlFile.getPath(), createImportedTabContent(xmlFile));

        TabPane tabPane = taskManagerController.getTabPane();
        tabPane.getTabs().add(newTab);
        tabPane.getSelectionModel().select(newTab);
    }

    private Node createImportedTabContent(File importedFile) {
        Importer importer = (Importer) applicationContext.getBean("xmlImporter");
        List<TaskDto> taskList = importer.doImport(importedFile);
        ObservableList<TaskDto> taskListObservable = FXCollections.observableArrayList(taskList);

        // Compare current running tasks and imported
        Map<String, TaskDto> leftMap = new HashMap<>();
        // Make sure is grouped by name (no duplicate names in the list)
        for (TaskDto taskDto : this.taskList) {
            leftMap.put(taskDto.getName(), taskDto);
        }

        Map<String, TaskDto> rightMap = new HashMap<>();
        for (TaskDto taskDto : taskList) {
            rightMap.put(taskDto.getName(), taskDto);
        }

        Set<String> allTaskNames = new HashSet<>(leftMap.keySet());
        allTaskNames.addAll(rightMap.keySet());

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

        // Left task
        TableColumn<TaskDtoDiff, String> leftNameColumn = new TableColumn<>("Name");
        TableColumn<TaskDtoDiff, Number> leftPidColumn = new TableColumn<>("PID");
        TableColumn<TaskDtoDiff, Number> leftMemoryColumn = new TableColumn<>("Memory");
        leftNameColumn.setCellValueFactory(cellData -> cellData.getValue().getLeft().nameProperty());
        leftPidColumn.setCellValueFactory(cellData -> cellData.getValue().getLeft().pidProperty());
        leftMemoryColumn.setCellValueFactory(cellData -> cellData.getValue().getLeft().memoryProperty());

        // Diff sign
        TableColumn<TaskDtoDiff, String> diffSignColumn = new TableColumn<>("Diff");
        diffSignColumn.setCellValueFactory(cellData -> cellData.getValue().getDiffSign().nameProperty());

        // Right task
        TableColumn<TaskDtoDiff, String> rightNameColumn = new TableColumn<>("Name");
        TableColumn<TaskDtoDiff, Number> rightPidColumn = new TableColumn<>("PID");
        TableColumn<TaskDtoDiff, Number> rightMemoryColumn = new TableColumn<>("Memory");
        rightNameColumn.setCellValueFactory(cellData -> cellData.getValue().getRight().nameProperty());
        rightPidColumn.setCellValueFactory(cellData -> cellData.getValue().getRight().pidProperty());
        rightMemoryColumn.setCellValueFactory(cellData -> cellData.getValue().getRight().memoryProperty());

        table.getColumns().addAll(leftNameColumn, leftPidColumn, leftMemoryColumn,
                diffSignColumn, rightNameColumn, rightPidColumn, rightMemoryColumn);

        BorderPane tabRoot = new BorderPane();
        tabRoot.setCenter(table);
        return tabRoot;
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
        exporter = (Exporter) applicationContext.getBean("xmlExporter");
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
