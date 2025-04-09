package manager;

import exception.ManagerSaveException;
import task.*;
import util.Managers;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    protected final Map<Integer, Task> tasks;
    protected final Map<Integer, Epic> epics;
    protected final Map<Integer, Subtask> subtasks;
    protected int current;
    private final HistoryManager historyManager;
    private final TreeSet<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime,
            Comparator.nullsLast(Comparator.naturalOrder())));

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
        Epic epic = epics.get(epicId);
        return epic.getSubtaskId().stream()
                .map(subtasks::get)
                .collect(Collectors.toList());
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
    public void getEpicId(Integer id) {
        historyManager.add(epics.get(id));
        epics.get(id);
    }


    public void getSubtaskId(Integer id) {
        historyManager.add(subtasks.get(id));
        subtasks.get(id);
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
        Epic epic = epics.get(subtask.getEpicId());
        epic.setDuration(getDuration(epic));
        epic.setStartTime(getStartTime(epic));
        getEndTime(epic);
        updateStatusOfEpic(epic);
    }

    @Override
    public void addTask(Task task) {
        try {
            if (isOverlap(task)) {
                throw new ManagerSaveException("Задача пересекается с другой по времени");
            }
            task.setTaskId(currentId());
            tasks.put(task.getTaskId(), task);
            prioritizedTasks.add(task);
            historyManager.add(task);
        } catch (Exception e) {
            System.out.println("Ошибка при добавлении задачи: " + e.getMessage());
        }
    }

    @Override
    public void addSubtask(Subtask subtask) {
        try {
            if (isOverlap(subtask)) {
                throw new ManagerSaveException("Подзадача пересекается с другой задачей по времени");
            }
            int epicId = subtask.getEpicId();
            Epic epic = epics.get(epicId);
            if (epic != null) {
                subtask.setTaskId(currentId());
                epic.setSubtaskId(subtask.getTaskId());
                subtasks.put(subtask.getTaskId(), subtask);
                epic.setDuration(getDuration(epic));
                epic.setStartTime(getStartTime(epic));
                getEndTime(epic);
                prioritizedTasks.add(subtask);
                updateStatusOfEpic(epics.get(epicId));
            }
        } catch (Exception e) {
            System.out.println("Ошибка при добавлении задачи: " + e.getMessage());
        }
    }

    @Override
    public void addEpic(Epic epic) {
        try {
            if (isOverlap(epic)) {
                throw new ManagerSaveException("Эпик пересекается с другой задачей или подзадачей по времени");
            }
            epic.setTaskId(currentId());
            epics.put(epic.getTaskId(), epic);
        } catch (Exception e) {
            System.out.println("Ошибка при добавлении эпика: " + e.getMessage());
        }
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
            Epic epic = epics.get(epicId);
            epic.removeSubtaskId(id);
            subtasks.remove(id);
            updateStatusOfEpic(epic);
            epic.setDuration(getDuration(epic));
            epic.setStartTime(getStartTime(epic));
            getEndTime(epic);
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

        if (epic.getSubtaskId().isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }

        long newCount = epic.getSubtaskId().stream()
                .filter(subtaskId -> subtasks.get(subtaskId).getStatus() == Status.NEW)
                .count();

        long doneCount = epic.getSubtaskId().stream()
                .filter(subtaskId -> subtasks.get(subtaskId).getStatus() == Status.DONE)
                .count();

        if (newCount == epic.getSubtaskId().size()) {
            epic.setStatus(Status.NEW);
        } else if (doneCount == epic.getSubtaskId().size()) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

    @Override
    public LocalDateTime getStartTime(Epic epic) {
        LocalDateTime earliestStart = null;
        for (Integer subtaskId : epic.getSubtaskId()) {
            Subtask subtask = subtasks.get(subtaskId);
            if (subtask != null && subtask.getStartTime() != null) {
                if (earliestStart == null || subtask.getStartTime().isBefore(earliestStart)) {
                    earliestStart = subtask.getStartTime();
                }
            }
        }
        return earliestStart;
    }

    @Override
    public void getEndTime(Epic epic) {
        LocalDateTime lastEnd = null;
        for (Integer subtaskId : epic.getSubtaskId()) {
            Subtask subtask = subtasks.get(subtaskId);
            if (subtask != null) {
                LocalDateTime subtaskEnd = subtask.getEndTime();
                if (subtaskEnd != null && (lastEnd == null || subtaskEnd.isAfter(lastEnd))) {
                    lastEnd = subtaskEnd;
                }
            }
        }
        epic.setEndTime(lastEnd);
    }

    @Override
    public Duration getDuration(Epic epic) {
        Duration totalDuration = Duration.ZERO;
        for (Integer subtaskId : epic.getSubtaskId()) {
            Subtask subtask = subtasks.get(subtaskId);
            if (subtask != null && subtask.getDuration() != null) {
                totalDuration = totalDuration.plus(subtask.getDuration());
            }
        }
        return totalDuration;
    }

    public boolean isOverlap(Task task) {
        return tasks.values().stream().anyMatch(existingTask -> overlappingTask(task, existingTask)) ||
                epics.values().stream().anyMatch(existingEpic -> overlappingTask(task, existingEpic)) ||
                subtasks.values().stream().anyMatch(existingSubtask -> overlappingTask(task, existingSubtask));
    }

    @Override
    public boolean overlappingTask(Task task1, Task task2) {
        if (task1.getStartTime() == null || task2.getStartTime() == null) {
            return false;
        }
        LocalDateTime task1Start = task1.getStartTime();
        LocalDateTime task2Start = task2.getStartTime();
        LocalDateTime task1End;
        LocalDateTime task2End;

        if (task1.getEndTime() != null) {
            task1End = task1.getEndTime();
        } else {
            task1End = task1Start.plus(task1.getDuration());
        }

        if (task2.getEndTime() != null) {
            task2End = task2.getEndTime();
        } else {
            task2End = task2Start.plus(task2.getDuration());
        }
        return (task1Start.isBefore(task2End) && task1End.isAfter(task2Start));
    }

    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }
}

