package service;

import model.Epic;
import model.StatusOfTask;
import model.SubTask;
import model.Task;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    TaskManager taskManager = new InMemoryTaskManager();
    Task task1 = new Task("task1", "descr1", StatusOfTask.DONE);
    int task1ID = taskManager.setTask(task1);
    Task task2 = new Task("task2", "descr2", StatusOfTask.NEW);
    int task2ID = taskManager.setTask(task2);
    Epic epic1 = new Epic ("epic1", "descr3");
    int epic1ID = taskManager.setEpic(epic1);
    SubTask subTask1 = new SubTask("subtask1", "descr4", StatusOfTask.NEW, epic1.getId());
    int subTask1ID = taskManager.setSubTask(subTask1);
    SubTask subTask2 = new SubTask("subtask2", "descr5", StatusOfTask.NEW, epic1.getId());
    int subTask2ID = taskManager.setSubTask(subTask2);
    Epic epic2 = new Epic ("epic2", "descr6");
    int epic2ID = taskManager.setEpic(epic2);
    SubTask subTask3 = new SubTask("subtask3", "descr7", StatusOfTask.NEW, epic2.getId());
    int subTask3ID = taskManager.setSubTask(subTask3);

    @Test
    void checkTaskAddedCheckDifferentType() {
        assertEquals(2, taskManager.getAllTasks().size(), "Количество элементов списка Задач" +
                " не соответствует");
        assertEquals(2, taskManager.getAllEpics().size(), "Количество элементов списка Эпиков" +
                " не соответствует");
        assertEquals(3, taskManager.getAllSubTasks().size(), "Количество элементов списка Подзадач" +
                " не соответствует");
    }

    @Test
    void taskFIndById() {
        assertEquals(task1, taskManager.getTask(task1ID), "По айди получен неверный Таск");
        assertEquals(epic1, taskManager.getEpic(epic1ID), "По айди получен неверный Эпик");
        assertEquals(subTask1, taskManager.getSubTask(subTask1ID), "По айди получен неверный СабТаск");
    }

    @Test
    void idConflictCheck() {
        int id = taskManager.getTask(task2.getId()).getId();
        Task task2same = new Task("task2", "descr2", StatusOfTask.NEW, 77);
        taskManager.updateTask(task2same);
        boolean areTheSameTasks = task2.equals(task2same);
        assertFalse(areTheSameTasks, "объекты равны");

        Task task3same2same = new Task("task2", "descr2", StatusOfTask.NEW, 77);
        taskManager.updateTask(task3same2same);
        boolean areTheSameTasks2 = task2same.equals(task3same2same);
        assertTrue(areTheSameTasks2, "объекты не равны");

        Task task4SameTask2 = new Task("task2", "descr2", StatusOfTask.NEW, task2.getId());
        taskManager.updateTask(task4SameTask2);
        boolean areTheSameTasks3 = task2.equals(task4SameTask2);
        assertTrue(areTheSameTasks3, "объекты не равны");
    }

    @Test
    void taskConstancy () {
        Task task5 = new Task("task5", "descr5", StatusOfTask.DONE);
        int task5ID = taskManager.setTask(task5);
        assertEquals("task5", taskManager.getTask(task5ID).getName(), "Имя изменилось после " +
                " добавления Задачи" );
        assertEquals("descr5", taskManager.getTask(task5ID).getDescription(), "Описание " +
                "изменилось после добавления Задачи" );
        assertEquals(StatusOfTask.DONE, taskManager.getTask(task5ID).getStatus(), "Статус задачи " +
                "изменился после добавления Задачи" );
    }

    @Test
    void removeAllTasksTest() {
        taskManager.removeAllTasks();
        assertEquals(0, taskManager.getAllTasks().size(), "Не все таски были удалены");
    }

    @Test
    void removeAllSubTasksTest() {
        taskManager.removeAllSubTasks();
        assertEquals(0, taskManager.getAllSubTasks().size(), "Не все подзадачи были удалены");
    }

    @Test
    void removeAllEpicTest() {
        taskManager.removeAllEpics();
        assertEquals(0, taskManager.getAllSubTasks().size(), "Не все подзадачи были удалены");
        assertEquals(0, taskManager.getAllEpics().size(), "Не все эпики были удалены");
    }

    @Test
    void deleteTaskByIdTest() {
        taskManager.deleteTaskById(task1.getId());
        assertNull(taskManager.getTask(task1.getId()), "Таск не был удален");
    }

    @Test
    void deleteSubTaskByIdTest() {
        taskManager.deleteSubTaskById(subTask1.getId());
        assertNull(taskManager.getSubTask(subTask1.getId()), "Подзадача не была удалена");
    }

    @Test
    void deleteEpicByIdTest() {
        taskManager.deleteEpicById(epic2.getId());
        assertNull(taskManager.getEpic(epic2.getId()), "Эпик не был удален");
        assertNull(taskManager.getSubTaskByEpic(epic2.getId()), "Подзадачи не были удалены");
    }
}