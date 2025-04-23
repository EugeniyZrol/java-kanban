package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;
import task.Task;

import java.io.IOException;
import java.util.List;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager manager;
    private final Gson gson;

    public HistoryHandler(TaskManager manager, Gson gson) {
        this.manager = manager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange e) throws IOException {
        try {
            if ("GET".equals(e.getRequestMethod())) {
                handleGetRequest(e);
            } else {
                sendNotFound(e, "Этот метод нельзя вызывать");
            }
        } catch (Exception exchange) {
            sendNotFound(e, "Произошла ошибка: " + exchange.getMessage());
        }
    }

    private void handleGetRequest(HttpExchange e) throws IOException {
        List<Task> history = manager.getHistory();
        String resp = gson.toJson(history);
        sendText(e, resp);
    }
}