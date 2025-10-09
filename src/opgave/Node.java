package opgave;

/**
 * A read-only view on the node of a simple tree.
 *
 * @param <E> the type of elements that can be contained in the tree
 */
public interface Node<E extends Comparable<E>> {

    /**
     * @return the current value of this node
     */
    E getValue();

    /**
     * @return the left child of this node, or <tt>null</tt> if the node has no left child
     */
    Node<E> getLeft();

    /**
     * @return the right child of this node, or <tt>null</tt> if the node has no right child
     */
    Node<E> getRight();

}
