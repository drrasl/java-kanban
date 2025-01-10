package tasks;

import java.util.Objects;

public class Task {
    protected String name;
    protected String description;
    protected StatusOfTask status;
    protected int id;

    public Task(String name, String description, StatusOfTask status) {
        this.name = name;
        this.description = description;
        this.status = status;
    }
    //Конструктор только для проверки
    // создания обновленного объекта с тем же айди. Будет удален (если не требуется).
    public Task(String name, String description, StatusOfTask status, Integer id) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(name, task.name)
                && Objects.equals(description, task.description) && status == task.status
                && id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, status, id);
    }

    @Override
    public String toString() {
        return this.getClass().getName() + " {" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status + '\'' +
                ", id=" + id +
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
}
