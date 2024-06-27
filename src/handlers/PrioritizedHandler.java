package handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import converter.DurationAdapter;
import converter.localDateTimeAdapter;
import service.TaskManager;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

public class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;

    public PrioritizedHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    enum Endpoint {GET, UNKNOWN}

    ;

    @Override
    public void handle(HttpExchange prioritizedHandler) throws IOException {
        try (prioritizedHandler) {
            try {
                Endpoint endpoint = getEndpoint(prioritizedHandler.getRequestURI().getPath(), prioritizedHandler.getRequestMethod());
                if (endpoint == Endpoint.UNKNOWN) {
                    sendText(prioritizedHandler, "Такого эндпоинта не существует", 400);
                    return;
                }
                Gson gson = getGson();
                String response = gson.toJson(taskManager.getPrioritizedTasks());
                sendText(prioritizedHandler, response, 200);
            } catch (Exception e) {
                e.printStackTrace();
                sendText(prioritizedHandler, "Internal Server Error", 500);
            }
        }
    }

    private Gson getGson() {
        localDateTimeAdapter ldtAdapter = new localDateTimeAdapter();
        DurationAdapter durationAdapter = new DurationAdapter();
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setPrettyPrinting().registerTypeAdapter(LocalDateTime.class, ldtAdapter).registerTypeAdapter(Duration.class, durationAdapter).serializeNulls();
        return gsonBuilder.create();
    }

    private Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] pathParts = requestPath.split("/");

        if (requestMethod.equals("GET") && pathParts.length == 2) {
            return Endpoint.GET;
        }
        return Endpoint.UNKNOWN;
    }
}
