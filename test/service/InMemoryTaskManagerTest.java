package service;

import model.StatusOfTask;
import model.Task;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @Override
    @Test
    void setTaskTest() {
        super.setTaskTest();
    }

    @Override
    @Test
    void setEpicTest() {
        super.setEpicTest();
    }

    @Override
    @Test
    void setSubTaskTest() {
        super.setSubTaskTest();
    }

    @Override
    @Test
    void getAllTasksTest() {
        super.getAllTasksTest();
    }

    @Override
    @Test
    void getAllSubTasksTest() {
        super.getAllSubTasksTest();
    }

    @Override
    @Test
    void getAllEpicsTest() {
        super.getAllEpicsTest();
    }

    @Override
    @Test
    void removeAllTasksTest() {
        super.removeAllTasksTest();
    }

    @Override
    @Test
    void removeAllSubTasksTest() {
        super.removeAllSubTasksTest();
    }

    @Override
    @Test
    void removeAllEpicsTest() {
        super.removeAllEpicsTest();
    }

    @Override
    @Test
    void getTaskTest() {
        super.getTaskTest();
    }

    @Override
    @Test
    void getSubTaskTest() {
        super.getSubTaskTest();
    }

    @Override
    @Test
    void updateTaskTest() {
        super.updateTaskTest();
    }

    @Override
    @Test
    void getEpicTest() {
        super.getEpicTest();
    }

    @Override
    @Test
    void updateSubTaskTest() {
        super.updateSubTaskTest();
    }

    @Override
    @Test
    void updateEpicTest() {
        super.updateEpicTest();
    }

    @Override
    @Test
    void deleteTaskByIdTest() {
        super.deleteTaskByIdTest();
    }

    @Override
    @Test
    void deleteSubTaskByIdTest() {
        super.deleteSubTaskByIdTest();
    }

    @Override
    @Test
    void deleteEpicByIdTest() {
        super.deleteEpicByIdTest();
    }

    @Override
    @Test
    void getSubTaskByEpic() {
        super.getSubTaskByEpic();
    }

    @Test
    void taskFindById() {
        assertEquals(task1, taskManager.getTask(task1ID).orElse(null), "По айди получен неверный Таск");
        assertEquals(epic1, taskManager.getEpic(epic1ID).orElse(null), "По айди получен неверный Эпик");
        assertEquals(subTask1, taskManager.getSubTask(subTask1ID).orElse(null), "По айди получен неверный СабТаск");
    }

    @Test
    void idConflictCheck() {
        int id = taskManager.getTask(task2.getId()).orElseThrow().getId();
        Task task2same = new Task("task2", "descr2", StatusOfTask.NEW, 77, LocalDateTime.of(2010, 03, 04, 12, 00), Duration.ofMinutes(60));
        taskManager.updateTask(task2same);
        boolean areTheSameTasks = task2.equals(task2same);
        assertFalse(areTheSameTasks, "объекты равны");

        Task task3same2same = new Task("task2", "descr2", StatusOfTask.NEW, 77, LocalDateTime.of(2010, 03, 04, 12, 00), Duration.ofMinutes(60));
        taskManager.updateTask(task3same2same);
        boolean areTheSameTasks2 = task2same.equals(task3same2same);
        assertTrue(areTheSameTasks2, "объекты не равны");

        Task task4SameTask2 = new Task("task2", "descr2", StatusOfTask.NEW, task2.getId(), LocalDateTime.of(2022, 03, 04, 12, 00), Duration.ofMinutes(60));
        taskManager.updateTask(task4SameTask2);
        boolean areTheSameTasks3 = task2.equals(task4SameTask2);
        assertTrue(areTheSameTasks3, "объекты не равны");
    }

    @Test
    void taskConstancy() {
        Task task5 = new Task("task5", "descr5", StatusOfTask.DONE);
        int task5ID = taskManager.setTask(task5);
        assertEquals("task5", taskManager.getTask(task5ID).orElseThrow().getName(), "Имя изменилось после " +
                " добавления Задачи");
        assertEquals("descr5", taskManager.getTask(task5ID).orElseThrow().getDescription(), "Описание " +
                "изменилось после добавления Задачи");
        assertEquals(StatusOfTask.DONE, taskManager.getTask(task5ID).orElseThrow().getStatus(), "Статус задачи " +
                "изменился после добавления Задачи");
    }

}