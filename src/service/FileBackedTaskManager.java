package service;

import converter.TaskConverter;
import exception.ManagerSaveException;
import model.Epic;
import model.SubTask;
import model.Task;
import model.TaskType;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {

    String file;
    TaskConverter taskConverter = new TaskConverter();

    FileBackedTaskManager(String file) {
        super();
        this.file = file;
        loadFromFile(file);
    }

    @Override
    public Task createTask(Task task) {
        super.createTask(task);
        save();
        return task;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public void clearTask() {
        super.clearTask();
        save();
    }

    @Override
    public void clearSubTask() {
        super.clearSubTask();
        save();
    }

    @Override
    public SubTask createSubTask(SubTask subTask) {
        super.createSubTask(subTask);
        save();
        return subTask;
    }

    @Override
    public SubTask updateSubTask(SubTask subTask) {
        super.updateSubTask(subTask);
        save();
        return subTask;
    }

    @Override
    public void deleteSubTask(SubTask subTask) {
        super.deleteSubTask(subTask);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public Epic createEpic(Epic epic) {
        super.createEpic(epic);
        save();
        return epic;
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
        save();
    }

    @Override
    public void clearEpic() {
        super.clearEpic();
        save();
    }

    @Override
    public void save() {
        try (Writer fileWriter = new FileWriter(file, StandardCharsets.UTF_8)) {
            fileWriter.write("id,type,name,status,description,epic\n");
            for (Task task : tasks.values()) {
                fileWriter.write(taskConverter.toString(task) + "\n");
            }
            for (Epic epic : epics.values()) {
                fileWriter.write(taskConverter.toString(epic) + "\n");
                for (SubTask subTask : epic.getSubTasks()) {
                    fileWriter.write(taskConverter.toString(subTask) + "\n");
                }
            }
        } catch (IOException e) {
            try {
                throw new ManagerSaveException("Ошибка сохранения", e);
            } catch (ManagerSaveException ex) {
                System.out.println(ex.getDetailMessage());
            }
        }
    }

    public void loadFromFile(String file) {
        List<String> list = new ArrayList<>();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            while (bufferedReader.ready()) {
                list.add(bufferedReader.readLine());
            }
            for (int i = 1; i < list.size(); i++) {
                Task task = TaskConverter.fromString(list.get(i));
                if (task.getType().equals(TaskType.TASK)) {
                    tasks.put(task.getId(), task);
                } else if (task.getType().equals(TaskType.EPIC)) {
                    Epic epic = (Epic) task;
                    epics.put(task.getId(), epic);
                } else if (task.getType().equals(TaskType.SUBTASK)) {
                    SubTask subTask = (SubTask) task;
                    subTask.setEpic(epics.get(subTask.getEpicId()));
                    subTasks.put(subTask.getId(), subTask);
                    epics.get(subTask.getEpicId()).getSubTasks().add(subTask);
                }
                if (task.getId() > seq) {
                    seq = task.getId();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
