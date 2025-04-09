package manager;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import task.*;
import util.Managers;
import org.junit.jupiter.api.Test;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;
import static task.Status.*;

public class InMemoryTaskManagerTest {

    static TaskManager taskManager = Managers.getDefaultInMemory();

    @AfterAll
    public static void clear() {
        taskManager.clearTask();
        taskManager.clearEpic();
    }

    @Test
    void testAllSubtasksNew() {
        Epic epic = new Epic("epic1", "Описание эпика 1", NEW);
        taskManager.addEpic(epic);
        Subtask subtask1 = new Subtask("подзадача 1", "Описание подзадачи 1", NEW, epic.getTaskId());
        taskManager.addSubtask(subtask1);
        Subtask subtask2 = new Subtask("подзадача 2", "Описание подзадачи 2", NEW, epic.getTaskId());
        taskManager.addSubtask(subtask2);

        assertEquals(Status.NEW, epic.getStatus());
    }

    @Test
    void testAllSubtasksDone() {
        Epic epic = new Epic("epic1", "Описание эпика 1", NEW);
        taskManager.addEpic(epic);
        Subtask subtask1 = new Subtask("подзадача 1", "Описание подзадачи 1", DONE, epic.getTaskId());
        taskManager.addSubtask(subtask1);
        Subtask subtask2 = new Subtask("подзадача 2", "Описание подзадачи 2", DONE, epic.getTaskId());
        taskManager.addSubtask(subtask2);

        assertEquals(Status.DONE, epic.getStatus());
    }

    @Test
    void testSubtasksNewAndDone() {
        Epic epic = new Epic("epic1", "Описание эпика 1", NEW);
        taskManager.addEpic(epic);
        Subtask subtask1 = new Subtask("подзадача 1", "Описание подзадачи 1", NEW, epic.getTaskId());
        taskManager.addSubtask(subtask1);
        Subtask subtask2 = new Subtask("подзадача 2", "Описание подзадачи 2", DONE, epic.getTaskId());
        taskManager.addSubtask(subtask2);

        assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }

    @Test
    void testSubtasksInProgress() {
        Epic epic = new Epic("epic1", "Описание эпика 1", NEW);
        taskManager.addEpic(epic);
        Subtask subtask1 = new Subtask("подзадача 1", "Описание подзадачи 1", IN_PROGRESS, epic.getTaskId());
        taskManager.addSubtask(subtask1);
        Subtask subtask2 = new Subtask("подзадача 2", "Описание подзадачи 2", IN_PROGRESS, epic.getTaskId());
        taskManager.addSubtask(subtask2);

        assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }

    @Test
    void taskEqualsTaskOfId() {
        Task task = new Task("Test addNewTask", "Test addNewTask description", NEW);
        Task task2 = new Task("Test addNewTask", "Test addNewTask description", NEW);
        task2.setTaskId(0);
        assertEquals(task.getTaskId(), task2.getTaskId());
    }

    @Test
    void heirsEqualsHeirsOfId() {
        Epic firstEpic = new Epic("Первый эпик", "Описание первого эпика", Status.NEW);
        Epic secondEpic = new Epic("Второй эпик", "Описание второго эпика", Status.NEW);
        secondEpic.setTaskId(0);
        assertEquals(firstEpic.getTaskId(), secondEpic.getTaskId());
    }

    @Test
    void addTaskInMemoryTaskManager() {
        Task task = new Task("Название задачи", "Описание", NEW);
        taskManager.addTask(task);

        assertEquals(task, taskManager.getTaskId(task.getTaskId()));
    }

    @Test
    void comparingFieldsAfterAddingManager() {
        Task task = new Task("Test addNewTask", "Test addNewTask description", NEW);
        taskManager.addTask(task);
        assertEquals(task, taskManager.getTaskId(task.getTaskId()));
    }

    @Test
    void checkTaskAndTaskHistory() {
        Task task = new Task("Test addNewTask", "Test addNewTask description", NEW);
        taskManager.addTask(task);
        assertEquals(Status.NEW, taskManager.getTaskId(1).getStatus());
        assertEquals("Test addNewTask", taskManager.getTaskId(1).getName());

        task = new Task("name", "descreption", Status.IN_PROGRESS);
        taskManager.updateTask(task);
        assertEquals("Test addNewTask", taskManager.getHistory().getFirst().getName());
    }

    @Test
    void testSubtaskHasCorrectEpicId() {
        Epic epic = new Epic("Первый эпик", "Описание первого эпика", Status.NEW);
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("подзадача 1", "Описание подзадачи 1", IN_PROGRESS, epic.getTaskId());
        taskManager.addSubtask(subtask);

        // Проверяем, что сабтаск связан с эпиком
        assertEquals(epic.getTaskId(), subtask.getEpicId());
    }

    @Test
    public void testEpicHasSubtask() {
        Epic epic = new Epic("Первый эпик", "Описание первого эпика", Status.NEW);
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("подзадача 1", "Описание подзадачи 1", IN_PROGRESS, epic.getTaskId());
        taskManager.addSubtask(subtask);

        // Проверяем, что эпик содержит сабтаск
        Assertions.assertTrue(taskManager.getEpicSubtasks(epic.getTaskId()).contains(subtask), "Эпик содержит в себе подзадачу");
    }

    @Test
    public void testTaskOverlap() {
        // Создаем задачи
        Task task1 = new Task("Task 1", "Описание", Status.NEW, Duration.ofMinutes(30),
                LocalDateTime.of(2023, 10, 1, 10, 0));
        Task task2 = new Task("Task 2", "Описание", Status.NEW, Duration.ofMinutes(30),
                LocalDateTime.of(2023, 10, 1, 10, 15));
        Task task3 = new Task("Task 3", "Описание", Status.NEW, Duration.ofMinutes(30),
                LocalDateTime.of(2023, 10, 1, 11, 0));

        // Добавляем задачи в TreeSet
        TreeSet<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime,
                Comparator.nullsLast(Comparator.naturalOrder())));

        prioritizedTasks.add(task1);
        prioritizedTasks.add(task2);

        assertFalse(doesOverlap(prioritizedTasks, task3), "задача 3 не пересекается с задачами 1 и 2");

        Assertions.assertTrue(doesOverlap(prioritizedTasks, task2), "задача 2 пересекается с задачей 1");
    }

    private boolean doesOverlap(TreeSet<Task> tasks, Task newTask) {
        for (Task task : tasks) {
            if (task.getEndTime().isAfter(newTask.getStartTime()) && task.getStartTime().isBefore(newTask.getEndTime())) {
                return true;
            }
        }
        return false;
    }
}
