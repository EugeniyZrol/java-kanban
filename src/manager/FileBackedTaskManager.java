package manager;

import exception.ManagerSaveException;
import task.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {

    protected Path path;
    private static final String CAP = "id,type,name,status,description,epic";

    FileBackedTaskManager(Path path) {
        this.path = path;
    }

    public void save() {
        try (FileWriter fileRecord = new FileWriter(path.toString())) {
            fileRecord.write(CAP + "\n");
            for (Integer key : tasks.keySet()) {
                fileRecord.write(tasks.get(key).toString() + "\n");
            }
            for (Integer key : epics.keySet()) {
                fileRecord.write(epics.get(key).toString() + "\n");
            }
            for (Integer key : subtasks.keySet()) {
                fileRecord.write(subtasks.get(key).toString() + "\n");
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Произошла ошибка записи в файл");
        }
    }

    private static Task fromString(String value) throws IllegalArgumentException {
        String[] split = value.split(",", 6);
        Status status = switch (split[3]) {
            case "NEW" -> Status.NEW;
            case "IN_PROGRESS" -> Status.IN_PROGRESS;
            case "DONE" -> Status.DONE;
            default -> throw new IllegalArgumentException("Ошибка статуса задачи в файле данных!");
        };

        Task task;
        int taskId = Integer.parseInt(split[0]);

        switch (split[1]) {
            case "TASK":
                task = new Task(split[2], split[4], status);
                task.setTaskId(taskId);
                break;

            case "EPIC":
                task = new Epic(split[2], split[4], status);
                task.setTaskId(taskId);
                break;

            case "SUBTASK":
                int epicId = Integer.parseInt(split[5].trim());
                Epic epic = new Epic("", "", Status.NEW); // пустышка для передачи EpicId
                epic.setTaskId(epicId);
                task = new Subtask(split[2], split[4], status, epicId);
                task.setTaskId(taskId);
                break;
            default:
                throw new IllegalArgumentException("Ошибка типа задачи в файле данных!");
        }
        return task;
    }

    static FileBackedTaskManager loadFromFile(Path path) {
        FileBackedTaskManager manager = new FileBackedTaskManager(path);
        try (BufferedReader br = new BufferedReader(new FileReader(path.toString(), StandardCharsets.UTF_8))) {
            br.readLine();
            while (br.ready()) {
                String line = br.readLine();
                if (line == null) {
                    break;
                }
                Task task = fromString(line);
                switch (getType(task)) {
                    case "TASK" -> manager.addTask(task);
                    case "EPIC" -> manager.addEpic((Epic) task);
                    case "SUBTASK" -> manager.addSubtask((Subtask) task);
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка чтения данных.");
        }
        return manager;
    }


    private static String getType(Task task) {
        if (task instanceof Epic) {
            return "EPIC";
        } else if (task instanceof Subtask) {
            return "SUBTASK";
        } else {
            return "TASK";
        }
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
    public int addTask(Task task) {
        super.addTask(task);
        save();
        return task.getTaskId();
    }

    @Override
    public int addSubtask(Subtask subtask) {
        super.addSubtask(subtask);
        save();
        return subtask.getTaskId();
    }

    @Override
    public int addEpic(Epic epic) {
        super.addEpic(epic);
        save();
        return epic.getTaskId();
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

        Task firstTask = new Task("Первая задача", "Описание первой задачи", Status.NEW);
        Task secondTask = new Task("Вторая задача", "Описание второй задачи", Status.NEW);
        fileManager.addTask(firstTask);
        fileManager.addTask(secondTask);

        Epic firstEpic = new Epic("Первый эпик",
                "Описание первого эпика", Status.NEW);
        fileManager.addEpic(firstEpic);

        Subtask firstSubtask = new Subtask("Первая подзадача первого эпика",
                "Описание первой подзадачи", Status.NEW, firstEpic.getTaskId());
        fileManager.addSubtask(firstSubtask);

        Subtask secondSubtask = new Subtask("Вторая подзадача первого эпика",
                "Описание второй подзадачи", Status.IN_PROGRESS, firstEpic.getTaskId());
        fileManager.addSubtask(secondSubtask);

        Epic secondEpic = new Epic("Второй эпик",
                "Описание второго эпика", Status.NEW);
        fileManager.addEpic(secondEpic);

        Subtask thirdSubtask = new Subtask("Подзадача второго эпика",
                "Описание подзадачи второго эпика", Status.IN_PROGRESS, secondEpic.getTaskId());
        fileManager.addSubtask(thirdSubtask);

        taskManager = loadFromFile(file.toPath());
        printAllTasks(fileManager);
        printAllTasks(taskManager);
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
