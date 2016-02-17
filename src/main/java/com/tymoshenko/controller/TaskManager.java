package com.tymoshenko.controller;

import com.tymoshenko.model.TaskDto;

import java.io.File;
import java.util.List;

/**
 * Main service.
 *
 * @author Yakiv
 * @since 29.01.2016
 */
public interface TaskManager {

    /**
     * Initilizes task list.
     */
    List<TaskDto> taskList();

    /**
     * Tasks grouped by name and their memory aggregated.
     * No duplicated task names.
     */
    List<TaskDto> taskListCollapseDuplicates(List<TaskDto> taskList);

    //TODo : export to Excel should also generate a chart about used memory by each task
    void exportTaskList(File exportFile);

    // TODO : after import must compare current task list with the imported one (GUI)
    void importTaskList(File importFile);
}
