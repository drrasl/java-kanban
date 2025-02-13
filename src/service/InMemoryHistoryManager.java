package service;

import model.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

    private final Map<Integer, Node<Task>> historyMap;
    private Node<Task> tail;
    private Node<Task> head;

    public InMemoryHistoryManager() {
        historyMap = new HashMap<>();
    }

    @Override
    public void setHistory (Task task) {
        if (task != null) {
            if (historyMap.containsKey(task.getId())) {
                removeNode(historyMap.get(task.getId()));
                historyMap.remove(task.getId());
            }
            linkLast(task.getSnapshot());
        }
    }

    public void linkLast(Task task) {
            Node<Task> oldTail = tail;
            Node<Task> newNode = new Node<>(oldTail, task, null);
            tail = newNode;
            if (oldTail == null) {
                head = newNode;
            } else {
                oldTail.next = newNode;
            }
            historyMap.put(task.getId(), tail);
    }

    @Override
    public ArrayList<Task> getTasks() {
        List<Task> history = new ArrayList<>();
        Node<Task> node = head;
        while (node != null) {
            history.add(node.getData());
            node = node.next;
        }
        return new ArrayList<>(history);
    }

    @Override
    public void removeHistory (int id) {
        if (historyMap.containsKey(id)) {
            removeNode(historyMap.get(id));
            historyMap.remove(id);
        }
    }

    public int removeNode (Node<Task> nodeToRemove) {
        if (nodeToRemove == null) {
            return -1;
        }
        Node<Task> nextToNodeToRemove = null;
        Node<Task> prevToNodeToRemove = null;

        if (nodeToRemove.next == null && nodeToRemove.prev == null) {
            head = null;
            tail = null;
            return nodeToRemove.getData().getId();
        } else {

            if (nodeToRemove.next != null) {
                nextToNodeToRemove = nodeToRemove.next;
            } else {
                tail = nodeToRemove;
            }
            if (nodeToRemove.prev != null) {
                prevToNodeToRemove = nodeToRemove.prev;
            } else {
                head = nodeToRemove;
            }
            if (prevToNodeToRemove != null) {
                prevToNodeToRemove.next = nextToNodeToRemove;
            } else {
                head = nextToNodeToRemove;
                nextToNodeToRemove.prev = null;
            }
            if (nextToNodeToRemove != null) {
                nextToNodeToRemove.prev = prevToNodeToRemove;
            } else {
                tail = prevToNodeToRemove;
                prevToNodeToRemove.next = null;
            }
            return nodeToRemove.getData().getId();
        }
    }

    public Node<Task> getTail() {
        return tail;
    }

    public Node<Task> getHead() {
        return head;
    }

    public Map<Integer, Node<Task>> getHistoryMap() {
        return historyMap;
    }
}
