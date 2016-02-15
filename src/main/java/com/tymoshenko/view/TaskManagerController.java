package com.tymoshenko.view;

import com.tymoshenko.MainApp;
import com.tymoshenko.model.ExportFormat;
import com.tymoshenko.model.TaskDto;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;

import java.io.File;


/**
 * @author Yakiv Tymoshenko
 * @since 05.02.2016
 */
public class TaskManagerController {

    private MainApp mainApp;

    @FXML
    private TableView<TaskDto> taskListTable;

    @FXML
    private TableColumn<TaskDto, String> nameColumn;

    @FXML
    private TableColumn<TaskDto, Number> pidColumn;

    @FXML
    private TableColumn<TaskDto, Number> memoryUsedColumn;

    @FXML
    private CheckBox groupByNameCheckBox;

    @FXML
    private Label tasksNumberLabel;

    @FXML
    private void initialize() {
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        pidColumn.setCellValueFactory(cellData -> cellData.getValue().pidProperty());
        memoryUsedColumn.setCellValueFactory(cellData -> cellData.getValue().memoryProperty());
    }

    @FXML
    private void handleShowTasks() {
        mainApp.refreshTaskList();
    }

    @FXML
    private void handleGroupByName() {
        if (groupByNameCheckBox.isSelected()) {
            mainApp.groupTasksByName();
        } else {
            mainApp.refreshTaskList();
        }
    }

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;

        ObservableList<TaskDto> taskList = mainApp.getTaskList();
        taskListTable.setItems(taskList);
        tasksNumberLabel.setText("" + taskList.size());
        taskList.addListener((ListChangeListener<TaskDto>) c -> {
            tasksNumberLabel.setText("" + taskList.size());
        });
    }

}
