package Task;
import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subtasksIds;

    public Epic(String name, String description, Status status) {
        super(name, description, status);
        this.subtasksIds = new ArrayList<>();
    }

    public ArrayList<Integer> getSubtaskIds() {
        return subtasksIds;
    }

    public void setSubtaskIds(ArrayList<Integer> subtaskIds) {
        this.subtasksIds = subtaskIds;
    }

    public void setSubtaskIds(Integer id) {
        subtasksIds.add(id);
    }

    public void cleanSubtaskIds() {
        subtasksIds.clear();
    }

    public void removeSubtaskIds(Integer id) {
        subtasksIds.remove(id);
    }

    @Override
    public String toString() {
        ArrayList<Integer> subtaskds = new ArrayList<>();
        for (Integer subtask : subtasksIds) {
            subtaskds.add(subtask);
        }
        return " Название " + super.getName().toUpperCase() +
                ", описание: " + super.getDescription().toUpperCase() +
                " (" + super.getStatus() +
                ", id=" + super.getTaskId() +
                ", subIds=" + subtaskds +
                ")";
    }
}

