package manager;

import task.Subtask;
import task.Task;
import  task.Epic;
import java.util.List;

public interface TaskManager {


    List<Task> getTasks();

    List<Task> getEpic();

    List<Task> getSubtask();

    List<Subtask> getEpicSubtasks(int epicId);

    void clearTask();

    void clearEpic();

    void clearSubtask();

    Task getTaskId(Integer id);

    Epic getEpicId(Integer id);

    Subtask getSubtaskId(Integer id);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask);

    int addTask(Task task);

    int addSubtask(Subtask subtask);

    int addEpic(Epic epic);

    void deleteTaskId(int id);

    void deleteSubtaskId(int id);

    void deleteEpicId(int id);

    List<Task> getHistory();
}
