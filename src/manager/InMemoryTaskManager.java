package manager;


import task.*;
import util.Managers;

import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryTaskManager implements TaskManager {
    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, Epic> epics;
    private static HashMap<Integer, Subtask> subtasks;
    private int current;
    static HistoryManager historyManager;

    public InMemoryTaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
        current = 0;
        historyManager = Managers.getDefaultHistory();
    }

    private int currentId() {
        return ++current;
    }

    @Override
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Task> getEpic() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public ArrayList<Task> getSubtask() {
        return new ArrayList<>(subtasks.values());

    }

    @Override
    public ArrayList<Subtask> getEpicSubtasks(int epicId) {
        ArrayList<Subtask> subtaskList = new ArrayList<>();
        Epic epic = epics.get(epicId);
        for (Integer idSubtask : epic.getSubtaskId()) {
            subtaskList.add(subtasks.get(idSubtask));
        }
        return subtaskList;
    }

    @Override
    public void clearTask() {
        tasks.clear();
    }

    @Override
    public void clearEpic() {
        subtasks.clear();
        epics.clear();
    }

    @Override
    public void clearSubtask() {
        subtasks.clear();
        for (Integer id : epics.keySet()) {
            Epic epic = epics.get(id);
            epic.cleanSubtaskId();
            epic.setStatus(Status.NEW);
        }
    }

    @Override
    public Task getTaskId(Integer id) {
            historyManager.add(tasks.get(id));
            return tasks.get(id);
    }

    @Override
    public Epic getEpicId(Integer id) {
        historyManager.add(epics.get(id));
        return epics.get(id);
    }


    public static Subtask getSubtaskId(Integer id) {
        historyManager.add(subtasks.get(id));
        return subtasks.get(id);
    }

    @Override
    public void updateTask(Task task) {
        tasks.put(task.getTaskId(), task);
    }

    @Override
    public void updateEpic(Epic epic) {
        ArrayList<Integer> subtaskIds = epics.get(epic.getTaskId()).getSubtaskId();
        epic.setSubtaskId(subtaskIds);
        epics.put(epic.getTaskId(), epic);
        updateStatusOfEpic(epic);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getTaskId(), subtask);
        updateStatusOfEpic(epics.get(subtask.getEpicId()));
    }

    @Override
    public int addTask(Task task) {
        task.setTaskId(currentId());
        tasks.put(task.getTaskId(), task);
        return current;
    }

    @Override
    public int addSubtask(Subtask subtask) {
        int epicId = subtask.getEpicId();
        if (epics.get(epicId) != null) {
            subtask.setTaskId(currentId());
            epics.get(epicId).setSubtaskId(subtask.getTaskId());
            subtasks.put(subtask.getTaskId(), subtask);
        }
        return current;
    }

    @Override
    public int addEpic(Epic epic) {
        epic.setTaskId(currentId());
        epics.put(epic.getTaskId(), epic);
        return current;
    }

    @Override
    public void deleteTaskId(int id) {
        tasks.remove(id);
    }

    @Override
    public void deleteSubtaskId(int id) {
        if (subtasks.get(id) != null) {
            int epicId = subtasks.get(id).getEpicId();
            epics.get(epicId).removeSubtaskId(id);
            subtasks.remove(id);
            updateStatusOfEpic(epics.get(epicId));
        }
    }

    @Override
    public void deleteEpicId(int id) {

        for (Integer idSubtask : epics.get(id).getSubtaskId()) {
            subtasks.remove(idSubtask);
        }
        epics.get(id).cleanSubtaskId();
        epics.remove(id);
    }

    @Override
    public ArrayList<Task> getHistory(){
        return historyManager.getHistory();
    }


    public static void updateStatusOfEpic(Epic epic) {

        boolean isStatusNew = true;
        boolean isStatusDone = true;

        if (!epic.getSubtaskId().isEmpty()) {

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

