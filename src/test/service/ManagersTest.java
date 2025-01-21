package service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {
    @Test
    public void checkCreationObjectsOfManagerAndTaskManagerAndHistoryManager() {

        TaskManager taskManager = Managers.getDefault();
        HistoryManager historyManager = Managers.getDefaultHistory();
        assertNotNull(taskManager, "Объект TaskManager не создан = null");
        assertNotNull(historyManager, "Объект HistoryManager не создан = null");
    }
}