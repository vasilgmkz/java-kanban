package model;

import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Test.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import service.HistoryManager;
import service.InMemoryHistoryManager;
import service.InMemoryTaskManager;
import service.TaskManager;
import java.util.ArrayList;


@DisplayName("Эпик")
class EpicTest {

    @Test
    @DisplayName("эпик должен совпадать со своей копией")
    void shouldEqualsWithCopy() {
        Epic epic = new Epic("name", "name");
        Epic epic1 = new Epic("name", "name");
        assertEqualsTask(epic, epic1, "Эпики должны совпадать");
    }
    private static void assertEqualsTask (Task epic, Task epic1, String message) {
        assertEquals(epic.getId(), epic1.getId(), message + ", id");
        assertEquals(epic.getName(), epic1.getName(), message + ", name");
        assertEquals(epic.getStatus(), epic1.getStatus(), message + ", status");
        assertEquals(epic.getDescription(), epic1.getDescription(), message + ", description");
    }
}