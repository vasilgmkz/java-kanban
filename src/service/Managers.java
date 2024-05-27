package service;

public class Managers {
    public static TaskManager getDefault() {
        return new FileBackedTaskManager("src/resources/file.csv");
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
