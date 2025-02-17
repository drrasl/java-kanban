package model;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {

    private List<SubTask> subTasks;

    public Epic(String name, String description) {
        super(name, description, StatusOfTask.NEW);
        subTasks = new ArrayList<>();
    }

    // Для создания или обновления эпиков + занесения в историю уникального экземпляра
    public Epic(String name, String description, StatusOfTask status, Integer id) {
        super(name, description, status, id);
        subTasks = new ArrayList<>();
    }

    @Override
    public Epic getSnapshot() {
        return new Epic(this.getName(), this.getDescription(), this.getStatus(), this.getId());
    }

    public void setSubTask(SubTask subTask) {
        subTasks.add(subTask);

    }

    public void deleteSubTask(SubTask subTask) {
        subTasks.remove(subTask);
    }

    public ArrayList<SubTask> getSubTasks() {
        return new ArrayList<>(subTasks);
    }

    public void removeSubTasks() {
        this.subTasks.clear();
    }

    public void updateEpicStatus() {
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
                status = StatusOfTask.NEW;
            } else if (doneStatusCounter == subTasks.size()) {
                status = StatusOfTask.DONE;
            } else {
                status = StatusOfTask.IN_PROGRESS;
            }
        } else {
            status = StatusOfTask.NEW;
        }
    }
}
