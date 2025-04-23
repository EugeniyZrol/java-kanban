package manager;

import exception.ManagerSaveException;
import exception.NotFoundException;
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
    public List<Epic> getEpic() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getSubtask() {
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
    public Optional<Task> getTaskId(Integer id) {
        try {
            Task task = tasks.get(id);
            if (task != null) {
                historyManager.add(task);
                return Optional.of(task);
            } else {
                throw new NotFoundException("Задача с идентификатором " + id + " не найдена");
            }
        } catch (Exception e) {
            throw new RuntimeException("При извлечении задачи произошла ошибка: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Epic> getEpicId(Integer id) {
        try {
            Epic epic = epics.get(id);
            if (epic != null) {
                historyManager.add(epic);
                return Optional.of(epic);
            } else {
                throw new NotFoundException("Эпик с идентификатором " + id + " не найден");
            }
        } catch (Exception e) {
            throw new RuntimeException("При извлечении эпика произошла ошибка: " + e.getMessage(), e);
        }
    }


    @Override
    public Optional<Subtask> getSubtaskId(Integer id) {
        try {
            Subtask subtask = subtasks.get(id);
            if (subtask != null) {
                historyManager.add(subtask);
                return Optional.of(subtask);
            } else {
                throw new NotFoundException("Подзадача с идентификатором " + id + " не найдена");
            }
        } catch (Exception e) {
            throw new RuntimeException("При извлечении подзадачи произошла ошибка: " + e.getMessage(), e);
        }
    }


    @Override
    public void updateTask(Task task) {
        try {
            if (isOverlap(task)) {
                throw new ManagerSaveException("Задача пересекается с другой по времени");
            }
            tasks.put(task.getTaskId(), task);
        } catch (Exception e) {
            System.out.println("Ошибка при добавлении задачи: " + e.getMessage());
        }
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
        try {
            if (isOverlap(subtask)) {
                throw new ManagerSaveException("Подзадача пересекается с другой задачей по времени");
            }
            subtasks.put(subtask.getTaskId(), subtask);
            Epic epic = epics.get(subtask.getEpicId());
            updateEpicTime(epic);
            updateStatusOfEpic(epic);
        } catch (Exception e) {
            System.out.println("Ошибка при добавлении задачи: " + e.getMessage());
        }
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
                updateEpicTime(epic);
                prioritizedTasks.add(subtask);
                updateStatusOfEpic(epics.get(epicId));
            }
        } catch (Exception e) {
            System.out.println("Ошибка при добавлении задачи: " + e.getMessage());
        }
    }

    @Override
    public void addEpic(Epic epic) {
        epic.setTaskId(currentId());
        epics.put(epic.getTaskId(), epic);
    }

    @Override
    public void deleteTaskId(int id) {
        try {
            if (tasks.remove(id) == null) {
                throw new NotFoundException("Задача с идентификатором " + id + " не найдена");
            }
            historyManager.remove(id);
        } catch (Exception exception) {
            throw new RuntimeException("Ошибка при удалении задачи: " + exception.getMessage(), exception);
        }
    }

    @Override
    public void deleteSubtaskId(int id) {
        try {
            Subtask subtask = subtasks.get(id);
            if (subtask == null) {
                throw new NotFoundException("Подзадача с идентификатором " + id + " не найдена");
            }
            int epicId = subtask.getEpicId();
            Epic epic = epics.get(epicId);
            epic.removeSubtaskId(id);
            subtasks.remove(id);
            updateStatusOfEpic(epic);
            updateEpicTime(epic);

            historyManager.remove(id);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при удалении подзадачи: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteEpicId(int id) {
        try {
            if (!epics.containsKey(id)) {
                throw new NotFoundException("Эпик с идентификатором " + id + " не найден");
            }

            for (Integer idSubtask : epics.get(id).getSubtaskId()) {
                historyManager.remove(idSubtask);
                subtasks.remove(idSubtask);
            }
            epics.get(id).cleanSubtaskId();
            epics.remove(id);
            historyManager.remove(id);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при удалении эпика: " + e.getMessage(), e);
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
    public void updateEpicTime(Epic epic) {
        LocalDateTime earliestStart = null;
        LocalDateTime lastEnd = null;
        Duration totalDuration = Duration.ZERO;

        for (Integer subtaskId : epic.getSubtaskId()) {
            Subtask subtask = subtasks.get(subtaskId);
            if (subtask != null) {

                LocalDateTime subtaskStart = subtask.getStartTime();
                if (subtaskStart != null) {
                    if (earliestStart == null || subtaskStart.isBefore(earliestStart)) {
                        earliestStart = subtaskStart;
                    }
                }

                LocalDateTime subtaskEnd = subtask.getEndTime();
                if (subtaskEnd != null) {
                    if (lastEnd == null || subtaskEnd.isAfter(lastEnd)) {
                        lastEnd = subtaskEnd;
                    }
                }

                Duration subtaskDuration = subtask.getDuration();
                if (subtaskDuration != null) {
                    totalDuration = totalDuration.plus(subtaskDuration);
                }
            }
        }

        epic.setStartTime(earliestStart);
        epic.setEndTime(lastEnd);
        epic.setDuration(totalDuration);
    }

    public boolean isOverlap(Task task) {
        return prioritizedTasks.stream().anyMatch(existingTask -> overlappingTask(task, existingTask));
    }

    @Override
    public boolean overlappingTask(Task task1, Task task2) {
        if (task1.getStartTime() == null || task2.getStartTime() == null) {
            return false;
        }
        LocalDateTime task1Start = task1.getStartTime();
        LocalDateTime task2Start = task2.getStartTime();
        LocalDateTime task1End = task1.getEndTime();
        LocalDateTime task2End = task2.getEndTime();

        if (task1End == null || task2End == null) {
            return false;
        }
        return (task1Start.isBefore(task2End) && task1End.isAfter(task2Start));
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }
}