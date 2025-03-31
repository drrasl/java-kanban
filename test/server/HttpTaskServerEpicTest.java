package server;

import com.google.gson.Gson;
import model.Epic;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class HttpTaskServerEpicTest {

    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = GsonBuilder.getGson();

    HttpTaskServerEpicTest() throws IOException {
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
    public void testAddEpic() throws IOException, InterruptedException {

        Epic task = new Epic("Test 2", "Testing task 2");
        String taskJson = gson.toJson(task);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // проверяем код ответа
        assertEquals(201, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        List<Epic> tasksFromManager = manager.getAllEpics();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 2", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    public void testUpdateEpic() throws IOException, InterruptedException {

        Epic task = new Epic("Test 2", "Testing task 2");
        manager.setEpic(task);
        //Указали id - должен быть updateTask
        Epic task1 = new Epic("Test 22", "Testing task 22", 1);
        String taskJson = gson.toJson(task1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Код не 201");

        // проверяем, что осталась одна задача с обновленным именем
        List<Epic> tasksFromManager = manager.getAllEpics();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 22", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    public void testAddEpicNoBodyNotFound() throws IOException, InterruptedException {

        Epic task = new Epic("Test 2", "Testing task 2");
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString("")).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode(), "Код не 404");
    }


    @Test
    public void testAddEpicWithMinusId() throws IOException, InterruptedException {

        Epic task = new Epic("Test 2", "Testing task 2", -2);
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode(), "Ошибка: Должна быть ошибка 400");
    }

    @Test
    public void testUnknownEndPoint() throws IOException, InterruptedException {

        Epic task = new Epic("Test 2", "Testing task 2");
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).PUT(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode(), "Ошибка: Должна быть ошибка 400");
    }

    @Test
    public void testUnknownEpicsPostLink() throws IOException, InterruptedException {

        Epic task = new Epic("Test 2", "Testing task 2");
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode(), "Ошибка: Должна быть ошибка 400");
    }

    //Тест на метод DELETE
    @Test
    public void testDeleteEpic() throws IOException, InterruptedException {

        Epic task = new Epic("Test 2", "Testing task 2");
        manager.setEpic(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Ошибка: Должен быть код 200");

        List<Epic> tasksFromManager = manager.getAllEpics();

        assertEquals(0, tasksFromManager.size(), "Некорректное количество задач");
    }

    @Test
    public void testTryToDeleteEpicWithWrongId() throws IOException, InterruptedException {

        Epic task = new Epic("Test 2", "Testing task 2");
        manager.setEpic(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode(), "Ошибка: Должен быть код 404");
    }

    @Test
    public void testTryToDeleteEpicWithMinusId() throws IOException, InterruptedException {

        Epic task = new Epic("Test 2", "Testing task 2");
        manager.setEpic(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/-2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode(), "Ошибка: Должен быть код 404");
    }

    @Test
    public void testTryToDeleteEpicWithWrongLink() throws IOException, InterruptedException {

        Epic task = new Epic("Test 2", "Testing task 2");
        manager.setEpic(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode(), "Ошибка: Должен быть код 400");
    }

    //Тест на метод GET_TASKS_ID
    @Test
    public void testGetEpic() throws IOException, InterruptedException {

        Epic task = new Epic("Test 2", "Testing task 2");
        manager.setEpic(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Ошибка: Должен быть код 200");

        List<Epic> tasksFromManager = manager.getAllEpics();

        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("{\"subTasks\":[],\"endTime\":null,\"name\":\"Test 2\",\"description\":\"Testing task 2\",\"status\":\"NEW\",\"id\":1,\"duration\":null,\"startTime\":null}", response.body(), "Ошибка в возврате задачи");
    }

    @Test
    public void testTryToGetEpicWithWrongId() throws IOException, InterruptedException {

        Epic task = new Epic("Test 2", "Testing task 2");
        manager.setEpic(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode(), "Ошибка: Должен быть код 404");
    }

    @Test
    public void testTryToGetEpicWithMinusId() throws IOException, InterruptedException {

        Epic task = new Epic("Test 2", "Testing task 2");
        manager.setEpic(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/-2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode(), "Ошибка: Должен быть код 404");
    }

    @Test
    public void testTryToGetEpicWithWrongLink() throws IOException, InterruptedException {

        Epic task = new Epic("Test 2", "Testing task 2");
        manager.setEpic(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode(), "Ошибка: Должен быть код 400");
    }

    //Тест на метод GET_TASKS
    @Test
    public void testGetAllEpics() throws IOException, InterruptedException {

        Epic task = new Epic("Test 2", "Testing task 2");
        manager.setEpic(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Ошибка: Должен быть код 200");

        List<Epic> tasksFromManager = manager.getAllEpics();

        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("[{\"subTasks\":[],\"endTime\":null,\"name\":\"Test 2\",\"description\":\"Testing task 2\",\"status\":\"NEW\",\"id\":1,\"duration\":null,\"startTime\":null}]", response.body(), "Ошибка в возврате задачи");
    }
}