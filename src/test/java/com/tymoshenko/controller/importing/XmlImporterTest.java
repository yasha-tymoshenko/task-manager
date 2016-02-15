package com.tymoshenko.controller.importing;

import com.tymoshenko.MainApp;
import com.tymoshenko.model.TaskDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author Yakiv
 * @since 15.02.2016
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = MainApp.class)
public class XmlImporterTest {

    private static final Logger LOG = LoggerFactory.getLogger(XmlImporterTest.class);

    @Autowired
    private Importer importer;
//    private final String _importFilePath = "/importing/import.xml";
    private final String _importFilePath = "C:\\Projects\\task-manager\\src\\test\\resources\\importing\\import.xml";

    @Test
    public void importShouldImportListOfTaskDtoFromFile() throws Exception {
        File file = new File(_importFilePath);

        List<TaskDto> taskList = importer.doImport(file);

        assertNotNull("Importer returned null list", taskList);
        assertEquals("Wrong imported list size", 4, taskList.size());

        TaskDto firstTask = taskList.get(0);
        assertEquals("idea64.exe", firstTask.getName());
        assertEquals(7164, (long) firstTask.getPid());
        assertEquals(740108, (long) firstTask.getMemory());
        TaskDto lastTask = taskList.get(3);
        assertEquals("chrome.exe", lastTask.getName());
        assertEquals(6220, (long) lastTask.getPid());
        assertEquals(159024, (long) lastTask.getMemory());
    }

    private File loadFile(String path) throws FileNotFoundException {
        File file = new File(_importFilePath);
        if (!file.exists()) {
            throw new FileNotFoundException("File not found: " + path);
        }
        return file;
    }
}