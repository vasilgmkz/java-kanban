package handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import converter.DurationAdapter;
import converter.LocalDateTimeAdapter;
import exception.NotFoundException;
import exception.ValidationException;
import model.Task;
import service.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;

    enum Endpoint {
        GET_TASKS,
        GET_TASK_ID,
        POST_TASK,
        DELETE_TASK,
        UNKNOWN
    }

    public TaskHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange taskHandler) throws IOException {
        Endpoint endpoint = getEndpoint(taskHandler.getRequestURI().getPath(), taskHandler.getRequestMethod());

        switch (endpoint) {
            case GET_TASKS: {
                getTasks(taskHandler);
                break;
            }
            case GET_TASK_ID: {
                getTaskId(taskHandler);
                break;
            }
            case POST_TASK: {
                postTask(taskHandler);
                break;
            }
            case DELETE_TASK: {
                deleteTask(taskHandler);
                break;
            }
            default:
                sendText(taskHandler, "Такого эндпоинта не существует", 400);
        }
    }


    private Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] pathParts = requestPath.split("/");

        if (requestMethod.equals("GET")) {
            if (pathParts.length == 2) {
                return Endpoint.GET_TASKS;
            }
            if (pathParts.length == 3) {
                return Endpoint.GET_TASK_ID;
            }
        }
        if (requestMethod.equals("POST")) {
            if (pathParts.length == 2) {
                return Endpoint.POST_TASK;
            }
        }
        if (requestMethod.equals("DELETE")) {
            if (pathParts.length == 3) {
                return Endpoint.DELETE_TASK;
            }
        }
        return Endpoint.UNKNOWN;
    }

    private void postTask(HttpExchange taskHandler) throws IOException {
        try (taskHandler) {
            try {
                Optional optional = parseComment(taskHandler.getRequestBody());
                GsonBuilder gsonBuilder = new GsonBuilder();
                DurationAdapter durationAdapter = new DurationAdapter();
                LocalDateTimeAdapter ldtAdapter = new LocalDateTimeAdapter();
                gsonBuilder.setPrettyPrinting().registerTypeAdapter(LocalDateTime.class, ldtAdapter).registerTypeAdapter(Duration.class, durationAdapter).serializeNulls();
                Gson gson = gsonBuilder.create();
                if (optional.isEmpty()) {
                    sendText(taskHandler, "Запрос не содержит задачу", 400);
                    return;
                }
                Task task = (Task) optional.get();
                if (task.getId() == 0) {
                    Task createTask = taskManager.createTask(new Task(task.getName(), task.getStatus(), task.getDescription(), task.getStartTime(), task.getDuration()));
                    String response = gson.toJson(createTask);
                    sendText(taskHandler, response, 201);
                    return;
                }
                if (taskManager.getAllTaskId().contains(task.getId())) {
                    taskManager.updateTask(task);
                    String response = gson.toJson(task);
                    sendText(taskHandler, response, 201);
                    return;
                }
                sendText(taskHandler, "Задачи с таким id не найдено", 400);
            } catch (ValidationException e) {
                sendText(taskHandler, "Задача пересекается с существующей задачей", 406);
            } catch (Exception e) {
                sendText(taskHandler, "Internal Server Error", 500);
                e.printStackTrace();
            }
        }
    }

    private Optional<Task> parseComment(InputStream bodyInputStream) throws IOException {

        String body = new String(bodyInputStream.readAllBytes(), StandardCharsets.UTF_8);
        if (body.isBlank()) {
            return Optional.empty();
        }
        GsonBuilder gsonBuilder = new GsonBuilder();
        DurationAdapter durationAdapter = new DurationAdapter();
        LocalDateTimeAdapter ldtAdapter = new LocalDateTimeAdapter();
        gsonBuilder.setPrettyPrinting().registerTypeAdapter(LocalDateTime.class, ldtAdapter).registerTypeAdapter(Duration.class, durationAdapter).serializeNulls();
        Gson gson = gsonBuilder.create();
        Task task = gson.fromJson(body, Task.class);
        return Optional.of(task);
    }

    private void getTasks(HttpExchange taskHandler) throws IOException {
        try (taskHandler) {
            try {
                LocalDateTimeAdapter ldtAdapter = new LocalDateTimeAdapter();
                DurationAdapter durationAdapter = new DurationAdapter();
                GsonBuilder gsonBuilder = new GsonBuilder();
                gsonBuilder.setPrettyPrinting().registerTypeAdapter(LocalDateTime.class, ldtAdapter).registerTypeAdapter(Duration.class, durationAdapter).serializeNulls();
                Gson gson = gsonBuilder.create();
                String response = gson.toJson(taskManager.getAllTask());
                sendText(taskHandler, response, 200);
            } catch (Exception e) {
                sendText(taskHandler, "Internal Server Error", 500);
                e.printStackTrace();
            }
        }
    }

    private void getTaskId(HttpExchange taskHandler) throws IOException {
        try (taskHandler) {
            try {
                Optional<Integer> postIdOpt = getPostId(taskHandler);
                if (postIdOpt.isEmpty()) {
                    sendText(taskHandler, "Некорректный идентификатор id", 404);
                    return;
                }
                int postId = postIdOpt.get();
                try {
                    LocalDateTimeAdapter ldtAdapter = new LocalDateTimeAdapter();
                    DurationAdapter durationAdapter = new DurationAdapter();
                    GsonBuilder gsonBuilder = new GsonBuilder();
                    gsonBuilder.setPrettyPrinting().registerTypeAdapter(Duration.class, durationAdapter).registerTypeAdapter(LocalDateTime.class, ldtAdapter).serializeNulls();
                    Gson gson = gsonBuilder.create();
                    String response = gson.toJson(taskManager.getTask(postId));
                    sendText(taskHandler, response, 200);
                } catch (NotFoundException e) {
                    sendText(taskHandler, "Задача с таким id отсутствует", 404);
                }
            } catch (Exception e) {
                sendText(taskHandler, "Internal Server Error", 500);
                e.printStackTrace();
            }
        }
    }

    private void deleteTask(HttpExchange taskHandler) throws IOException {
        try (taskHandler) {
            try {
                String id = taskHandler.getRequestURI().getPath().split("/")[2];
                if (taskManager.getAllTaskId().contains(Integer.parseInt(id))) {
                    taskManager.deleteTask(Integer.parseInt(id));
                    sendText(taskHandler, "Задача удалена", 200);
                    return;
                }
                sendText(taskHandler, "Задача с таким id отсутствует", 404);
            } catch (Exception e) {
                sendText(taskHandler, "Internal Server Error", 500);
                e.printStackTrace();
            }
        }
    }

    private Optional<Integer> getPostId(HttpExchange taskHandler) {
        String[] pathParts = taskHandler.getRequestURI().getPath().split("/");
        try {
            return Optional.of(Integer.parseInt(pathParts[2]));
        } catch (NumberFormatException exception) {
            return Optional.empty();
        }
    }
}
