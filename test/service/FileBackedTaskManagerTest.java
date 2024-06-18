package service;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Менеджер задач с сохранением")
class FileBackedTaskManagerTest {
    Path tempFile;
    TaskManager taskManager;

    Task task;
    Epic epic;
    SubTask subTask;

    @BeforeEach
    void init() {
        try {
            tempFile = Files.createTempFile("file", ".csv");
            taskManager = new FileBackedTaskManager(tempFile.toString());
            task = taskManager.createTask(new Task("task", Status.NEW, "task", LocalDateTime.parse("2024-06-12T20:30"), Duration.ofMinutes(15))); //1
            epic = taskManager.createEpic(new Epic("epic", "epic")); //2
            subTask = taskManager.createSubTask(new SubTask("subTask", Status.NEW, "subTask", epic, LocalDateTime.parse("2024-06-12T20:50"), Duration.ofMinutes(15)));//3
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @DisplayName("Чтение задач из файла")
    @Test
    void shouldAdd() {
        List<String> list = new ArrayList<>();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(tempFile.toString(), StandardCharsets.UTF_8))) {
            while (bufferedReader.ready()) {
                list.add(bufferedReader.readLine());
            }
            assertEquals(list.get(0), "id,type,name,status,description,epic,startTime,duration", "должны быть равны");
            assertEquals(list.get(1), "1,TASK,task,NEW,task,null,2024-06-12T20:30,15", "должны быть равны");
            assertEquals(list.get(2), "2,EPIC,epic,NEW,epic,null,2024-06-12T20:50,15", "должны быть равны");
            assertEquals(list.get(3), "3,SUBTASK,subTask,NEW,subTask,2,2024-06-12T20:50,15", "должны быть равны");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}