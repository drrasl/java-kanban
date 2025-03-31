package server;

import com.google.gson.Gson;
import model.StatusOfTask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class HttpTaskServerTaskTest {

    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = GsonBuilder.getGson();

    HttpTaskServerTaskTest() throws IOException {
    }

    @BeforeEach
    public void setUp() {
        manager.removeAllTasks();
        manager.removeAllSubTasks();
        manager.removeAllEpics();
        taskServer.startServer();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stopServer();
    }

    //Тесты на методы POST
    @Test
    public void testAddTask() throws IOException, InterruptedException {

        Task task = new Task("Test 2", "Testing task 2", StatusOfTask.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        String taskJson = gson.toJson(task);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // проверяем код ответа
        assertEquals(201, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        List<Task> tasksFromManager = manager.getAllTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 2", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    public void testUpdateTask() throws IOException, InterruptedException {

        Task task = new Task("Test 2", "Testing task 2", StatusOfTask.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        manager.setTask(task);
        //Указали id - должен быть updateTask
        Task task1 = new Task("Test 22", "Testing task 22", StatusOfTask.DONE, 1, LocalDateTime.now(), Duration.ofMinutes(5));
        String taskJson = gson.toJson(task1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Код не 201");

        // проверяем, что осталась одна задача с обновленным именем
        List<Task> tasksFromManager = manager.getAllTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 22", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    public void testAddTaskNoBodyNotFound() throws IOException, InterruptedException {

        Task task = new Task("Test 2", "Testing task 2", StatusOfTask.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString("")).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode(), "Код не 404");
    }

    @Test
    public void testIntersectionalTaskWhenAdd() throws IOException, InterruptedException {

        Task task = new Task("Test 2", "Testing task 2", StatusOfTask.NEW, LocalDateTime.of(2025, 03, 04, 12, 00), Duration.ofMinutes(5));
        manager.setTask(task);

        Task task1 = new Task("Test 22", "Testing task 22", StatusOfTask.DONE, LocalDateTime.of(2025, 03, 04, 12, 00), Duration.ofMinutes(10));
        String taskJson = gson.toJson(task1);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).header("Accept", "application/json").POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(0, task1.getId(), "Id задачи создался, а должен быть = 0");
        assertEquals(406, response.statusCode(), "Код не 406");
        assertEquals("Новая задача пересекается с существующими задачами", response.body(), "Ошибка: задача не пересекается");
    }

    @Test
    public void testIntersectionalTaskWhenUpdate() throws IOException, InterruptedException {

        Task task = new Task("Test 2", "Testing task 2", StatusOfTask.NEW, LocalDateTime.of(2025, 03, 04, 12, 00), Duration.ofMinutes(5));
        Task task1 = new Task("Test 2", "Testing task 2", StatusOfTask.NEW, LocalDateTime.of(2024, 03, 04, 12, 00), Duration.ofMinutes(5));
        manager.setTask(task);
        manager.setTask(task1);
        //Указали id - должен быть updateTask
        Task task2 = new Task("Test 22", "Testing task 22", StatusOfTask.DONE, 1, LocalDateTime.of(2024, 03, 04, 12, 00), Duration.ofMinutes(10));
        String taskJson = gson.toJson(task2);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(1, task2.getId(), "Id задачи увеличился, а должен быть старым = 1");
        assertEquals(406, response.statusCode(), "Код не 406");
        assertEquals("Новая задача пересекается с существующими задачами", response.body(), "Ошибка: задача не пересекается");
    }

    @Test
    public void testAddTaskWithMinusId() throws IOException, InterruptedException {

        Task task = new Task("Test 2", "Testing task 2", StatusOfTask.NEW, -2, LocalDateTime.now(), Duration.ofMinutes(5));
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode(), "Ошибка: Должна быть ошибка 400");
    }

    @Test
    public void testUnknownEndPoint() throws IOException, InterruptedException {

        Task task = new Task("Test 2", "Testing task 2", StatusOfTask.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).PUT(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode(), "Ошибка: Должна быть ошибка 400");
    }

    @Test
    public void testUnknownTasksPostLink() throws IOException, InterruptedException {

        Task task = new Task("Test 2", "Testing task 2", StatusOfTask.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode(), "Ошибка: Должна быть ошибка 400");
    }

    //Тест на метод DELETE
    @Test
    public void testDeleteTask() throws IOException, InterruptedException {

        Task task = new Task("Test 2", "Testing task 2", StatusOfTask.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        manager.setTask(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Ошибка: Должен быть код 200");

        List<Task> tasksFromManager = manager.getAllTasks();

        assertEquals(0, tasksFromManager.size(), "Некорректное количество задач");
    }

    @Test
    public void testTryToDeleteTaskWithWrongId() throws IOException, InterruptedException {

        Task task = new Task("Test 2", "Testing task 2", StatusOfTask.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        manager.setTask(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode(), "Ошибка: Должен быть код 404");
    }

    @Test
    public void testTryToDeleteTaskWithMinusId() throws IOException, InterruptedException {

        Task task = new Task("Test 2", "Testing task 2", StatusOfTask.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        manager.setTask(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/-2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode(), "Ошибка: Должен быть код 404");
    }

    @Test
    public void testTryToDeleteTaskWithWrongLink() throws IOException, InterruptedException {

        Task task = new Task("Test 2", "Testing task 2", StatusOfTask.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        manager.setTask(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/1/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode(), "Ошибка: Должен быть код 400");
    }

    //Тест на метод GET_TASKS_ID
    @Test
    public void testGetTask() throws IOException, InterruptedException {

        Task task = new Task("Test 2", "Testing task 2", StatusOfTask.NEW, LocalDateTime.of(2021, 01, 01, 12, 00), Duration.ofMinutes(30));
        manager.setTask(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Ошибка: Должен быть код 200");

        List<Task> tasksFromManager = manager.getAllTasks();

        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("{\"name\":\"Test 2\",\"description\":\"Testing task 2\",\"status\":\"NEW\",\"id\":1,\"duration\":30,\"startTime\":\"2021-01-01 12:00\"}", response.body(), "Ошибка в возврате задачи");
    }

    @Test
    public void testTryToGetTaskWithWrongId() throws IOException, InterruptedException {

        Task task = new Task("Test 2", "Testing task 2", StatusOfTask.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        manager.setTask(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode(), "Ошибка: Должен быть код 404");
    }

    @Test
    public void testTryToGetTaskWithMinusId() throws IOException, InterruptedException {

        Task task = new Task("Test 2", "Testing task 2", StatusOfTask.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        manager.setTask(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/-2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode(), "Ошибка: Должен быть код 404");
    }

    @Test
    public void testTryToGetTaskWithWrongLink() throws IOException, InterruptedException {

        Task task = new Task("Test 2", "Testing task 2", StatusOfTask.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        manager.setTask(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/1/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode(), "Ошибка: Должен быть код 400");
    }

    //Тест на метод GET_TASKS
    @Test
    public void testGetAllTasks() throws IOException, InterruptedException {

        Task task = new Task("Test 2", "Testing task 2", StatusOfTask.NEW, LocalDateTime.of(2021, 01, 01, 12, 00), Duration.ofMinutes(30));
        manager.setTask(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Ошибка: Должен быть код 200");

        List<Task> tasksFromManager = manager.getAllTasks();

        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("[{\"name\":\"Test 2\",\"description\":\"Testing task 2\",\"status\":\"NEW\",\"id\":1,\"duration\":30,\"startTime\":\"2021-01-01 12:00\"}]", response.body(), "Ошибка в возврате задачи");
    }
}