package manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Status;
import task.Task;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class InMemoryHistoryManagerTest {

    private HistoryManager historyManager;

    private final Task task = new Task("Задача 1", "Описание 1", Status.NEW);
    private final Task task2 = new Task("Задача 2", "Описание 2", Status.IN_PROGRESS);
    private final Task task3 = new Task("Задача 3", "Описание 3", Status.DONE);

    @BeforeEach
    void beforeEach() {
        task.setTaskId(1);
        task2.setTaskId(2);
        task3.setTaskId(3);
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    public void testEmptyHistory() {
        List<Task> history = historyManager.getHistory();
        Assertions.assertTrue(history.isEmpty(), "История должна быть пустой");
    }

    @Test
    void add_shouldAddTasksToHistoryList() {
        historyManager.add(task);
        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size(), "История должна содержать одну задачу");
        assertEquals(task, history.getFirst(), "Задачей в истории должна быть задача 1");
    }

    @Test
    public void testDuplicateTask() {
        historyManager.add(task);
        historyManager.add(task);
        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size(), "История по-прежнему должна содержать только одну задачу");
        assertEquals(task, history.getFirst(), "Задачей в истории должна быть задача 1");
    }

    @Test
    void remove_shouldRemoveATaskFromHistory() {
        historyManager.add(task);
        historyManager.add(task2);
        historyManager.remove(task.getTaskId());
        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size(), "История должна содержать одну задачу после удаления");
        assertEquals(task2, history.getFirst(), "Задачей в истории должно быть задание 2");
    }

    @Test
    void remove_shouldDoNothingIfPassedIdIsIncorrect() {
        Task thisTask = historyManager.add(task);
        Task thisTask1 = historyManager.add(task2);

        historyManager.remove(3);
        assertEquals(List.of(thisTask, thisTask1), historyManager.getHistory());
    }

    @Test
    void add_shouldNotAddExistingTaskToHistoryList() {
        Task thisTask = historyManager.add(task);
        historyManager.add(task);

        assertEquals(List.of(thisTask), historyManager.getHistory());
    }

    @Test
    void remove_shouldRemoveParticularTaskFromHistoryList() {
        Task thisTask = historyManager.add(task);
        Task thisTask1 = historyManager.add(task2);
        Task thisTask2 = historyManager.add(task3);
        historyManager.remove(thisTask1.getTaskId());

        assertEquals(List.of(thisTask, thisTask2), historyManager.getHistory());
    }
}
