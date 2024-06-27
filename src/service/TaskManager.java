package service;

import model.Epic;
import model.SubTask;
import model.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public interface TaskManager {
    Task createTask(Task task);

    Task getTask(int id);

    void updateTask(Task task);

    ArrayList<Task> getAllTask();

    ArrayList<Integer> getAllTaskId();

    ArrayList<Integer> getAllSubTaskId();

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

    ArrayList<Integer> getAllEpicId();

    Epic getEpic(int id);

    void deleteEpic(int id);

    ArrayList<SubTask> listSubTasksEpic(int id);

    void clearEpic();

    List<Task> getHistory();

    void save();

    public TreeSet<Task> getPrioritizedTasks();

}
