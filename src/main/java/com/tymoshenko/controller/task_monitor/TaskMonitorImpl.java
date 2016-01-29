package com.tymoshenko.controller.task_monitor;

import com.tymoshenko.controller.task_monitor.command.Command;
import com.tymoshenko.controller.task_monitor.comparator.MemoryUsedDescendingComparator;
import com.tymoshenko.model.TaskDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Yakiv Tymoshenko
 * @since 29.01.2016
 */
@Component
public class TaskMonitorImpl implements TaskMonitor {

    @Autowired
    private Command<List<String>> taskListCommand;

    @Autowired
    private TaskListParser taskListParser;


    public List<TaskDto> getTaskList() {
        List<String> taskListOut = taskListCommand.execute();
        List<TaskDto> taskDtoList = taskListParser.parse(taskListOut);
        taskDtoList.sort(new MemoryUsedDescendingComparator());
        return taskDtoList;
    }

    public void printTaskListToConsole() {
        List<String> taskListOutput = taskListCommand.execute();
        taskListOutput.forEach(System.out::println);
    }
}
