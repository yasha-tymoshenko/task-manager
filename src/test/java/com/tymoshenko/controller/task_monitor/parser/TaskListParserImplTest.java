package com.tymoshenko.controller.task_monitor.parser;

import com.tymoshenko.MainApp;
import com.tymoshenko.model.TaskDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * @author Yakiv
 * @since 29.01.2016
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = MainApp.class)
public class TaskListParserImplTest {

    // Processes = 21, but siz = 24. 3 redundant lines for formatting (e.g. col names).
    private static final List<String> _taskListOut = Arrays.asList(
            "\n",
            "Имя образа                     PID Имя сессии          № сеанса       Память\n",
            "========================= ======== ================ =========== ============\n",
            "System Idle Process              0 Services                   0         4 КБ\n",
            "System                           4 Services                   0   594 912 КБ\n",
            "smss.exe                       420 Services                   0       216 КБ\n",
            "csrss.exe                      588 Services                   0     1 428 КБ\n",
            "csrss.exe                      680 Console                    1     5 832 КБ\n",
            "services.exe                   752 Services                   0     3 908 КБ\n"
    );

    @Autowired
    private TaskListParserImpl parser;

    @Test
    public void parse_ShouldIgnoreLinesThatAreNotProcesses() throws Exception {
        List<TaskDto> taskDtoList = parser.parse(_taskListOut);
        assertEquals("Unexpected taskDtoList size", _taskListOut.size() - TaskListParserImpl.FIRST_PROCESS_LINE_INDEX, taskDtoList.size());
    }


    @Test
    public void parseLine_ShouldFormValidTaskDto() throws Exception {
        final String _line = "System                           4 Services                   0   594 912 КБ\n";

        TaskDto taskDto = parser.parseLine(_line);

        assertEquals("System", taskDto.getName());
        assertEquals("4", taskDto.getPid());
        assertEquals("594912", taskDto.getMemory());
    }

    @Test
    public void parseLine_ShouldParseTaskNameWithSpaces() throws Exception {
        final String _line = "System Idle Process              0 Services                   0         4 КБ\n";

        TaskDto taskDto = parser.parseLine(_line);

        assertEquals("System Idle Process", taskDto.getName());
        assertEquals("0", taskDto.getPid());
        assertEquals("4", taskDto.getMemory());
    }

    @Test
    public void parseLine_ShouldParseTaskNameWithManySpaces() throws Exception {
        final String _line = "System       Idle     Process              0 Services                   0         4 КБ\n";

        TaskDto taskDto = parser.parseLine(_line);

        assertEquals("System       Idle     Process", taskDto.getName());
        assertEquals("0", taskDto.getPid());
        assertEquals("4", taskDto.getMemory());
    }

    @Test
    public void parseLine_ShouldParseTaskNameWithDigits() throws Exception {
        final String _line = "SystemTask007              0 Services                   0         4 КБ\n";

        TaskDto taskDto = parser.parseLine(_line);

        assertEquals("SystemTask007", taskDto.getName());
        assertEquals("0", taskDto.getPid());
        assertEquals("4", taskDto.getMemory());
    }

    @Test
    public void parseLine_ShouldParseRussianName() throws Exception {

    }

    @Test
    public void parseLine_ShouldParseChineseName() throws Exception {

    }

    @Test(expected = ParseException.class)
    public void parseLine_ShouldThrowException_WhenNameIsDigitsOnly() throws Exception {
        final String _line = "9823949234              0 Services                   0         4 КБ\n";

        TaskDto taskDto = parser.parseLine(_line);
        assertNull(taskDto);
    }

    @Test(expected = ParseException.class)
    public void parseLine_ShouldThrowException_WhenLineIsNotTask() throws Exception {
        final String _line = "Имя образа                     PID Имя сессии          № сеанса       Память\n";

        TaskDto taskDto = parser.parseLine(_line);
    }

    @Test(expected = ParseException.class)
    public void parseLine_ShouldThrowException_WhenLineIsBlank() throws Exception {
        final String _line = "";

        parser.parseLine(_line);
    }

    @Test
    public void parseLine_ShouldRemoveEmptyCharFromUsedMemory() throws Exception {
        // Process #2 has MemoryUsed = 594 912
        String processNumberTwo_System = _taskListOut.get(4);
        TaskDto taskDto = parser.parseLine(processNumberTwo_System);
        assertNotNull(taskDto);
        assertEquals("594912", taskDto.getMemory());
    }

    @Test
    public void testName() throws Exception {
//        "smss.exe                       420 Services                   0       216 КБ\n",
        final String _line = _taskListOut.get(5);
        String regex = "(^\\p{all}+[\\s?\\p{all}]*?) (\\d+?) (\\s?\\p{Alnum}*?) (\\d+?) (\\p{all}*)";

        Pattern pattern = Pattern.compile(regex, Pattern.UNICODE_CASE);
        Matcher matcher = pattern.matcher(_line);
        if (matcher.find()) {
            System.out.println(matcher.groupCount());
            for (int i = 1; i <= matcher.groupCount(); i++) {
                System.out.println(i + ": " + matcher.group(i));
            }
        }
    }
}