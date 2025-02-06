package Manager;

import Task.*;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, Epic> epics;
    private static  HashMap<Integer, Subtask> subtasks;
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

    public ArrayList getTasks() {
        return new ArrayList<>(tasks.values());
    }

    public ArrayList getEpic() {
        return new ArrayList<>(epics.values());
    }

    public ArrayList getSubtask() {
        return new ArrayList(subtasks.values());

    }

    public ArrayList<Subtask> getEpicSubtasks(int epicId) {
        ArrayList<Subtask> subtaskList = new ArrayList<>();
        Epic epic = epics.get(epicId);
        for (Integer idSubtask : epic.getSubtaskId()) {
            subtaskList.add(subtasks.get(idSubtask));
        }
        return subtaskList;
    }

    public void clearTask() {
        tasks.clear();
    }

    public void clearEpic() {
        subtasks.clear();
        epics.clear();
    }

    public void clearSubtask() {
        subtasks.clear();
        for (Integer id : epics.keySet()) {
            for (int i = 0; i < epics.size(); i++) {
                epics.get(id).cleanSubtaskId();
                epics.get(id).setStatus(Status.NEW);
            }
        }
    }

    public Task getTaskId(Integer id) {
            return tasks.get(id);
    }

    public Epic getEpicId(Integer id) {
            return epics.get(id);
    }

    public static Subtask getSubtaskId(Integer id) {
            return subtasks.get(id);
    }

    public void updateTask(Task task) {
        tasks.put(task.getTaskId(), task);
    }

    public void updateEpic(Epic epic) {
        ArrayList<Integer> subtaskIds = epics.get(epic.getTaskId()).getSubtaskId();
        epic.setSubtaskId(subtaskIds);
        epics.put(epic.getTaskId(), epic);
        updateStatusOfEpic(epic);
    }

    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getTaskId(), subtask);
        updateStatusOfEpic(epics.get(subtask.getEpicId()));
    }

    public void addTask(Task task) { // Создание нового объекта Task
        task.setTaskId(currentId());
        tasks.put(task.getTaskId(), task);
    }

    public void addSubtask(Subtask subtask) { // Создание нового объекта Subtask
        int epicId = subtask.getEpicId();
        if (epics.get(epicId) != null){
        subtask.setTaskId(currentId());
        epics.get(epicId).setSubtaskId(subtask.getTaskId());
        subtasks.put(subtask.getTaskId(), subtask);
        }
    }

    public void addEpic(Epic epic) { // Создание нового объекта Epic
        epic.setTaskId(currentId());
        epics.put(epic.getTaskId(), epic);
    }

    public void deleteTaskId(int id) {
        tasks.remove(id);
    }

    public void deleteSubtaskId(int id) {
        if (subtasks.get(id) != null) {
            int epicId = subtasks.get(id).getEpicId();
            epics.get(epicId).removeSubtaskId(id);
            subtasks.remove(id);
            updateStatusOfEpic(epics.get(epicId));
        }
    }

    public void deleteEpicId(int id) {

        for (Integer idSubtask : epics.get(id).getSubtaskId()) {
            subtasks.remove(idSubtask);
        }
        epics.get(id).cleanSubtaskId();
        epics.remove(id);
    }

    public static void updateStatusOfEpic(Epic epic) {

        boolean isStatusNew = true;
        boolean isStatusDone = true;

        if (epic.getSubtaskId() != null) {

            for (Integer idSubtask : epic.getSubtaskId()) {
                Subtask subtask = getSubtaskId(idSubtask);
                if (!subtask.getStatus().equals(Status.IN_PROGRESS)) {
                    epic.setStatus(Status.IN_PROGRESS);
                }
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
        } else {
            epic.setStatus(Status.NEW);
        }
    }
}
