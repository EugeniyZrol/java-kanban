package HttpServer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import task.Status;
import task.Task;
import java.io.IOException;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;

public class HistoryHandlerTest extends HttpTaskManagerTasksTest {

    @Test
    public void testGetHistory() throws IOException, InterruptedException {
        Task task = new Task("Test Task", "Description", Status.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        manager.addTask(task);
        manager.getTaskId(task.getTaskId());

        HttpResponse<String> response = sendGetRequest("http://localhost:8080/history");

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(gson.toJson(Collections.singletonList(task)), response.body());
    }
}