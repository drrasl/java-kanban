package service;

import exceptions.ManagerSaveException;
import model.StatusOfTask;
import model.Task;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.*;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    static Path path;
    static Path path1;

    @BeforeAll
    public static void createFile() {
        try {
            path = Files.createTempFile(Paths.get("test/service/"), "test", ".csv");
        } catch (IOException e) {
            System.out.println("Ошибка: Временный файл не создался");
        }
    }

    FileBackedTaskManager taskManager = new FileBackedTaskManager(path);
    int task1ID = taskManager.setTask(task1);
    int task2ID = taskManager.setTask(task2);

    FileBackedTaskManager taskManager3 = new FileBackedTaskManager(path1);

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
        assertEquals("1,TASK,task1,DONE,descr1,2023-03-04T12:00,60,2023-03-04T13:00", lines[1], "Таски 1 разные");
        assertEquals("2,TASK,task2,NEW,descr2,2022-03-04T12:00,60,2022-03-04T13:00", lines[2], "Таски 2  разные");
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
        assertEquals("1,TASK,task1,DONE,descr1,2023-03-04T12:00,60,2023-03-04T13:00", lines[1], "Метод неправильно " +
                "преобразовывает в строку");
    }

    @Test
    void fromStringTest() {
        assertEquals(task1, taskManager.getTask(task1.getId()).orElse(null), "Метод неправильно " +
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
        Task task3 = new Task("task3", "descr3", StatusOfTask.NEW,
                LocalDateTime.of(2021, 03, 04, 12, 00), Duration.ofMinutes(60));
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
        assertEquals("3,TASK,task3,NEW,descr3,2021-03-04T12:00,60,2021-03-04T13:00", lines[3], "Таски 3 разные, запись не добавилась " +
                "после загрузки нового менеджера и создания нового таска");
    }

    void file2CreationWrongRef() throws IOException {
        path1 = Files.createTempFile(Paths.get("test/test_1/service/"), "test_1_", ".csv");
    }

    @Test
    void creationExceptionTest() {
        assertThrows(IOException.class, () -> {
            file2CreationWrongRef();
        }, "Здесь должна быть ошибка создания файла для записи, а ее нет");
    }

    void file2Creation() {
        try {
            path1 = Files.createTempFile(Paths.get("test/service/"), "test_1_", ".csv");
        } catch (IOException e) {
            System.out.println("Ошибка: Временный файл не создался");
        }

        Task task3 = new Task("task3", "descr3", StatusOfTask.NEW,
                LocalDateTime.of(1900, 03, 04, 12, 00), Duration.ofMinutes(60));

        taskManager3.setTask(task3);

        try {
            Files.delete(path1);
        } catch (NoSuchFileException x) {
            System.err.format("%s: no such" + " file or directory%n", path1);
        } catch (DirectoryNotEmptyException x) {
            System.err.format("%s not empty%n", path1);
        } catch (IOException x) {
            // File permission problems are caught here.
            System.err.println(x);
        }
    }

    @Test
    void exceptionTest() {
        file2Creation();
        assertThrows(ManagerSaveException.class, () -> {
            taskManager3.save();
        }, "Отсутствие файла должно приводить к ошибке");
    }

    @AfterAll
    static void deleteAtTheEnd() {
        File dir = new File((path.toString()));
        dir.deleteOnExit();
    }
}