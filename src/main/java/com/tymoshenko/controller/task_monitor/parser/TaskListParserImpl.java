package com.tymoshenko.controller.task_monitor.parser;

import com.tymoshenko.model.TaskDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.collect.Lists.newArrayList;

/**
 * @author Yakiv Tymoshenko
 * @since 29.01.2016
 */
@Component
class TaskListParserImpl implements TaskListParser {

    private final static Logger LOG = LoggerFactory.getLogger(TaskListParserImpl.class);

    public List<TaskDto> parse(List<String> taskListOut) {
        List<TaskDto> taskList = newArrayList();
        for (int i = FIRST_PROCESS_LINE_INDEX; i < taskListOut.size(); i++) {
            TaskDto taskDto = null;
            try {
                taskDto = parseLine(taskListOut.get(i));
                taskList.add(taskDto);
            } catch (ParseException e) {
                LOG.warn(e.getMessage());
            }
        }
        return taskList;
    }

    TaskDto parseLine(String line) throws ParseException {
        TaskDto taskDto;
         /* We expect the line to be in the following format:
         (1) Name (2) PID <space> Session_Name (3) Session_Number (4) Memory_Used <space> Memory_Unit
         e.g.:

            Имя образа                     PID Имя сессии          № сеанса       Память
            ========================= ======== ================ =========== ============
            System Idle Process              0 Services                   0         4 КБ
            System                           4 Services                   0   594 912 КБ
            smss.exe                       420 Services                   0       216 КБ
         */
//        String regex = "(^\\p{L}+[\\s?\\p{Alnum}]*?) (\\d+?) ([\\s?\\p{Alnum}]*?) (\\d+?) (\\p{all}*)";
        String regex = "(^\\p{L}+[\\s?\\p{Alnum}.,_~!@#$%^&*()]*?) (\\d+[.,_]?) ([\\s?\\p{Alnum}.,_]*?) (\\d+[.,_]?) (\\p{all}*)";
        Pattern pattern = Pattern.compile(regex, Pattern.UNICODE_CHARACTER_CLASS);
        Matcher matcher = pattern.matcher(line);
        if (matcher.find()) {
            String taskName = matcher.group(1).trim();
            String pid = matcher.group(2).trim();
            String memoryUsed = removeNonDigitChars(matcher.group(5).trim());
            taskDto = new TaskDto.Builder()
                    .withName(taskName)
                    .withPid(pid)
                    .withMemory(memoryUsed)
                    .build();
        } else {
            // FIXME : workaround - better to fix regex or convert _line to Unicode before parsing
            // If failed to recognise Unicode task names
            String tokens[] = line.split("\\s{2,}");
            if (tokens.length != 4) {
                throw new ParseException(String.format("Can't parse line representing a Task: %s", line), 0);
            }
            String taskName = tokens[0];
            String pid = tokens[1].split("\\s")[0];
            String memoryUsed = removeNonDigitChars(tokens[3]);
            try {
                // Check if pid and memory used are numbers
                int i = Integer.valueOf(pid);
                i = Integer.valueOf(memoryUsed);
            } catch (NumberFormatException e) {
                throw new ParseException(String.format("Can't parse line representing a Task: %s", line), 0);
            }
            taskDto = new TaskDto.Builder()
                    .withName(taskName)
                    .withPid(pid)
                    .withMemory(memoryUsed)
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
