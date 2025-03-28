package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.SubTask;
import service.TaskManager;

import java.util.Optional;

public class SubTaskHandler extends BaseHttpHandler implements HttpHandler {
    private final Gson gson;
    TaskManager taskManager;

    public SubTaskHandler(TaskManager taskManager) {
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
                case GET_SUBTASKS:
                    if (taskManager.getAllSubTasks().isEmpty()) {
                        sendText(exchange, "Список подзадач пуст");
                    }
                    response = gson.toJson(taskManager.getAllSubTasks());
                    sendText(exchange, response);
                    break;
                case GET_SUBTASKS_ID:
                    if (idParser(exchange, splitPath[2]) > 0) {
                        id = idParser(exchange, splitPath[2]);
                    } else {
                        sendNotFound(exchange, "Подзадача с id " + splitPath[2] + " не найдена");
                        return;
                    }
                    if (taskManager.getSubTask(id).isPresent()) {
                        response = gson.toJson(taskManager.getSubTask(id).get());
                        sendText(exchange, response);
                    } else {
                        sendNotFound(exchange, "Подзадача с id " + splitPath[2] + " не найдена");
                    }
                    break;
                case DELETE_SUBTASKS_ID:
                    if (idParser(exchange, splitPath[2]) > 0) {
                        id = idParser(exchange, splitPath[2]);
                    } else {
                        sendNotFound(exchange, "Подзадача с id " + splitPath[2] + " не найдена");
                        return;
                    }
                    if (taskManager.getSubTask(id).isPresent()) {
                        taskManager.deleteSubTaskById(id);
                        sendText(exchange, "Подзадача с id " + splitPath[2] + " успешно удалена");
                    } else {
                        sendNotFound(exchange, "Подзадача с id " + splitPath[2] + " не найдена");
                    }
                    break;
                case POST_SUBTASKS:
                    Optional<String> body = getBodyFromTheClient(exchange);
                    if (body.isEmpty()) {
                        sendNotFound(exchange, "Подзадача не передана в теле POST запроса");
                        return;
                    }
                    String subTaskFromBody = body.get();
                    SubTask subTask = gson.fromJson(subTaskFromBody, SubTask.class);
                    if (subTask.getId() > 0) {
                        if (taskManager.isNoTaskIntersection(subTask)) {
                            taskManager.updateSubTask(subTask);
                            sendSuccess(exchange, "Подзадача успешно обновлена");
                        } else {
                            sendHasInteractions(exchange, "Новая подзадача пересекается с существующими задачами");
                        }
                    } else if (subTask.getId() == 0) {
                        int newId = taskManager.setSubTask(subTask);
                        if (newId == -1) {
                            sendHasInteractions(exchange, "Новая подзадача пересекается с существующими задачами " +
                                    "или id эпика не добавлен/не корректен");
                            return;
                        }
                        sendSuccess(exchange, "Подзадача успешно добавлена. Id подзадачи: " + newId);
                    } else {
                        sendBadRequest(exchange, "Ошибка id подзадачи");
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
