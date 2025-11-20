package oplossing;

import opgave.PriorityNode;

public class PriorityTop<E extends Comparable<E>> extends Top<E> implements PriorityNode<E> {

    private long priority;

    public PriorityTop(E value, long priority) {
        super(value);
        this.priority = priority;
    }

    @Override
    public long getPriority() {
        return priority;
    }

    public void setPriority(long priority) {
        this.priority = priority;
    }

    @Override
    public PriorityTop<E> getLeft() {
        return (PriorityTop<E>) super.getLeft();
    }

    @Override
    public PriorityTop<E> getRight() {
        return (PriorityTop<E>) super.getRight();
    }

    public void setLeft(PriorityTop<E> left) {
        super.setLeft(left);
    }

    public void setRight(PriorityTop<E> right) {
        super.setRight(right);
    }

    @Override
    public PriorityTop<E> rotateLeft() {
        return (PriorityTop<E>) super.rotateLeft();
    }

    @Override
    public PriorityTop<E> rotateRight() {
        return (PriorityTop<E>) super.rotateRight();
    }
}