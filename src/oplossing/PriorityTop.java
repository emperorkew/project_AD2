package oplossing;

import opgave.PriorityNode;

public class PriorityTop<E extends Comparable<E>> implements PriorityNode<E> {

    @Override
    public long getPriority() {
        return 0;
    }

    @Override
    public E getValue() {
        return null;
    }

    @Override
    public PriorityNode<E> getLeft() {
        return null;
    }

    @Override
    public PriorityNode<E> getRight() {
        return null;
    }
}
