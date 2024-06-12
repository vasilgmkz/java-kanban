package model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Подзадача")
class SubTaskTest {
    @Test
    @DisplayName("подзадача должна совпадать со своей копией")
    void shouldEqualsWithCopy() {
        Epic epic = new Epic("name", "name");
        SubTask subTask = new SubTask("subTask", Status.NEW, "subTask", epic, LocalDateTime.parse("2024-06-12T20:30"), Duration.ofMinutes(15));
        SubTask subTask1 = new SubTask("subTask", Status.NEW, "subTask", epic, LocalDateTime.parse("2024-06-12T20:30"), Duration.ofMinutes(15));
        assertEqualsTask(subTask, subTask1, "подзадачи должны совпадать");
    }

    private static void assertEqualsTask(Task subtask, Task subTask1, String message) {
        assertEquals(subtask.getId(), subTask1.getId(), message + ", id");
        assertEquals(subtask.getName(), subTask1.getName(), message + ", name");
        assertEquals(subtask.getStatus(), subTask1.getStatus(), message + ", status");
        assertEquals(subtask.getDescription(), subTask1.getDescription(), message + ", description");
        assertEquals(subtask.getEpic().getId(), subTask1.getEpic().getId(), message + ", epic id");
        assertEquals(subtask.getStartTime(), subTask1.getStartTime(), message + ", startTime");
        assertEquals(subtask.getDuration(), subTask1.getDuration(), message + ", duration");
    }
}