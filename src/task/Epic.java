package task;
import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subtasksId;

    public Epic(String name, String description, Status status) {
        super(name, description, status);
        subtasksId = new ArrayList<>();
    }

    public ArrayList<Integer> getSubtaskId() {
        return subtasksId;
    }

    public void setSubtaskId(ArrayList<Integer> subtaskIds) {
        this.subtasksId = subtaskIds;
    }

    public void setSubtaskId(Integer id) {
        subtasksId.add(id);
    }

    public void cleanSubtaskId() {
        subtasksId.clear();
    }

    public void removeSubtaskId(Integer id) {
        subtasksId.remove(id);
    }

    @Override
    public String toString() {
        ArrayList<Integer> subtaskIds = new ArrayList<>(subtasksId);
        return " Название " + super.getName().toUpperCase() +
                ", описание: " + super.getDescription().toUpperCase() +
                " (" + super.getStatus() +
                ", id=" + super.getTaskId() +
                ", subIds=" + subtaskIds +
                ")";
    }
}

