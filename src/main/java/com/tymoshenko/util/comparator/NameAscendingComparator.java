package com.tymoshenko.util.comparator;

import com.tymoshenko.model.TaskDto;

import java.util.Comparator;

/**
 * Compares TaskDto names in lexycographical order:
 * (A..Z).
 *
 * @author Yakiv Tymoshenko
 * @since 30.01.2016
 */
public class NameAscendingComparator implements Comparator<TaskDto> {

    @Override
    public int compare(TaskDto o1, TaskDto o2) {
        return o1.getName().compareToIgnoreCase(o2.getName());
    }
}
