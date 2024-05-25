package service;

import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private static class Node {
        Task item;
        Node next;
        Node prev;

        Node(Node prev, Task element, Node next) {
            this.item = element;
            this.next = next;
            this.prev = prev;
        }
    }

    HashMap<Integer, Node> history = new HashMap<>();
    Node first;
    Node last;

    @Override
    public void add(Task task) {
        remove(task.getId());
        linkLast(task);
        history.put(task.getId(), last);
    }

    @Override
    public void remove(int id) {
        removeNode(history.get(id));
        history.remove(id);
    }

    @Override
    public List<Task> getHistory() {
        ArrayList<Task> list = new ArrayList<>();
        Node current = first;
        while (current != null) {
            list.add(current.item);
            current = current.next;
        }
        return list;
    }

    private void removeNode(Node node) {
        if (node == null) {
            return;
        }
        Node nodePrev = node.prev;
        Node nodeNext = node.next;
        if (nodePrev == null && nodeNext != null) {
            nodeNext.prev = null;
            first = nodeNext;
        } else if (nodeNext == null && nodePrev != null) {
            nodePrev.next = null;
            last = nodePrev;
        } else if (nodeNext == null) {
            first = null;
            last = null;
        } else {
            nodePrev.next = nodeNext;
            nodeNext.prev = nodePrev;
        }
    }

    private void linkLast(Task task) {
        final Node newNode = new Node(last, task, null);
        if (first == null) {
            first = newNode;
        } else {
            last.next = newNode;
        }
        last = newNode;
    }
}

