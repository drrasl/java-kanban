package model;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import service.InMemoryTaskManager;
import service.TaskManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SubTaskTest {
    static TaskManager taskManager = new InMemoryTaskManager();

    @BeforeAll
    static void setEpic() {
        Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description");
        final int epicId = taskManager.setEpic(epic);
    }

    @Test
    void addNewSubTask() {
        SubTask subTask = new SubTask("Test addNewSubTask", "Test addNewSubTask description", StatusOfTask.NEW, 1);
        final int subTaskId = taskManager.setSubTask(subTask);
        assertEquals(2, subTaskId, "Подзадача не создалась, так как нет привязки к нужному эпику");
        final SubTask savedSubTask = taskManager.getSubTask(subTaskId);
        assertNotNull(savedSubTask, "Задача не найдена.");
        assertEquals(subTask, savedSubTask, "Задачи не совпадают.");
        final List<SubTask> subTasks = taskManager.getAllSubTasks();
        assertNotNull(subTasks, "Задачи не возвращаются.");
        assertEquals(1, subTasks.size(), "Неверное количество задач.");
        assertEquals(subTask, subTasks.get(0), "Задачи не совпадают.");
    }
    // Здесь должен быть тест - проверьте, что объект Epic нельзя добавить в самого себя в виде подзадачи;
//    @Test
//    void errorWhenEpicAddedAsSubTask() {
//        Epic epic1 = new Epic("Test addNewEpic1", "Test addNewEpic description1");
//        final int epicId1 = taskManager.setSubTask((SubTask) epic1);
//        assertNull(epicId1, "Эпик добавился как Подзадача - это ошибка");
//    }


}