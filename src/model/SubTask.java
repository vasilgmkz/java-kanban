package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class SubTask extends Task {

    transient private Epic epic;
    private int epicId;

    public SubTask(String name, Status status, String description, Epic epic, LocalDateTime startTime, Duration duration) {
        super(name, status, description, startTime, duration);
        this.epic = epic;
        this.epicId = epic.getId();
    }


    public SubTask(int id, String name, Status status, String description, int epicId, LocalDateTime startTime, Duration duration) {
        super(id, name, status, description, startTime, duration);
        this.epicId = epicId;
    }

    @Override
    public Epic getEpic() {
        return epic;
    }

    @Override
    public Integer getEpicId() {
        if (epic != null) {
            return epic.getId();
        }
        return epicId;
    }


    public void setEpic(Epic epic) {
        this.epic = epic;
    }

    public void setEpicId(int id) {
        this.epicId = id;
    }

    @Override
    public TaskType getType() {
        return TaskType.SUBTASK;
    }

    @Override
    public String toString() {
        return "\n" + "SubTask{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", status=" + status +
                ", description='" + description + '\'' +
                ", epicId='" + epicId + '\'' +
                ", startTime='" + startTime + '\'' +
                ", duration='" + duration + '\'' +
                "} ";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        SubTask subTask = (SubTask) o;
        return id == subTask.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id);
    }
}


