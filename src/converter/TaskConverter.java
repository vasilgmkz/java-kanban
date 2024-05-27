package converter;

import model.*;

public class TaskConverter {


    public String toString(Task task) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(task.getId() + "," + task.getType() + "," + task.getName() + "," + task.getStatus() + ","
                + task.getDescription() + "," + task.getEpicId());
        return stringBuilder.toString();
    }

    public static Task fromString(String value) {
        String[] task = value.split(",");
        final int id = Integer.parseInt(task[0]);
        final TaskType type = TaskType.valueOf(task[1]);
        final String name = task[2];
        final Status status = Status.valueOf(task[3]);
        final String description = task[4];
        final int epicId = Integer.parseInt(task[5]);
        if (type.equals(TaskType.TASK)) {
            return new Task(id, name, status, description);
        } else if (type.equals(TaskType.EPIC)) {
            return new Epic(id, name, status, description);
        } else if (type.equals(TaskType.SUBTASK)) {
            return new SubTask(id, name, status, description, epicId);
        } else {
            return null;
        }
    }
}

