package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {

    private List<SubTask> subTasks;
    protected LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, description, StatusOfTask.NEW);
        subTasks = new ArrayList<>();
    }

    public Epic(String name, String description, Integer id) {
        super(name, description, StatusOfTask.NEW);
        super.id = id;
        subTasks = new ArrayList<>();
    }

    // Для создания или обновления эпиков + занесения в историю уникального экземпляра
    public Epic(String name, String description, StatusOfTask status, Integer id, LocalDateTime startTime,
                Duration duration, LocalDateTime endTime) {
        super(name, description, status, id, startTime, duration);
        subTasks = new ArrayList<>();
        this.endTime = endTime;
    }

    @Override
    public Epic getSnapshot() {
        updateEpicTime();
        return new Epic(this.getName(), this.getDescription(), this.getStatus(), this.getId(), this.getStartTime(),
                this.getDuration(), this.getEndTime());
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

    // updateEpicStatus также "под капотом" вызывает updateEpicTime(), так как их нужно запускать совместно.
    // На будущее будет сделан отдельный метод для единовременного обновления Статуса и Времени Эпика.
    public void updateEpicStatus() {
        int newStatusCounter = 0;
        int doneStatusCounter = 0;
        if (subTasks != null) {
            for (SubTask subTask : subTasks) {
                if (subTask.getStatus() == StatusOfTask.NEW) {
                    newStatusCounter++;
                }
                if (subTask.getStatus() == StatusOfTask.DONE) {
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
        updateEpicTime();
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    private void updateEpicTime() {
        this.startTime = getEarliestSubTaskTime(subTasks);
        this.endTime = getLatestSubTaskEndTime(subTasks);
        this.duration = subTasks.stream()
                .filter(Objects::nonNull)
                .map(SubTask::getDuration)
                .filter(Objects::nonNull)
                .reduce(Duration.ZERO, Duration::plus);
    }

    private LocalDateTime getEarliestSubTaskTime(List<SubTask> subTasks) {
        return subTasks.stream()
                .filter(Objects::nonNull)
                .map(SubTask::getStartTime)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .orElse(null);
    }

    private LocalDateTime getLatestSubTaskEndTime(List<SubTask> subTasks) {
        return subTasks.stream()
                .filter(Objects::nonNull)
                .map(SubTask::getEndTime)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .orElse(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Epic task = (Epic) o;
        return Objects.equals(name, task.name)
                && Objects.equals(description, task.description) && status == task.status
                && id == task.id && startTime.equals(task.startTime) && duration.equals(task.duration)
                && endTime.equals(task.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, status, id, duration, startTime, endTime);
    }
}