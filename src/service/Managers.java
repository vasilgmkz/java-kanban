package service;

import model.Epic;
import model.SubTask;
import model.Task;

import java.util.ArrayList;
import java.util.List;

public class Managers {
    public static TaskManager getDefault () {
        return new InMemoryTaskManager(new InMemoryHistoryManager());
    }
}
