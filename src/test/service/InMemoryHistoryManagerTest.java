package service;

import model.StatusOfTask;
import model.Task;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    HistoryManager historyManager = new InMemoryHistoryManager();
    Task task1 = new Task("task1", "descr1", StatusOfTask.NEW);
    Task task2 = new Task("task2", "descr2", StatusOfTask.NEW);
    Task task3 = new Task("task3", "descr3", StatusOfTask.NEW);
    Task task4 = new Task("task4", "descr4", StatusOfTask.NEW);
    Task task5 = new Task("task5", "descr5", StatusOfTask.NEW);
    Task task6 = new Task("task6", "descr6", StatusOfTask.NEW);
    Task task7 = new Task("task7", "descr7", StatusOfTask.NEW);
    Task task8 = new Task("task8", "descr8", StatusOfTask.NEW);
    Task task9 = new Task("task9", "descr9", StatusOfTask.NEW);
    Task task10 = new Task("task10", "descr10", StatusOfTask.NEW);
    Task task11 = new Task("task11", "descr11", StatusOfTask.NEW);

    void fillInArray() {
        historyManager.setHistory(task1);
        historyManager.setHistory(task2);
        historyManager.setHistory(task3);
        historyManager.setHistory(task4);
        historyManager.setHistory(task5);
        historyManager.setHistory(task6);
        historyManager.setHistory(task7);
        historyManager.setHistory(task8);
        historyManager.setHistory(task9);
        historyManager.setHistory(task10);
        historyManager.setHistory(task11);
    }

    @Test
    void addHistory() {
        historyManager.setHistory(task1);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size(), "История пустая.");
    }

    @Test
    void check10History() {
        fillInArray();
        final List<Task> history = historyManager.getHistory();
        assertEquals(10, history.size(), "История содержит более 10 историй.");
        assertEquals(task2, history.getFirst(), "Первый объект не удалился с 0ой позиции");
    }

    @Test
    void changeTaskNumber10AndPutInHistoryManager() {
        fillInArray();
        Task task10upd = new Task("task10UPD", "descr10UPD", StatusOfTask.DONE, task10.getId());
        historyManager.setHistory(task10upd);

        final List<Task> history = historyManager.getHistory();
        assertEquals(task10, history.get(7), "Объект поменялся, после добавления обновления 1");
        assertEquals(task10upd, history.get(9), "Объект поменялся, после добавления обновления 2");
    }

    @Test
    void changeTask7StatusInHistory() {
        historyManager.setHistory(task7);
        task7.setStatus(StatusOfTask.DONE);
        historyManager.setHistory(task7);
        boolean isDifferent = task7.equals(historyManager.getHistory().getFirst());
        assertFalse(isDifferent, "таски одинаковые");

    }
}