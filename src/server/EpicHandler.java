package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.Epic;
import service.TaskManager;

import java.util.Optional;

public class EpicHandler extends BaseHttpHandler implements HttpHandler {
    private final Gson gson;
    TaskManager taskManager;

    public EpicHandler(TaskManager taskManager) {
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
                case GET_EPICS:
                    if (taskManager.getAllEpics().isEmpty()) {
                        sendText(exchange, "Список эпиков пуст");
                    }
                    response = gson.toJson(taskManager.getAllEpics());
                    sendText(exchange, response);
                    break;
                case GET_EPICS_ID:
                    if (idParser(exchange, splitPath[2]) > 0) {
                        id = idParser(exchange, splitPath[2]);
                    } else {
                        sendNotFound(exchange, "Эпик с id " + splitPath[2] + " не найден");
                        return;
                    }
                    if (taskManager.getEpic(id).isPresent()) {
                        response = gson.toJson(taskManager.getEpic(id).get());
                        sendText(exchange, response);
                    } else {
                        sendNotFound(exchange, "Эпик с id " + splitPath[2] + " не найден");
                    }
                    break;
                case DELETE_EPICS_ID:
                    if (idParser(exchange, splitPath[2]) > 0) {
                        id = idParser(exchange, splitPath[2]);
                    } else {
                        sendNotFound(exchange, "Эпик с id " + splitPath[2] + " не найден");
                        return;
                    }
                    if (taskManager.getEpic(id).isPresent()) {
                        taskManager.deleteEpicById(id);
                        sendText(exchange, "Эпик с id " + splitPath[2] + " успешно удален");
                    } else {
                        sendNotFound(exchange, "Эпик с id " + splitPath[2] + " не найден");
                    }
                    break;
                case POST_EPICS:
                    Optional<String> body = getBodyFromTheClient(exchange);
                    if (body.isEmpty()) {
                        sendNotFound(exchange, "Эпик не передан в теле POST запроса");
                        return;
                    }
                    String epicFromBody = body.get();
                    Epic epic = gson.fromJson(epicFromBody, Epic.class);
                    if (epic.getId() > 0) {
                        taskManager.updateEpic(epic);
                        sendSuccess(exchange, "Эпик успешно обновлен");
                    } else if (epic.getId() == 0) {
                        int newId = taskManager.setEpic(epic);
                        sendSuccess(exchange, "Эпик успешно добавлен. Id эпика: " + newId);
                    } else {
                        sendBadRequest(exchange, "Ошибка id эпика");
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
