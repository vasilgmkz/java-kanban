package model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Задача")
class TaskTest {
    @Test
    @DisplayName("задача должна совпадать со своей копией")
    void shouldEqualsWithCopy() {
        Task task = new Task("Task", Status.NEW, "subTask");
        Task task1 = new Task("Task", Status.NEW, "subTask");
        assertEqualsTask(task, task1, "задачи должны совпадать");
    }
    private static void assertEqualsTask (Task task, Task task1, String message) {
        assertEquals(task.getId(), task1.getId(), message + ", id");
        assertEquals(task.getName(), task1.getName(), message + ", name");
        assertEquals(task.getStatus(), task1.getStatus(), message + ", status");
        assertEquals(task.getDescription(), task1.getDescription(), message + ", description");
    }
}