package Task;
import java.util.ArrayList;

public class Epic extends Task {

    private final ArrayList<Subtask> subtasks;

    public Epic( String name, String description, Status status) {
        super(name, description, status);
        this.subtasks = new ArrayList<>();
    }
    public ArrayList<Subtask> getSubtasks(){
        return subtasks;
    }

    public void addSubtaskEpic(Subtask subtask) {
        subtasks.add(subtask);
        checkEpicStatus();
    }
    public void deleteSubtask(Subtask subtask) {
        subtasks.remove(subtask);
        checkEpicStatus();
    }
    public void checkEpicStatus(){
        Status epicStatus;
        Status firstSubtaskStatus;
        Status statusSubtask;
        epicStatus = getStatus();

        if (getSubtasks().isEmpty()) {
            epicStatus = Status.NEW;
        }
        else {
            firstSubtaskStatus = subtasks.getFirst().getStatus();
            for (Subtask subtask : getSubtasks()) {
                statusSubtask = subtask.getStatus();
                if (statusSubtask.equals(Status.IN_PROGRESS) || !statusSubtask.equals(firstSubtaskStatus)) {
                    epicStatus = Status.IN_PROGRESS;
                    break;
                }
                epicStatus = firstSubtaskStatus;
            }
        }
        setStatus(epicStatus);
    }


    @Override
    public String toString() {
        ArrayList<Integer> subtaskIds  = new ArrayList<>();
        for (Subtask subtask : subtasks) {
            subtaskIds.add(subtask.getTaskId());
        }
        return " Название " + super.getName().toUpperCase() +
                ", описание: " + super.getDescription().toUpperCase() +
                " (" + super.getStatus() +
                ", id=" + super.getTaskId() +
                ", subIds=" + subtaskIds +
                ")";

    }
}
