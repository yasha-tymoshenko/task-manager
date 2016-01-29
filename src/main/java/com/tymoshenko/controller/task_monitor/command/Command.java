package com.tymoshenko.controller.task_monitor.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Executes win32 command.
 * Make sure to invoke init() as first statement when overriding execute()
 *
 * @author Yakiv Tymoshenko
 * @since 29.01.2016
 */
public abstract class Command<T> {

    static Logger LOG = LoggerFactory.getLogger(Command.class);

    protected String args = System.getenv("windir") + "\\system32\\";
    protected Process process;

    Command(String win32Command) {
        this.args += win32Command;
    }

    void init() {
        Runtime runtime = Runtime.getRuntime();
        try {
            process = runtime.exec(args);
        } catch (IOException e) {
            LOG.error(String.format("CMD command failed: \"%s\". Reason: %s", args, e.getMessage()));
        }
    }

    public abstract T execute();
}
