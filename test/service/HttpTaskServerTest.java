package service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HttpTaskServerTest {
    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer server = new HttpTaskServer(manager);
    Gson gson = server.getGson();

    public static class ListTypeToken extends TypeToken<List<Task>> {
    }

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

    @DisplayName("Работа с задачами")
    @Test
    public void testAddTask() throws IOException, InterruptedException {
        //Добавление задачи
        Task task = new Task("Task1", Status.NEW, "Task1", LocalDateTime.now(), Duration.ofMinutes(15));
        String taskJson = gson.toJson(task);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Код должен быть равен 201");
        assertEquals(1, manager.getTask(1).getId(), "ID должен быть равен 1");
        //Обновление задачи
        Task taskUpdate = new Task(1, "Task1_update", Status.IN_PROGRESS, "Task1_update", LocalDateTime.now(), Duration.ofMinutes(20));
        String taskJsonUpdate = gson.toJson(taskUpdate);
        URI urlUpdate = URI.create("http://localhost:8080/tasks");
        HttpRequest requestUpdate = HttpRequest.newBuilder().uri(urlUpdate).POST(HttpRequest.BodyPublishers.ofString(taskJsonUpdate)).build();
        HttpResponse<String> responseUpdate = client.send(requestUpdate, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, responseUpdate.statusCode(), "Код должен быть равен 201");
        assertEquals("Task1_update", manager.getTask(1).getName(), "Имя должно быть Task1_update");
        assertEquals(Status.IN_PROGRESS, manager.getTask(1).getStatus(), "Статус должен быть IN_PROGRESS");
        //Получение задачи по id
        URI urlGet = URI.create("http://localhost:8080/tasks/1");
        HttpRequest requestGet = HttpRequest.newBuilder().uri(urlGet).GET().build();
        HttpResponse<String> responseGet = client.send(requestGet, HttpResponse.BodyHandlers.ofString());
        Task taskGet = gson.fromJson(responseGet.body(), Task.class);
        assertEquals(200, responseGet.statusCode(), "Код должен быть равен 200");
        assertEquals(20, taskGet.getDuration().toMinutes(), "Продолжительность должна быть равна 20");
        //Получение истории
        URI urlHistory = URI.create("http://localhost:8080/history");
        HttpRequest requestHistory = HttpRequest.newBuilder().uri(urlHistory).GET().build();
        HttpResponse<String> responseHistory = client.send(requestHistory, HttpResponse.BodyHandlers.ofString());
        String history = responseHistory.body();
        List<Task> taskHistory = gson.fromJson(history, new ListTypeToken().getType());
        assertEquals("Task1_update", taskHistory.get(0).getName(), "Имя должно быть Task1_update");
        //Получение приоритетного списка
        URI urlPrioritized = URI.create("http://localhost:8080/prioritized");
        HttpRequest requestPrioritized = HttpRequest.newBuilder().uri(urlPrioritized).GET().build();
        HttpResponse<String> responsePrioritized = client.send(requestPrioritized, HttpResponse.BodyHandlers.ofString());
        String prioritized = responsePrioritized.body();
        List<Task> taskPrioritized = gson.fromJson(prioritized, new ListTypeToken().getType());
        assertEquals("Task1_update", taskPrioritized.get(0).getName(), "Имя должно быть Task1_update");
        //Удаление задачи по id
        URI urlDelete = URI.create("http://localhost:8080/tasks/1");
        HttpRequest requestDelete = HttpRequest.newBuilder().uri(urlDelete).DELETE().build();
        HttpResponse<String> responseDelete = client.send(requestDelete, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, responseDelete.statusCode(), "Код должен быть равен 200");
        assertEquals(0, manager.getAllTask().size(), "Размер должен быть равен 0");
    }

    @DisplayName("Работа с подзадачами и эпиками")
    @Test
    public void testAddEpic() throws IOException, InterruptedException {
        //Добавление эпика
        Epic epic = new Epic("Epic1", "Epic1");
        String epicJson = gson.toJson(epic);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Код должен быть равен 201");
        assertEquals(1, manager.getEpic(1).getId(), "ID должен быть равен 1");
        //Добавление подзадачи
        SubTask subTask = new SubTask("Subtask1", Status.IN_PROGRESS, "Subtask1", manager.getEpic(1), LocalDateTime.now(), Duration.ofMinutes(15));
        String subTaskJson = gson.toJson(subTask);
        URI urls = URI.create("http://localhost:8080/subtasks");
        HttpRequest requests = HttpRequest.newBuilder().uri(urls).POST(HttpRequest.BodyPublishers.ofString(subTaskJson)).build();
        HttpResponse<String> responses = client.send(requests, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, responses.statusCode(), "Код должен быть равен 201");
        assertEquals(2, manager.getSubTask(2).getId(), "ID должен быть равен 2");
        //Получение эпика по id
        URI urlGet = URI.create("http://localhost:8080/epics/1");
        HttpRequest requestGet = HttpRequest.newBuilder().uri(urlGet).GET().build();
        HttpResponse<String> responseGet = client.send(requestGet, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, responseGet.statusCode(), "Код должен быть равен 200");
        Epic epics = gson.fromJson(responseGet.body(), Epic.class);
        assertEquals(15, epics.getDuration().toMinutes(), "Продолжительность должна быть равна 15");
        //Получение подзадачи по id
        URI urlGetSubTask = URI.create("http://localhost:8080/subtasks/2");
        HttpRequest requestGetSubtask = HttpRequest.newBuilder().uri(urlGetSubTask).GET().build();
        HttpResponse<String> responseGetSubTask = client.send(requestGetSubtask, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, responseGetSubTask.statusCode(), "Код должен быть равен 200");
        SubTask subTasks = gson.fromJson(responseGetSubTask.body(), SubTask.class);
        assertEquals("Subtask1", subTasks.getName(), "Имя должно быть Subtask1");
        //Получение приоритетного списка
        URI urlPrioritized = URI.create("http://localhost:8080/prioritized");
        HttpRequest requestPrioritized = HttpRequest.newBuilder().uri(urlPrioritized).GET().build();
        HttpResponse<String> responsePrioritized = client.send(requestPrioritized, HttpResponse.BodyHandlers.ofString());
        String prioritized = responsePrioritized.body();
        List<Task> taskPrioritized = gson.fromJson(prioritized, new ListTypeToken().getType());
        assertEquals("Subtask1", taskPrioritized.get(0).getName(), "Имя должно быть Subtask1");
        //Удаление подзадачи
        assertEquals(1, manager.getAllSubTask().size(), "Размер должен быть равен 1");
        URI urlDelete = URI.create("http://localhost:8080/subtasks/2");
        HttpRequest requestDelete = HttpRequest.newBuilder().uri(urlDelete).DELETE().build();
        HttpResponse<String> responseDelete = client.send(requestDelete, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, responseDelete.statusCode(), "Код должен быть равен 200");
        assertEquals(0, manager.getAllSubTask().size(), "Размер должен быть равен 0");
        //Удаление эпика
        assertEquals(1, manager.getAllEpic().size(), "Размер должен быть равен 1");
        URI urlDeleteEpic = URI.create("http://localhost:8080/epics/1");
        HttpRequest requestDeleteEpic = HttpRequest.newBuilder().uri(urlDeleteEpic).DELETE().build();
        HttpResponse<String> responseDeleteEpic = client.send(requestDeleteEpic, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, responseDelete.statusCode(), "Код должен быть равен 200");
        assertEquals(0, manager.getAllEpic().size(), "Размер должен быть равен 0");
    }
}