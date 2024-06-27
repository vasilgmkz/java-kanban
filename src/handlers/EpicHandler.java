package handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import converter.DurationAdapter;
import converter.LocalDateTimeAdapter;
import model.Epic;
import model.Status;
import service.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

public class EpicHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;

    public EpicHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    enum Endpoint {GET_EPICS, GET_EPIC_ID, GET_EPIC_ID_SUBTASK, POST_EPIC, DELETE_EPIC, UNKNOWN}

    @Override
    public void handle(HttpExchange epicHandler) throws IOException {
        Endpoint endpoint = getEndpoint(epicHandler.getRequestURI().getPath(), epicHandler.getRequestMethod());
        switch (endpoint) {
            case GET_EPICS: {
                getEpics(epicHandler);
                break;
            }
            case GET_EPIC_ID: {
                getEpicId(epicHandler);
                break;
            }
            case GET_EPIC_ID_SUBTASK: {
                getEpicIdSubTask(epicHandler);
                break;
            }
            case POST_EPIC: {
                postEpic(epicHandler);
                break;
            }
            case DELETE_EPIC: {
                deleteEpic(epicHandler);
                break;
            }
            default:
                sendText(epicHandler, "Такого эндпоинта не существует", 400);
        }
    }

    private void getEpics(HttpExchange epicHandler) throws IOException {
        try (epicHandler) {
            try {
                Gson gson = getGson();
                String response = gson.toJson(taskManager.getAllEpic());
                sendText(epicHandler, response, 200);
            } catch (Exception e) {
                e.printStackTrace();
                sendText(epicHandler, "Internal Server Error", 500);
            }
        }
    }

    private void getEpicId(HttpExchange epicHandler) throws IOException {
        try (epicHandler) {
            try {
                Optional<Integer> epicId = getEpicIdOptional(epicHandler);
                if (epicId.isEmpty()) {
                    sendText(epicHandler, "Некорректный идентификатор id", 404);
                    return;
                }
                int id = epicId.get();
                Gson gson = getGson();
                String response = gson.toJson(taskManager.getEpic(id));
                if (response.equals("null")) {
                    sendText(epicHandler, "Эпик с таким id отсутствует", 404);
                } else {
                    sendText(epicHandler, response, 200);
                }
            } catch (Exception e) {
                e.printStackTrace();
                sendText(epicHandler, "Internal Server Error", 500);
            }
        }
    }

    private void getEpicIdSubTask(HttpExchange epicHandler) throws IOException {
        try (epicHandler) {
            try {
                Optional<Integer> epicId = getEpicIdOptional(epicHandler);
                if (epicId.isEmpty()) {
                    sendText(epicHandler, "Некорректный идентификатор id", 404);
                    return;
                }
                int id = epicId.get();
                Gson gson = getGson();
                String response = gson.toJson(taskManager.getEpic(id).getSubTasks());
                sendText(epicHandler, response, 200);
            } catch (NullPointerException e) {
                sendText(epicHandler, "Эпик с таким id отсутствует", 404);
            } catch (Exception e) {
                e.printStackTrace();
                sendText(epicHandler, "Internal Server Error", 500);
            }
        }
    }

    private void postEpic(HttpExchange epicHandler) throws IOException {
        try (epicHandler) {
            try {
                Optional<Epic> epicOptional = parseEpic(epicHandler.getRequestBody());
                Gson gson = getGson();
                if (epicOptional.isEmpty()) {
                    sendText(epicHandler, "Запрос не содержит эпик", 400);
                    return;
                }
                Epic epic = epicOptional.get();
                if (epic.getId() != 0 || epic.getStatus() != Status.NEW || epic.getStartTime() != null || epic.getDuration() != null) {
                    sendText(epicHandler, "Недопустимое заполнение полей", 400);
                    return;
                }
                System.out.println(epic);
                Epic createEpic = taskManager.createEpic(new Epic(epic.getName(), epic.getDescription()));
                String response = gson.toJson(createEpic);
                sendText(epicHandler, response, 201);
            } catch (Exception e) {
                e.printStackTrace();
                sendText(epicHandler, "Internal Server Error", 500);
            }
        }
    }

    private void deleteEpic(HttpExchange epicHandler) throws IOException {
        try (epicHandler) {
            try {
                Optional<Integer> epicId = getEpicIdOptional(epicHandler);
                if (epicId.isEmpty()) {
                    sendText(epicHandler, "Некорректный идентификатор id", 404);
                    return;
                }
                int id = epicId.get();
                taskManager.deleteEpic(id);
                sendText(epicHandler, "Эпик удален", 200);
            } catch (NullPointerException e) {
                sendText(epicHandler, "Эпик с таким id отсутствует", 404);
            } catch (Exception e) {
                e.printStackTrace();
                sendText(epicHandler, "Internal Server Error", 500);
            }
        }
    }

    private Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] pathParts = requestPath.split("/");

        if (requestMethod.equals("GET")) {
            if (pathParts.length == 2) {
                return Endpoint.GET_EPICS;
            }
            if (pathParts.length == 3) {
                return Endpoint.GET_EPIC_ID;
            }
            if (pathParts.length == 4 && pathParts[3].equals("subtasks")) {
                return Endpoint.GET_EPIC_ID_SUBTASK;
            }
        }
        if (requestMethod.equals("POST")) {
            if (pathParts.length == 2) {
                return Endpoint.POST_EPIC;
            }
        }
        if (requestMethod.equals("DELETE")) {
            if (pathParts.length == 3) {
                return Endpoint.DELETE_EPIC;
            }
        }
        return Endpoint.UNKNOWN;
    }

    private Gson getGson() {
        LocalDateTimeAdapter ldtAdapter = new LocalDateTimeAdapter();
        DurationAdapter durationAdapter = new DurationAdapter();
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setPrettyPrinting().registerTypeAdapter(LocalDateTime.class, ldtAdapter).registerTypeAdapter(Duration.class, durationAdapter).serializeNulls();
        return gsonBuilder.create();
    }

    private Optional<Integer> getEpicIdOptional(HttpExchange epicHandler) {
        String[] pathParts = epicHandler.getRequestURI().getPath().split("/");
        try {
            return Optional.of(Integer.parseInt(pathParts[2]));
        } catch (NumberFormatException exception) {
            return Optional.empty();
        }
    }

    private Optional<Epic> parseEpic(InputStream bodyInputStream) throws IOException {
        try (bodyInputStream) {
            String body = new String(bodyInputStream.readAllBytes(), StandardCharsets.UTF_8);
            if (body.isBlank()) {
                return Optional.empty();
            }
            Gson gson = getGson();
            Epic epic = gson.fromJson(body, Epic.class);
            return Optional.of(epic);
        }
    }
}
