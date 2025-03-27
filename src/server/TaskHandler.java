package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.Task;
import service.TaskManager;

import java.util.Optional;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {
    private final Gson gson;
    TaskManager taskManager;

    public TaskHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
        gson = GsonBuilder.getGson();
    }

    @Override
    public void handle(HttpExchange exchange) {
        try (exchange) {
            String path = exchange.getRequestURI().getPath();
            String[] splitPath = path.split("/");
            Endpoint endpoint = getEndpoint(path, exchange.getRequestMethod());
            String response;
            int id;

            switch (endpoint) {
                case GET_TASKS:
                    if (taskManager.getAllTasks().isEmpty()) {
                        sendText(exchange, "Список задач пуст");
                    }
                    response = gson.toJson(taskManager.getAllTasks());
                    sendText(exchange, response);
                    break;
                case GET_TASKS_ID:
                    if (idParser(exchange, splitPath[2]) > 0) {
                        id = idParser(exchange, splitPath[2]);
                    } else {
                        sendNotFound(exchange, "Задача с id " + splitPath[2] + " не найдена");
                        return;
                    }
                    if (taskManager.getTask(id).isPresent()) {
                        response = gson.toJson(taskManager.getTask(id).get());
                        sendText(exchange, response);
                    } else {
                        sendNotFound(exchange, "Задача с id " + splitPath[2] + " не найдена");
                    }
                    break;
                case DELETE_TASKS_ID:
                    if (idParser(exchange, splitPath[2]) > 0) {
                        id = idParser(exchange, splitPath[2]);
                    } else {
                        sendNotFound(exchange, "Задача с id " + splitPath[2] + " не найдена");
                        return;
                    }
                    if (taskManager.getTask(id).isPresent()) {
                        taskManager.deleteTaskById(id);
                        sendText(exchange, "Задача с id " + splitPath[2] + " успешно удалена");
                    } else {
                        sendNotFound(exchange, "Задача с id " + splitPath[2] + " не найдена");
                    }
                    break;
                case POST_TASKS:
                    Optional<String> body = getBodyFromTheClient(exchange);
                    if (body.isEmpty()) {
                        sendNotFound(exchange, "Задача не передана в теле POST запроса");
                        return;
                    }
                    String taskFromBody = body.get();
                    Task task = gson.fromJson(taskFromBody, Task.class);
                    if (task.getId() > 0) {
                        if (taskManager.isNoTaskIntersection(task)) {
                            taskManager.updateTask(task);
                            sendSuccess(exchange, "Задача успешно добавлена");
                        } else {
                            sendHasInteractions(exchange, "Новая задача пересекается с существующими задачами");
                        }
                    } else if (task.getId() == 0) {
                        int newId = taskManager.setTask(task);
                        if (newId == -1) {
                            sendHasInteractions(exchange, "Новая задача пересекается с существующими задачами");
                            return;
                        }
                        sendSuccess(exchange, "Задача успешно добавлена");
                    } else {
                        sendBadRequest(exchange, "Ошибка id задачи");
                    }
                    break;
                case UNKNOWN:
                    sendBadRequest(exchange, "Неизвестный тип запроса: " + exchange.getRequestMethod() + " " + path);
                    break;
                default:
                    sendBadRequest(exchange, "Эндпоинт не определен: \"" + path + "\". попробуйте еще раз.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
