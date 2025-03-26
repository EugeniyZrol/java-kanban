package manager;

import task.*;
import util.Managers;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    protected final Map<Integer, Task> tasks;
    protected final Map<Integer, Epic> epics;
    protected final Map<Integer, Subtask> subtasks;
    protected int current;
    private final HistoryManager historyManager;

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

    public int getCurrent() {
        return current;
    }

    protected void setCurrent(int current) {
        this.current = current;
    }

    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Task> getEpic() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Task> getSubtask() {
        return new ArrayList<>(subtasks.values());

    }

    @Override
    public List<Subtask> getEpicSubtasks(int epicId) {
        List<Subtask> subtaskList = new ArrayList<>();
        Epic epic = epics.get(epicId);
        for (Integer idSubtask : epic.getSubtaskId()) {
            subtaskList.add(subtasks.get(idSubtask));
        }
        return subtaskList;
    }

    @Override
    public void clearTask() {
        for (Task task : tasks.values()) {
            historyManager.remove(task.getTaskId());
        }
        tasks.clear();
    }

    @Override
    public void clearEpic() {
        for (Epic epic : epics.values()) {
            historyManager.remove(epic.getTaskId());
        }
        for (Subtask subtask : subtasks.values()) {
            historyManager.remove(subtask.getTaskId());
        }
        subtasks.clear();
        epics.clear();
    }

    @Override
    public void clearSubtask() {
        for (Subtask subtask : subtasks.values()) {
            historyManager.remove(subtask.getTaskId());
        }
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


    public Subtask getSubtaskId(Integer id) {
        historyManager.add(subtasks.get(id));
        return subtasks.get(id);
    }


    @Override
    public void updateTask(Task task) {
        tasks.put(task.getTaskId(), task);
    }

    @Override
    public void updateEpic(Epic epic) {
        List<Integer> subtaskIds = epics.get(epic.getTaskId()).getSubtaskId();
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
            updateStatusOfEpic(epics.get(epicId));
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
        historyManager.remove(id);
    }

    @Override
    public void deleteSubtaskId(int id) {
        if (subtasks.get(id) != null) {
            int epicId = subtasks.get(id).getEpicId();
            epics.get(epicId).removeSubtaskId(id);
            subtasks.remove(id);
            updateStatusOfEpic(epics.get(epicId));
        }
        historyManager.remove(id);
    }

    @Override
    public void deleteEpicId(int id) {
        if (epics.containsKey(id)) {

            for (Integer idSubtask : epics.get(id).getSubtaskId()) {
                historyManager.remove(idSubtask);
                subtasks.remove(idSubtask);
            }
            epics.get(id).cleanSubtaskId();
            epics.remove(id);
            historyManager.remove(id);
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }


    public void updateStatusOfEpic(Epic epic) {
        boolean isStatusNew = true;
        boolean isStatusDone = true;

        if (!epic.getSubtaskId().isEmpty()) {

            for (Integer epicSubtask : epic.getSubtaskId()) {
                Status status = subtasks.get(epicSubtask).getStatus();

                if (!status.equals(Status.IN_PROGRESS)) {
                    epic.setStatus(Status.IN_PROGRESS);
                }
                if (!status.equals(Status.NEW)) {
                    isStatusNew = false;
                }
                if (!status.equals(Status.DONE)) {
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

