package com.tymoshenko.controller.task_monitor;

import com.tymoshenko.MainApp;
import com.tymoshenko.controller.task_monitor.command.Command;
import com.tymoshenko.controller.task_monitor.parser.TaskListParser;
import com.tymoshenko.model.TaskDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Yakiv Tymoshenko
 * @since 29.01.2016
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = MainApp.class)
public class TaskMonitorImplTest {

    @Autowired
    private TaskMonitor taskMonitor;

    @Autowired
    private Command<List<String>> taskListCommand;

    @Test// TODO investigate failure
    public void testGetTaskList() throws Exception {
        List<TaskDto> taskDtos = taskMonitor.taskList();

        int expectedSize = taskListCommand.execute().size() - TaskListParser.FIRST_PROCESS_LINE_INDEX;
        assertNotNull("tasklist is null", taskDtos);
        assertEquals("tasklist contains wrong entries", expectedSize, taskDtos.size());
    }

    @Test
    public void collapseDuplicatesByNameAndAggregateMemoryUsed_ok_allDuplicated() throws Exception {
        String duplicatedName = "name";
        TaskDto duplicate1 = new TaskDto.Builder()
                .withName(duplicatedName)
                .withPid("1")
                .withMemory(1)
                .build();
        TaskDto duplicate2 = new TaskDto.Builder()
                .withName(duplicatedName)
                .withPid("2")
                .withMemory(2)
                .build();
        TaskDto duplicate3 = new TaskDto.Builder()
                .withName(duplicatedName)
                .withPid("3")
                .withMemory(3)
                .build();
        List<TaskDto> taskDtoList = newArrayList(duplicate1, duplicate2, duplicate3);

        List<TaskDto> collapsedList = taskMonitor.collapseDuplicatesByNameAndAggregateMemoryUsed(taskDtoList);

        assertEquals(1, collapsedList.size());
        TaskDto collapsedDuplicate = collapsedList.get(0);
        assertEquals("Wrong name", duplicatedName, collapsedDuplicate.getName());
        assertEquals("Wrong pid", "1", collapsedDuplicate.getPid());
        assertEquals("Wrong memory", 6L, (long) collapsedDuplicate.getMemory());
    }

    @Test
    public void collapseDuplicatesByNameAndAggregateMemoryUsed_ok_distinctAtStart() throws Exception {
        String duplicatedName = "name";
        TaskDto distinct = new TaskDto.Builder()
                .withName("aaaaaaa")
                .withPid("01")
                .withMemory(1000)
                .build();
        TaskDto duplicate1 = new TaskDto.Builder()
                .withName(duplicatedName)
                .withPid("1")
                .withMemory(1)
                .build();
        TaskDto duplicate2 = new TaskDto.Builder()
                .withName(duplicatedName)
                .withPid("2")
                .withMemory(2)
                .build();
        TaskDto duplicate3 = new TaskDto.Builder()
                .withName(duplicatedName)
                .withPid("3")
                .withMemory(3)
                .build();
        List<TaskDto> taskDtoList = newArrayList(distinct, duplicate1, duplicate2, duplicate3);

        List<TaskDto> collapsedList = taskMonitor.collapseDuplicatesByNameAndAggregateMemoryUsed(taskDtoList);

        assertEquals(2, collapsedList.size());
        TaskDto collapsedDuplicate = collapsedList.get(1);
        assertEquals("Wrong name", duplicatedName, collapsedDuplicate.getName());
        assertEquals("Wrong pid", "1", collapsedDuplicate.getPid());
        assertEquals("Wrong memory", 6, (long) collapsedDuplicate.getMemory());
    }

    @Test
    public void collapseDuplicatesByNameAndAggregateMemoryUsed_ok_distinctAtEnd() throws Exception {
        String duplicatedName = "name";
        TaskDto distinct = new TaskDto.Builder()
                .withName("zzzzzzzz")
                .withPid("01")
                .withMemory(1000)
                .build();
        TaskDto duplicate1 = new TaskDto.Builder()
                .withName(duplicatedName)
                .withPid("1")
                .withMemory(1)
                .build();
        TaskDto duplicate2 = new TaskDto.Builder()
                .withName(duplicatedName)
                .withPid("2")
                .withMemory(2)
                .build();
        TaskDto duplicate3 = new TaskDto.Builder()
                .withName(duplicatedName)
                .withPid("3")
                .withMemory(3)
                .build();
        List<TaskDto> taskDtoList = newArrayList(duplicate1, duplicate2, duplicate3, distinct);

        List<TaskDto> collapsedList = taskMonitor.collapseDuplicatesByNameAndAggregateMemoryUsed(taskDtoList);

        assertEquals(2, collapsedList.size());
        TaskDto collapsedDuplicate = collapsedList.get(0);
        assertEquals("Wrong name", duplicatedName, collapsedDuplicate.getName());
        assertEquals("Wrong pid", "1", collapsedDuplicate.getPid());
        assertEquals("Wrong memory", 6, (long) collapsedDuplicate.getMemory());
    }

