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
        if (type.equals(TaskType.TASK)) {
            return new Task(id, task[2], Status.valueOf(task[3]), task[4]);
        } else if (type.equals(TaskType.EPIC)) {
            return new Epic(id, task[2], Status.valueOf(task[3]), task[4]);
        } else if (type.equals(TaskType.SUBTASK)) {
            return new SubTask(id, task[2], Status.valueOf(task[3]),
                    task[4], Integer.parseInt(task[5]));
        } else {
            return null;
        }
    }


}

