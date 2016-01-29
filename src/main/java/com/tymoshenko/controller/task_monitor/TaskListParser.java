package com.tymoshenko.controller.task_monitor;

import com.tymoshenko.model.TaskDto;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Parses console output of wni32//tasklist command into List<TaskDto>.
 *
 * @author Yakiv Tymoshenko
 * @since 29.01.2016
 */
@Component
class TaskListParser {
    // Skip column names
    static final int FIRST_PROCESS_LINE_INDEX = 3;

    List<TaskDto> parse(List<String> taskListOut) {
        List<TaskDto> taskList = newArrayList();
        for (int i = FIRST_PROCESS_LINE_INDEX; i < taskListOut.size(); i++) {
            TaskDto taskDto = parseLine(taskListOut.get(i));
            if (taskDto != null) {
                taskList.add(taskDto);
            }
        }
        return taskList;
    }

    TaskDto parseLine(String line) {
        final String _space = "\\s";
        final String _twoOrMoreSpaces = "\\s{2,}";
        /* We expect the line to be in the following format:
         (1) Name (2) PID <space> Session_Name (3) Session_Number (4) Memory_Used <space> Memory_Unit
         e.g.:

            Имя образа                     PID Имя сессии          № сеанса       Память
            ========================= ======== ================ =========== ============
            System Idle Process              0 Services                   0         4 КБ
            System                           4 Services                   0   594 912 КБ
            smss.exe                       420 Services                   0       216 КБ
         */
        final int _expectedTokensCount = 4;
        TaskDto taskDto = null;
        String[] tokens = line.split(_twoOrMoreSpaces);
        if (tokens.length == _expectedTokensCount) {
            String name = tokens[0];
            String pid = tokens[1].split(_space)[0];
            // Skip Memory_Unit and undo Integer formatting
            String memory = removeNonDigitChars(tokens[3].split(_space)[0]);
            taskDto = new TaskDto.Builder()
                    .withName(name)
                    .withPid(pid)
                    .withMemory(memory)
                    .build();
        }
        return taskDto;
    }

    private String removeNonDigitChars(String number) {
        StringBuilder sb = new StringBuilder();
        char[] chars = number.toCharArray();
        for (char c : chars) {
            if (Character.isDigit(c)) {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
