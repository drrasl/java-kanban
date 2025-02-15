package service;

import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {

    private final Map<Integer, Node<Task>> historyMap;
    private Node<Task> tail;
    private Node<Task> head;

    public InMemoryHistoryManager() {
        historyMap = new HashMap<>();
    }

    @Override
    public void setHistory(Task task) {
        if (task != null) {
            if (historyMap.containsKey(task.getId())) {
                removeNode(historyMap.get(task.getId()));
                historyMap.remove(task.getId());
            }
            linkLast(task.getSnapshot());
        }
    }

    private void linkLast(Task task) {
        final Node<Task> oldTail = tail;
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
    public void removeHistory(int id) {
        if (historyMap.containsKey(id)) {
            removeNode(historyMap.get(id));
            historyMap.remove(id);
        }
    }

    private void removeNode(Node<Task> nodeToRemove) {
        if (nodeToRemove == null) {
            return;
        }
        Node<Task> nextToNodeToRemove;
        Node<Task> prevToNodeToRemove;

        if (nodeToRemove.next == null && nodeToRemove.prev == null) {
            head = null;
            tail = null;
        } else {

            if (nodeToRemove.next != null) {
                nextToNodeToRemove = nodeToRemove.next;
                nextToNodeToRemove.prev = nodeToRemove.prev;
            } else {
                prevToNodeToRemove = nodeToRemove.prev;
                tail = prevToNodeToRemove;
                tail.next = null;
            }
            if (nodeToRemove.prev != null) {
                prevToNodeToRemove = nodeToRemove.prev;
                prevToNodeToRemove.next = nodeToRemove.next;
            } else {
                nextToNodeToRemove = nodeToRemove.next;
                head = nextToNodeToRemove;
                head.prev = null;
            }
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
