package com.tymoshenko.controller.importing;

import com.tymoshenko.model.TaskDto;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

/**
 * @author Yakiv
 * @since 15.02.2016
 */
@Component
public interface Importer {

    List<TaskDto> doImport(File file);
}
