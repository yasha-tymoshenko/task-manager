package com.tymoshenko.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.Locale;

/**
 * @author Yakiv Tymoshenko
 * @since 29.01.2016
 */
public class TaskDto {

    /**
     * Builds the TaskDto instance
     */
    public static class Builder {
        private String name;
        private String pid;
        private String memory;

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

        public Builder withPid(String pid) {
            this.pid = pid;
            return this;
        }

        public Builder withMemory(String memory) {
            this.memory = memory;
            return this;
        }
    }

    //================= Instance fileds and methods =================
    private StringProperty name;
    private StringProperty pid;
    private StringProperty memory;

    // Private bu intent - use the Builder instead
    private TaskDto(Builder builder) {
        this.name = new SimpleStringProperty(builder.name);
        this.pid = new SimpleStringProperty(builder.pid);
        this.memory = new SimpleStringProperty(builder.memory);
    }

    @Override
    public String toString() {
        return String.format(Locale.ENGLISH, "%-30s %10s %,15d", name.get(), pid.get(), Integer.valueOf(memory.get()));
    }

    public String getName() {
        return name.get();
    }

    public String getPid() {
        return pid.get();
    }

    public String getMemory() {
        return memory.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public StringProperty pidProperty() {
        return pid;
    }

    public StringProperty memoryProperty() {
        return memory;
    }

    public void setMemory(String memory) {
        this.memory.set(memory);
    }
}
