package com.tymoshenko.model;

import javafx.beans.property.*;

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

        public Builder withPid(String pid) {
            this.pid = pid;
            return this;
        }

        public Builder withMemory(long memory) {
            this.memory = memory;
            return this;
        }
    }

    //================= Instance fileds and methods =================
    private StringProperty name;
    private StringProperty pid;
    private LongProperty memory;

    // Private bu intent - use the Builder instead
    private TaskDto(Builder builder) {
        this.name = new SimpleStringProperty(builder.name);
        this.pid = new SimpleStringProperty(builder.pid);
        this.memory = new SimpleLongProperty(builder.memory);
    }

    @Override
    public String toString() {
        return String.format(Locale.ENGLISH, "%-30s %10s %,15d", name.get(), pid.get(), memory.get());
    }

    public String getName() {
        return name.get();
    }

    public String getPid() {
        return pid.get();
    }

    public Long getMemory() {
        return memory.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public StringProperty pidProperty() {
        return pid;
    }

    public LongProperty memoryProperty() {
        return memory;
    }

    public void setMemory(long memory) {
        this.memory.set(memory);
    }
}
