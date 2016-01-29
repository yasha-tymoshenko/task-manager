package com.tymoshenko.model;

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
    private String name;
    private String pid;
    private String memory;

    // Private bu intent - use the Builder instead
    private TaskDto(Builder builder) {
        this.name = builder.name;
        this.pid = builder.pid;
        this.memory = builder.memory;
    }

    @Override
    public String toString() {
        return String.format(Locale.ENGLISH, "%-30s %10s %,15d", name, pid, Integer.valueOf(memory));
    }

    public String getName() {
        return name;
    }

    public String getPid() {
        return pid;
    }

    public String getMemory() {
        return memory;
    }

    public void setMemory(String memory) {
        this.memory = memory;
    }
}
