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
    public void add(Task task) {
        if (task != null) {
            if (taskHistory.size() < MAX_NUMBER_OF_TASK) {
                taskHistory.add(task);
            } else {
                taskHistory.removeFirst();
                taskHistory.add(task);
            }
        }
    }

    @Override
    public ArrayList<Task> getHistory() {
        return new ArrayList<>(taskHistory);
    }
}
