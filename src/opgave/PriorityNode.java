package opgave;

/**
 * A read-only view on the node of a treap.
 *
 * @param <E> the type of elements that can be contained in the tree
 */
public interface PriorityNode<E extends Comparable<E>> extends Node<E> {

    /**
     * @return the priority of this node
     */
    long getPriority();

    /**
     * @return the left child of this node, or <tt>null</tt> if the node has no left child
     */
    PriorityNode<E> getLeft();

    /**
     * @return the right child of this node, or <tt>null</tt> if the node has no right child
     */
    PriorityNode<E> getRight();
}
