package com.tymoshenko.controller.task_monitor.comparator;

import com.tymoshenko.model.TaskDto;

import java.util.Comparator;

/**
 * Sorting order: Z..A.
 *
 * @author Yakiv Tymoshenko
 * @since 30.01.2016
 */
public class MemoryUsedDescendingComparator implements Comparator<TaskDto> {

    @Override
    /**
     * Sorting order: Z..A.
     */
    public int compare(TaskDto first, TaskDto second) {
        Integer memoryUsedFirst = Integer.valueOf(first.getMemory());
        Integer memoryUsedSecond = Integer.valueOf(second.getMemory());
        return memoryUsedSecond - memoryUsedFirst;
    }
}
