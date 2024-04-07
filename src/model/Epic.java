package model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

public class Epic extends Task {
    private ArrayList<SubTask> subTasks = new ArrayList<>();

    public Epic (String name, Status status, String description) {
        super(name, status, description);
        updateStatus();
    }
    public Epic (int id, String name, Status status, String description) {
        super(id, name, status, description);
        updateStatus();
    }

    public ArrayList<SubTask> getSubTasks() {
        return subTasks;
    }
    public void addTask (SubTask subTask) {

    }
    public void removeTask (SubTask subTask) {

    }
    public void updateStatus() {
        if (subTasks == null) {
            status = Status.NEW; 
        } else if (checkForNew(subTasks)) {
            status = Status.NEW;
        }
        else if (checkForDone(subTasks)) {
            status = Status.DONE;
        }
        else {
            status = Status.IN_PROGRESS;
        }
    }

    private boolean checkForNew (ArrayList<SubTask> subTasks) {
        int sumNew = 0;
        for (SubTask subTask: subTasks) {
            if (subTask.status == Status.NEW) {
                sumNew++;
            }
        }
        if (sumNew == subTasks.size()) {
            return true;
        }
        else {
            return false;
        }
    }
    private boolean checkForDone (ArrayList<SubTask> subTasks) {
        int sumDone = 0;
        for(SubTask subTask: subTasks) {
            if (subTask.status == Status.DONE) {
               sumDone++;
            }
        }
        if (sumDone == subTasks.size()) {
            return true;
        }
        else {
           return false;
        }
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
                '}';
    }
}

