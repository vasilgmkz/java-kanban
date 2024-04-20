package service;

import model.Epic;
import model.SubTask;
import model.Task;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {
    Task createTask(Task task);

    Task getTask(int id);

    void updateTask(Task task);

    ArrayList<Task> getAllTask();

    void deleteTask(int id);

    void clearTask();

    ArrayList<SubTask> getAllSubTask();

    void clearSubTask();

    SubTask getSubTask(int id);

    SubTask createSubTask(SubTask subTask);

    SubTask updateSubTask(SubTask subTask);

    void deleteSubTask(SubTask subTask);

    void updateEpic(Epic epic);

    Epic createEpic(Epic epic);

    ArrayList<Epic> getAllEpic();

    Epic getEpic(int id);

    void deleteEpic(int id);

    ArrayList<SubTask> listSubTasksEpic(int id);

    void clearEpic();
    HistoryManager getHistoryManager();

}
