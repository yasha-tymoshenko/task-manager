package com.tymoshenko.controller.task_monitor.command;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * TODO : support different command line args like "/fo csv"
 * TODO : use java.lang.ProcessBuilder
 * Executes win32//tasklist.
 * The result is a formattes list of running processes.
 *
 * Example output:
 *        ��� ������                     PID ��� ������          � ������       ������
 *        ========================= ======== ================ =========== ============
 *        System Idle Process              0 Services                   0         4 ��
 *        System                           4 Services                   0   587�732 ��
 *        smss.exe                       420 Services                   0       216 ��
 *        csrss.exe                      588 Services                   0     1�444 ��
 *        csrss.exe                      680 Console                    1     5�744 ��
 *        wininit.exe                    704 Services                   0       716 ��
 *
 * @author Yakiv Tymoshenko
 * @since 29.01.2016
 */
@Component
public class TaskListCommand extends Command<List<String>> {

    @Autowired
    private Command<Charset> charsetCommand;

    public TaskListCommand() {
        super("tasklist");
    }

    @Override
    public List<String> execute() {
        init();

        List<String> consoleLines = newArrayList();
        Charset charset = charsetCommand.execute();
        try (BufferedReader taskListOutput = new BufferedReader(new InputStreamReader(process.getInputStream(), charset))) {
            String line;
            while ((line = taskListOutput.readLine()) != null) {
                consoleLines.add(line);
            }
        } catch (IOException e) {
            LOG.error(e.getMessage());
        }
        return consoleLines;
    }
}
