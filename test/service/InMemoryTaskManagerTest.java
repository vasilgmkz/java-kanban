package service;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Менеджер задач")
class InMemoryTaskManagerTest {
    TaskManager inMemoryTaskManager;
    Epic epic1;
    Epic epic3 = new Epic("epic3", "epic3");
    SubTask subTask1;

    @BeforeEach
    void init() {
        inMemoryTaskManager = Managers.getDefault();
        inMemoryTaskManager.createTask(new Task("task1", Status.DONE, "task1"));//id1
        epic1 = inMemoryTaskManager.createEpic(new Epic("epic1", "epic1"));//id2
        subTask1= inMemoryTaskManager.createSubTask(new SubTask("subTask1", Status.IN_PROGRESS, "subTask1", epic1));//id3
    }

    @DisplayName("Получение истории задач")
    @Test
    void shouldGetHistoryManager() {
        Task task = inMemoryTaskManager.getTask(1);//Просмотрели одну задачу
        assertEquals(task.getId(), inMemoryTaskManager.getHistory().get(0).getId(), "id должны совпадать");
        assertEquals(1, inMemoryTaskManager.getHistory().size(), "размер должен быть 1");
    }

    @DisplayName("Создание задачи")
    @Test
    void shouldCreateTask() {
        inMemoryTaskManager.createTask(new Task("task2", Status.DONE, "task2"));
        assertEquals(2, inMemoryTaskManager.getAllTask().size(), "размер должен быть 2");
        assertEquals("task2", inMemoryTaskManager.getTask(4).getName(), "имя должно быть task2");
    }

    @DisplayName("Получение задачи")
    @Test
    void shouldGetTask() {
        Task task = inMemoryTaskManager.getTask(1);
        assertEquals(1, task.getId(), "id должен быть 1");
        assertEquals("task1", task.getName(), "name должено быть task1");
    }

    @DisplayName("Обновление задачи")
    @Test
    void shouldUpdateTask() {
        inMemoryTaskManager.updateTask(new Task(1, "task1NEW", Status.NEW, "task1NEW"));
        Task task = inMemoryTaskManager.getTask(1);
        assertEquals("task1NEW", task.getName(), "name должено быть task1NEW");
        assertEquals(Status.NEW, task.getStatus(), "status должено быть NEW");
        assertEquals("task1NEW", task.getDescription(), "description должено быть task1NEW");
    }

    @DisplayName("Получение всех задач")
    @Test
    void shouldGetAllTask() {
        inMemoryTaskManager.createTask(new Task("task2", Status.DONE, "task2"));
        inMemoryTaskManager.createTask(new Task("task3", Status.DONE, "task3"));
        assertEquals(3, inMemoryTaskManager.getAllTask().size(), "должно быть 3 задачи");
    }

    @DisplayName("Удаление задачи")
    @Test
    void shouldDeleteTask() {
        inMemoryTaskManager.createTask(new Task("task2", Status.DONE, "task2"));//id4
        inMemoryTaskManager.deleteTask(1);
        assertEquals(1, inMemoryTaskManager.getAllTask().size(), "должна быть 1 задача");
        assertEquals(4, inMemoryTaskManager.getTask(4).getId(), "должен быть id4");
    }

    @DisplayName("Удаление всех задач")
    @Test
    void shouldСlearTask() {
        inMemoryTaskManager.clearTask();
        assertEquals(0, inMemoryTaskManager.getAllTask().size(), "должено быть 0");
    }

    @DisplayName("Создание эпика")
    @Test
    void shouldCreateEpic() {
        inMemoryTaskManager.createEpic(new Epic("epic2", "epic2"));//id4
        assertEquals(2, inMemoryTaskManager.getAllEpic().size(), "должно быть 2 эпика");
        assertEquals("epic2", inMemoryTaskManager.getEpic(4).getName(), "должно быть имя epic2");
        assertEquals(Status.NEW, inMemoryTaskManager.getEpic(4).getStatus(), "статус должен быть NEW");
    }

    @DisplayName("Обновление эпика")
    @Test
    void shouldUpdateEpic() {
        inMemoryTaskManager.updateEpic(new Epic(2, "epic1NEW", "epic1NEW"));
        assertEquals("epic1NEW", inMemoryTaskManager.getEpic(2).getName(), "должно быть имя epic1NEW");
        assertEquals("epic1NEW", inMemoryTaskManager.getEpic(2).getDescription(), "должно быть описание epic1NEW");
        assertEquals(Status.IN_PROGRESS, inMemoryTaskManager.getEpic(2).getStatus(), "статус должен быть IN_PROGRESS");
        assertEquals("epic1NEW", inMemoryTaskManager.getSubTask(3).getEpic().getName(), "обновление эпика в подзадаче");
    }

