package service;

import model.Task;

import java.util.ArrayList;

public interface HistoryManager {

    public void setHistory (Task anyTask);

    public ArrayList<Task> getHistory ();
}
