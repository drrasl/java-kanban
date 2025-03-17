package service;

import model.Epic;
import model.StatusOfTask;
import model.SubTask;
import model.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {

    TaskManager taskManager = new InMemoryTaskManager();
    Task task1 = new Task("task1", "descr1", StatusOfTask.DONE, LocalDateTime.of(2023, 03, 04, 12, 00), Duration.ofMinutes(60));
    int task1ID = taskManager.setTask(task1);
    Task task2 = new Task("task2", "descr2", StatusOfTask.NEW, LocalDateTime.of(2022, 03, 04, 12, 00), Duration.ofMinutes(60));
    int task2ID = taskManager.setTask(task2);
    Epic epic1 = new Epic("epic1", "descr3");
    int epic1ID = taskManager.setEpic(epic1);
    SubTask subTask1 = new SubTask("subtask1", "descr4", StatusOfTask.NEW, epic1.getId(), LocalDateTime.of(2020, 03, 04, 12, 00), Duration.ofMinutes(60));
    int subTask1ID = taskManager.setSubTask(subTask1);
    SubTask subTask2 = new SubTask("subtask2", "descr5", StatusOfTask.NEW, epic1.getId(), LocalDateTime.of(2019, 03, 04, 12, 00), Duration.ofMinutes(60));
    int subTask2ID = taskManager.setSubTask(subTask2);
    Epic epic2 = new Epic("epic2", "descr6");
    int epic2ID = taskManager.setEpic(epic2);
    SubTask subTask3 = new SubTask("subtask3", "descr7", StatusOfTask.NEW, epic2.getId(), LocalDateTime.of(2017, 03, 04, 12, 00), Duration.ofMinutes(60));
    int subTask3ID = taskManager.setSubTask(subTask3);

    void setTaskTest() {
        assertEquals(2, taskManager.getAllTasks().size(), "Количество элементов списка Задач" +
                " не соответствует");
    }

    void setEpicTest() {
        assertEquals(2, taskManager.getAllEpics().size(), "Количество элементов списка Эпиков" +
                " не соответствует");
    }

    void setSubTaskTest() {
        assertEquals(3, taskManager.getAllSubTasks().size(), "Количество элементов списка Подзадач" +
                " не соответствует");
    }

    void getAllTasksTest() {
        List<Task> taskToCheck = List.of(task1, task2);
        assertEquals(taskToCheck, taskManager.getAllTasks(), "Списки задач не совпадают!");
    }

    void getAllSubTasksTest() {
        List<SubTask> taskToCheck = List.of(subTask1, subTask2, subTask3);
        assertEquals(taskToCheck, taskManager.getAllSubTasks(), "Списки подзадач не совпадают!");
    }

    void getAllEpicsTest() {
        List<Epic> taskToCheck = List.of(epic1, epic2);
        assertEquals(taskToCheck, taskManager.getAllEpics(), "Списки эпиков не совпадают!");
    }

    void removeAllTasksTest() {
        taskManager.removeAllTasks();
        assertEquals(0, taskManager.getAllTasks().size(), "Не все таски были удалены");
    }

    void removeAllSubTasksTest() {
        taskManager.removeAllSubTasks();
        assertEquals(0, taskManager.getAllSubTasks().size(), "Не все подзадачи были удалены");
    }

    void removeAllEpicsTest() {
        taskManager.removeAllEpics();
        assertEquals(0, taskManager.getAllSubTasks().size(), "Не все подзадачи были удалены");
        assertEquals(0, taskManager.getAllEpics().size(), "Не все эпики были удалены");
    }

    void getTaskTest() {
        assertEquals(task1, taskManager.getTask(task1ID).orElse(null), "Вернулся неверный таск");
    }

    void getSubTaskTest() {
        assertEquals(subTask1, taskManager.getSubTask(subTask1ID).orElse(null), "Вернулась неверная подзадача");
    }

    void getEpicTest() {
        assertEquals(epic1, taskManager.getEpic(epic1ID).orElse(null), "Вернулся неверный эпик");
    }

    void updateTaskTest() {
        Task task1UPD = new Task("task1UPD", "descr1UPD", StatusOfTask.DONE, task1.getId(), LocalDateTime.of(2010, 03, 04, 12, 00), Duration.ofMinutes(60));
        taskManager.updateTask(task1UPD);
        assertNotEquals(task1, task1UPD, "Таски равны - ошибка");
        assertEquals(task1UPD, taskManager.getTask(task1ID).orElse(null), "Обновленный таск не добавился в мапу");
    }

    void updateSubTaskTest() {
        SubTask subTask1UPD = new SubTask("subtask1UPD", "descr4UPD", StatusOfTask.NEW, subTask1ID, epic1.getId(), LocalDateTime.of(2010, 03, 04, 12, 00), Duration.ofMinutes(60));
        taskManager.updateSubTask(subTask1UPD);
        assertNotEquals(subTask1, subTask1UPD, "Субтаски равны - ошибка");
        assertEquals(subTask1UPD, taskManager.getSubTask(subTask1ID).orElse(null), "Обновленный субтаск не добавился в мапу");
    }

    void updateEpicTest() {
        Epic epic1UPD = new Epic("epic1UPD", "descr3UPD", epic1ID);
        taskManager.updateEpic(epic1UPD);
        assertNotEquals(epic1, epic1UPD, "Эпики равны - ошибка");
        assertEquals("epic1UPD", taskManager.getEpic(epic1ID).orElse(null).getName(), "Имя эпика не обновилось");
        assertEquals("descr3UPD", taskManager.getEpic(epic1ID).orElse(null).getDescription(), "Описание эпика не обновилось");
    }

    void deleteTaskByIdTest() {
        taskManager.deleteTaskById(task1.getId());
        assertNull(taskManager.getTask(task1.getId()).orElse(null), "Таск не был удален");
    }

    void deleteSubTaskByIdTest() {
        taskManager.deleteSubTaskById(subTask1.getId());
        assertNull(taskManager.getSubTask(subTask1.getId()).orElse(null), "Подзадача не была удалена");
    }

    void deleteEpicByIdTest() {
        taskManager.deleteEpicById(epic2.getId());
        assertNull(taskManager.getEpic(epic2.getId()).orElse(null), "Эпик не был удален");
        assertNull(taskManager.getSubTaskByEpic(epic2.getId()), "Подзадачи не были удалены");
    }

    void getSubTaskByEpic() {
        List<SubTask> taskToCheck = List.of(subTask1, subTask2);
        assertEquals(taskToCheck, taskManager.getSubTaskByEpic(epic1ID), "Вернулись неверные подзадачи");
    }
}