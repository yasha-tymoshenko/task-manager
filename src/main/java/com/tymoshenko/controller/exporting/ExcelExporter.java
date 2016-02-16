package com.tymoshenko.controller.exporting;

import com.tymoshenko.model.TaskDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBException;
import java.io.*;
import java.util.List;

/**
 * @author Yakiv Tymoshenko
 * @since 16.02.2016
 */
@Component
public class ExcelExporter implements Exporter {

    private static final Logger LOG = LoggerFactory.getLogger(ExcelExporter.class);

    public File export(List<TaskDto> taskList, File file) throws IOException, JAXBException {
        writeToCsv(taskList, file);
        return file;
    }

    private void writeToCsv(List<TaskDto> taskDtoList, File csvFile) {
        final String _csvSeparator = ",";
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(csvFile), "UTF-8"))) {
            for (TaskDto taskDto : taskDtoList) {
                StringBuilder line = new StringBuilder();
                line.append(taskDto.getName().trim());
                line.append(_csvSeparator);
                line.append(taskDto.getPid());
                line.append(_csvSeparator);
                line.append(taskDto.getMemory());
                writer.write(line.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            LOG.error(e.getMessage());
        }
    }
}
