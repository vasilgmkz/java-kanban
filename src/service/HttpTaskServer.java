package service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import converter.DurationAdapter;
import converter.LocalDateTimeAdapter;
import handlers.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {
    private static final int PORT = 8080;
    TaskManager taskManager;
    HttpServer httpServer;

    HttpTaskServer(TaskManager taskManager) throws IOException {
        this.taskManager = taskManager;
        httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
    }
    public static void main(String[] args) throws IOException {
        HttpTaskServer server = new HttpTaskServer(Managers.getDefault());
        server.start();
    }

    public void start() {
        httpServer.createContext("/tasks", new TaskHandler(taskManager));
        httpServer.createContext("/subtasks", new SubtasksHandler(taskManager));
        httpServer.createContext("/epics", new EpicHandler(taskManager));
        httpServer.createContext("/history", new HistoryHandler(taskManager));
        httpServer.createContext("/prioritized", new PrioritizedHandler(taskManager));
        httpServer.start();
    }

    public void stop() {
        httpServer.stop(50);
    }

    public Gson getGson() {
        LocalDateTimeAdapter ldtAdapter = new LocalDateTimeAdapter();
        DurationAdapter durationAdapter = new DurationAdapter();
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setPrettyPrinting().registerTypeAdapter(LocalDateTime.class, ldtAdapter).registerTypeAdapter(Duration.class, durationAdapter).serializeNulls();
        return gsonBuilder.create();
    }
}
