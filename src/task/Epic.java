package task;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private List<Integer> subtasksId;

    public Epic(String name, String description, Status status) {
        super(name, description, status);
        subtasksId = new ArrayList<>();
    }

    public List<Integer> getSubtaskId() {
        return subtasksId;
    }

    public void setSubtaskId(List<Integer> subtaskIds) {
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
        return getTaskId() + "," + TypeTask.EPIC + "," + getName() + "," + getStatus() + "," + getDescription();
    }
}

