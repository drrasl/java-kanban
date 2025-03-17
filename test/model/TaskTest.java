package model;

import org.junit.jupiter.api.Test;
import service.InMemoryTaskManager;
import service.TaskManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class TaskTest {

    TaskManager taskManager = new InMemoryTaskManager();

    @Test
    void addNewTask() {
        Task task = new Task("Test addNewTask", "Test addNewTask description", StatusOfTask.NEW,
                LocalDateTime.of(2023, 03, 04, 12, 00), Duration.ofMinutes(60));
        final int taskId = taskManager.setTask(task);
        final Task savedTask = taskManager.getTask(taskId).orElseThrow();
        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");
        final List<Task> tasks = taskManager.getAllTasks();
        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void addEpicCannotPutInTask() {
        Epic epic = new Epic("epic3", "descr15");
        assertEquals(-1, taskManager.setTask(epic), "эпик добавился в таск");
    }

}