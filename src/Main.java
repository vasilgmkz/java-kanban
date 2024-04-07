import service.TaskManager;
import model.Task;
import model.SubTask;
import model.Epic;
import model.Status;

import java.net.Socket;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();
        System.out.println("Проверка функций трекера задач:");
        System.out.println();
        System.out.println("Задачи:");
        System.out.println("Создание трех задач...");
        Task task1 = taskManager.createTask(new Task("task1" , Status.NEW, "task1"));
        Task task2 = taskManager.createTask(new Task("task2" , Status.DONE, "task2"));
        Task task3 = taskManager.createTask(new Task("task3" , Status.IN_PROGRESS, "task3"));
        System.out.println("Вывод всех задач:");
        System.out.println(taskManager.getAllTask());
        System.out.println("Вывод задачи по id 3:");
        System.out.println(taskManager.getTask(3));
        System.out.println("Обновить задачу по id 2:");
        taskManager.updateTask(new Task(2, "task2NEW", Status.IN_PROGRESS, "task2NEW"));
        System.out.println(taskManager.getAllTask());
        System.out.println("Удалить по id 1:");
        taskManager.deleteTask(1);
        System.out.println(taskManager.getAllTask());
        System.out.println("Удалить все задачи...");
        taskManager.clearTask();
        System.out.println(taskManager.getAllTask());
        System.out.println();
        System.out.println("///////////////////////////////////////////////////////////////////////////////////");
        System.out.println();
        System.out.println("Эпики: ");
        System.out.println("Создание трех эпиков...");
        Epic epic1 = taskManager.createEpic(new Epic("epic1" , Status.DONE, "epic1"));
        Epic epic2 = taskManager.createEpic(new Epic("epic2" , Status.DONE, "epic2"));
        Epic epic3 = taskManager.createEpic(new Epic("epic3" , Status.DONE, "epic3"));
        System.out.println("Вывод всех Эпиков:");
        System.out.println(taskManager.getAllEpic());
        System.out.println("Вывод эпика по id 5:");
        System.out.println(taskManager.getEpic(5));
        System.out.println("Удалить эпик по id 4");
        taskManager.deleteEpic(4);
        System.out.println(taskManager.getAllEpic());
        System.out.println("Обновить эпик по id 6");
        taskManager.updateEpic(new Epic(6, "epic3NEW", Status.IN_PROGRESS, "epic3NEW"));
        System.out.println(taskManager.getAllEpic());
        System.out.println("Список подзадач эпика id 6");
        System.out.println(taskManager.listSubTasksEpic(6));
        System.out.println("Удалить все эпики...");
        taskManager.clearEpic();
        System.out.println(taskManager.getAllEpic());
        System.out.println("///////////////////////////////////////////////////////////////////////////////////");
        System.out.println();
        System.out.println("Подзадачи: ");
        System.out.println("Создание трех эпиков...");
        Epic epic4 = taskManager.createEpic(new Epic("epic4" , Status.DONE, "epic4")); //id7
        Epic epic5 = taskManager.createEpic(new Epic("epic5" , Status.DONE, "epic5")); //id8
        Epic epic6 = taskManager.createEpic(new Epic("epic6" , Status.DONE, "epic6")); //id9
        System.out.println("Создание девяти подзадач...");
        SubTask subTask1 = taskManager.createSubTask(new SubTask("subTask1", Status.NEW, "subTask1"), epic4);
        SubTask subTask2 = taskManager.createSubTask(new SubTask("subTask2", Status.NEW, "subTask2"), epic4);
        SubTask subTask3 = taskManager.createSubTask(new SubTask("subTask3", Status.NEW, "subTask3"), epic4);
        SubTask subTask4 = taskManager.createSubTask(new SubTask("subTask4", Status.NEW, "subTask4"), epic5);
        SubTask subTask5 = taskManager.createSubTask(new SubTask("subTask5", Status.DONE, "subTask5"), epic5);
        SubTask subTask6 = taskManager.createSubTask(new SubTask("subTask6", Status.IN_PROGRESS, "subTask6"), epic5);
        SubTask subTask7 = taskManager.createSubTask(new SubTask("subTask7", Status.DONE, "subTask7"), epic6);
        SubTask subTask8 = taskManager.createSubTask(new SubTask("subTask8", Status.DONE, "subTask8"), epic6);
        SubTask subTask9 = taskManager.createSubTask(new SubTask("subTask9", Status.DONE, "subTask9"), epic6);
        System.out.println("Вывод всех Подзадач:");
        System.out.println(taskManager.getAllSubTask());
        System.out.println("Вывод подзадач эпика id 8");
        System.out.println(taskManager.listSubTasksEpic(8));
        System.out.println("Вывод всех Эпиков:");
        System.out.println(taskManager.getAllEpic());
        System.out.println("Вывод подзадачи по id 14:");
        System.out.println(taskManager.getSubTask(14));
        System.out.println("Удаление подзадачи subTask5:");
        taskManager.deleteSubTask(subTask5);
        System.out.println("Вывод подзадач эпика id 8");
        System.out.println(taskManager.listSubTasksEpic(8));
        System.out.println("Удаление подзадачи subTask6:");
        taskManager.deleteSubTask(subTask6);
        System.out.println("Вывод подзадач эпика id 8");
        System.out.println(taskManager.listSubTasksEpic(8));
        System.out.println("Вывод эпика id 8");
        System.out.println(taskManager.getEpic(8));
        System.out.println("Обновление подзадачи id 13");
        taskManager.updateSubTask(new SubTask(13, "subTask4NEW", Status.IN_PROGRESS, "subTask4NEW"));
        System.out.println("Вывод всех Подзадач:");
        System.out.println(taskManager.getAllSubTask());
        System.out.println("Вывод подзадачи по id 13:");
        System.out.println(taskManager.getSubTask(13));
        System.out.println("Вывод подзадач эпика id 8");
        System.out.println(taskManager.listSubTasksEpic(8));
        System.out.println("Удалить все подзадачи");
        taskManager.clearSubTask();
        System.out.println("Вывод всех Подзадач:");
        System.out.println(taskManager.getAllSubTask());
        System.out.println("Вывод подзадач эпика id 8");
        System.out.println(taskManager.listSubTasksEpic(8));

    }
}
