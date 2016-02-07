package com.tymoshenko.controller.export;

import com.tymoshenko.model.TaskDto;
import org.junit.Test;

import java.io.File;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.Assert.*;

/**
 * @author Yakiv
 * @since 07.02.2016
 */
public class XmlExporterTest {

    private Exporter exporter = new XmlExporter();

//    @Test
    public void testName() throws Exception {
        TaskDto distinct = new TaskDto.Builder()
                .withName("zzzzzzzz")
                .withPid("01")
                .withMemory(1000)
                .build();
        TaskDto duplicate1 = new TaskDto.Builder()
                .withName("duplicatedName1")
                .withPid("1")
                .withMemory(1)
                .build();
        TaskDto duplicate2 = new TaskDto.Builder()
                .withName("duplicatedName1")
                .withPid("2")
                .withMemory(2)
                .build();
        TaskDto duplicate3 = new TaskDto.Builder()
                .withName("duplicatedName1")
                .withPid("3")
                .withMemory(3)
                .build();
        TaskDto duplicate4 = new TaskDto.Builder()
                .withName("duplicatedName2")
                .withPid("1")
                .withMemory(10)
                .build();
        TaskDto duplicate5 = new TaskDto.Builder()
                .withName("duplicatedName2")
                .withPid("2")
                .withMemory(20)
                .build();
        TaskDto duplicate6 = new TaskDto.Builder()
                .withName("duplicatedName2")
                .withPid("3")
                .withMemory(30)
                .build();

        List<TaskDto> taskDtoList = newArrayList(duplicate1, duplicate2, duplicate4, duplicate3, distinct, duplicate6, duplicate5);

        exporter.export(taskDtoList, new File("tasklist.xml"));
    }
}