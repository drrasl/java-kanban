package service;

import model.StatusOfTask;
import model.Task;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

class NodeTest {

    Task task = new Task("Task1", "Description1", StatusOfTask.NEW);
    Node node = new Node(null, task, null);

    @Test
    void isObjectCreated() {
        assertNotNull(node, "Объект не создан");
    }

    @Test
    void shallBeEqual() {
        assertEquals(task, node.getData(), "Узел не содержит задачу");
    }
}