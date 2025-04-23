package HttpServer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Status;
import task.Subtask;
import java.io.IOException;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class EpicHandlerTest extends HttpTaskManagerTasksTest {

    @Test
    public void testAddEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Test Epic", "Epic Description", Status.NEW);
        String epicJson = gson.toJson(epic);

        HttpResponse<String> response = sendPostRequest("http://localhost:8080/epics", epicJson);

        Assertions.assertEquals(201, response.statusCode());
        Assertions.assertEquals(1, manager.getEpic().size());
    }

    @Test
    public void testGetEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Test Epic", "Epic Description", Status.NEW);
        manager.addEpic(epic);

        HttpResponse<String> response = sendGetRequest("http://localhost:8080/epics/" + epic.getTaskId());

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(gson.toJson(epic), response.body());
    }

    @Test
    public void testGetNonExistentEpic() throws IOException, InterruptedException {
        HttpResponse<String> response = sendGetRequest("http://localhost:8080/epics/999");

        Assertions.assertEquals(404, response.statusCode());
    }

    @Test
    public void testGetSubtaskByEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Epic description", Status.NEW);
        manager.addEpic(epic);
        Subtask subtask = new Subtask("Subtask", "Subtask description", Status.IN_PROGRESS,
                Duration.ofMinutes(30), LocalDateTime.now(), epic.getTaskId());
        manager.addSubtask(subtask);

        HttpResponse<String> response = sendGetRequest("http://localhost:8080/epics/" + epic.getTaskId() +
                "/subtasks");
        Assertions.assertEquals(200, response.statusCode());

        // Проверяем, что подзадача возвращается
        List<Subtask> subtasksFromManager = manager.getEpicSubtasks(epic.getTaskId());
        Assertions.assertNotNull(subtasksFromManager, "Подзадачи не возвращаются");
        Assertions.assertEquals(1, subtasksFromManager.size(), "Некорректное количество подзадач");
        Assertions.assertEquals("Subtask", subtasksFromManager.getFirst().getName(), "Некорректное имя подзадачи");
    }

    @Test
    public void testGetSubtaskByInvalidEpicId() throws IOException, InterruptedException {
        HttpResponse<String> response = sendGetRequest("http://localhost:8080/epics/999/subtasks");

        Assertions.assertEquals(404, response.statusCode());
    }
}