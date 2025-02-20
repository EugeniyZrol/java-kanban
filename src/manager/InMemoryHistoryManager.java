package manager;

import task.Status;
import task.Task;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager{
    private static ArrayList<Task> taskHistory;
    private static final int MAX_NUMBER_OF_TASK = 10;

    public InMemoryHistoryManager(){
        taskHistory = new ArrayList<>();
    }

    @Override
    public void addTaskList(Task task) {
        String name = task.getName();
        String description = task.getDescription();
        Status status = task.getStatus();
        int taskId = task.getTaskId();
        Task t = new Task(name, description, status);
        t.setTaskId(taskId);

        if(taskHistory.size()<MAX_NUMBER_OF_TASK){
            taskHistory.add(t);

        } else {
            taskHistory.removeFirst();
            taskHistory.add(t);
        }
    }

    @Override
    public ArrayList<Task> getHistory() {
        return taskHistory;
    }
}
