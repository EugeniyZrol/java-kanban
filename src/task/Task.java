package task;

import java.util.Objects;

public class Task {
    private String name;
    private int taskId;
    private final String description;
    private Status status;
    protected TypeTask typeTask;

    public Task(String name, String description, Status status) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.typeTask = TypeTask.TASK;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public int getTaskId() {
        return taskId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public TypeTask getType() {
        return typeTask;
    }

    public Integer getEpicId() {
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return taskId == task.taskId && Objects.equals(name, task.name) && Objects.equals(description, task.description) && status == task.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, taskId, description, status);
    }

    @Override
    public String toString() {
        return taskId + "," + TypeTask.TASK + "," + name + "," + status + "," + description;
    }
}
