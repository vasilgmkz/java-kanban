package converter;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;

public class TaskConverter {


    public String toString(Task task) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(task.getId() + "," + task.getType() + "," + task.getName() + "," + task.getStatus() + ","
                + task.getDescription() + "," + task.getEpicId());
        return stringBuilder.toString();
    }

    public static Task fromString(String value) {
        String[] task = value.split(",");
        if (task[1].equals("TASK")) {
            return new Task(Integer.parseInt(task[0]), task[2], Status.valueOf(task[3]), task[4]);
        } else if (task[1].equals("EPIC")) {
            return new Epic(Integer.parseInt(task[0]), task[2], Status.valueOf(task[3]), task[4]);
        } else if (task[1].equals("SUBTASK")) {
            return new SubTask(Integer.parseInt(task[0]), task[2], Status.valueOf(task[3]),
                    task[4], Integer.parseInt(task[5]));
        } else {
            return null;
        }
    }
}

