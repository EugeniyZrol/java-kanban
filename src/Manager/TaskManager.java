package Manager;

import Task.*;
import java.util.HashMap;

public class TaskManager {
    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, Epic> epics;
    private final HashMap<Integer, Subtask> subtasks;
    private int current;

    public TaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
        current = 0;
    }

    private int CurrentId() {
        return ++current;
    }

    // пункт 2.а тз
    public void printAllTasks() {
        for (Task task : tasks.values()) {
            System.out.println(task);
        }
        for (Epic epic : epics.values()) {
            System.out.println(epic);
            getEpicSubtasks(epic);
        }
        System.out.println();
    }

    // пункт 3.а тз
    public void getEpicSubtasks(Epic epic) {
        for (Subtask subtask : epic.getSubtasks()) {
            System.out.println(subtask);
        }
    }

    // пункт 2.б тз
    public void clearAll() {
        current = 0;
        tasks.clear();
        subtasks.clear();
        epics.clear();
    }

    // пункт 2.ц тз
    public Task getTask(Integer id) {
        if (tasks.containsKey(id)) {
            return tasks.get(id);
        } else if (epics.containsKey(id)) {
            return epics.get(id);
        } else if (subtasks.containsKey(id)) {
            return subtasks.get(id);
        }
        return null;
    }

    public void updateTask(Task task) {
        if (task.getClass() == Task.class) {
            tasks.put(task.getTaskId(), task);
        } else if (task.getClass() == Subtask.class) {
            subtasks.put(task.getTaskId(), (Subtask) task);
            if (!(task.getTaskId() == 0)) {
                ((Subtask) task).getEpic().deleteSubtask((Subtask) task);
            }
            ((Subtask) task).getEpic().addSubtaskEpic((Subtask) task);
        } else if (task.getClass() == Epic.class) {
            epics.put(task.getTaskId(), (Epic) task);
        }
    }

    // пункты 2.д и 2.е
    public void addTask(Task task, Integer id) {
        boolean newTask = false;
        if (id == 0) {
            id = CurrentId();
            newTask = true;
        }
        task.setTaskId(id);

        if (task.getClass() == Task.class) {
            tasks.put(id, task);
        } else if (task.getClass() == Subtask.class) {
            subtasks.put(id, (Subtask) task);
            if (!newTask) {
                ((Subtask) task).getEpic().deleteSubtask((Subtask) task);
            }
            ((Subtask) task).getEpic().addSubtaskEpic((Subtask) task);
        } else if (task.getClass() == Epic.class) {
            epics.put(id, (Epic) task);
        }
    }

    // пункт 2.ф
    public void deleteTask(Integer id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
        } else if (subtasks.containsKey(id)) {
            Epic epic = subtasks.get(id).getEpic();
            epic.deleteSubtask(subtasks.get(id));
            subtasks.remove(id);
        } else if (epics.containsKey(id)) {
            for (Subtask subtask : epics.get(id).getSubtasks()) {
                subtasks.remove(subtask);
            }
            epics.remove(id);
        }
    }
}


