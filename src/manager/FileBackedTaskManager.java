package manager;

import exception.ManagerSaveException;
import task.*;
import converter.TaskConverter;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {

    protected Path path;
    private static final String CAP = "id,type,name,status,description,epic";
    private final TaskConverter taskConverter = new TaskConverter();

    public FileBackedTaskManager(Path path) {
        this.path = path;
    }

    public void save() {
        try (FileWriter fileRecord = new FileWriter(path.toString())) {
            fileRecord.write(CAP + "\n");
            for (Task value : tasks.values()) {
                fileRecord.write(taskConverter.taskToString(value) + "\n");
            }
            for (Epic value : epics.values()) {
                fileRecord.write(taskConverter.taskToString(value) + "\n");
            }
            for (Subtask value : subtasks.values()) {
                fileRecord.write(taskConverter.taskToString(value) + "\n");
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
