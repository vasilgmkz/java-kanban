package service;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Менеджер историй")
class InMemoryHistoryManagerTest {
    InMemoryHistoryManager inMemoryHistoryManager;
    Task task;
    Epic epic;
    SubTask subTask;
    SubTask subTask1;
    @BeforeEach
    void init() {
        inMemoryHistoryManager = new InMemoryHistoryManager();
        task = new Task("task", Status.NEW, "task");
        epic = new Epic("epic", "epic");
        subTask = new SubTask("subTask", Status.NEW, "subTask", epic);
    }

    @DisplayName("Добавление задач")
    @Test
    void shouldAdd() {
        for (int i = 0; i < 15; i++) {
            inMemoryHistoryManager.add(task);
        }
        inMemoryHistoryManager.add(epic);
        inMemoryHistoryManager.add(subTask);
        inMemoryHistoryManager.add(subTask1);
        assertEquals(10, inMemoryHistoryManager.getHistory().size(), "размер должен быть равен 10");
        assertEquals("task", inMemoryHistoryManager.getHistory().get(7).getName(), "7 должен быть task");
        assertEquals("epic", inMemoryHistoryManager.getHistory().get(8).getName(), "8 должен быть epic");
        assertEquals("subTask", inMemoryHistoryManager.getHistory().get(9).getName(), "9 должен быть subTask");
    }

    @DisplayName("Получение истории задач")
    @Test
    void shouldGetAll() {
        inMemoryHistoryManager.add(task);
        inMemoryHistoryManager.add(epic);
        inMemoryHistoryManager.add(subTask);
        assertEquals(3, inMemoryHistoryManager.getHistory().size(), "должно быть 3 задачи");
    }
}