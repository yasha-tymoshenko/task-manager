package com.tymoshenko.controller.importing;

import com.tymoshenko.model.TaskDto;

import java.io.File;
import java.util.List;

/**
 * @author Yakiv
 * @since 15.02.2016
 */
public interface Importer {

    List<TaskDto> doImport(File file);
}
