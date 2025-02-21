package manager;
import java.util.ArrayList;
import task.Task;

public interface HistoryManager {

    void add(Task task);

    ArrayList<Task> getHistory();
}
