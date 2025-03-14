package model;

import java.time.Duration;
import java.time.LocalDateTime;

public class SubTask extends Task {

    protected int epicId;

    public SubTask(String name, String description, StatusOfTask status, int epicId) {
        super(name, description, status);
        this.epicId = epicId;
        this.startTime = null;
        this.duration = null;
    }

    // Для создания или обновления подзадач + занесения в историю уникального экземпляра
    public SubTask(String name, String description, StatusOfTask status, int id, int epicId, LocalDateTime startTime,
                   Duration duration) {
        super(name, description, status, id, startTime, duration);
        this.epicId = epicId;
    }

    public SubTask(String name, String description, StatusOfTask status, int epicId, LocalDateTime startTime,
                   Duration duration) {
        super(name, description, status, startTime, duration);
        this.epicId = epicId;
    }

    @Override
    public SubTask getSnapshot() {
        return new SubTask(this.getName(), this.getDescription(), this.getStatus(), this.getId(), this.epicId,
                this.getStartTime(), this.getDuration());
    }

    public int getEpicId() {
        return epicId;
    }
}