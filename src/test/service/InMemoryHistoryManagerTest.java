package service;

import model.Epic;
import model.StatusOfTask;
import model.Task;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    HistoryManager historyManager = new InMemoryHistoryManager();
    Task task1 = new Task("task1", "descr1", StatusOfTask.NEW, 1);
    Task task2 = new Task("task2", "descr2", StatusOfTask.NEW, 2);
    Task task3 = new Task("task3", "descr3", StatusOfTask.NEW, 3);
    Task task4 = new Task("task4", "descr4", StatusOfTask.NEW, 4);
    Task task5 = new Task("task5", "descr5", StatusOfTask.NEW, 5);
    Task task6 = new Task("task6", "descr6", StatusOfTask.NEW, 6);
    Task task7 = new Task("task7", "descr7", StatusOfTask.NEW, 7);
    Task task8 = new Task("task8", "descr8", StatusOfTask.NEW, 8);
    Task task9 = new Task("task9", "descr9", StatusOfTask.NEW, 9);
    Task task10 = new Task("task10", "descr10", StatusOfTask.NEW, 10);
    Task task11 = new Task("task11", "descr11", StatusOfTask.NEW, 11);
    Epic epic1 = new Epic("epic1", "descr1", StatusOfTask.NEW, 12);

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
        historyManager.setHistory(epic1);
    }

    @Test
    void addHistoryTest() {
        historyManager.setHistory(task1);
        final List<Task> history = historyManager.getTasks();
        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size(), "История пустая.");
    }

    @Test
    void checkHistoryLengthTest() {
        fillInArray();
        final List<Task> history = historyManager.getTasks();
        assertEquals(12, history.size(), "История содержит не 11 записей.");
    }

    @Test
    void changeTaskNumber10AndPutInHistoryManagerTast() {
        fillInArray();
        Task task10upd = new Task("task10UPD", "descr10UPD", StatusOfTask.DONE, task10.getId());
        historyManager.setHistory(task10upd);

        final List<Task> history = historyManager.getTasks();
        assertNotEquals(task10, history.get(9), "Объект не удалился со своей позиции, " +
                "после добавления обновления");
        assertEquals(task10upd, history.get(11), "Объект не появился в конце списка после обновления " +
                "и/или не равен себе, после добавления обновления");
    }

    @Test
    void changeTask7StatusInHistoryTest() {
        historyManager.setHistory(task7);
        task7.setStatus(StatusOfTask.DONE);
        historyManager.setHistory(task7);
        boolean isSame = task7.equals(historyManager.getTasks().getFirst());
        assertTrue(isSame, "таски разные и/или не удалось удалить первый таск в истории и заменить" +
                "новым");
    }

    @Test
    void checkEpicEqualityInHistoryTest() {
        Epic epic2 = new Epic("epic2", "descr1", StatusOfTask.NEW, 12);
        Epic epic3sameEpic1 = new Epic("epic1", "descr1", StatusOfTask.NEW, 12);
        assertNotEquals(epic1, epic2);
        assertEquals(epic1, epic3sameEpic1);
    }

    @Test
    void add1TaskAndPutItAgainTest() {
        historyManager.setHistory(task1);
        historyManager.setHistory(task1);
        final List<Task> history = historyManager.getTasks();
        assertNotNull(history.get(0), "Таск удалился и не появился");
        assertEquals(1, history.size(), "Длинна != 1, либо таск не создался после удаления, " +
                "либо не удалился и добавился вторым");
    }

    @Test
    void add2TasksAndChangeFirstTest() {
        historyManager.setHistory(task1);
        historyManager.setHistory(task2);
        task1.setStatus(StatusOfTask.DONE);
        historyManager.setHistory(task1);
        final List<Task> history = historyManager.getTasks();
        assertEquals(2, history.size(), "Длинна != 2, либо таск не создался после удаления, " +
                "либо не удалился и добавился третьим");
        assertEquals(task2, history.getFirst(), "Первый в очереди таск отличается от таск2");
        assertEquals(task1, history.get(1), "Второй в очереди таск отличается от таск1");
    }

    @Test
    void checkProperFirstNodeAddedInHashMapTest() {
        InMemoryHistoryManager inMemoryHistoryManager = new InMemoryHistoryManager();
        inMemoryHistoryManager.linkLast(task1);
        assertNotNull(inMemoryHistoryManager.getHead(), "head при добавлении 1го элемента остался null");
        assertNotNull(inMemoryHistoryManager.getTail(), "tail при добавлении 1го элемента остался null");
        assertNotNull(inMemoryHistoryManager.getHistoryMap().get(task1.getId()), "node1 не добавлена в " +
                "HashMap");
        assertEquals(task1, inMemoryHistoryManager.getHistoryMap().get(task1.getId()).getData(), "task1 не лежит" +
                "в node1");
    }

    @Test
    void checkProperSecondNodeAddedInHashMapTest() {
        InMemoryHistoryManager inMemoryHistoryManager = new InMemoryHistoryManager();
        inMemoryHistoryManager.linkLast(task1);
        inMemoryHistoryManager.linkLast(task2);
        assertEquals(2, inMemoryHistoryManager.getHistoryMap().size(), "размер не соответствует 2");
        assertEquals(task2, inMemoryHistoryManager.getHistoryMap().get(task2.getId()).getData(), "task2 не лежит" +
                "в node2");
    }

    @Test
    void removeHistoryTest() {
        historyManager.setHistory(task1);
        historyManager.removeHistory(task1.getId());
        final List<Task> history = historyManager.getTasks();
        assertEquals(0, history.size(), "объект не удалился");

    }

    @Test
    void nodeRemoveTest() {
        InMemoryHistoryManager inMemoryHistoryManager = new InMemoryHistoryManager();
        inMemoryHistoryManager.linkLast(task1);
        inMemoryHistoryManager.linkLast(task2);
        inMemoryHistoryManager.linkLast(task3);
        HashMap<Integer, Node<Task>> history = (HashMap<Integer, Node<Task>>) inMemoryHistoryManager.getHistoryMap();
        inMemoryHistoryManager.removeHistory(task2.getId());
        assertFalse(history.containsKey(task2.getId()), "Нода 2 не удалилась из хеш-мапы");
        assertNull(history.get(task1.getId()).prev, "prev первого элемента не null");
        assertEquals(history.get(task3.getId()).prev, history.get(task1.getId()), "prev третьего элемента не связался с первым");
        assertEquals(history.get(task3.getId()), history.get(task1.getId()).next, "третий элемент не связался с next первого");
        assertNull(history.get(task3.getId()).next, "next третьего элемента не null");
    }


}