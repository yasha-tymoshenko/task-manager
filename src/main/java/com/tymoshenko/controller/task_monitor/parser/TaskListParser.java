package com.tymoshenko.controller.task_monitor.parser;

import com.tymoshenko.model.TaskDto;

import java.util.List;

/**
 * Parses console output of wni32//tasklist command into List<TaskDto>.
 *
 * @author Yakiv
 * @since 30.01.2016
 */
public interface TaskListParser {
    List<TaskDto> parse(List<String> taskList);
}
