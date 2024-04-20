package service;

import model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    List<Task> history = new ArrayList<>();
    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }
        System.out.println("Добавлена в историю задача: " + task.getId());
        if (history.size() > 9) {
            history.removeFirst();
        }
        history.add(task);
    }

    @Override
    public List<Task> getAll() {
        return history;
    }
}
