package com.tymoshenko.controller.task_monitor;

import com.tymoshenko.model.TaskDto;

import java.util.List;

/**
 * Core functionality.
 * Creates a list of running system processes.
 *
 * Should operate on Windows.
 * Should handle different system setting (32/64, charsets, etc.).
 *
 * @author Yakiv
 * @since 29.01.2016
 */
public interface TaskMonitor {

    List<TaskDto> getTaskList();

    void printTaskListToConsole();
}
