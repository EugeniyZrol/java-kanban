package manager;

import org.junit.jupiter.api.AfterAll;
import task.*;
import util.Managers;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static task.Status.*;

public class InMemoryTaskManagerTest {

    static TaskManager taskManager = Managers.getDefault();
    HistoryManager historyManager = Managers.getDefaultHistory();

    @AfterAll
    public static void clear(){
        taskManager.clearTask();
        taskManager.clearEpic();
    }
    @Test
    public void shouldReturnEpicStatusAsNewWhenAllSubtasksInStatusNew(){
        System.out.println(taskManager);
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

        assertEquals(task.getTaskId(), taskManager.getTaskId(2).getTaskId());
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
        final List<Task> history = historyManager.getHistory();
        assertEquals(history, taskManager.getHistory());
    }

    @Test
    void checkTaskAndTaskHistory(){
        Task task = new Task("Test addNewTask", "Test addNewTask description", NEW);
        taskManager.addTask(task);
        assertEquals(Status.NEW, taskManager.getTaskId(1).getStatus());
        assertEquals("Test addNewTask", taskManager.getTaskId(1).getName());

        task = new Task("name", "descreption", Status.IN_PROGRESS);
        taskManager.updateTask(task);
        assertEquals("Test addNewTask", taskManager.getHistory().getFirst().getName());
    }
}