    @DisplayName("Получение эпика")
    @Test
    void shouldGetEpic() {
        Epic epic = inMemoryTaskManager.getEpic(2);
        assertEquals(2, epic.getId(), "id должен быть 2");
        assertEquals("epic1", epic.getName(), "name должено быть epic1");
    }
    @DisplayName("Получение всех эпиков")
    @Test
    void shouldGetAllEpic() {
        inMemoryTaskManager.createEpic(new Epic("epic2", "epic2"));
        inMemoryTaskManager.createEpic(new Epic("epic3", "epic3"));
        assertEquals(3, inMemoryTaskManager.getAllEpic().size(), "должно быть 3 эпика");
    }
    @DisplayName("Удалить эпик")
    @Test
    void shouldDeleteEpic() {
        inMemoryTaskManager.createEpic(new Epic("epic2", "epic2"));
        inMemoryTaskManager.deleteEpic(2);
        assertEquals(1, inMemoryTaskManager.getAllEpic().size(), "должен быть 1 эпик");
        assertEquals("epic2", inMemoryTaskManager.getEpic(4).getName(), "должно быть имя epic2");
        assertEquals(0, inMemoryTaskManager.getAllSubTask().size(), "должно быть 0 подзадач");
    }
    @DisplayName("Получить подзадачи эпика")
    @Test
    void shouldListSubTasksEpic() {
        SubTask subTask2 = inMemoryTaskManager.createSubTask(new SubTask("subTask2", Status.IN_PROGRESS, "subTask2", epic1));
        SubTask subTask3 = inMemoryTaskManager.createSubTask(new SubTask("subTask3", Status.IN_PROGRESS, "subTask3", epic1));
        assertEquals(3, inMemoryTaskManager.listSubTasksEpic(2).size(), "должно быть 3 подзадачи у эпика");
        inMemoryTaskManager.deleteSubTask(subTask2);//удалили подзадачу 2
        inMemoryTaskManager.deleteSubTask(subTask3);//удалили подзадачу 3
        assertEquals(1, inMemoryTaskManager.listSubTasksEpic(2).size(), "должно быть 1 подзадача у эпика");
        assertEquals("subTask1", inMemoryTaskManager.listSubTasksEpic(2).get(0).getName(), "имя подзадачи должно быть subTask1");
    }
    @DisplayName("Удалить все эпики")
    @Test
    void shouldClearEpic() {
        inMemoryTaskManager.createEpic(new Epic("epic2", "epic2"));
        inMemoryTaskManager.clearEpic();
        assertEquals(0, inMemoryTaskManager.getAllEpic().size(), "должно быть 0 эпиков");
        assertEquals(0, inMemoryTaskManager.getAllSubTask().size(), "должно быть 0 подзадач");
    }
    @DisplayName("Создать подзадачу")
    @Test
    void shouldCreateSubTask() {
        inMemoryTaskManager.createSubTask(new SubTask("subTask2", Status.IN_PROGRESS, "subTask2", epic1));
        assertEquals(2, inMemoryTaskManager.getAllSubTask().size(), "должно быть 2 подзадачи");
        assertEquals(2, inMemoryTaskManager.listSubTasksEpic(2).size(), "в эпике должно быть 2 подзадачи");
        SubTask subTaskubTask = inMemoryTaskManager.createSubTask(new SubTask("subTask3", Status.IN_PROGRESS, "subTask3", epic3));
        assertNull(subTaskubTask, "должен быть null");
    }
    @DisplayName("Получить подзадачу")
    @Test
    void shouldGetSubTask() {
        SubTask subTask = inMemoryTaskManager.getSubTask(3);
        assertEquals("subTask1", subTask.getName());
        assertEquals("subTask1", subTask.getDescription());
        assertEquals(Status.IN_PROGRESS, subTask.getStatus());
        assertEquals(2, inMemoryTaskManager.getSubTask(3).getEpic().getId());
    }
    @DisplayName("Обновить подзадачу")
    @Test
    void shouldUpdateSubTask() {
        inMemoryTaskManager.updateSubTask(new SubTask(3, "subTask1NEW", Status.DONE, "subTask1NEW"));
        SubTask subTask = inMemoryTaskManager.getSubTask(3);
        assertEquals("subTask1NEW", subTask.getName(), "имя должно быть subTask1NEW");
        assertEquals("subTask1NEW", subTask.getDescription(), "комментарий должен быть subTask1NEW");
        assertEquals(Status.DONE, subTask.getStatus(), "статус должен быть Status.DONE");
        assertEquals(Status.DONE, inMemoryTaskManager.getEpic(2).getStatus(), "статус эпика должен быть Status.DONE");
        assertEquals("subTask1NEW", inMemoryTaskManager.getEpic(2).getSubTasks().get(0).getName(), "имя должно быть subTask1NEW");
    }
    @DisplayName("Удалить подзадачу")
    @Test
    void shouldDeleteSubTask() {
        inMemoryTaskManager.createSubTask(new SubTask("subTask2", Status.IN_PROGRESS, "subTask2", epic1));
        inMemoryTaskManager.deleteSubTask(subTask1);
        assertEquals(1, inMemoryTaskManager.getAllSubTask().size(), "должна остаться одна подзадача");
        assertEquals(1, inMemoryTaskManager.getEpic(2).getSubTasks().size(), "в эпике должна остаться одна подзадача");
        assertEquals("subTask2", inMemoryTaskManager.getEpic(2).getSubTasks().get(0).getName(), "имя подзадачи в эпике должно быть subTask2");
    }
    @DisplayName("Получить все подзадачи")
    @Test
    void shouldGetAllSubTask() {
        inMemoryTaskManager.createSubTask(new SubTask("subTask2", Status.IN_PROGRESS, "subTask2", epic1));
        assertEquals(2, inMemoryTaskManager.getAllSubTask().size(), "должно быть две подзадачи");
        assertEquals(3, inMemoryTaskManager.getAllSubTask().get(0).getId(), "id первой подзадачи должен быть равен 3");
        assertEquals(4, inMemoryTaskManager.getAllSubTask().get(1).getId(), "id второй подзадачи должен быть равен 4");
    }
    @DisplayName("Удалить все подзадачи")
    @Test
    void shouldClearSubTask() {
        inMemoryTaskManager.createSubTask(new SubTask("subTask2", Status.IN_PROGRESS, "subTask2", epic1));
        inMemoryTaskManager.clearSubTask();
        assertEquals(0, inMemoryTaskManager.getAllSubTask().size(), "должно быть 0 подзадач");
        assertEquals(0, inMemoryTaskManager.getEpic(2).getSubTasks().size(), "в эпике должно быть 0 подзадач");
    }
}

