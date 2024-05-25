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
    TaskManager taskManager;

    Task task;
    Epic epic;
    SubTask subTask;


    @BeforeEach
    void init() {
        taskManager = new InMemoryTaskManager();
        task = taskManager.createTask(new Task("task", Status.NEW, "task")); //1
        epic = taskManager.createEpic(new Epic("epic", "epic")); //2
        subTask = taskManager.createSubTask(new SubTask("subTask", Status.NEW, "subTask", epic)); //3
    }

    @DisplayName("Добавление задач")
    @Test
    void shouldAdd() {
        taskManager.getEpic(2);
        taskManager.getSubTask(3);
        taskManager.getTask(1);
        assertEquals(3, taskManager.getHistory().size(), "размер должен быть равен 3");
        assertEquals("task", taskManager.getHistory().get(2).getName(), "2 должен быть task");
        assertEquals("epic", taskManager.getHistory().get(0).getName(), "0 должен быть epic");
        assertEquals("subTask", taskManager.getHistory().get(1).getName(), "1 должен быть subTask");
    }

    @DisplayName("Получение истории задач")
    @Test
    void shouldGetAll() {
        taskManager.getEpic(2);
        taskManager.getSubTask(3);
        taskManager.getTask(1);
        assertEquals(3, taskManager.getHistory().size(), "должно быть 3 задачи");
    }

    @DisplayName("Удаление задач")
    @Test
    void shouldDeleteTask() {
        Task task1 = taskManager.createTask(new Task("task1", Status.NEW, "task1")); //4
        Epic epic1 = taskManager.createEpic(new Epic("epic1", "epic1")); //5
        SubTask subTask1 = taskManager.createSubTask(new SubTask("subTask1", Status.NEW, "subTask1", epic1)); //6
        taskManager.getEpic(2);
        taskManager.getSubTask(3);
        taskManager.getTask(1);
        taskManager.getEpic(5);
        taskManager.getSubTask(6);
        taskManager.getTask(4);
        assertEquals(6, taskManager.getHistory().size(), "размер должен быть равен 6");
        taskManager.deleteEpic(2);
        assertEquals(4, taskManager.getHistory().size(), "размер должен быть равен 4");
        assertEquals("task", taskManager.getHistory().getFirst().getName(), "имя должно быть subTask");
    }
}