    @Test
    public void collapseDuplicatesByNameAndAggregateMemoryUsed_ok_multipleDuplicatesSets() throws Exception {
        String duplicatedName1 = "name1";
        String duplicatedName2 = "name2";
        TaskDto distinct = new TaskDto.Builder()
                .withName("zzzzzzzz")
                .withPid("01")
                .withMemory(1000)
                .build();
        TaskDto duplicate1 = new TaskDto.Builder()
                .withName(duplicatedName1)
                .withPid("1")
                .withMemory(1)
                .build();
        TaskDto duplicate2 = new TaskDto.Builder()
                .withName(duplicatedName1)
                .withPid("2")
                .withMemory(2)
                .build();
        TaskDto duplicate3 = new TaskDto.Builder()
                .withName(duplicatedName1)
                .withPid("3")
                .withMemory(3)
                .build();
        TaskDto duplicate4 = new TaskDto.Builder()
                .withName(duplicatedName2)
                .withPid("1")
                .withMemory(10)
                .build();
        TaskDto duplicate5 = new TaskDto.Builder()
                .withName(duplicatedName2)
                .withPid("2")
                .withMemory(20)
                .build();
        TaskDto duplicate6 = new TaskDto.Builder()
                .withName(duplicatedName2)
                .withPid("3")
                .withMemory(30)
                .build();
        List<TaskDto> taskDtoList = newArrayList(duplicate1, duplicate2, duplicate4, duplicate3, distinct, duplicate6, duplicate5);

        List<TaskDto> collapsedList = taskMonitor.collapseDuplicatesByNameAndAggregateMemoryUsed(taskDtoList);

        assertEquals(3, collapsedList.size());
        TaskDto collapsedDuplicate = collapsedList.get(0);
        assertEquals("Wrong name", duplicatedName1, collapsedDuplicate.getName());
        assertEquals("Wrong pid", "1", collapsedDuplicate.getPid());
        assertEquals("Wrong memory", 6, (long) collapsedDuplicate.getMemory());

        collapsedDuplicate = collapsedList.get(1);
        assertEquals("Wrong name", duplicatedName2, collapsedDuplicate.getName());
        assertEquals("Wrong pid", "1", collapsedDuplicate.getPid());
        assertEquals("Wrong memory", 60, (long) collapsedDuplicate.getMemory());

    }

    @Test
    public void collapseDuplicatesByNameAndAggregateMemoryUsed_ok_oneElement() throws Exception {
        TaskDto distinct = new TaskDto.Builder()
                .withName("zzzzzzzz")
                .withPid("01")
                .withMemory(1000)
                .build();
        List<TaskDto> taskDtoList = newArrayList(distinct);

        List<TaskDto> collapsedList = taskMonitor.collapseDuplicatesByNameAndAggregateMemoryUsed(taskDtoList);

        assertEquals(1, collapsedList.size());
        TaskDto collapsedDuplicate = collapsedList.get(0);
        assertEquals("Wrong name", "zzzzzzzz", collapsedDuplicate.getName());
        assertEquals("Wrong pid", "01", collapsedDuplicate.getPid());
        assertEquals("Wrong memory", 1000, (long) collapsedDuplicate.getMemory());
    }

    @Test
    public void collapseDuplicatesByNameAndAggregateMemoryUsed_ok_twoElements() throws Exception {
        TaskDto one = new TaskDto.Builder()
                .withName("zzzzzzzz")
                .withPid("01")
                .withMemory(1000)
                .build();
        TaskDto two = new TaskDto.Builder()
                .withName("aaa")
                .withPid("02")
                .withMemory(200)
                .build();
        List<TaskDto> taskDtoList = newArrayList(one, two);

        List<TaskDto> collapsedList = taskMonitor.collapseDuplicatesByNameAndAggregateMemoryUsed(taskDtoList);

        assertEquals(2, collapsedList.size());
        TaskDto collapsedDuplicate = collapsedList.get(0);
        assertEquals("Wrong name", "aaa", collapsedDuplicate.getName());
        assertEquals("Wrong pid", "02", collapsedDuplicate.getPid());
        assertEquals("Wrong memory", 200, (long) collapsedDuplicate.getMemory());

        collapsedDuplicate = collapsedList.get(1);
        assertEquals("Wrong name", "zzzzzzzz", collapsedDuplicate.getName());
        assertEquals("Wrong pid", "01", collapsedDuplicate.getPid());
        assertEquals("Wrong memory", 1000, (long) collapsedDuplicate.getMemory());
    }

    @Test(expected = IllegalArgumentException.class)
    public void collapseDuplicatesByNameAndAggregateMemoryUsed_emptyList() throws Exception {
        taskMonitor.collapseDuplicatesByNameAndAggregateMemoryUsed(newArrayList());
    }
}
