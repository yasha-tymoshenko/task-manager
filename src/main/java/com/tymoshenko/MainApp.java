package com.tymoshenko;/**
 * @author Yakiv
 * @since 04.02.2016
 */

import com.tymoshenko.controller.TaskManager;
import com.tymoshenko.controller.exporting.Exporter;
import com.tymoshenko.controller.importing.Importer;
import com.tymoshenko.model.ExportFormat;
import com.tymoshenko.model.TaskDto;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

    public void doExport(File exportTo, ExportFormat exportFormat) {
        try {
            exporter.export(new ArrayList<>(taskList), exportTo);
        } catch (Exception e) {
            LOG.error(String.format("Failed export to XML file=%s. Exception: %s", exportTo.getPath(), e.getMessage()));
            // TODO show Dialog
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

        TableView<TaskDto> table = new TableView<>(taskListObservable);
        TableColumn<TaskDto, String> nameColumn = new TableColumn<>("Name");
        TableColumn<TaskDto, Number> pidColumn = new TableColumn<>("PID");
        TableColumn<TaskDto, Number> memoryColumn = new TableColumn<>("Memory");
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        pidColumn.setCellValueFactory(cellData -> cellData.getValue().pidProperty());
        memoryColumn.setCellValueFactory(cellData -> cellData.getValue().memoryProperty());
        table.getColumns().addAll(nameColumn, pidColumn, memoryColumn);

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
        exporter = applicationContext.getBean(Exporter.class);
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
