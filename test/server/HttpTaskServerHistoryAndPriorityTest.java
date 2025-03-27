package server;

import com.google.gson.Gson;
import model.StatusOfTask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.InMemoryHistoryManager;
import service.InMemoryTaskManager;
import service.TaskManager;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HttpTaskServerHistoryAndPriorityTest {

    TaskManager manager = new InMemoryTaskManager();
    InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = GsonBuilder.getGson();

    HttpTaskServerHistoryAndPriorityTest() throws IOException {
    }

    @BeforeEach
    public void setUp() {
        manager.removeAllTasks();
        manager.removeAllSubTasks();
        manager.removeAllEpics();
        taskServer.startServer();
        for (int i = 0; i < historyManager.getTasks().size(); i++) {
            historyManager.removeHistory(i + 1);
        }
    }

    @AfterEach
    public void shutDown() {
        taskServer.stopServer();
    }

    @Test
    public void testAddHistory() throws IOException, InterruptedException {
        Task task1 = new Task("task1", "descr1", StatusOfTask.DONE, LocalDateTime.of(2025, 01, 01, 12, 00), Duration.ofMinutes(60));
        manager.setTask(task1);
        manager.getTask(1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Ошибка: Должен быть код 200");

        List<Task> tasksFromManager = manager.getTasks();

        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("[{\"name\":\"task1\",\"description\":\"descr1\",\"status\":\"DONE\",\"id\":1,\"duration\":60,\"startTime\":\"2025-01-01 12:00\"}]", response.body(), "Ошибка в возврате задачи");
    }

    @Test
    public void testAddPriority() throws IOException, InterruptedException {
        Task task1 = new Task("task1", "descr1", StatusOfTask.DONE, LocalDateTime.of(2025, 01, 01, 12, 00), Duration.ofMinutes(60));
        manager.setTask(task1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Ошибка: Должен быть код 200");

        Set<Task> tasksFromManager = manager.getPrioritizedTasks();

        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("[{\"name\":\"task1\",\"description\":\"descr1\",\"status\":\"DONE\",\"id\":1,\"duration\":60,\"startTime\":\"2025-01-01 12:00\"}]", response.body(), "Ошибка в возврате задачи");
    }

}