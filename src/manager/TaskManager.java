package manager;

import task.Subtask;
import task.Task;
import  task.Epic;

import java.util.ArrayList;

public interface TaskManager {


    ArrayList<Task> getTasks();

    ArrayList<Task> getEpic();

    ArrayList<Task> getSubtask();

    ArrayList<Subtask> getEpicSubtasks(int epicId);

    void clearTask();

    void clearEpic();

    void clearSubtask();

    Task getTaskId(Integer id);

    Epic getEpicId(Integer id);

    static Subtask getSubtaskId(Integer id) {
        return null;
    }

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask);

    int addTask(Task task);

    int addSubtask(Subtask subtask);

    int addEpic(Epic epic);

    void deleteTaskId(int id);

    void deleteSubtaskId(int id);

    void deleteEpicId(int id);

    ArrayList<Task> getHistory();
}
