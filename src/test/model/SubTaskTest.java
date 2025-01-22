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

    @Test
    void errorWhenEpicAddedAsSubTaskAndBack() {
        Epic epic1 = new Epic("Test addNewEpic1", "Test addNewEpic description1");
        SubTask subTask1 = new SubTask("Test addNewSubTask", "Test addNewSubTask description",
                StatusOfTask.NEW, epic1.getId());
        assertNotEquals(SubTask.class, epic1.getClass(), "Классы одинаковые-эпик может добавиться в субтаск" );
        assertNotEquals(Epic.class, subTask1.getClass(), "Классы одинаковые-субтаск может добавиться в эпик" );

        //    assertEquals(-1, taskManager.setSubTask(epic1);, "Эпик добавился как Подзадача - это ошибка");
        // таким образом не получается проверить, программа сразу ругается, если на вход субтаска поступает эпик
        // и выдает ошибку, даже, если в аргументе привести эпик к субтаску, то тест выдает ошибку, но пропускает
        // в компилятор: assertEquals(-1, taskManager.setSubTask((SubTask) (Task) epic1);
    }
}