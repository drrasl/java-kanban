package tasks;

import java.util.Objects;

public class Task {
    private final String name;
    private final String description;
    private StatusOfTask status;

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        status = StatusOfTask.NEW;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(name, task.name)
                && Objects.equals(description, task.description) && status == task.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, status);
    }

    @Override
    public String toString() {
        return this.getClass().getName() + " {" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }

    public void setStatus(StatusOfTask status) {
        this.status = status;
    }

    public StatusOfTask getStatus() {
        return status;
    }
}
