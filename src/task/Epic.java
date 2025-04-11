package task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private List<Integer> subtasksId;
    LocalDateTime endTime;

    public Epic(String name, String description, Status status) {
        super(name, description, status);
        subtasksId = new ArrayList<>();
    }

    public Epic(String name, String description, Status status, Duration duration, LocalDateTime startTime) {
        super(name, description, status, duration, startTime);
        subtasksId = new ArrayList<>();
        if (duration != null && startTime != null) {
            this.endTime = startTime.plus(duration);
        } else {
            this.endTime = null;
        }
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public List<Integer> getSubtaskId() {
        return subtasksId;
    }

    public void setSubtaskId(List<Integer> subtaskId) {
        this.subtasksId = subtaskId;
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
    public TypeTask getType() {
        return TypeTask.EPIC;
    }

    @Override
    public String toString() {
        return getTaskId() + "," + TypeTask.EPIC + "," + getName() + "," + getStatus() + "," + getDescription()
                + "," + getDuration() + "," + getStartTime();
    }
}

