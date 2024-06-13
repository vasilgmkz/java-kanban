package service;

import exception.NotFoundException;
import exception.ValidationException;
import model.Epic;
import model.SubTask;
import model.Task;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected int seq = 0;
    protected HashMap<Integer, Task> tasks;
    protected HashMap<Integer, Epic> epics;
    protected HashMap<Integer, SubTask> subTasks;
    private final HistoryManager historyManager = Managers.getDefaultHistory();
    protected TreeSet<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime, Comparator.nullsLast(Comparator.naturalOrder())).thenComparing(Task::getId));

    public InMemoryTaskManager() {
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subTasks = new HashMap<>();
    }

    private int generateId() {
        return ++seq;
    }

    @Override
    public TreeSet<Task> getPrioritizedTasks() {
        return prioritizedTasks;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public Task createTask(Task task) {
        task.setId(generateId());
        if (checkTaskTime(task)) {
            prioritizedTasks.add(task);
        }
        tasks.put(task.getId(), task);
        return task;
    }

    @Override
    public Task getTask(int id) {
        if (tasks.get(id) == null) {
            throw new NotFoundException(new StringBuilder("Задача с id " + id + " не найдена!"));
        }
        historyManager.add(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public void updateTask(Task task) {
        if (tasks.get(task.getId()) == null) {
            throw new NotFoundException(new StringBuilder("Задача с id " + task.getId() + " не найдена!"));
        }
        if (checkTaskTime(task)) {
            prioritizedTasks.remove(tasks.get(task.getId()));
            prioritizedTasks.add(task);
        }
        if (task.getStartTime() == null || task.getDuration() == null) {
            prioritizedTasks.remove(tasks.get(task.getId()));
        }
        tasks.put(task.getId(), task);
    }

    @Override
    public ArrayList<Task> getAllTask() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void deleteTask(int id) {
        prioritizedTasks.remove(tasks.remove(id));
        historyManager.remove(id);
    }

    @Override
    public void clearTask() {
        tasks.keySet().stream().peek(id -> historyManager.remove(id)).forEach(id -> prioritizedTasks.remove(tasks.get(id)));
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
            prioritizedTasks.remove(subTasks.get(id));
        }
        subTasks.clear();
        for (Epic epic : epics.values()) {
            epic.getSubTasks().clear();
            epic.updateStatus();
        }
    }

    @Override
    public SubTask getSubTask(int id) {
        try {
            if (subTasks.get(id) == null) {
                throw new NotFoundException(new StringBuilder("Подзадача с id " + id + " не найдена!"));
            }
            historyManager.add(subTasks.get(id));
            return subTasks.get(id);
        } catch (NotFoundException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    @Override
    public SubTask createSubTask(SubTask subTask) {
        try {
            if (epics.get(subTask.getEpicId()) == null) {
                throw new NotFoundException(new StringBuilder("Эпик не найден!"));
            }
            Epic saveEpic = epics.get(subTask.getEpic().getId());
            subTask.setId(generateId());
            if (checkTaskTime(subTask)) {
                prioritizedTasks.add(subTask);
            }
            subTasks.put(subTask.getId(), subTask);
            saveEpic.getSubTasks().add(subTask);
            saveEpic.updateStatus();
            epics.put(saveEpic.getId(), saveEpic);
            return subTask;
        } catch (NotFoundException | ValidationException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }


    @Override
    public SubTask updateSubTask(SubTask subTask) {
        try {
            if (subTasks.get(subTask.getId()) == null) {
                throw new NotFoundException(new StringBuilder("Подзадача с id " + subTask.getId() + " не найдена!"));
            }
            if (epics.get(subTask.getEpicId()) == null) {
                throw new NotFoundException(new StringBuilder("Эпик с id " + subTask.getEpicId() + " не найден!"));
            }
            if (subTask.getStartTime() == null || subTask.getDuration() == null) {
                prioritizedTasks.remove(subTasks.get(subTask.getId()));
            }
            SubTask saved = new SubTask(subTask.getId(), subTask.getName(), subTask.getStatus(), subTask.getDescription(), subTask.getEpicId(), subTask.getStartTime(), subTask.getDuration());
            saved.setEpic(epics.get(saved.getEpicId()));
            if (checkTaskTime(saved)) {
                prioritizedTasks.remove(subTasks.get(saved.getId()));
                prioritizedTasks.add(saved);
            }
            epics.get(saved.getEpic().getId()).getSubTasks().remove(subTasks.get(subTask.getId()));
            epics.get(saved.getEpic().getId()).getSubTasks().add(saved);
            epics.get(saved.getEpic().getId()).updateStatus();
            subTasks.put(saved.getId(), saved);
            return saved;
        } catch (NotFoundException | ValidationException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }


    @Override
    public void deleteSubTask(SubTask subTask) {
        historyManager.remove(subTask.getId());
        prioritizedTasks.remove(subTask);
        SubTask delSubTask = subTasks.remove(subTask.getId());
        Epic delSubTaskinEpic = epics.get(delSubTask.getEpic().getId());
        delSubTaskinEpic.getSubTasks().remove(delSubTask);
        delSubTaskinEpic.updateStatus();
        epics.put(delSubTaskinEpic.getId(), delSubTaskinEpic);
    }

    @Override
    public void updateEpic(Epic epic) {
        try {
            if (epics.get(epic.getId()) == null) {
                throw new NotFoundException(new StringBuilder("Эпик с id " + epic.getId() + " не найден!"));
            }
            Epic saved = epics.get(epic.getId());
            saved.setName(epic.getName());
            saved.setDescription(epic.getDescription());
            epics.put(epic.getId(), saved);
        } catch (NotFoundException e) {
            System.out.println(e.getMessage());
        }
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
        try {
            if (epics.get(id) == null) {
                throw new NotFoundException(new StringBuilder("Эпик с id " + id + " не найден!"));
            }
            historyManager.add(epics.get(id));
            return epics.get(id);
        } catch (NotFoundException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }


    @Override
    public void deleteEpic(int id) {
        historyManager.remove(id);
        Epic deleteEpic = epics.remove(id);
        for (SubTask subTask : deleteEpic.getSubTasks()) {
            historyManager.remove(subTask.getId());
            subTasks.remove(subTask.getId());
            prioritizedTasks.remove(subTask);
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
            prioritizedTasks.remove(subTasks.get(id));
        }
        subTasks.clear();
        for (int id : epics.keySet()) {
            historyManager.remove(id);
        }
        epics.clear();
    }

    @Override
    public void save() {
    }

    protected boolean checkTaskTime(Task task) {
        if (task.getStartTime() == null || task.getDuration() == null) {
            return false;
        }
        if (prioritizedTasks.isEmpty()) {
            return true;
        }
        for (Task t : prioritizedTasks) {
            if (t.getId() == task.getId()) {
                continue;
            }
            if (!t.getStartTime().isBefore(task.endTime()) && t.endTime().isAfter(task.getStartTime())) {
                continue;
            } else if (!task.getStartTime().isBefore(t.endTime()) && task.endTime().isAfter(t.getStartTime())) {
                continue;
            } else {
                throw new ValidationException(new StringBuilder("Ошибка валидации"));
            }
        }
        return true;
    }
}


