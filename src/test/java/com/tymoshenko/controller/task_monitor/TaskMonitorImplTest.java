package com.tymoshenko.controller.task_monitor;

import com.tymoshenko.controller.Application;
import com.tymoshenko.controller.task_monitor.command.Command;
import com.tymoshenko.model.TaskDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author Yakiv Tymoshenko
 * @since 29.01.2016
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = Application.class)
public class TaskMonitorImplTest {

    @Autowired
    private TaskMonitor taskMonitor;

    @Autowired
    private Command<List<String>> taskListCommand;

    @Test
    public void testPrintTaskListToConsole() throws Exception {
        taskMonitor.printTaskListToConsole();
    }

    @Test
    public void testGetTaskList() throws Exception {
        List<TaskDto> taskDtos = taskMonitor.taskList();
        taskDtos.forEach(System.out::println);

//        int expectedSize = taskListCommand.execute().size() - TaskListParser.FIRST_PROCESS_LINE_INDEX;
//        assertEquals(expectedSize, taskDtos.size());
    }
}
