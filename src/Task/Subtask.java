package Task;

public class Subtask extends Task {

    private int epicId;

    public Subtask(String name, String description, Status status, Integer epicId) {
        super(name, description, status);
        this.epicId = epicId;
    }

    public Integer getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {

        return  "  - " + super.getName() +
                ": " + super.getDescription() +
                " (" + super.getStatus() +
                ", ID=" + super.getTaskId() +
                ", epicID=" + epicId +
                ")";
    }
}
