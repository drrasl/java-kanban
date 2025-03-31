package server;

import com.google.gson.Gson;
import model.Epic;
import model.StatusOfTask;
import model.SubTask;
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

class HttpTaskServerSubTaskTest {

    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = GsonBuilder.getGson();

    HttpTaskServerSubTaskTest() throws IOException {
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
    public void testAddSubTask() throws IOException, InterruptedException {

        Epic epic = new Epic("Epic1", "EpicDescription1");
        manager.setEpic(epic);
        SubTask subTask = new SubTask("Test 2", "Testing task 2", StatusOfTask.NEW, 1, LocalDateTime.now(), Duration.ofMinutes(5));
        String taskJson = gson.toJson(subTask);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // проверяем код ответа
        assertEquals(201, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        List<SubTask> tasksFromManager = manager.getAllSubTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 2", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    public void testUpdateSubTask() throws IOException, InterruptedException {

        Epic epic = new Epic("Epic1", "EpicDescription1");
        manager.setEpic(epic);
        SubTask subTask = new SubTask("Test 2", "Testing task 2", StatusOfTask.NEW, 1, LocalDateTime.now(), Duration.ofMinutes(5));
        manager.setSubTask(subTask);
        //Указали id - должен быть updateTask
        SubTask subtask1 = new SubTask("Test 22", "Testing task 22", StatusOfTask.DONE, 2, 1, LocalDateTime.now(), Duration.ofMinutes(5));
        String taskJson = gson.toJson(subtask1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Код не 201");

        // проверяем, что осталась одна задача с обновленным именем
        List<SubTask> tasksFromManager = manager.getAllSubTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 22", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    public void testAddSubTaskNoBodyNotFound() throws IOException, InterruptedException {

        Epic epic = new Epic("Epic1", "EpicDescription1");
        manager.setEpic(epic);

        SubTask subTask = new SubTask("Test 2", "Testing task 2", StatusOfTask.NEW, 1, LocalDateTime.now(), Duration.ofMinutes(5));
        String taskJson = gson.toJson(subTask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString("")).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode(), "Код не 404");
    }

    @Test
    public void testIntersectionalSubTaskWhenAdd() throws IOException, InterruptedException {

        Epic epic = new Epic("Epic1", "EpicDescription1");
        manager.setEpic(epic);

        SubTask subTask = new SubTask("Test 2", "Testing task 2", StatusOfTask.NEW, 1, LocalDateTime.of(2025, 03, 04, 12, 00), Duration.ofMinutes(5));
        manager.setSubTask(subTask);

        SubTask subTask1 = new SubTask("Test 22", "Testing task 22", StatusOfTask.DONE, 1, LocalDateTime.of(2025, 03, 04, 12, 00), Duration.ofMinutes(10));
        String taskJson = gson.toJson(subTask1);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).header("Accept", "application/json").POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(0, subTask1.getId(), "Id задачи создался, а должен быть = 0");
        assertEquals(406, response.statusCode(), "Код не 406");
        assertEquals("Новая подзадача пересекается с существующими задачами или id эпика не добавлен/не корректен", response.body(), "Ошибка: задача не пересекается");
    }

    @Test
    public void testIntersectionalSubTaskWhenUpdate() throws IOException, InterruptedException {

        Epic epic = new Epic("Epic1", "EpicDescription1");
        manager.setEpic(epic);

        SubTask task = new SubTask("Test 2", "Testing task 2", StatusOfTask.NEW, 1, LocalDateTime.of(2025, 03, 04, 12, 00), Duration.ofMinutes(5));
        SubTask task1 = new SubTask("Test 2", "Testing task 2", StatusOfTask.NEW, 1, LocalDateTime.of(2024, 03, 04, 12, 00), Duration.ofMinutes(5));
        manager.setSubTask(task);
        manager.setSubTask(task1);
        //Указали id - должен быть updateTask
        SubTask task2 = new SubTask("Test 22", "Testing task 22", StatusOfTask.DONE, 2, 1, LocalDateTime.of(2024, 03, 04, 12, 00), Duration.ofMinutes(10));
        String taskJson = gson.toJson(task2);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(2, task2.getId(), "Id задачи увеличился, а должен быть старым = 2");
        assertEquals(406, response.statusCode(), "Код не 406");
        assertEquals("Новая подзадача пересекается с существующими задачами", response.body(), "Ошибка: задача не пересекается");
    }

    @Test
    public void testAddSubTaskWithMinusId() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic1", "EpicDescription1");
        manager.setEpic(epic);
        SubTask task = new SubTask("Test 2", "Testing task 2", StatusOfTask.NEW, -2, 1, LocalDateTime.now(), Duration.ofMinutes(5));
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode(), "Ошибка: Должна быть ошибка 400");
    }

    @Test
    public void testUnknownEndPoint() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic1", "EpicDescription1");
        manager.setEpic(epic);
        SubTask task = new SubTask("Test 2", "Testing task 2", StatusOfTask.NEW, 1, LocalDateTime.now(), Duration.ofMinutes(5));
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).PUT(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode(), "Ошибка: Должна быть ошибка 400");
    }

    @Test
    public void testUnknownSubTasksPostLink() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic1", "EpicDescription1");
        manager.setEpic(epic);
        SubTask task = new SubTask("Test 2", "Testing task 2", StatusOfTask.NEW, 1, LocalDateTime.now(), Duration.ofMinutes(5));
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode(), "Ошибка: Должна быть ошибка 400");
    }

    //Тест на метод DELETE
    @Test
    public void testDeleteSubTask() throws IOException, InterruptedException {

        Epic epic = new Epic("Epic1", "EpicDescription1");
        manager.setEpic(epic);
        SubTask task = new SubTask("Test 2", "Testing task 2", StatusOfTask.NEW, 1, LocalDateTime.now(), Duration.ofMinutes(5));
        manager.setSubTask(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Ошибка: Должен быть код 200");

        List<SubTask> tasksFromManager = manager.getAllSubTasks();

        assertEquals(0, tasksFromManager.size(), "Некорректное количество задач");
    }

    @Test
    public void testTryToDeleteSubTaskWithWrongId() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic1", "EpicDescription1");
        manager.setEpic(epic);
        SubTask task = new SubTask("Test 2", "Testing task 2", StatusOfTask.NEW, 1, LocalDateTime.now(), Duration.ofMinutes(5));
        manager.setSubTask(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode(), "Ошибка: Должен быть код 404");
    }

    @Test
    public void testTryToDeleteSubTaskWithMinusId() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic1", "EpicDescription1");
        manager.setEpic(epic);
        SubTask task = new SubTask("Test 2", "Testing task 2", StatusOfTask.NEW, 1, LocalDateTime.now(), Duration.ofMinutes(5));
        manager.setSubTask(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/-2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode(), "Ошибка: Должен быть код 404");
    }

    @Test
    public void testTryToDeleteSubTaskWithWrongLink() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic1", "EpicDescription1");
        manager.setEpic(epic);
        SubTask task = new SubTask("Test 2", "Testing task 2", StatusOfTask.NEW, 1, LocalDateTime.now(), Duration.ofMinutes(5));
        manager.setSubTask(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/2/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode(), "Ошибка: Должен быть код 400");
    }

    //Тест на метод GET_SUBTASKS_ID
    @Test
    public void testGetSubTask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic1", "EpicDescription1");
        manager.setEpic(epic);
        SubTask task = new SubTask("Test 2", "Testing task 2", StatusOfTask.NEW, 1, LocalDateTime.of(2021, 01, 01, 12, 00), Duration.ofMinutes(30));
        manager.setSubTask(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Ошибка: Должен быть код 200");

        List<SubTask> tasksFromManager = manager.getAllSubTasks();

        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("{\"epicId\":1,\"name\":\"Test 2\",\"description\":\"Testing task 2\",\"status\":\"NEW\",\"id\":2,\"duration\":30,\"startTime\":\"2021-01-01 12:00\"}", response.body(), "Ошибка в возврате задачи");
    }

    @Test
    public void testTryToGetSubTaskWithWrongId() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic1", "EpicDescription1");
        manager.setEpic(epic);
        SubTask task = new SubTask("Test 2", "Testing task 2", StatusOfTask.NEW, 1, LocalDateTime.now(), Duration.ofMinutes(5));
        manager.setSubTask(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode(), "Ошибка: Должен быть код 404");
    }

    @Test
    public void testTryToGetTaskWithMinusId() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic1", "EpicDescription1");
        manager.setEpic(epic);
        SubTask task = new SubTask("Test 2", "Testing task 2", StatusOfTask.NEW, 1, LocalDateTime.now(), Duration.ofMinutes(5));
        manager.setSubTask(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/-2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode(), "Ошибка: Должен быть код 404");
    }

    @Test
    public void testTryToGetSubTaskWithWrongLink() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic1", "EpicDescription1");
        manager.setEpic(epic);
        SubTask task = new SubTask("Test 2", "Testing task 2", StatusOfTask.NEW, 1, LocalDateTime.now(), Duration.ofMinutes(5));
        manager.setSubTask(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/2/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode(), "Ошибка: Должен быть код 400");
    }

    //Тест на метод GET_TASKS
    @Test
    public void testGetAllSubTasks() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic1", "EpicDescription1");
        manager.setEpic(epic);
        SubTask task = new SubTask("Test 2", "Testing task 2", StatusOfTask.NEW, 1, LocalDateTime.of(2021, 01, 01, 12, 00), Duration.ofMinutes(30));
        manager.setSubTask(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Ошибка: Должен быть код 200");

        List<SubTask> tasksFromManager = manager.getAllSubTasks();

        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("[{\"epicId\":1,\"name\":\"Test 2\",\"description\":\"Testing task 2\",\"status\":\"NEW\",\"id\":2,\"duration\":30,\"startTime\":\"2021-01-01 12:00\"}]", response.body(), "Ошибка в возврате задачи");
    }
}