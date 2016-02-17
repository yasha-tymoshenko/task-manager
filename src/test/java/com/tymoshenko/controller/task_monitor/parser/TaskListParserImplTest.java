package com.tymoshenko.controller.task_monitor.parser;

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

/**
 * @author Yakiv
 * @since 29.01.2016
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/resources/beans.xml")
public class TaskListParserImplTest {

    // Processes = 21, but siz = 24. 3 redundant lines for formatting (e.g. col names).
    private static final List<String> _taskListOut = Arrays.asList(
            "\n",
            "��� ������                     PID ��� ������          � ������       ������\n",
            "========================= ======== ================ =========== ============\n",
            "System Idle Process              0 Services                   0         4 ��\n",
            "System                           4 Services                   0   594�912 ��\n",
            "smss.exe                       420 Services                   0       216 ��\n",
            "csrss.exe                      588 Services                   0     1�428 ��\n",
            "����� ������ ����������777              1000000 ������                   400000000         4 ��\n",
            "csrss.exe                      680 Console                    1     5�832 ��\n",
            "services.exe                   752 Services                   0     3�908 ��\n"
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
        final String _line = "System                           4 Services                   0   594�912 ��\n";

        TaskDto taskDto = parser.parseLine(_line);

        assertEquals("System", taskDto.getName());
        assertEquals(4, (long) taskDto.getPid());
        assertEquals(594912, (long) taskDto.getMemory());
    }

    @Test
    public void parseLine_ShouldParseTaskNameWithSpaces() throws Exception {
        final String _line = "System Idle Process              0 Services                   0         4 ��\n";

        TaskDto taskDto = parser.parseLine(_line);

        assertEquals("System Idle Process", taskDto.getName());
        assertEquals(0, (long) taskDto.getPid());
        assertEquals(4, (long) taskDto.getMemory());
    }

    @Test
    public void parseLine_ShouldParseTaskNameWithManySpaces() throws Exception {
        final String _line = "System       Idle     Process              0 Services                   0         4 ��\n";

        TaskDto taskDto = parser.parseLine(_line);

        assertEquals("System       Idle     Process", taskDto.getName());
        assertEquals(0, (long) taskDto.getPid());
        assertEquals(4, (long) taskDto.getMemory());
    }

    @Test
    public void parseLine_ShouldParseTaskNameWithDigits() throws Exception {
        final String _line = "SystemTask007              0 Services                   0         4 ��\n";

        TaskDto taskDto = parser.parseLine(_line);

        assertEquals("SystemTask007", taskDto.getName());
        assertEquals(0, (long) taskDto.getPid());
        assertEquals(4, (long) taskDto.getMemory());
    }

    @Test
    public void parseLine_ShouldParseTaskNameWithSpecialChars() throws Exception {
        final String _line = "SystemTask007 specChar%&^@#!@#$%^&*()              0 Services                   0         4 ��\n";

        TaskDto taskDto = parser.parseLine(_line);

        assertEquals("SystemTask007 specChar%&^@#!@#$%^&*()", taskDto.getName());
        assertEquals(0, (long) taskDto.getPid());
        assertEquals(4, (long) taskDto.getMemory());
    }

    @Test
    public void parseLine_ShouldParseUkrainianName() throws Exception {
        final String _line = "����� ������ ����������777              1000000 ������                   400000000         4 ��\n";

        TaskDto taskDto = parser.parseLine(_line);

        assertEquals("����� ������ ����������777", taskDto.getName());
        assertEquals(1000000, (long) taskDto.getPid());
        assertEquals(4, (long) taskDto.getMemory());

    }

    @Test
    public void parseLine_ShouldParseChineseName() throws Exception {
        final String _line = " ??? ??? ? ??              1000000 ???                   400000000         4 ��\n";

        TaskDto taskDto = parser.parseLine(_line);

        assertEquals(" ??? ??? ? ??", taskDto.getName());
        assertEquals(1000000, (long) taskDto.getPid());
        assertEquals(4, (long) taskDto.getMemory());
    }

    @Test
    public void parseLine_ShouldParseWhenNameIsDigitsOnly() throws Exception {
        final String _line = "9823949234              0 Services                   0         4 ��\n";

        TaskDto taskDto = parser.parseLine(_line);

        assertEquals("9823949234", taskDto.getName());
        assertEquals(0, (long) taskDto.getPid());
        assertEquals(4, (long) taskDto.getMemory());
    }

    @Test(expected = ParseException.class)
    public void parseLine_ShouldThrowException_WhenLineIsNotTask() throws Exception {
        final String _line = "��� ������                     PID ��� ������          � ������       ������\n";

        parser.parseLine(_line);
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
        assertEquals(594912, (long) taskDto.getMemory());
    }


    //TODO remove this test (needed to test regex only)
//    @Test
    public void testName() throws Exception {
//        "smss.exe                       420 Services                   0       216 ��\n",
        final String _line = _taskListOut.get(5);
        String regex = "(^\\p{L}+[\\s?\\p{Alnum}.,_]*?) (\\d+[.,_]?) ([\\s?\\p{Alnum}.,_]*?) (\\d+[.,_]?) (\\p{all}*)";

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