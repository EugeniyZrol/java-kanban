package Task;

public class Subtask extends Task {

    private final Epic epic;

    public Subtask(String name, String description, Status status, Epic epic) {
        super(name, description, status);
        this.epic = epic;
    }

    public Epic getEpic(){
        return epic;
    }
    @Override
    public String toString() {

        return  "  - " + super.getName() +
                ": " + super.getDescription() +
                " (" + super.getStatus() +
                ", ID=" + super.getTaskId() +
                ", epicID=" + epic.getTaskId() +
                ")";
    }
}
