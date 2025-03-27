package server;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class BaseHttpHandler {
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    //Метод определения конечной точки
    protected Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] splitPath = requestPath.split("/");

        switch (splitPath[1]) {
            case "tasks":
                if (requestMethod.equals("GET")) {
                    if (splitPath.length == 3) {
                        return Endpoint.GET_TASKS_ID;
                    } else if (splitPath.length == 2) {
                        return Endpoint.GET_TASKS;
                    } else {
                        return Endpoint.UNKNOWN;
                    }
                }
                if (requestMethod.equals("POST") && splitPath.length == 2) {
                    return Endpoint.POST_TASKS;
                }
                if (requestMethod.equals("DELETE") && splitPath.length == 3) {
                    return Endpoint.DELETE_TASKS_ID;
                }
                return Endpoint.UNKNOWN;
            case "subtasks":
                if (requestMethod.equals("GET")) {
                    if (splitPath.length == 3) {
                        return Endpoint.GET_SUBTASKS_ID;
                    } else if (splitPath.length == 2) {
                        return Endpoint.GET_SUBTASKS;
                    } else {
                        return Endpoint.UNKNOWN;
                    }
                }
                if (requestMethod.equals("POST") && splitPath.length == 2) {
                    return Endpoint.POST_SUBTASKS;
                }
                if (requestMethod.equals("DELETE") && splitPath.length == 3) {
                    return Endpoint.DELETE_SUBTASKS_ID;
                }
                return Endpoint.UNKNOWN;
            case "epics":
                if (requestMethod.equals("GET")) {
                    if (splitPath.length == 3) {
                        return Endpoint.GET_EPICS_ID;
                    } else if (splitPath.length == 2) {
                        return Endpoint.GET_EPICS;
                    } else {
                        return Endpoint.UNKNOWN;
                    }
                }
                if (requestMethod.equals("POST") && splitPath.length == 2) {
                    return Endpoint.POST_EPICS;
                }
                if (requestMethod.equals("DELETE") && splitPath.length == 3) {
                    return Endpoint.DELETE_EPICS_ID;
                }
                return Endpoint.UNKNOWN;
            case "history":
                return Endpoint.GET_HISTORY;
            case "prioritized":
                return Endpoint.GET_PRIORITIZED;
            default:
                return Endpoint.UNKNOWN;
        }
    }

    protected int idParser(HttpExchange exchange, String idInString) throws IOException {
        try {
            return Integer.parseInt(idInString);
        } catch (NumberFormatException e) {
            sendBadRequest(exchange, "Некорректный формат id задачи");
            return -1;
        }
    }

    protected Optional<String> getBodyFromTheClient(HttpExchange httpExchange) throws IOException {
        InputStream inputStream = httpExchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
        inputStream.close();

        if (body.isBlank()) {
            return Optional.empty();
        }
        return Optional.of(body);
    }

    protected void sendText(HttpExchange httpExchange, String text) throws IOException {
        byte[] resp = text.getBytes(DEFAULT_CHARSET);
        httpExchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        httpExchange.sendResponseHeaders(200, resp.length);
        httpExchange.getResponseBody().write(resp);
        httpExchange.close();
    }

    protected void sendNotFound(HttpExchange httpExchange, String text) throws IOException {
        byte[] resp = text.getBytes(DEFAULT_CHARSET);
        httpExchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        httpExchange.sendResponseHeaders(404, resp.length);
        httpExchange.getResponseBody().write(resp);
        httpExchange.close();
    }

    protected void sendHasInteractions(HttpExchange httpExchange, String text) throws IOException {
        byte[] resp = text.getBytes(DEFAULT_CHARSET);
        httpExchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        httpExchange.sendResponseHeaders(406, resp.length);
        httpExchange.getResponseBody().write(resp);
        httpExchange.close();
    }

    protected void sendBadRequest(HttpExchange httpExchange, String text) throws IOException {
        byte[] resp = text.getBytes(DEFAULT_CHARSET);
        httpExchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        httpExchange.sendResponseHeaders(400, resp.length);
        httpExchange.getResponseBody().write(resp);
        httpExchange.close();
    }

    protected void sendSuccess(HttpExchange httpExchange, String text) throws IOException {
        byte[] resp = text.getBytes(DEFAULT_CHARSET);
        httpExchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        httpExchange.sendResponseHeaders(201, resp.length);
        httpExchange.getResponseBody().write(resp);
        httpExchange.close();
    }


}
