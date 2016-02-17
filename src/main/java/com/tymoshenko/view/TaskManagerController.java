package com.tymoshenko.view;

import com.tymoshenko.MainApp;
import com.tymoshenko.model.TaskDto;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;


/**
 * @author Yakiv Tymoshenko
 * @since 05.02.2016
 */
public class TaskManagerController {

    private MainApp mainApp;

    @FXML
    private TabPane tabPane;

    @FXML
    private TableView<TaskDto> taskListTable;

    @FXML
    private TableColumn<TaskDto, String> nameColumn;

    @FXML
    private TableColumn<TaskDto, Number> pidColumn;

    @FXML
    private TableColumn<TaskDto, String> memoryUsedColumn;

    @FXML
    private CheckBox groupByNameCheckBox;

    @FXML
    private Label tasksNumberLabel;

    @FXML
    private void initialize() {
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        pidColumn.setCellValueFactory(cellData -> cellData.getValue().pidProperty());
        memoryUsedColumn.setCellValueFactory(cellData -> cellData.getValue().memoryHumanReadableProperty());
    }

    @FXML
    private void handleShowTasks() {
        refreshTaskList();
    }

    @FXML
    private void handleGroupByName() {
        refreshTaskList();
    }

    private void refreshTaskList() {
        if (groupByNameCheckBox.isSelected()) {
            mainApp.groupTasksByName();
        } else {
            mainApp.refreshTaskList();
        }
    }

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
        mainApp.setTaskManagerController(this);

        ObservableList<TaskDto> taskList = mainApp.getTaskList();
        taskListTable.setItems(taskList);
        tasksNumberLabel.setText("" + taskList.size());
        taskList.addListener((ListChangeListener<TaskDto>) c -> {
            tasksNumberLabel.setText("" + taskList.size());
        });
    }

    public TabPane getTabPane() {
        return tabPane;
    }
}
