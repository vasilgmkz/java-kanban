package handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import converter.DurationAdapter;
import converter.LDT_Adapter;
import exception.ValidationException;
import model.SubTask;
import service.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;


public class SubtasksHandler extends BaseHttpHandler implements HttpHandler {

    private final TaskManager taskManager;

    public SubtasksHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    enum Endpoint {GET_SUB_TASKS, GET_SUB_TASK_ID, POST_SUB_TASK, DELETE_SUB_TASK, UNKNOWN}

    @Override
    public void handle(HttpExchange subtasksHandler) throws IOException {
        Endpoint endpoint = getEndpoint(subtasksHandler.getRequestURI().getPath(), subtasksHandler.getRequestMethod());

        switch (endpoint) {
            case GET_SUB_TASKS: {
                getSubTasks(subtasksHandler);
                break;
            }
            case GET_SUB_TASK_ID: {
                getSubTaskId(subtasksHandler);
                break;
            }
            case POST_SUB_TASK: {
                postSubTask(subtasksHandler);
                break;
            }
            case DELETE_SUB_TASK: {
                deleteSubTask(subtasksHandler);
                break;
            }
            default:
                sendText(subtasksHandler, "Такого эндпоинта не существует", 400);
        }
    }

    private SubtasksHandler.Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] pathParts = requestPath.split("/");

        if (requestMethod.equals("GET")) {
            if (pathParts.length == 2) {
                return Endpoint.GET_SUB_TASKS;
            }
            if (pathParts.length == 3) {
                return Endpoint.GET_SUB_TASK_ID;
            }
        }
        if (requestMethod.equals("POST")) {
            if (pathParts.length == 2) {
                return Endpoint.POST_SUB_TASK;
            }
        }
        if (requestMethod.equals("DELETE")) {
            if (pathParts.length == 3) {
                return Endpoint.DELETE_SUB_TASK;
            }
        }
        return Endpoint.UNKNOWN;
    }

    private void getSubTasks(HttpExchange subtasksHandler) throws IOException {
        try (subtasksHandler) {
            try {
                Gson gson = getGson();
                String response = gson.toJson(taskManager.getAllSubTask());
                sendText(subtasksHandler, response, 200);
            } catch (Exception e) {
                e.printStackTrace();
                sendText(subtasksHandler, "Internal Server Error", 500);
            }
        }
    }

    private void getSubTaskId(HttpExchange subtasksHandler) throws IOException {
        try (subtasksHandler) {
            try {
                Optional<Integer> subTasksId = getSubTasksId(subtasksHandler);
                if (subTasksId.isEmpty()) {
                    sendText(subtasksHandler, "Некорректный идентификатор id", 404);
                    return;
                }
                int id = subTasksId.get();
                Gson gson = getGson();
                String response = gson.toJson(taskManager.getSubTask(id));
                if (response.equals("null")) {
                    sendText(subtasksHandler, "Задача с таким id отсутствует", 404);
                } else {
                    sendText(subtasksHandler, response, 200);
                }
            } catch (Exception e) {
                e.printStackTrace();
                sendText(subtasksHandler, "Internal Server Error", 500);
            }
        }
    }

    private void postSubTask(HttpExchange subtasksHandler) throws IOException {
        try (subtasksHandler) {
            try {
                Optional<SubTask> subTaskOptional = parseSubTask(subtasksHandler.getRequestBody());
                Gson gson = getGson();
                if (subTaskOptional.isEmpty()) {
                    sendText(subtasksHandler, "Запрос не содержит подзадачу", 400);
                    return;
                }
                SubTask subTask = subTaskOptional.get();
                if (!taskManager.getAllEpicId().contains(subTask.getEpicId())) {
                    sendText(subtasksHandler, "Отсутствует эпик указанный в подзадаче", 400);
                    return;
                }
                if (subTask.getId() == 0) {
                    SubTask create_subTask = taskManager.createSubTask(new SubTask(subTask.getName(), subTask.getStatus(), subTask.getDescription(), taskManager.getEpic(subTask.getEpicId()), subTask.getStartTime(), subTask.getDuration()));
                    if (create_subTask == null) {
                        throw new ValidationException(new StringBuilder());
                    }
                    create_subTask.setEpicId(subTask.getEpicId());
                    String response = gson.toJson(create_subTask);
                    sendText(subtasksHandler, response, 201);
                    return;
                }
                if (taskManager.getAllSubTaskId().contains(subTask.getId())) {
                    SubTask updateSubTask = taskManager.updateSubTask(subTask);
                    if (updateSubTask == null) {
                        throw new ValidationException(new StringBuilder());
                    }
                    String response = gson.toJson(subTask);
                    sendText(subtasksHandler, response, 201);
                    return;
                }
                sendText(subtasksHandler, "Подадачи с таким id не найдено", 400);
            } catch (ValidationException e) {
                sendText(subtasksHandler, "Подзадача пересекается с существующей задачей", 406);
            } catch (Exception e) {
                e.printStackTrace();
                sendText(subtasksHandler, "Internal Server Error", 500);
            }
        }
    }

    private void deleteSubTask(HttpExchange subtasksHandler) throws IOException {
        try (subtasksHandler) {
            try {
                String id = subtasksHandler.getRequestURI().getPath().split("/")[2];
                if (taskManager.getAllSubTaskId().contains(Integer.parseInt(id))) {
                    taskManager.deleteSubTask(taskManager.getSubTask(Integer.parseInt(id)));
                    sendText(subtasksHandler, "Подзадача удалена", 200);
                    return;
                }
                sendText(subtasksHandler, "Подадача с таким id отсутствует", 404);
            } catch (Exception e) {
                e.printStackTrace();
                sendText(subtasksHandler, "Internal Server Error", 500);
            }
        }
    }

    private Gson getGson() {
        LDT_Adapter ldtAdapter = new LDT_Adapter();
        DurationAdapter durationAdapter = new DurationAdapter();
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setPrettyPrinting().registerTypeAdapter(LocalDateTime.class, ldtAdapter).registerTypeAdapter(Duration.class, durationAdapter).serializeNulls();
        return gsonBuilder.create();
    }

    private Optional<Integer> getSubTasksId(HttpExchange subtasksHandler) {
        String[] pathParts = subtasksHandler.getRequestURI().getPath().split("/");
        try {
            return Optional.of(Integer.parseInt(pathParts[2]));
        } catch (NumberFormatException exception) {
            return Optional.empty();
        }
    }

    private Optional<SubTask> parseSubTask(InputStream bodyInputStream) throws IOException {
        try (bodyInputStream) {
            String body = new String(bodyInputStream.readAllBytes(), StandardCharsets.UTF_8);
            if (body.isBlank()) {
                return Optional.empty();
            }
            Gson gson = getGson();
            SubTask subTask = gson.fromJson(body, SubTask.class);
            return Optional.of(subTask);
        }
    }
}
