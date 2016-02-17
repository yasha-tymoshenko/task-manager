package com.tymoshenko.controller.task_monitor.comparator;

import com.tymoshenko.model.TaskDtoDiff;

import java.util.Comparator;

/**
 * @author Yakiv Tymoshenko
 * @since 17.02.2016
 */
public class TaskDtoDiffMaxMemoryDescendingComparator implements Comparator<TaskDtoDiff> {

    @Override
    public int compare(TaskDtoDiff first, TaskDtoDiff second) {
        Long firstMaxMemory = Math.max(first.getLeft().getMemory(), first.getRight().getMemory());
        Long secondMaxMemory = Math.max(second.getLeft().getMemory(), second.getRight().getMemory());
        return secondMaxMemory.compareTo(firstMaxMemory);
    }
}
