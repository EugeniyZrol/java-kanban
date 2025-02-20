package manager;
import java.util.ArrayList;
import task.Task;

public interface HistoryManager {

    void addTaskList(Task task);

    ArrayList<Task> getHistory();
}
