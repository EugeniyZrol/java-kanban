package task;

public class Subtask extends Task {

    private final int epicId;

    public Subtask(String name, String description, Status status, Integer epicId) {
        super(name, description, status);
        this.epicId = epicId;
    }

    public Integer getEpicId() {
        return epicId;
    }

    @Override
    public TypeTask getType() {
        return TypeTask.SUBTASK;
    }

    @Override
    public String toString() {
        return getTaskId() + "," + TypeTask.SUBTASK + "," + getName() + "," + getStatus() + "," + getDescription()
                + "," + getEpicId();
    }
}
