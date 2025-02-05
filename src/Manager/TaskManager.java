package Manager;

import Task.*;

import java.util.ArrayList;
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

    private int currentId() {
        return ++current;
    }

    // пункт 2.а тз
    public ArrayList getTasks() {
        return new ArrayList<>(tasks.values());
    }
    public ArrayList getEpic() {
        return new ArrayList<>(epics.values());
    }
    public ArrayList getSubtask() {
        return new ArrayList(subtasks.values());

    }
    public void printAllTasks() {
        System.out.println(getTasks());
        System.out.println(getEpic());
        System.out.println(getSubtask());

    }
    // пункт 3.а тз
    public ArrayList<Subtask> getEpicSubtasks(int epicId) {
        ArrayList<Subtask> subtaskList = new ArrayList<>();
        Epic epic = epics.get(epicId);
        for (Integer idSubtask : epic.getSubtaskIds()) {
            subtaskList.add(subtasks.get(idSubtask));
        }
        return subtaskList;
    }
    // пункт 2.б тз
    public void clearTask() {
        tasks.clear();
    }
    public void clearEpic() {
        for (Integer id : epics.keySet()) {
            for (int i = 0; i < epics.size(); i++) {
                epics.get(id).cleanSubtaskIds();
            }
        }
        subtasks.clear();
        epics.clear();
    }
    public void clearSubtask() {
        subtasks.clear();
        for (Integer id : epics.keySet()) {
            for (int i = 0; i < epics.size(); i++) {
                epics.get(id).cleanSubtaskIds();
                updateStatusOfEpic(epics.get(id));
            }
        }
    }

    // пункт 2.ц тз
    public Task getTaskId(Integer id) {
        if (tasks.containsKey(id)) {
            return tasks.get(id);
        }
        return null;
    }
    public Epic getEpicId(Integer id) {
        if (epics.containsKey(id)) {
            return epics.get(id);
        }
        return null;
    }
    public Subtask getSubtaskId(Integer id) {
        if (subtasks.containsKey(id)) {
            return subtasks.get(id);
        }
        return null;
    }

    // пункт 2.е ГОТОВО
    public void updateTask(Task task) {
        tasks.put(task.getTaskId(), task);
    }
    public void updateEpic(Epic epic) {

        ArrayList<Integer> subtaskIds = epics.get(epic.getTaskId()).getSubtaskIds();
        epic.setSubtaskIds(subtaskIds);
        epics.put(epic.getTaskId(), epic);
    }
    public void updateSubtask(Subtask subtask) {
            subtasks.put(subtask.getTaskId(), subtask);
            updateStatusOfEpic(epics.get(subtask.getEpicId()));
    }
    // пункт 2.д
    public void addTask(Task task) { // Создание нового объекта Task
        task.setTaskId(currentId());
        tasks.put(task.getTaskId(), task);
    }
    public void addSubtask(Subtask subtask) { // Создание нового объекта Subtask
        subtask.setTaskId(currentId());
        int epicId = subtask.getEpicId();
        epics.get(epicId).setSubtaskIds(subtask.getTaskId());
        subtasks.put(subtask.getTaskId(), subtask);
    }
    public void addEpic(Epic epic) { // Создание нового объекта Epic
        epic.setTaskId(currentId());
        epics.put(epic.getTaskId(), epic);
    }
    // пункт 2.ф
    public void deleteTaskId(int id) {
        tasks.remove(id);
    }
    public void deleteSubtaskId(int id) {
        int epicId = subtasks.get(id).getEpicId();
        epics.get(epicId).removeSubtaskIds(id);
        subtasks.remove(id);
        updateStatusOfEpic(epics.get(epicId));
    }
    public void deleteEpicId(int id) {

        for (Integer idSubtask : epics.get(id).getSubtaskIds()) {
            subtasks.remove(idSubtask);
        }
        epics.get(id).cleanSubtaskIds();
        epics.remove(id);
    }

    public void updateStatusOfEpic(Epic epic) {

        boolean isStatusNew = true;
        boolean isStatusDone = true;

        if (epic.getSubtaskIds() != null) {

            for (Integer idSubtask : epic.getSubtaskIds()) {
                Subtask subtask = getSubtaskId(idSubtask);
                if (!subtask.getStatus().equals(Status.NEW)) {
                    isStatusNew = false;
                }
                if (!subtask.getStatus().equals(Status.DONE)) {
                    isStatusDone = false;
                }
            }
            if (isStatusNew) {
                epic.setStatus(Status.NEW);
            } else if (isStatusDone) {
                epic.setStatus(Status.DONE);
            } else {
                epic.setStatus(Status.IN_PROGRESS);
            }
            System.out.println(epic.getStatus());
        } else {
            System.out.println("У объекта эпика нет подзадач");
        }
    }

}
