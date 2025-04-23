package server;

import adapters.DurationAdapter;
import adapters.LocalDateTimeAdapter;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import exception.ServerException;
import manager.TaskManager;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;
import util.Managers;
import com.google.gson.Gson;


public class HttpTaskServer {

    private static final int PORT = 8080;
    private final TaskManager manager;
    private final HttpServer httpServer;
    private final Gson gson;

    public HttpTaskServer(TaskManager manager) throws ServerException {
        this.manager = manager;
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
        try {
            this.httpServer = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
            createContexts();
        } catch (IOException exception) {
            throw new ServerException("Не удалось создать HTTP-сервер на порту " + PORT);
        }
    }

    public void start() {
        httpServer.start();
        System.out.println("Сервер запущен на порту " + PORT);
    }

    public void stop() {
        httpServer.stop(0);
        System.out.println("Сервер остановлен");
    }

    public Gson getGson() {
        return this.gson;
    }

    private void createContexts() {
        httpServer.createContext("/tasks", new TaskHandler(manager, gson));
        httpServer.createContext("/epics", new EpicHandler(manager, gson));
        httpServer.createContext("/subtasks", new SubtaskHandler(manager, gson));
        httpServer.createContext("/history", new HistoryHandler(manager, gson));
        httpServer.createContext("/prioritized", new PrioritizedHandler(manager, gson));
    }

    public static void main(String[] args) throws ServerException {
        HttpTaskServer server = new HttpTaskServer(Managers.getDefaultInMemory());
        server.start();
    }
}