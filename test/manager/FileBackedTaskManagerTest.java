package manager;

import exception.ManagerSaveException;
import task.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.*;
import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {
    static Path tempFile;
    static FileBackedTaskManager taskManager;

    @BeforeAll
    static void createFileBackedTaskManager() {
        try {
            tempFile = Files.createTempFile(null, ".txt");
        } catch (java.io.IOException e) {
            System.out.println("ошибка в тесте");
        }
        taskManager = new FileBackedTaskManager(tempFile);
    }


    @Test
    void shouldCatchException() {
        Assertions.assertDoesNotThrow(() -> {
            new FileBackedTaskManager(Paths.get("C:\\txt.txt"));
        });
    }

    @Test
    void shouldCatchExceptionInConstruction() {
        Assertions.assertDoesNotThrow(() -> {
            new FileBackedTaskManager(Files.createTempFile(null, ".txt"));
        });
    }

    @Test
    void writeSomeTasks() {
        File file = new File("testFile4");
        FileBackedTaskManager manager = new FileBackedTaskManager(file.toPath());

        Task firstTask = new Task("Первая задача", "Описание первой задачи", Status.NEW);
        Task secondTask = new Task("Вторая задача", "Описание второй задачи", Status.NEW);
        manager.addTask(firstTask);
        manager.addTask(secondTask);

        Epic firstEpic = new Epic("Первый эпик",
                "Описание первого эпика", Status.NEW);
        manager.addEpic(firstEpic);

        Subtask firstSubtask = new Subtask("Первая подзадача первого эпика",
                "Описание первой подзадачи", Status.NEW, firstEpic.getTaskId());
        manager.addSubtask(firstSubtask);

        Subtask secondSubtask = new Subtask("Вторая подзадача первого эпика",
                "Описание второй подзадачи", Status.IN_PROGRESS, firstEpic.getTaskId());
        manager.addSubtask(secondSubtask);

        Epic secondEpic = new Epic("Второй эпик",
                "Описание второго эпика", Status.NEW);
        manager.addEpic(secondEpic);

        Subtask thirdSubtask = new Subtask("Подзадача второго эпика",
                "Описание подзадачи второго эпика", Status.IN_PROGRESS, secondEpic.getTaskId());
        manager.addSubtask(thirdSubtask);

        assertTrue(file.length() > 0);
    }

    @Test
    void writeOrReadAnEmptyFile() throws IOException {
        File file = File.createTempFile("testFile", ".txt");
        FileBackedTaskManager manager = new FileBackedTaskManager(file.toPath());
        assertTrue(manager.getTasks().isEmpty() && manager.getEpic().isEmpty() && manager.getSubtask().isEmpty());
        File file1 = File.createTempFile("testFile2", ".txt");
        FileBackedTaskManager newManager = FileBackedTaskManager.loadFromFile(file1.toPath());
        assertTrue(newManager.getTasks().isEmpty() && newManager.getSubtask().isEmpty() && newManager.getEpic().isEmpty());
        assertEquals(0, file.length());
    }

    @Test
    public void testExceptionHandling() {
        FileBackedTaskManager manager = new FileBackedTaskManager(Paths.get("testFile.txt"));

        assertDoesNotThrow(manager::save);

        // Попробуем загрузить файл, который не существует
        assertThrows(ManagerSaveException.class, () -> FileBackedTaskManager.loadFromFile(Paths.get("nonExistentFile.txt")));
    }
}
