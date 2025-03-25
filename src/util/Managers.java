package util;

import manager.*;

import java.nio.file.Path;

public class Managers {
    public static Path path;

    public static TaskManager getDefault() {
        return new FileBackedTaskManager(path);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
