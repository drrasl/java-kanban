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
        assertEquals("1,TASK,task1,DONE,descr1", lines[1], "Таски 1 разные");
        assertEquals("2,TASK,task2,NEW,descr2", lines[2], "Таски 2  разные");
    }

    @Test
    void testToStringTest() {
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
        assertEquals("1,TASK,task1,DONE,descr1", lines[1], "Метод неправильно " +
                "преобразовывает в строку");
    }

    @Test
    void fromStringTest() {
        assertEquals(task1, taskManager.getTask(task1.getId()), "Метод неправильно " +
                "преобразовывает из строки");
    }

    @Test
    void loadFromFileTest() {
        FileBackedTaskManager taskManager2 = FileBackedTaskManager.loadFromFile(path.toFile());
        assertEquals(taskManager.getAllTasks(), taskManager2.getAllTasks(), "Таскменеджеры не равны");
    }

    @Test
    void newIdAfterLoadFromFileIncreasedTest() {
        FileBackedTaskManager taskManager2 = FileBackedTaskManager.loadFromFile(path.toFile());
        Task task3 = new Task("task3", "descr3", StatusOfTask.NEW);
        int task3ID = taskManager2.setTask(task3);
        assertEquals(3, task3ID, "Новый айди не продолжился после загрузки нового таскменеджера");
    }

    @Test
    void newTaskGeneratedInExistedFile() {
        FileBackedTaskManager taskManager2 = FileBackedTaskManager.loadFromFile(path.toFile());
        Task task3 = new Task("task3", "descr3", StatusOfTask.NEW);
        int task3ID = taskManager2.setTask(task3);
        String[] lines = new String[5];
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
        assertEquals("3,TASK,task3,NEW,descr3", lines[3], "Таски 3 разные, запись не добавилась " +
                "после загрузки нового менеджера и создания нового таска");
    }

    @AfterAll
    static void deleteAtTheEnd() {
        File dir = new File((path.toString()));
        dir.deleteOnExit();
    }
}