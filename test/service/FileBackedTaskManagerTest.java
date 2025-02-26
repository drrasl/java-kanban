package service;

import model.StatusOfTask;
import model.Task;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class FileBackedTaskManagerTest {

    static Path path;

    @BeforeAll
    public static void createFile() {
        try {
            path = Files.createTempFile(Paths.get("test/service/"), "test", ".csv");
        } catch (IOException e) {
            System.out.println("Ошибка: Временный файл не создался");
        }
    }

    FileBackedTaskManager taskManager = new FileBackedTaskManager(path);
    Task task1 = new Task("task1", "descr1", StatusOfTask.DONE);
    int task1ID = taskManager.setTask(task1);
    Task task2 = new Task("task2", "descr2", StatusOfTask.NEW);
    int task2ID = taskManager.setTask(task2);

    @Test
    void checkBackupFileIsCreatedTest() {
        assertNotNull(path, "Тестовый временный файл не создался");
    }

    @Test
    void checkSaveMethodWorksCorrectlyTest() {
        String[] lines = new String[4];
        try (BufferedReader br = new BufferedReader(new FileReader(path.toString()))) {
            int i = 0;
            while (br.ready()) {
                lines[i] = br.readLine();
                i++;
            }
        } catch (FileNotFoundException ex) {
            System.out.println("Файл не найден");
        } catch (IOException e) {
            System.out.println("Не считался файл");
        }
        assertEquals(task1, FileBackedTaskManager.fromString(lines[1]), "Таски 1 разные");
        assertEquals(task2, FileBackedTaskManager.fromString(lines[2]), "Таски 2  разные");
    }

    @Test
    void testToString() {
        assertEquals("1,TASK,task1,DONE,descr1", taskManager.toString(task1), "Метод неправильно " +
                "преобразовывает в строку");
    }

    @Test
    void loadFromFile() {
        FileBackedTaskManager taskManager2 = FileBackedTaskManager.loadFromFile(path.toFile());
        assertEquals(taskManager.getAllTasks(), taskManager2.getAllTasks(), "Таскменеджеры не равны");
    }

    @Test
    void fromString() {
        assertEquals(task1, FileBackedTaskManager.fromString("1,TASK,task1,DONE,descr1"), "Метод неправильно " +
                "преобразовывает из строки");
    }

    @AfterAll
    static void deleteAtTheEnd() {
        File dir = new File((path.toString()));
        dir.deleteOnExit();
    }
}