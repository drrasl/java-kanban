package service;

import model.Task;

import java.util.List;

interface HistoryManager {

    void setHistory (Task task);

    List<Task> getTasks();

    void removeHistory (int id);
}
