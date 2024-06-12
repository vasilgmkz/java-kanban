package converter;

import model.*;

import java.time.Duration;
import java.time.LocalDateTime;

public class TaskConverter {


    public String toString(Task task) {
        StringBuilder stringBuilder = new StringBuilder();
        Long durationMinutes = null;
        if (task.getDuration() != null) {
            durationMinutes = task.getDuration().toMinutes();
        }
        stringBuilder.append(task.getId() + "," + task.getType() + "," + task.getName() + "," + task.getStatus() + ","
                + task.getDescription() + "," + task.getEpicId() + "," + task.getStartTime() + ","
                + durationMinutes);
        return stringBuilder.toString();
    }

    public static Task fromString(String value) {
        String[] task = value.split(",");
        final int id = Integer.parseInt(task[0]);
        final TaskType type = TaskType.valueOf(task[1]);
        final String name = task[2];
        final Status status = Status.valueOf(task[3]);
        final String description = task[4];
        Integer epicId = null;
        LocalDateTime startTime = null;
        Duration duration = null;
        if (!task[5].equals("null")) {
            epicId = Integer.parseInt(task[5]);
        }
        if (!task[6].equals("null")) {
            startTime = LocalDateTime.parse(task[6]);
        }
        if (!task[7].equals("null")) {
            duration = Duration.ofMinutes(Long.parseLong(task[7]));
        }
        if (type.equals(TaskType.TASK)) {
            return new Task(id, name, status, description, startTime, duration);
        } else if (type.equals(TaskType.EPIC)) {
            return new Epic(id, name, status, description, startTime, duration);
        } else if (type.equals(TaskType.SUBTASK)) {
            return new SubTask(id, name, status, description, epicId, startTime, duration);
        } else {
            return null;
        }
    }
}

