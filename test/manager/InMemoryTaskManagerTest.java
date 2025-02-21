package manager;

import task.*;
import util.Managers;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static task.Status.*;

public class InMemoryTaskManagerTest {

    static TaskManager taskManager = Managers.getDefault();
    HistoryManager historyManager = Managers.getDefaultHistory();


    @Test
    public void shouldReturnEpicStatusAsNewWhenAllSubtasksInStatusNew(){
        assertEquals(NEW, taskManager.getEpic().getFirst().getStatus(), "У эпика должен быть статус NEW");
    }

    @Test
    public void shouldReturnEpicStatusAsDoneWhenAllSubtasksInStatusDone(){
        Epic epic = new Epic("epic1", "Описание эпика 1", NEW);
        taskManager.addEpic(epic);
        Subtask subtask1 = new Subtask("подзадача 1","Описание подзадачи 1",DONE, epic.getTaskId());
        taskManager.addSubtask(subtask1);
        Subtask subtask2 = new Subtask("подзадача 2","Описание подзадачи 2",DONE, epic.getTaskId());
        taskManager.addSubtask(subtask2);
        assertEquals(DONE, taskManager.getEpicId(epic.getTaskId()).getStatus(), "У эпика должен быть статус DONE");
    }

    @Test
    void addNewTask() {
        Task task = new Task("Test addNewTask", "Test addNewTask description", NEW);
        final int taskId = taskManager.addTask(task);
        final Task savedTask = taskManager.getTaskId(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final ArrayList<Task> tasks = taskManager.getTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.getFirst(), "Задачи не совпадают.");
    }

    @Test
    void taskEqualsTaskOfId() {
        Task task = new Task("Test addNewTask", "Test addNewTask description", NEW);
        Task task2 = new Task("Test addNewTask", "Test addNewTask description", NEW);
        task2.setTaskId(0);
        assertEquals(task.getTaskId(), task2.getTaskId());
    }

    @Test
    void heirsEqualsHeirsOfId(){
        Epic firstEpic = new Epic("Первый эпик", "Описание первого эпика", Status.NEW);
        Epic secondEpic = new Epic("Второй эпик", "Описание второго эпика", Status.NEW);
        secondEpic.setTaskId(0);
        assertEquals(firstEpic.getTaskId(), secondEpic.getTaskId());
    }

    @Test
    void addTaskInMemoryTaskManager(){
        Task task = new Task("Test addNewTask", "Test addNewTask description", NEW);
        Epic firstEpic = new Epic("Первый эпик", "Описание первого эпика", Status.NEW);
        Subtask thirdSubtask = new Subtask("Подзадача второго эпика",
                "Описание подзадачи второго эпика", Status.NEW, firstEpic.getTaskId());
        taskManager.addTask(task);
        taskManager.addEpic(firstEpic);
        taskManager.addSubtask(thirdSubtask);

        assertEquals(task.getTaskId(), taskManager.getTaskId(1).getTaskId());
    }

    @Test
    void comparingFieldsAfterAddingManager(){
        Task task = new Task("Test addNewTask", "Test addNewTask description", NEW);
        taskManager.addTask(task);
        assertEquals(task, taskManager.getTaskId(task.getTaskId()));
    }

    @Test
    void compareHistoryTaskAndRealTask(){
        Task task = new Task("Test addNewTask", "Test addNewTask description", NEW);
        taskManager.addTask(task);
        final ArrayList<Task> history = historyManager.getHistory();
        assertEquals(history, taskManager.getHistory());
    }
}
