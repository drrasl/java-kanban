package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
    protected String name;
    protected String description;
    protected StatusOfTask status;
    protected int id;
    protected Duration duration; //Продолжительность задачи в минутах
    protected LocalDateTime startTime;

    //Конструктор по умолчанию: дату и время, продолжительность по умолчанию, как текущее дата и время и duration = 0.
    public Task(String name, String description, StatusOfTask status) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.startTime = null;
        this.duration = null;
    }

    // Для создания или обновления задач + занесения в историю уникального экземпляра
    public Task(String name, String description, StatusOfTask status, Integer id, LocalDateTime startTime,
                Duration duration) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.id = id;
        this.startTime = startTime;
        this.duration = duration;
    }

    public Task(String name, String description, StatusOfTask status, LocalDateTime startTime, Duration duration) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.startTime = startTime;
        this.duration = duration;
    }

    public Task getSnapshot() {
        return new Task(this.getName(), this.getDescription(), this.getStatus(), this.getId(),
                this.getStartTime(), this.getDuration());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(name, task.name)
                && Objects.equals(description, task.description) && status == task.status
                && id == task.id && startTime.equals(task.startTime) && duration.equals(task.duration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, status, id, duration, startTime);
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + " {" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status + '\'' +
                ", id=" + id +
                ", start time=" + startTime + '\'' +
                ", duration=" + duration + '\'' +
                ", end time=" + getEndTime() + '\'' +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public StatusOfTask getStatus() {
        return status;
    }

    public void setStatus(StatusOfTask status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getEndTime() {
        if (startTime == null | duration == null) {
            return null;
        } else {
            return startTime.plus(duration);
        }
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }
}