package service;

import com.google.gson.Gson;
import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HttpTaskServerTest {
    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer server = new HttpTaskServer(manager);
    Gson gson = server.getGson();

    HttpTaskServerTest() throws IOException {
    }

    @BeforeEach
    public void setUp() {
        manager.clearTask();
        manager.clearSubTask();
        manager.clearEpic();
        server.start();
    }

    @AfterEach
    public void shutDown() {
        server.stop();
    }

    @DisplayName("Заппрос добавления задачи")
    @Test
    public void testAddTask() throws IOException, InterruptedException {
        Task task = new Task("Task1", Status.NEW, "Task1", LocalDateTime.now(), Duration.ofMinutes(15));
        String taskJson = gson.toJson(task);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Код должен быть равен 201");
        assertEquals(1, manager.getTask(1).getId(), "ID должен быть равен 1");
    }

    @DisplayName("Запрос добавления эпика и подзадачи")
    @Test
    public void testAddEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic1", "Epic1");
        String epicJson = gson.toJson(epic);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Код должен быть равен 201");
        assertEquals(1, manager.getEpic(1).getId(), "ID должен быть равен 1");
        SubTask subTask = new SubTask("Subtask1", Status.IN_PROGRESS, "Subtask1", manager.getEpic(1), LocalDateTime.now(), Duration.ofMinutes(15));
        String subTaskJson = gson.toJson(subTask);
        URI urls = URI.create("http://localhost:8080/subtasks");
        HttpRequest requests = HttpRequest.newBuilder().uri(urls).POST(HttpRequest.BodyPublishers.ofString(subTaskJson)).build();
        HttpResponse<String> responses = client.send(requests, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, responses.statusCode(), "Код должен быть равен 201");
        assertEquals(2, manager.getSubTask(2).getId(), "ID должен быть равен 2");
    }
}