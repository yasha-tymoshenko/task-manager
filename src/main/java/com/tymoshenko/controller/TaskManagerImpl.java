package com.tymoshenko.controller;

import com.tymoshenko.controller.task_monitor.TaskMonitor;
import com.tymoshenko.controller.task_monitor.comparator.MemoryUsedDescendingComparator;
import com.tymoshenko.controller.task_monitor.comparator.NameAscendingComparator;
import com.tymoshenko.model.ExportFormat;
import com.tymoshenko.model.TaskDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

/**
 * @author Yakiv Tymoshenko
 * @since 29.01.2016
 */
@Component
public class TaskManagerImpl implements TaskManager {

    private List<TaskDto> taskList;

    @Autowired
    private TaskMonitor taskMonitor;

    public List<TaskDto> taskList() {
        this.taskList = taskMonitor.taskList();
        this.taskList.sort(new NameAscendingComparator());
        return taskList;
    }

    public void taskListCollapseDuplicates() {
        makeSureTaskListExists();
        this.taskList = taskMonitor.collapseDuplicatesByNameAndAggregateMemoryUsed(this.taskList);
    }

    @Override
    public void printTaskList() {
        makeSureTaskListExists();
        this.taskList.sort(new MemoryUsedDescendingComparator());
        this.taskList.forEach(System.out::println);
    }

    private void makeSureTaskListExists() {
        if (this.taskList == null) {
            taskList();
        }
    }

    public void exportTaskList(ExportFormat exportFormat, File exportFile) {

    }

    public void importTaskList(File importFile) {

    }
}
