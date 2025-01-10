package tasks;

public class SubTask extends Task {

    private int epicId;

    public SubTask(String name, String description, StatusOfTask status, int epicId) {
        super(name, description, status);
        this.epicId = epicId;
    }
    //Конструктор только для проверки
    // создания обновленного объекта с тем же айди. Будет удален (если не требуется).
    public SubTask(String name, String description, StatusOfTask status, int id, int epicId) {
        super(name, description, status, id);
        this.epicId = epicId;
    }

    public int getEpicId () {
        return epicId;
    }
}
