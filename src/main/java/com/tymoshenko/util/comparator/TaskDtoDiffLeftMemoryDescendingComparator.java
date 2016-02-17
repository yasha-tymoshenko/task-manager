package com.tymoshenko.util.comparator;

import com.tymoshenko.model.TaskDtoDiff;

import java.util.Comparator;

/**
 * @author Yakiv Tymoshenko
 * @since 17.02.2016
 */
public class TaskDtoDiffLeftMemoryDescendingComparator implements Comparator<TaskDtoDiff> {
    @Override
    public int compare(TaskDtoDiff first, TaskDtoDiff second) {
        return second.getLeft().getMemory().compareTo(first.getLeft().getMemory());
    }
}
