package model;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import service.InMemoryTaskManager;
import service.TaskManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class EpicTest {
    static TaskManager taskManager = new InMemoryTaskManager();

    @Test
    void addNewEpicTest() {
        Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description");
        int epicId = taskManager.setEpic(epic);
        final Epic savedEpic = taskManager.getEpic(epicId).orElse(null);
        assertNotNull(savedEpic, "Задача не найдена.");
        assertEquals(epic, savedEpic, "Задачи не совпадают.");
        final List<Epic> epics = taskManager.getAllEpics();
        assertNotNull(epics, "Задачи не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество задач.");
        assertEquals(epic, epics.get(0), "Задачи не совпадают.");
    }

    @AfterEach
    void clearMaps() {
        taskManager.removeAllSubTasks();
        taskManager.removeAllEpics();
    }

    @Test
    void checkEpicStatusNewTest() {
        Epic epic1 = new Epic("Test addNewEpic", "Test addNewEpic description");
        int epicId1 = taskManager.setEpic(epic1);
        SubTask subTask1 = new SubTask("subtask1", "descr1", StatusOfTask.NEW, epic1.getId(), LocalDateTime.of(2025, 03, 04, 12, 00), Duration.ofMinutes(60));
        SubTask subTask2 = new SubTask("subtask2", "descr2", StatusOfTask.NEW, epic1.getId(), LocalDateTime.of(2024, 01, 05, 12, 00), Duration.ofMinutes(30));
        SubTask subTask3 = new SubTask("subtask3", "descr3", StatusOfTask.NEW, epic1.getId(), LocalDateTime.of(2024, 02, 02, 12, 00), Duration.ofMinutes(40));
        taskManager.setSubTask(subTask1);
        taskManager.setSubTask(subTask2);
        taskManager.setSubTask(subTask3);
        assertEquals(StatusOfTask.NEW, taskManager.getEpic(epicId1).orElse(null).getStatus(), "Статус Эпика не NEW - ошибка");
    }

    @Test
    void checkEpicStatusDoneTest() {
        Epic epic2 = new Epic("Test addNewEpic", "Test addNewEpic description");
        int epicId2 = taskManager.setEpic(epic2);
        SubTask subTask4 = new SubTask("subtask1", "descr1", StatusOfTask.DONE, epic2.getId(), LocalDateTime.of(2025, 03, 04, 12, 00), Duration.ofMinutes(60));
        SubTask subTask5 = new SubTask("subtask2", "descr2", StatusOfTask.DONE, epic2.getId(), LocalDateTime.of(2024, 01, 05, 12, 00), Duration.ofMinutes(30));
        SubTask subTask6 = new SubTask("subtask3", "descr3", StatusOfTask.DONE, epic2.getId(), LocalDateTime.of(2024, 02, 02, 12, 00), Duration.ofMinutes(40));
        taskManager.setSubTask(subTask4);
        taskManager.setSubTask(subTask5);
        taskManager.setSubTask(subTask6);
        assertEquals(StatusOfTask.DONE, taskManager.getEpic(epicId2).orElse(null).getStatus(), "Статус Эпика не DONE - ошибка");
    }

    @Test
    void checkEpicStatusNewAndDoneTest() {
        Epic epic3 = new Epic("Test addNewEpic", "Test addNewEpic description");
        int epicId3 = taskManager.setEpic(epic3);
        SubTask subTask7 = new SubTask("subtask1", "descr1", StatusOfTask.NEW, epic3.getId(), LocalDateTime.of(2025, 03, 04, 12, 00), Duration.ofMinutes(60));
        SubTask subTask8 = new SubTask("subtask2", "descr2", StatusOfTask.DONE, epic3.getId(), LocalDateTime.of(2024, 01, 05, 12, 00), Duration.ofMinutes(30));
        SubTask subTask9 = new SubTask("subtask3", "descr3", StatusOfTask.NEW, epic3.getId(), LocalDateTime.of(2024, 02, 02, 12, 00), Duration.ofMinutes(40));
        taskManager.setSubTask(subTask7);
        taskManager.setSubTask(subTask8);
        taskManager.setSubTask(subTask9);
        assertEquals(StatusOfTask.IN_PROGRESS, taskManager.getEpic(epicId3).orElse(null).getStatus(), "Статус Эпика не IN_PROGRESS - ошибка");
    }

    @Test
    void checkEpicStatusInProgressTest() {
        Epic epic4 = new Epic("Test addNewEpic", "Test addNewEpic description");
        int epicId4 = taskManager.setEpic(epic4);
        SubTask subTask10 = new SubTask("subtask1", "descr1", StatusOfTask.IN_PROGRESS, epic4.getId(), LocalDateTime.of(2025, 03, 04, 12, 00), Duration.ofMinutes(60));
        SubTask subTask11 = new SubTask("subtask2", "descr2", StatusOfTask.IN_PROGRESS, epic4.getId(), LocalDateTime.of(2024, 01, 05, 12, 00), Duration.ofMinutes(30));
        SubTask subTask12 = new SubTask("subtask3", "descr3", StatusOfTask.IN_PROGRESS, epic4.getId(), LocalDateTime.of(2024, 02, 02, 12, 00), Duration.ofMinutes(40));
        taskManager.setSubTask(subTask10);
        taskManager.setSubTask(subTask11);
        taskManager.setSubTask(subTask12);
        assertEquals(StatusOfTask.IN_PROGRESS, taskManager.getEpic(epicId4).orElse(null).getStatus(), "Статус Эпика не IN_PROGRESS - ошибка");
    }

    @Test
    void checkEpicIdTestAndCorrectEpicTimeUpdateTest() {
        Epic epic1 = new Epic("Test addNewEpic", "Test addNewEpic description");
        int epicId1 = taskManager.setEpic(epic1);
        SubTask subTask1 = new SubTask("subtask1", "descr1", StatusOfTask.NEW, epic1.getId(), LocalDateTime.of(2025, 03, 04, 12, 00), Duration.ofMinutes(60));
        SubTask subTask2 = new SubTask("subtask2", "descr2", StatusOfTask.NEW, epic1.getId(), LocalDateTime.of(2024, 01, 05, 12, 00), Duration.ofMinutes(30));
        SubTask subTask3 = new SubTask("subtask3", "descr3", StatusOfTask.NEW, epic1.getId(), LocalDateTime.of(2024, 02, 02, 12, 00), Duration.ofMinutes(40));
        taskManager.setSubTask(subTask1);
        taskManager.setSubTask(subTask2);
        taskManager.setSubTask(subTask3);
        assertEquals(epicId1, taskManager.getSubTask(subTask1.getId()).orElse(null).getEpicId(), "ID связанного эпика разные - ошибка");
        assertEquals(epicId1, taskManager.getSubTask(subTask2.getId()).orElse(null).getEpicId(), "ID связанного эпика разные - ошибка");
        assertEquals(epicId1, taskManager.getSubTask(subTask3.getId()).orElse(null).getEpicId(), "ID связанного эпика разные - ошибка");

        assertEquals("2024-01-05T12:00", taskManager.getEpic(epicId1).orElse(null).getStartTime().toString(), "Время начала эпика - неверное");
        assertEquals("2025-03-04T13:00", taskManager.getEpic(epicId1).orElse(null).getEndTime().toString(), "Время начала эпика - неверное");
        assertEquals(130, taskManager.getEpic(epicId1).orElse(null).getDuration().toMinutes(), "Длительность эпика - неверная");
    }


}