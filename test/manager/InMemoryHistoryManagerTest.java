package manager;

import org.junit.jupiter.api.Test;
import task.Task;
import util.Managers;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static task.Status.NEW;

public class InMemoryHistoryManagerTest {

    @Test
    void add() {
        Task task = new Task("Test addNewTask", "Test addNewTask description", NEW);
        HistoryManager historyManager = Managers.getDefaultHistory();
        historyManager.add(task);
        final ArrayList<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size(), "История не пустая.");
    }
    
}
