package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Status;
import task.Task;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;



public class InMemoryHistoryManagerTest {

        private HistoryManager historyManager;

        private  Task task = new Task( "Задача 1", "Описание 1", Status.NEW);
        private  Task task2 = new Task( "Задача 2", "Описание 2", Status.IN_PROGRESS);
        private  Task task3 = new Task( "Задача 3", "Описание 3", Status.DONE);

    @BeforeEach
    void beforeEach() {
        task.setTaskId(1);
        task2.setTaskId(2);
        task3.setTaskId(3);
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    void add_shouldAddTasksToHistoryList() {
        Task thisTask = historyManager.add(task);
        Task thisTask1 = historyManager.add(task2);

        assertEquals(List.of(thisTask, thisTask1), historyManager.getHistory());
    }

    @Test
    void add_shouldReturnNullIfTaskIsEmpty() {
        historyManager.add(null);

        assertTrue(historyManager.getHistory().isEmpty());
    }

    @Test
    void add_shouldReturnAnEmptyListIfThereIsNoHistory() {
        assertTrue(historyManager.getHistory().isEmpty());
    }

    @Test
    void remove_shouldRemoveATaskFromHistory() {
        historyManager.add(task);
        Task thisTask1 = historyManager.add(task2);
        historyManager.remove(1);

        assertEquals(List.of(thisTask1), historyManager.getHistory());
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
