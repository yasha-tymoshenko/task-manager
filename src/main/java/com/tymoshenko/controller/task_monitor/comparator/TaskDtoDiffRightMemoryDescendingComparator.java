package com.tymoshenko.controller.task_monitor.comparator;

import com.tymoshenko.model.TaskDtoDiff;

import java.util.Comparator;

/**
 * @author Yakiv Tymoshenko
 * @since 17.02.2016
 */
public class TaskDtoDiffRightMemoryDescendingComparator implements Comparator<TaskDtoDiff> {

    @Override
    public int compare(TaskDtoDiff first, TaskDtoDiff second) {
        return second.getRight().getMemory().compareTo(first.getRight().getMemory());
    }
}
