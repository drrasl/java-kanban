package service;

import model.Task;

import java.util.List;

interface HistoryManager {

    void setHistory (Task anyTask);

    List<Task> getHistory ();
}
