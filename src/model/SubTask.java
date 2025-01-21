package model;

public class SubTask extends Task {

    private int epicId;

    public SubTask(String name, String description, StatusOfTask status, int epicId) {
        super(name, description, status);
        this.epicId = epicId;
    }
    // Для создания или обновления подзадач + занесения в историю уникального экземпляра
    public SubTask(String name, String description, StatusOfTask status, int id, int epicId) {
        super(name, description, status, id);
        this.epicId = epicId;
    }
    @Override
    public SubTask getSnapshot() {
        return new SubTask (this.getName(), this.getDescription(), this.getStatus(), this.getId(), this.epicId);
    }

    public int getEpicId () {
        return epicId;
    }
}
