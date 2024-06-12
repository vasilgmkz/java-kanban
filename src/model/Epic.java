package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {
    private ArrayList<SubTask> subTasks = new ArrayList<>();
    LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, Status.NEW, description, null, null);
        updateStatus();
    }

    public Epic(int id, String name, String description) {
        super(id, name, Status.NEW, description, null, null);
        updateStatus();
    }

    public Epic(int id, String name, Status status, String description, LocalDateTime startTime, Duration duration) {
        super(id, name, status, description, startTime, duration);
    }

    public ArrayList<SubTask> getSubTasks() {
        return subTasks;
    }

    @Override
    public LocalDateTime endTime() {
        return endTime;
    }

    public void addTask(SubTask subTask) {

    }

    public void removeTask(SubTask subTask) {

    }

    public void updateStatus() {
        if (subTasks == null) {
            status = Status.NEW;
        } else if (checkForNew(subTasks)) {
            status = Status.NEW;
        } else if (checkForDone(subTasks)) {
            status = Status.DONE;
        } else {
            status = Status.IN_PROGRESS;
        }
        if (subTasks != null && subTasks.size() != 0) {
            calculateTime();
        } else {
            startTime = null;
            endTime = null;
            duration = null;
        }
    }

    public void calculateTime() {
        LocalDateTime start = LocalDateTime.MAX;
        LocalDateTime end = LocalDateTime.MIN;
        Duration durations = Duration.ofMinutes(0);
        for (SubTask subTask : subTasks) {
            if (subTask.getStartTime() == null || subTask.getDuration() == null) {
                continue;
            }
            if (subTask.getStartTime().isBefore(start)) {
                start = subTask.getStartTime();
            }
            if (subTask.endTime().isAfter(end)) {
                end = subTask.endTime();
            }
            durations = durations.plus(subTask.getDuration());
        }
        startTime = start;
        endTime = end;
        duration = durations;
    }

    private boolean checkForNew(ArrayList<SubTask> subTasks) {
        int sumNew = 0;
        for (SubTask subTask : subTasks) {
            if (subTask.status == Status.NEW) {
                sumNew++;
            }
        }
        if (sumNew == subTasks.size()) {
            return true;
        } else {
            return false;
        }
    }

    private boolean checkForDone(ArrayList<SubTask> subTasks) {
        int sumDone = 0;
        for (SubTask subTask : subTasks) {
            if (subTask.status == Status.DONE) {
                sumDone++;
            }
        }
        if (sumDone == subTasks.size()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subTasks, epic.subTasks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subTasks);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", status=" + status +
                ", description='" + description + '\'' +
                ", startTime='" + startTime + '\'' +
                ", duration='" + duration + '\'' +
                '}';
    }
}

