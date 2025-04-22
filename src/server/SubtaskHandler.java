package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exception.ManagerSaveException;
import exception.NotFoundException;
import manager.TaskManager;
import task.Subtask;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class SubtaskHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager manager;
    private final Gson gson;

    public SubtaskHandler(TaskManager manager, Gson gson) {
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
                    break;
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
        if (pathParts.length == 2 && pathParts[1].equals("subtasks")) {
            List<Subtask> subtasks = manager.getSubtask();
            String resp = gson.toJson(subtasks);
            sendText(e, resp);
        } else if (pathParts.length == 3 && pathParts[1].equals("subtasks")) {
            int id = getIdFromPath(pathParts);
            Optional<Subtask> subtaskOptional = manager.getSubtaskId(id);
            if (subtaskOptional.isPresent()) {
                String response = gson.toJson(subtaskOptional.get());
                sendText(e, response);
            } else {
                sendNotFound(e, "Подзадача не найдена");
            }
        } else {
            sendNotFound(e, "Недопустимый запрос");
        }
    }

    private void handlePostRequest(HttpExchange e) throws IOException {
        String json = readRequestBody(e);
        Subtask subtask = gson.fromJson(json, Subtask.class);
        try {
            if (subtask.getTaskId() == 0) {
                manager.addSubtask(subtask);
                sendCreated(e, "Подзадача добавлена");
            } else {
                manager.updateSubtask(subtask);
                sendCreated(e, "Подзадача обновлена");
            }
        } catch (ManagerSaveException ex) {
            sendHasInteractions(e, ex.getMessage());
        }
    }

    private void handleDeleteRequest(HttpExchange e, String[] pathParts) throws IOException {
        if (pathParts.length == 3 && pathParts[1].equals("subtasks")) {
            int id = getIdFromPath(pathParts);
            manager.deleteSubtaskId(id);
            sendText(e, "Подзадача удалена");
        } else {
            sendNotFound(e, "Недопустимый запрос");
        }
    }


}