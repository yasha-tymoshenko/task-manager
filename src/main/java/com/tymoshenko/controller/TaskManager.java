package com.tymoshenko.controller;

import com.tymoshenko.model.ExportFormat;

import java.io.File;

/**
 * @author Yakiv
 * @since 29.01.2016
 */
public interface TaskManager {

    /**
     * Print task list to the Console, sorted by used memory
     */
    void taskList();

    /**
     * Tasks grouped by name and their memory aggregated.
     * No duplicated task names.
     */
    void taskListAggregatedByName();

    //TODo : export to Excel should also generate a chart about used memory by each task
    void exportTaskList(ExportFormat exportFormat, File exportFile);

    // TODO : after import must compare current task list with the imported one (GUI)
    void importTaskList(File importFile);

    void removeTask(String name);
}
