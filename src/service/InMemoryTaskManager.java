package service;

import model.Epic;
import model.SubTask;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private int seq = 0;
    private HashMap<Integer, Task> tasks;
    private HashMap<Integer, Epic> epics;
    private HashMap<Integer, SubTask> subTasks;
    private final HistoryManager historyManager = Managers.getDefaultHistory();

    public InMemoryTaskManager() {
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subTasks = new HashMap<>();
    }

    private int generateId() {
        return ++seq;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public Task createTask(Task task) {
        task.setId(generateId());
        tasks.put(task.getId(), task);
        return task;
    }

    @Override
    public Task getTask(int id) {
        historyManager.add(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    @Override
    public ArrayList<Task> getAllTask() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void deleteTask(int id) {
        tasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void clearTask() {
        for (int id : tasks.keySet()) {
            historyManager.remove(id);
        }
        tasks.clear();
    }

    @Override
    public ArrayList<SubTask> getAllSubTask() {
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public void clearSubTask() {
        for (int id : subTasks.keySet()) {
            historyManager.remove(id);
        }
        subTasks.clear();
        for (Epic epic : epics.values()) {
            epic.getSubTasks().clear();
            epic.updateStatus();
        }
    }

    @Override
    public SubTask getSubTask(int id) {
        historyManager.add(subTasks.get(id));
        return subTasks.get(id);
    }

    @Override
    public SubTask createSubTask(SubTask subTask) {
        Epic saveEpic = epics.get(subTask.getEpic().getId());
        if (saveEpic == null) {
            return null;
        } else {
            subTask.setId(generateId());
            subTasks.put(subTask.getId(), subTask);
            saveEpic.getSubTasks().add(subTask);
            saveEpic.updateStatus();
            epics.put(saveEpic.getId(), saveEpic);
            return subTask;
        }
    }

    @Override
    public SubTask updateSubTask(SubTask subTask) {
        SubTask saved = subTasks.get(subTask.getId());
        saved.setName(subTask.getName());
        saved.setStatus(subTask.getStatus());
        saved.setDescription(subTask.getDescription());
        subTasks.put(saved.getId(), saved);
        epics.get(saved.getEpic().getId()).updateStatus();
        return saved;
    }

    @Override
    public void deleteSubTask(SubTask subTask) {
        historyManager.remove(subTask.getId());
        SubTask delSubTask = subTasks.remove(subTask.getId());
        Epic delSubTaskinEpic = epics.get(delSubTask.getEpic().getId());
        delSubTaskinEpic.getSubTasks().remove(delSubTask);
        delSubTaskinEpic.updateStatus();
        epics.put(delSubTaskinEpic.getId(), delSubTaskinEpic);
    }

    @Override
    public void updateEpic(Epic epic) {
        Epic saved = epics.get(epic.getId());
        saved.setName(epic.getName());
        saved.setDescription(epic.getDescription());
        epics.put(epic.getId(), saved);
    }

    @Override
    public Epic createEpic(Epic epic) {
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
        return epic;
    }

    @Override
    public ArrayList<Epic> getAllEpic() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public Epic getEpic(int id) {
        historyManager.add(epics.get(id));
        return epics.get(id);
    }

    @Override
    public void deleteEpic(int id) {
        historyManager.remove(id);
        Epic deleteEpic = epics.remove(id);
        for (SubTask subTask : deleteEpic.getSubTasks()) {
            historyManager.remove(subTask.getId());
            subTasks.remove(subTask.getId());
        }
    }

    @Override
    public ArrayList<SubTask> listSubTasksEpic(int id) {
        return epics.get(id).getSubTasks();
    }

    @Override
    public void clearEpic() {
        for (int id : subTasks.keySet()) {
            historyManager.remove(id);
        }
        subTasks.clear();
        for (int id : epics.keySet()) {
            historyManager.remove(id);
        }
        epics.clear();
    }
}
