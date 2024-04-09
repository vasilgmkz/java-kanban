package model;

import java.util.Objects;

public class SubTask extends Task {
    private Epic epic;
    public SubTask (String name, Status status, String description, Epic epic) {
        super(name, status, description);
        this.epic = epic;
    }
    public SubTask (int id, String name, Status status, String description) {
        super(id, name, status, description);
    }

    @Override
    public Epic getEpic() {
        return epic;
    }

    public void setEpic(Epic epic) {
        this.epic = epic;
    }

    @Override
    public String toString() {
        return "\n" + "SubTask{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", status=" + status +
                ", description='" + description + '\'' +
                "} ";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        SubTask subTask = (SubTask) o;
        return Objects.equals(epic, subTask.epic);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epic);
    }
}


