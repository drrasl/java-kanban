package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import service.TaskManager;

public class PriorityHandler extends BaseHttpHandler implements HttpHandler {
    private final Gson gson;
    TaskManager taskManager;

    public PriorityHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
        gson = GsonBuilder.getGson();
    }

    @Override
    public void handle(HttpExchange exchange) {
        try (exchange) {
            String path = exchange.getRequestURI().getPath();
            Endpoint endpoint = getEndpoint(path, exchange.getRequestMethod());
            String response;

            switch (endpoint) {
                case GET_PRIORITIZED:
                    if (taskManager.getPrioritizedTasks().isEmpty()) {
                        sendText(exchange, "Список приоритетных задач пуст");
                    }
                    response = gson.toJson(taskManager.getPrioritizedTasks());
                    sendText(exchange, response);
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
