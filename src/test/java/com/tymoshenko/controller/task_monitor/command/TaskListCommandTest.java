package com.tymoshenko.controller.task_monitor.command;

import com.tymoshenko.controller.Application;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * @author Yakiv
 * @since 29.01.2016
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = Application.class)
public class TaskListCommandTest {

    @Autowired
    private Command<List<String>> command;

    @Test
    public void testRun() throws Exception {
        List<String> lines = command.execute();
        for (String line : lines) {
            System.out.println(line);
        }
    }
}