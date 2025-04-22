package HttpServer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import task.Status;
import task.Task;
import java.io.IOException;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

public class TaskHandlerTest extends HttpTaskManagerTasksTest {

    @Test
    public void testAddTask() throws IOException, InterruptedException {
        Task task = new Task("Test Task", "Description", Status.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        String taskJson = gson.toJson(task);

        HttpResponse<String> response = sendPostRequest("http://localhost:8080/tasks", taskJson);

        Assertions.assertEquals(201, response.statusCode());
        Assertions.assertNotNull(manager.getTasks());
        Assertions.assertEquals(1, manager.getTasks().size());
        Assertions.assertEquals("Test Task", manager.getTasks().getFirst().getName());
    }

    @Test
    public void testGetTask() throws IOException, InterruptedException {
        // Добавляем задачу
        Task task = new Task("Test Task", "Description", Status.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        manager.addTask(task);

        HttpResponse<String> response = sendGetRequest("http://localhost:8080/tasks/" + task.getTaskId());

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(gson.toJson(task), response.body());
    }

    @Test
    public void testGetNonExistentTask() throws IOException, InterruptedException {

        HttpResponse<String> response = sendGetRequest("http://localhost:8080/tasks/999");

        Assertions.assertEquals(404, response.statusCode());
    }

    @Test
    public void testDeleteTask() throws IOException, InterruptedException {
        Task task = new Task("Test Task", "Description", Status.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        manager.addTask(task);

        HttpResponse<String> response = sendDeleteRequest("http://localhost:8080/tasks/" + task.getTaskId());
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertTrue(manager.getTasks().isEmpty(), "Задача не была удалена");
    }

    @Test
    public void testDeleteNonExistentTask() throws IOException, InterruptedException {
        HttpResponse<String> response = sendDeleteRequest("http://localhost:8080/tasks/999");
        Assertions.assertEquals(404, response.statusCode());
    }
}