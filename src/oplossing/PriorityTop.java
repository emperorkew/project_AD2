package oplossing;

import opgave.PriorityNode;

/**
 * A node implementation that extends Top with priority support for use in treaps and similar structures.
 * <p>
 * This class combines the BST node functionality of Top with a priority value, enabling
 * data structures that maintain both BST ordering (by value) and heap ordering (by priority),
 * such as treaps.
 * <p>
 * Key features:
 * - Inherits all Top functionality (value storage, child references, rotations)
 * - Adds mutable priority field for heap-based ordering
 * - Implements PriorityNode interface
 * - Overrides getLeft/getRight/rotateLeft/rotateRight to return PriorityTop types
 * <p>
 * The priority is typically used to maintain heap properties in the tree structure,
 * while the value maintains BST properties.
 *
 * @param <E> the type of element stored in this node, must be Comparable
 * @author Remco Marien
 */

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