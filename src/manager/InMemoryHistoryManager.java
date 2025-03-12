package manager;

import task.Node;
import task.Task;
import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node<Task>> historyMap;
    private Node<Task> head;
    private Node<Task> tail;

    public InMemoryHistoryManager() {
        historyMap = new HashMap<>();
    }

    @Override
    public Task add(Task task) {
        if (task != null) {
            linkLast(task);
        }
        return task;
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    @Override
    public void remove(Integer id) {
        removeNode(historyMap.get(id));
        historyMap.remove(id);
    }

    public void linkLast(Task task) {
        if (historyMap.containsKey(task.getTaskId())) {
            removeNode(historyMap.get(task.getTaskId()));
        }
        final Node<Task> oldTail = tail;
        final Node<Task> newNode = new Node<>(tail, task, null);
        tail = newNode;
        historyMap.put(task.getTaskId(), newNode);
        if (oldTail == null) {
            head = newNode;
        } else {
            oldTail.setNext(newNode);
        }
    }

    public List<Task> getTasks() {
        List<Task> historyTasks = new ArrayList<>();
        Node<Task> curNode = head;

        while (curNode != null) {
            historyTasks.add(curNode.getData());
            curNode = curNode.getNext();
        }
        return historyTasks;
    }

    public void removeNode(Node<Task> task) {
        Node<Task> prev;
        Node<Task> next;
        if (task != null) {
            if (head != tail) {
                if (task == head) {
                    next = task.getNext();
                    next.setPrev(null);
                    head = next;
                } else if (task == tail) {
                    prev = task.getPrev();
                    prev.setNext(null);
                    tail = prev;
                } else {
                    next = task.getNext();
                    prev = task.getPrev();
                    next.setPrev(prev);
                    prev.setNext(next);
                }
            } else {
                head = null;
                tail = null;
            }
        }
    }
}

