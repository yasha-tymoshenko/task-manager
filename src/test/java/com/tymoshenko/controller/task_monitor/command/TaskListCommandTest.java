package com.tymoshenko.controller.task_monitor.command;

import com.tymoshenko.MainApp;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author Yakiv
 * @since 29.01.2016
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = MainApp.class)
public class TaskListCommandTest {

    @Autowired
    private Command<List<String>> command;

    @Test
    public void testRun() throws Exception {
        List<String> lines = command.execute();
        assertNotNull(lines);
        assertTrue(lines.size() >= 4);
    }
}