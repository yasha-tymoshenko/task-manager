package com.tymoshenko.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * @author Yakiv Tymoshenko
 * @since 07.02.2016
 */
@XmlRootElement(name = "tasks")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class TaskDtoList {
    private List<TaskDto> tasks;

    // Private constructor needed by JAXB
    private TaskDtoList() {
    }

    public TaskDtoList(List<TaskDto> tasks) {
        this.tasks = tasks;
    }

    @XmlElement(name = "task")
    public List<TaskDto> getTasks() {
        return tasks;
    }

    public void setTasks(List<TaskDto> tasks) {
        this.tasks = tasks;
    }
}

