package service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("Менеджер")
class ManagersTest {
    @DisplayName("Создание менеджера")
    @Test
    void shouldGetDefault() {
        assertNotNull(Managers.getDefault(), "должно быть не нулевое значение");
    }
}