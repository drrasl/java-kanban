package server;

import com.sun.net.httpserver.HttpServer;
import model.Epic;
import model.StatusOfTask;
import model.SubTask;
import model.Task;
import service.InMemoryTaskManager;
import service.Managers;
import service.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;


public class HttpTaskServer {
    private static final int PORT = 8080;
    private HttpServer httpServer;
    private TaskManager taskManager;


    public HttpTaskServer() throws IOException {
        this(Managers.getDefault());
    }

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        this.taskManager = taskManager;
        httpServer = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        httpServer.createContext("/tasks", new TaskHandler(taskManager));
        httpServer.createContext("/subtasks", new SubTaskHandler(taskManager));
        httpServer.createContext("/epics", new EpicHandler(taskManager));
        httpServer.createContext("/history", new HistoryHandler(taskManager));
        httpServer.createContext("/prioritized", new PriorityHandler(taskManager));
    }

    public void startServer() {
        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
        System.out.println("http://localhost:" + PORT);
        httpServer.start();
    }

    public void stopServer() {
        httpServer.stop(0);
        System.out.println("HTTP-сервер остановлен на " + PORT + " порту!");
    }

    //Проверяем работоспособность сервера
    public static void main(String[] args) throws IOException {

        TaskManager taskManager = new InMemoryTaskManager();


        Task task1 = new Task("task1", "descr1", StatusOfTask.DONE, LocalDateTime.of(2025, 01, 01, 12, 00), Duration.ofMinutes(60));
        Task task2 = new Task("task2", "descr2", StatusOfTask.NEW, LocalDateTime.of(2025, 02, 01, 13, 30), Duration.ofMinutes(60));
        taskManager.setTask(task1);
        taskManager.getTask(task1.getId());
        taskManager.setTask(task2);
        taskManager.getTask(task2.getId());

        Epic epic1 = new Epic("epic1", "descr3");
        taskManager.setEpic(epic1);

        SubTask subTask1 = new SubTask("subtask1", "descr4", StatusOfTask.NEW, epic1.getId(), LocalDateTime.of(2025, 03, 04, 12, 00), Duration.ofMinutes(60));
        SubTask subTask2 = new SubTask("subtask2", "descr5", StatusOfTask.IN_PROGRESS, epic1.getId(), LocalDateTime.of(2024, 01, 05, 12, 00), Duration.ofMinutes(30));
        SubTask subTask10 = new SubTask("subtask10", "descr10", StatusOfTask.DONE, epic1.getId(), LocalDateTime.of(2024, 02, 02, 12, 00), Duration.ofMinutes(40));

        taskManager.setSubTask(subTask1);
        taskManager.setSubTask(subTask2);
        taskManager.setSubTask(subTask10);

        taskManager.getSubTask(subTask1.getId());
        taskManager.getSubTask(subTask2.getId());
        taskManager.getSubTask(subTask10.getId());

        taskManager.getEpic(epic1.getId());

        taskManager.getAllTasks().forEach(System.out::println);
        taskManager.getAllEpics().forEach(System.out::println);
        taskManager.getAllSubTasks().forEach(System.out::println);


        HttpTaskServer server = new HttpTaskServer(taskManager);
        server.startServer();
    }


}
