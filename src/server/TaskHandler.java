package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exception.ManagerSaveException;
import exception.NotFoundException;
import manager.TaskManager;
import task.Task;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager manager;
    private final Gson gson;

    public TaskHandler(TaskManager manager, Gson gson) {
        this.manager = manager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange e) throws IOException {
        String path = e.getRequestURI().getPath();
        String[] pathParts = path.split("/");
        try {
            switch (e.getRequestMethod()) {
                case "GET":
                    handleGetRequest(e, pathParts);
                    break;
                case "POST":
                    handlePostRequest(e);
                    break;
                case "DELETE":
                    handleDeleteRequest(e, pathParts);
                default:
                    sendNotFound(e, "Этот метод нельзя вызывать");
            }
        } catch (NotFoundException exchange) {
            sendNotFound(e, exchange.getMessage());
        } catch (Exception exchange) {
            sendNotFound(e, "Произошла ошибка: " + exchange.getMessage());
        }
    }

    private void handleGetRequest(HttpExchange e, String[] pathParts) throws IOException {
        if (pathParts.length == 2 && pathParts[1].equals("tasks")) {
            List<Task> tasks = manager.getTasks();
            String resp = gson.toJson(tasks);
            sendText(e, resp);
        } else if (pathParts.length == 3 && pathParts[1].equals("tasks")) {
            int id = getIdFromPath(pathParts);
            Optional<Task> taskOptional = manager.getTaskId(id);
            if (taskOptional.isPresent()) {
                String response = gson.toJson(taskOptional.get());
                sendText(e, response);
            } else {
                sendNotFound(e, "Задача не найдена");
            }
        } else {
            sendNotFound(e, "Недопустимый запрос");
        }
    }

    private void handlePostRequest(HttpExchange e) throws IOException {
        String json = readRequestBody(e);
        Task task = gson.fromJson(json, Task.class);
        try {
            if (task.getTaskId() == 0) {
                manager.addTask(task);
                sendCreated(e, "Задача добавлена");
            } else {
                manager.updateTask(task);
                sendCreated(e, "Задача обнавлена");
            }
        } catch (ManagerSaveException ex) {
            sendHasInteractions(e, ex.getMessage());
        }
    }

    private void handleDeleteRequest(HttpExchange e, String[] pathParts) throws IOException {
        if (pathParts.length == 3 && pathParts[1].equals("tasks")) {
            int id = getIdFromPath(pathParts);
            manager.deleteTaskId(id);
            sendText(e, "Задача удалена");
        } else {
            sendNotFound(e, "Недопустимый запрос");
        }
    }
}