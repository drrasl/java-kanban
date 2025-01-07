package tasks;

import java.util.ArrayList;

public class Epic extends Task {

    private ArrayList<SubTask> subTasks;
    private StatusOfTask epicStatus;

     public Epic(String name, String description) {
        super(name, description);
        subTasks = new ArrayList<>();
    }

    public void setSubTask(SubTask subTask) {
         subTasks.add(subTask);

    }

    public ArrayList<SubTask> getSubTasks() {
        return subTasks;
    }

    public void removeSubTasks() {
        this.subTasks.clear();
    }

    @Override
    public StatusOfTask getStatus() {
        int newStatusCounter = 0;
        int doneStatusCounter = 0;
        if (subTasks != null) {
            for (int i = 0; i < subTasks.size(); i++) {
                if (subTasks.get(i).getStatus() == StatusOfTask.NEW) {
                    newStatusCounter++;
                }
                if (subTasks.get(i).getStatus() == StatusOfTask.DONE) {
                    doneStatusCounter++;
                }
            }
            if (subTasks.isEmpty() || newStatusCounter == subTasks.size()) {
                epicStatus = StatusOfTask.NEW;
            } else if (doneStatusCounter == subTasks.size()) {
                epicStatus = StatusOfTask.DONE;
            } else {
                epicStatus = StatusOfTask.IN_PROGRESS;
            }
        } else {
            epicStatus = StatusOfTask.NEW;
        }
        return epicStatus;
    }
}
