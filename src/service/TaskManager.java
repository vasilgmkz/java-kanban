package service;

import model.Epic;
import model.SubTask;
import model.Task;
import java.util.Iterator;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private  int seq = 0;
    private HashMap<Integer, Task> tasks;
    private HashMap<Integer, Epic> epics;
    private HashMap<Integer, SubTask> subTasks;

    public TaskManager() {
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this. subTasks = new HashMap<>();
    }
    private int generateId() {
        return ++seq;
    }
    public Task createTask(Task task) {
        task.setId(generateId());
        tasks.put(task.getId(), task);
        return task;
    }
    public Task getTask(int id) {
        return tasks.get(id);
    }
    public void updateTask (Task task) {
        tasks.put(task.getId(), task);
    }
    public ArrayList<Task> getAllTask() {
        return new ArrayList<>(tasks.values());
    }
    public void deleteTask(int id) {
        tasks.remove(id);
    }
    public void clearTask () {
        tasks.clear();
    }
    public ArrayList<SubTask> getAllSubTask() {
        return new ArrayList<>(subTasks.values());
    }
    public void clearSubTask () {
        subTasks.clear();
        for (Epic epic: epics.values()) {
            epic.getSubTasks().clear();
            epic.updateStatus();
        }
    }
    public SubTask getSubTask (int id) {
        return subTasks.get(id);
    }
    public SubTask createSubTask (SubTask subTask) {
        boolean existenceEpic = false;
        Epic epic = subTask.getEpic();
        for (Epic ep : epics.values()) {
            if (ep.getId() == epic.getId()) {
                existenceEpic = true;
            }
        }
        if (!existenceEpic) {
            Epic saveEpic = createEpic(epic);
            subTask.setId(generateId());
            subTask.setEpic(saveEpic);
            subTasks.put(subTask.getId(), subTask);
            saveEpic.getSubTasks().add(subTask);
            saveEpic.updateStatus();
            epics.put(saveEpic.getId(), saveEpic);
            return subTask;
        }
        else {
            subTask.setId(generateId());
            subTask.setEpic(epic);
            subTasks.put(subTask.getId(), subTask);
            Epic saveEpic = epics.get(epic.getId());
            saveEpic.getSubTasks().add(subTask);
            saveEpic.updateStatus();
            epics.put(saveEpic.getId(), saveEpic);
            return subTask;
        }
    }
    public SubTask updateSubTask (SubTask subTask) {
        SubTask saved = subTasks.get(subTask.getId());
        saved.setName(subTask.getName());
        saved.setStatus(subTask.getStatus());
        saved.setDescription(subTask.getDescription());
        subTasks.put(saved.getId(), saved);
        epics.get(saved.getEpic().getId()).updateStatus();
        return saved;
    }

    public void deleteSubTask (SubTask subTask) {
        SubTask delSubTask = subTasks.remove(subTask.getId());
        Epic delSubTaskinEpic = epics.get(delSubTask.getEpic().getId());
        delSubTaskinEpic.getSubTasks().remove(delSubTask);
        delSubTaskinEpic.updateStatus();
        epics.put(delSubTaskinEpic.getId(), delSubTaskinEpic);
    }

    public void updateEpic (Epic epic) {
        Epic saved = epics.get(epic.getId());
        saved.setName(epic.getName());
        saved.setDescription(epic.getDescription());
        epics.put(epic.getId(), saved);
    }

    public Epic createEpic (Epic epic) {
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
        return epic;
    }

    public ArrayList<Epic> getAllEpic() {
        return new ArrayList<>(epics.values());
    }

    public Epic getEpic (int id) {
        return epics.get(id);
    }
    public void deleteEpic (int id) {
        Epic deleteEpic = epics.remove(id);
        for (SubTask subTask : deleteEpic.getSubTasks()) {
            subTasks.remove(subTask.getId());
        }
    }

    public ArrayList<SubTask> listSubTasksEpic (int id) {
        return epics.get(id).getSubTasks();
    }
    public void clearEpic () {
        clearSubTask ();
        epics.clear();
    }
}
