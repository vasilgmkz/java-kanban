package handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import converter.DurationAdapter;
import converter.LDT_Adapter;
import service.TaskManager;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {

    private final TaskManager taskManager;

    public HistoryHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    enum Endpoint {GET, UNKNOWN}

    @Override
    public void handle(HttpExchange historyHandler) throws IOException {
        try (historyHandler) {
            try {
                Endpoint endpoint = getEndpoint(historyHandler.getRequestURI().getPath(), historyHandler.getRequestMethod());
                if (endpoint == Endpoint.UNKNOWN) {
                    sendText(historyHandler, "Такого эндпоинта не существует", 400);
                    return;
                }
                Gson gson = getGson();
                String response = gson.toJson(taskManager.getHistory());
                sendText(historyHandler, response, 200);
            } catch (Exception e) {
                e.printStackTrace();
                sendText(historyHandler, "Internal Server Error", 500);
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

    private Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] pathParts = requestPath.split("/");

        if (requestMethod.equals("GET") && pathParts.length == 2) {
            return Endpoint.GET;
        }
        return Endpoint.UNKNOWN;
    }
}
