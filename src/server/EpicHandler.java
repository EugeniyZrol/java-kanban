package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exception.ManagerSaveException;
import exception.NotFoundException;
import manager.TaskManager;
import task.Epic;
import task.Subtask;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class EpicHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager manager;
    private final Gson gson;

    public EpicHandler(TaskManager manager, Gson gson) {
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
        if (pathParts.length == 2 && pathParts[1].equals("epics")) {
            List<Epic> epics = manager.getEpic();
            String resp = gson.toJson(epics);
            sendText(e, resp);
        } else if (pathParts.length == 3 && pathParts[1].equals("epics")) {
            int id = getIdFromPath(pathParts);
            Optional<Epic> epicOptional = manager.getEpicId(id);
            if (epicOptional.isPresent()) {
                String response = gson.toJson(epicOptional.get());
                sendText(e, response);
            } else {
                sendNotFound(e, "Задача не найдена");
            }
        } else if (pathParts.length == 4 && pathParts[1].equals("epics") && pathParts[3].equals("subtasks")) {
            int epicId = Integer.parseInt(pathParts[2]);
            List<Subtask> subtasks = manager.getEpicSubtasks(epicId);
            String response = gson.toJson(subtasks);
            sendText(e, response);
        } else {
            sendNotFound(e, "Недопустимый запрос");
        }
    }

    private void handlePostRequest(HttpExchange e) throws IOException {
        String json = readRequestBody(e);
        Epic epic = gson.fromJson(json, Epic.class);
        try {
            if (epic.getTaskId() == 0) {
                manager.addEpic(epic);
                sendCreated(e, "Задача добавлена");
            } else {
                manager.updateEpic(epic);
                sendCreated(e, "Задача обнавлена");
            }
        } catch (ManagerSaveException ex) {
            sendHasInteractions(e, ex.getMessage());
        }
    }

    private void handleDeleteRequest(HttpExchange e, String[] pathParts) throws IOException {
        if (pathParts.length == 3 && pathParts[1].equals("epics")) {
            int id = getIdFromPath(pathParts);
            manager.deleteEpicId(id);
            sendText(e, "Задача удалена");
        } else {
            sendNotFound(e, "Недопустимый запрос");
        }
    }
}