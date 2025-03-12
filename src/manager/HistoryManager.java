package manager;
import java.util.List;
import task.Task;

public interface HistoryManager {

    Task add(Task task);

    void remove(Integer id);

    List<Task> getHistory();
}
