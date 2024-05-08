import model.Task;
import model.SubTask;
import model.Epic;
import model.Status;
import service.Managers;
import service.TaskManager;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();
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
        System.out.println(taskManager.getTask(1));
        System.out.println(taskManager.getTask(1));
        System.out.println(taskManager.getTask(1));
        System.out.println(taskManager.getTask(2));
        System.out.println(taskManager.getTask(2));
        System.out.println(taskManager.getTask(2));
        System.out.println(taskManager.getTask(3));
        System.out.println(taskManager.getTask(3));
        System.out.println(taskManager.getTask(1));
        System.out.println("Вывод истории:");
        System.out.println(taskManager.getHistory());
        taskManager.deleteTask(2);
        System.out.println("Вывод истории:");
        System.out.println(taskManager.getHistory());
        //taskManager.clearTask();
        System.out.println("Вывод истории:");
        System.out.println(taskManager.getHistory());
        System.out.println("Создание трех эпиков...");
        Epic epic1 = taskManager.createEpic(new Epic("Epic1", "Epic1")); //id4
        Epic epic2 = taskManager.createEpic(new Epic("Epic2", "Epic2")); //id5
        Epic epic3 = taskManager.createEpic(new Epic("Epic3", "Epic3")); //id6
        System.out.println(taskManager.getEpic(4));
        System.out.println(taskManager.getEpic(5));
        System.out.println(taskManager.getEpic(6));
        taskManager.deleteEpic(5);
        SubTask subTask1 = taskManager.createSubTask(new SubTask("subTask1", Status.IN_PROGRESS, "subTask1", epic1)); //id7
        SubTask subTask2 = taskManager.createSubTask(new SubTask("subTask2", Status.NEW, "subTask2", epic3)); //id8
        System.out.println(taskManager.getSubTask(7));

        System.out.println("Вывод истории:");
        System.out.println(taskManager.getHistory());
        taskManager.deleteEpic(4);
        System.out.println("Вывод истории:");
        System.out.println(taskManager.getHistory());
    }
}
