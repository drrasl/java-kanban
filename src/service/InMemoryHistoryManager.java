package service;

import model.Task;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {
    private final ArrayList<Task> history;

    public InMemoryHistoryManager() {
        history = new ArrayList<>();
    }

    @Override
    public void setHistory (Task task) {
        if (task != null) {
            history.add(task.getSnapshot());
            if (history.size() > 10) {
                history.removeFirst();
            }

        }
    }

    @Override
    public ArrayList<Task> getHistory () {
        return new ArrayList<>(history);
    }
}
