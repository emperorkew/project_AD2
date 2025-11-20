package oplossing;

import opgave.PriorityNode;

public class PriorityTop<E extends Comparable<E>> implements PriorityNode<E> {

    private E value;
    private long priority;
    private PriorityTop<E> left;
    private PriorityTop<E> right;

    public PriorityTop(E value, long priority) {
        this.value = value;
        this.priority = priority;
        this.left = null;
        this.right = null;
    }

    @Override
    public E getValue() {
        return value;
    }

    @Override
    public long getPriority() {
        return priority;
    }

    @Override
    public PriorityNode<E> getLeft() {
        return left;
    }

    @Override
    public PriorityNode<E> getRight() {
        return right;
    }

    // Setters for tree manipulation
    public void setValue(E value) {
        this.value = value;
    }

    public void setPriority(long priority) {
        this.priority = priority;
    }

    public void setLeft(PriorityTop<E> left) {
        this.left = left;
    }

    public void setRight(PriorityTop<E> right) {
        this.right = right;
    }
}