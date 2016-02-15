package com.tymoshenko.controller.importing;

import com.tymoshenko.model.TaskDto;
import com.tymoshenko.model.TaskDtoList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.util.List;

/**
 * @author Yakiv Tymoshenko
 * @since 15.02.2016
 */
@Component
public class XmlImporter implements Importer {

    private static final Logger LOG = LoggerFactory.getLogger(XmlImporter.class);

    public List<TaskDto> doImport(File file) {
        return unmarshall(file);
    }

    private List<TaskDto> unmarshall(File file) {
        List<TaskDto> result = null;
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(TaskDtoList.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

            TaskDtoList taskDtoList = (TaskDtoList) unmarshaller.unmarshal(file);
            result = taskDtoList.getTasks();
        } catch (JAXBException e) {
            LOG.error(String.format("Failed to import file \'%s\'. Error: %s", file.getPath(), e.getMessage()));
        }
        return result;
    }
}
