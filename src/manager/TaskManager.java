package manager;

import task.Subtask;
import task.Task;
import  task.Epic;

import java.time.Duration;
import java.time.LocalDateTime;
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

    void getEpicId(Integer id);

    void getSubtaskId(Integer id);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask);

    void addTask(Task task);

    void addSubtask(Subtask subtask);

    void addEpic(Epic epic);

    void deleteTaskId(int id);

    void deleteSubtaskId(int id);

    void deleteEpicId(int id);

    List<Task> getHistory();

    LocalDateTime getStartTime(Epic epic);

    void getEndTime(Epic epic);

    boolean overlappingTask(Task task1, Task task2);

    Duration getDuration(Epic epic);
}
