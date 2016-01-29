package com.tymoshenko.controller.task_monitor;

import com.tymoshenko.controller.Application;
import com.tymoshenko.model.TaskDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Yakiv
 * @since 29.01.2016
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = Application.class)
public class TaskListParserTest {

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
    private TaskListParser parser;

    @Test
    public void parse_ShouldIgnoreLinesThatAreNotProcesses() throws Exception {
        List<TaskDto> taskDtoList = parser.parse(_taskListOut);
        assertEquals("Unexpected taskDtoList size", _taskListOut.size() - TaskListParser.FIRST_PROCESS_LINE_INDEX, taskDtoList.size());
    }

    @Test
    public void parseLine_ShouldRemoveEmptyCharFromUsedMemory() throws Exception {
        // Process #2 has MemoryUsed = 594 912
        String processNumberTwo_System = _taskListOut.get(4);
        TaskDto taskDto = parser.parseLine(processNumberTwo_System);
        assertNotNull(taskDto);
        assertEquals("594912", taskDto.getMemory());
    }
}