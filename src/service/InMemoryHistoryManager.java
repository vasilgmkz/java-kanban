package service;

import model.Task;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    List<Task> history = new LinkedList<>();
    private final static int HISTORY_SIZE = 10;
    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }
        if (history.size() == HISTORY_SIZE) {
            history.removeFirst();
        }
        history.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return history;
    }
}
