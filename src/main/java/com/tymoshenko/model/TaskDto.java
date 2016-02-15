package com.tymoshenko.model;

import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import javax.xml.bind.annotation.*;
import java.util.Locale;

/**
 * @author Yakiv Tymoshenko
 * @since 29.01.2016
 */
@XmlRootElement(name = "task")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(propOrder = {"name", "pid", "memory"})
public class TaskDto {

    /**
     * Builds the TaskDto instance
     */
    public static class Builder {
        private String name;
        private long pid;
        private long memory;

        /**
         *
         * @return new instance of TaskDto
         */
        public TaskDto build() {
            return new TaskDto(this);
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withPid(long pid) {
            this.pid = pid;
            return this;
        }

        public Builder withMemory(long memory) {
            this.memory = memory;
            return this;
        }
    }

    //================= Instance fileds and methods =================
    private StringProperty name = new SimpleStringProperty();
    private LongProperty pid = new SimpleLongProperty();
    private LongProperty memory = new SimpleLongProperty();

    // Private constructor needed by JAXB
    private TaskDto() {
    }

    // Private bu intent - use the Builder instead
    private TaskDto(Builder builder) {
        this.name.set(builder.name);
        this.pid.set(builder.pid);
        this.memory.set(builder.memory);
    }

    @Override
    public String toString() {
        return String.format(Locale.ENGLISH, "%-30s %10s %,15d", name.get(), pid.get(), memory.get());
    }

    @XmlElement(name = "name")
    public String getName() {
        return name.get();
    }

    @XmlElement(name = "pid")
    public Long getPid() {
        return pid.get();
    }

    @XmlElement(name = "memory")
    public Long getMemory() {
        return memory.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public void setPid(Long pid) {
        this.pid.set(pid);
    }

    public void setMemory(Long memory) {
        this.memory.set(memory);
    }

    public StringProperty nameProperty() {
        return name;
    }

    public LongProperty pidProperty() {
        return pid;
    }

    public LongProperty memoryProperty() {
        return memory;
    }
}
