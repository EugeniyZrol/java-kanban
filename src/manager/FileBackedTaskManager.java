package manager;

import exception.ManagerSaveException;
import task.*;
import converter.TaskConverter;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {

    protected Path path;
    private static final String CAP = "id,type,name,status,description,duration,startTime,epic";

    public FileBackedTaskManager(Path path) {
        this.path = path;
    }

    public void save() {
        try (FileWriter fileRecord = new FileWriter(path.toString())) {
            fileRecord.write(CAP + "\n");

            for (Task value : tasks.values()) {
                fileRecord.write(TaskConverter.taskToString(value) + "\n");
            }
            for (Epic value : epics.values()) {
                fileRecord.write(TaskConverter.taskToString(value) + "\n");
            }
            for (Subtask value : subtasks.values()) {
                fileRecord.write(TaskConverter.taskToString(value) + "\n");
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Произошла ошибка записи в файл");
        }
    }

    public static FileBackedTaskManager loadFromFile(Path path) {
        FileBackedTaskManager manager = new FileBackedTaskManager(path);
        try (BufferedReader br = new BufferedReader(new FileReader(path.toString(), StandardCharsets.UTF_8))) {
            br.readLine();
            while (br.ready()) {
                String line = br.readLine();
                if (line == null) {
                    break;
                }
                Task task = TaskConverter.fromString(line);

                switch (task.getType()) {
                    case TASK -> manager.tasks.put(task.getTaskId(), task);
                    case EPIC -> manager.epics.put(task.getTaskId(), (Epic) task);
                    case SUBTASK -> manager.subtasks.put(task.getTaskId(), (Subtask) task);
                }
                if (manager.getCurrent() < task.getTaskId()) {
                    manager.setCurrent(task.getTaskId());
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка чтения данных.");
        }
        return manager;
    }

    @Override
    public void clearTask() {
        super.clearTask();
        save();
    }

    @Override
    public void clearEpic() {
        super.clearEpic();
        save();
    }

    @Override
    public void clearSubtask() {
        super.clearSubtask();
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void addSubtask(Subtask subtask) {
        super.addSubtask(subtask);
        save();
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    @Override
    public void deleteTaskId(int id) {
        super.deleteTaskId(id);
        save();
    }

    @Override
    public void deleteSubtaskId(int id) {
        super.deleteSubtaskId(id);
        save();
    }

    @Override
    public void deleteEpicId(int id) {
        super.deleteEpicId(id);
        save();
    }
// реализуем пользовательский сценарий

    public static void main(String[] args) {
        final File file = new File("testFile4");
        FileBackedTaskManager fileManager = new FileBackedTaskManager(file.toPath());
        InMemoryTaskManager taskManager;
        LocalDateTime now = LocalDateTime.now();

        Task firstTask = new Task("Первая задача", "Описание первой задачи", Status.NEW, Duration.ofMinutes(30), now);
        Task secondTask = new Task("Вторая задача", "Описание второй задачи", Status.NEW, Duration.ofMinutes(30), now.plusHours(1));
        fileManager.addTask(firstTask);
        fileManager.addTask(secondTask);

        Epic firstEpic = new Epic("Первый эпик",
                "Описание первого эпика", Status.NEW, Duration.ofMinutes(30), now.plusHours(2));
        fileManager.addEpic(firstEpic);

        Subtask firstSubtask = new Subtask("Первая подзадача первого эпика",
                "Описание первой подзадачи", Status.IN_PROGRESS, Duration.ofMinutes(30), now.plusHours(3), firstEpic.getTaskId());
        fileManager.addSubtask(firstSubtask);

        Subtask secondSubtask = new Subtask("Вторая подзадача первого эпика",
                "Описание второй подзадачи", Status.IN_PROGRESS, Duration.ofMinutes(30), now.plusHours(4), firstEpic.getTaskId());
        fileManager.addSubtask(secondSubtask);

        Epic secondEpic = new Epic("Второй эпик",
                "Описание второго эпика", Status.NEW, Duration.ofMinutes(30), now.plusHours(5));
        fileManager.addEpic(secondEpic);

        Subtask thirdSubtask = new Subtask("Подзадача второго эпика",
                "Описание подзадачи второго эпика", Status.DONE, Duration.ofMinutes(30), now.plusHours(6), secondEpic.getTaskId());
        fileManager.addSubtask(thirdSubtask);
        taskManager = loadFromFile(file.toPath());
        printAllTasks(fileManager);
        printAllTasks(taskManager);
        System.out.println(fileManager.getPrioritizedTasks());
    }

    private static void printAllTasks(InMemoryTaskManager manager) {
        System.out.println("Задачи:");
        for (Task task : manager.getTasks()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Task epic : manager.getEpic()) {
            System.out.println(epic);

            for (Task task : manager.getEpicSubtasks(epic.getTaskId())) {
                System.out.println("--> " + task);
            }
        }
        System.out.println("Подзадачи:");
        for (Task subtask : manager.getSubtask()) {
            System.out.println(subtask);
        }

        System.out.println("История:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }
}
