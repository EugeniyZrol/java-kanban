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

public class SubtaskHandlerTest extends HttpTaskManagerTasksTest {

    @Test
    public void testAddSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Test Epic", "Epic Description", Status.NEW);
        manager.addEpic(epic);

        Subtask subtask = new Subtask("Test Subtask", "Subtask Description", Status.NEW, Duration.ofMinutes(5), LocalDateTime.now(), epic.getTaskId());
        String subtaskJson = gson.toJson(subtask);

        HttpResponse<String> response = sendPostRequest("http://localhost:8080/subtasks", subtaskJson);

        Assertions.assertEquals(201, response.statusCode());
        Assertions.assertEquals(1, manager.getSubtask().size());
    }

    @Test
    public void testGetSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Test Epic", "Epic Description", Status.NEW);
        manager.addEpic(epic);

        Subtask subtask = new Subtask("Test Subtask", "Subtask Description", Status.NEW, Duration.ofMinutes(5), LocalDateTime.now(), epic.getTaskId());
        manager.addSubtask(subtask);

        HttpResponse<String> response = sendGetRequest("http://localhost:8080/subtasks/" + subtask.getTaskId());

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(gson.toJson(subtask), response.body());
    }

    @Test
    public void testGetNonExistentSubtask() throws IOException, InterruptedException {
        HttpResponse<String> response = sendGetRequest("http://localhost:8080/subtasks/999");

        Assertions.assertEquals(404, response.statusCode());
    }
}