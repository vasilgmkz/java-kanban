package handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import converter.DurationAdapter;
import converter.LDT_Adapter;
import exception.NotFoundException;
import service.TaskManager;

import javax.xml.datatype.Duration;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;

    enum Endpoint {GET_TASKS, GET_TASK_ID, POST_TASK, DELETE_TASK, UNKNOWN}

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

                break;
            }
            case DELETE_TASK: {

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
            if (pathParts.length == 2) {
                return Endpoint.DELETE_TASK;
            }
        }
        return Endpoint.UNKNOWN;
    }

    private void getTasks(HttpExchange taskHandler) throws IOException {
        try (taskHandler) {
            try {
                LDT_Adapter ldtAdapter = new LDT_Adapter();
                DurationAdapter durationAdapter = new DurationAdapter();
                GsonBuilder gsonBuilder = new GsonBuilder();
                gsonBuilder.setPrettyPrinting().registerTypeAdapter(LocalDateTime.class, ldtAdapter).registerTypeAdapter(Duration.class, durationAdapter);
                Gson gson = gsonBuilder.create();
                String response = gson.toJson(taskManager.getAllTask());
                sendText(taskHandler, response, 200);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void getTaskId(HttpExchange taskHandler) throws IOException {
        try (taskHandler) {
            try {
                Optional<Integer> postIdOpt = getPostId(taskHandler);
                if (postIdOpt.isEmpty()) {
                    sendText(taskHandler, "Некорректный идентификатор поста", 404);
                    return;
                }
                int postId = postIdOpt.get();
                try {
                    LDT_Adapter ldtAdapter = new LDT_Adapter();
                    DurationAdapter durationAdapter = new DurationAdapter();
                    GsonBuilder gsonBuilder = new GsonBuilder();
                    gsonBuilder.setPrettyPrinting().registerTypeAdapter(Duration.class, durationAdapter).registerTypeAdapter(LocalDateTime.class, ldtAdapter);
                    Gson gson = gsonBuilder.create();
                    String response = gson.toJson(taskManager.getTask(postId));
                    sendText(taskHandler, response, 200);
                } catch (NotFoundException e) {
                    sendText(taskHandler, "Такой задачи нет", 404);
                }
            } catch (Exception e) {
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
