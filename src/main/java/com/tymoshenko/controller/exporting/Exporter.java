package com.tymoshenko.controller.exporting;

import com.tymoshenko.model.TaskDto;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @author Yakiv
 * @since 07.02.2016
 */
@Component
public interface Exporter {
    File export(List<TaskDto> taskList, File file) throws IOException, JAXBException;
}
