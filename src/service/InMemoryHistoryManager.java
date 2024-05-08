package service;

import model.Task;

import java.util.*;

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
    HashMap <Integer, Node> history = new HashMap<>();
    Node first;
    Node last;

    @Override
    public void add(Task task) {
        Node node = history.get(task.getId());
        if (node != null) {
            removeNode(node);
            linkLast(task);
        }
        else {
            linkLast(task);
        }
    }
    @Override
    public void remove (int id) {
        if (history.containsKey(id)) {
            removeNode(history.get(id));
        }
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

    private void removeNode (Node node) {
            Node nodePrev = node.prev;
            Node nodeNext = node.next;
            history.remove(node.item.getId());
            if (nodePrev == null && nodeNext != null) {
                nodeNext.prev = null;
                history.put(nodeNext.item.getId(), nodeNext);
                first = nodeNext;
            }
            else if (nodeNext == null && nodePrev != null) {
                nodePrev.next = null;
                history.put(nodePrev.item.getId(), nodePrev);
                last = nodePrev;
            }
            else if (nodeNext == null) {
                first = null;
                last = null;
            }
            else {
                nodePrev.next = nodeNext;
                nodeNext.prev = nodePrev;
                history.put(nodeNext.item.getId(), nodeNext);
                history.put(nodePrev.item.getId(), nodePrev);
            }
    }



    private void linkLast (Task task) {
        final Node oldLast  = last;
        final Node newNode = new Node(oldLast, task, null);
        last = newNode;
        if (oldLast == null) {
            first = newNode;
        }
        else {
            oldLast.next = newNode;
            history.put(oldLast.item.getId(), oldLast);
        }
        history.put(last.item.getId(), last);
    }
}
