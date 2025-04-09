package converter;

import task.*;
import java.time.Duration;
import java.time.LocalDateTime;

public class TaskConverter {

    public static String taskToString(Task task) {
        String toString;
        toString = task.getTaskId() + "," + task.getType() + "," + task.getName() + "," + task.getStatus() + ","
                + task.getDescription() + "," + task.getDuration() + "," + task.getStartTime() + ",";
        if (task instanceof Subtask) {
            toString = toString + task.getEpicId();
        }
        return toString;
    }

    public static Task fromString(String value) throws IllegalArgumentException {
        Task task;
        String[] split = value.split(",", 8);
        String statusValue = split[3];
        int taskId = Integer.parseInt(split[0]);
        String name = split[2];
        String description = split[4];
        String typeTask = split[1];
        Duration duration = Duration.parse(split[5]);
        LocalDateTime startTime = LocalDateTime.parse(split[6]);

        Status status = switch (Status.valueOf(statusValue)) {
            case NEW -> Status.NEW;
            case IN_PROGRESS -> Status.IN_PROGRESS;
            case DONE -> Status.DONE;
        };

        switch (TypeTask.valueOf(typeTask)) {
            case TASK:
                task = new Task(name, description, status, duration, startTime);
                task.setTaskId(taskId);
                break;
            case EPIC:
                task = new Epic(name, description, status, duration, startTime);
                task.setTaskId(taskId);
                break;
            case SUBTASK:
                int epicId = Integer.parseInt(split[7].trim());
                task = new Subtask(name, description, status, duration, startTime, epicId);
                task.setTaskId(taskId);
                break;
            default:
                throw new IllegalArgumentException("Ошибка типа задачи в файле данных!");
        }
        return task;
    }
}
