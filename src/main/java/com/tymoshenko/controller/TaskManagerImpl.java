package com.tymoshenko.controller;

import com.tymoshenko.controller.task_monitor.TaskMonitor;
import com.tymoshenko.model.ExportFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * @author Yakiv Tymoshenko
 * @since 29.01.2016
 */
@Component
public class TaskManagerImpl implements TaskManager {

    @Autowired
    private TaskMonitor taskMonitor;

    public void taskList() {
        taskMonitor.printTaskListToConsole();
    }

    public void taskListAggregatedByName() {

    }

    public void removeTask(String name) {

    }

    public void exportTaskList(ExportFormat exportFormat, File exportFile) {

    }

    public void importTaskList(File importFile) {

    }

    public void exportTaskList(ExportFormat exportFormat, String exportFileName) {

    }

    private void fillTaskDTOs() {

    }

    public void setTaskMonitor(TaskMonitor taskMonitor) {
        this.taskMonitor = taskMonitor;
    }
}
