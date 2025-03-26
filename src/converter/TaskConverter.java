package converter;

import task.*;

public class TaskConverter {

    public static String taskToString(Task task) {
        String toString;
        toString = task.getTaskId() + "," + task.getType() + "," + task.getName() + "," + task.getStatus() + ","
                        + task.getDescription() + ",";
        if (task instanceof Subtask) {
            toString = toString + task.getEpicId();
        }
        return toString;
    }

    public static Task fromString(String value) throws IllegalArgumentException {
        Task task;
        String[] split = value.split(",", 6);
        String statusValue = split[3];
        int taskId = Integer.parseInt(split[0]);
        String name = split[2];
        String description = split[4];
        String typeTask = split[1];

        Status status = switch (Status.valueOf(statusValue)) {
            case NEW -> Status.NEW;
            case IN_PROGRESS -> Status.IN_PROGRESS;
            case DONE -> Status.DONE;
        };

        switch (TypeTask.valueOf(typeTask)) {
            case TASK:
                task = new Task(name, description, status);
                task.setTaskId(taskId);
                break;
            case EPIC:
                task = new Epic(name, description, status);
                task.setTaskId(taskId);
                break;
            case SUBTASK:
                int epicId = Integer.parseInt(split[5].trim());
                task = new Subtask(name, description, status, epicId);
                task.setTaskId(taskId);
                break;
            default:
                throw new IllegalArgumentException("Ошибка типа задачи в файле данных!");
        }
        return task;
    }
}
