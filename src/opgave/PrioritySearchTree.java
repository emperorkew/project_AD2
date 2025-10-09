package opgave;

/**
 *  A simple search-tree API with support for basic operations and direct access
 *  to the root node. This tree uses a PriorityNode that carries both a key and a priority.
 *  Your treaps tree should at least implement this interface.
 *
 * @param <E> the type of elements that can be contained in this tree
 */
public interface PrioritySearchTree<E extends Comparable<E>> extends SearchTree<E> {

    /**
     * Access the internal tree structure. This method is intended for testing and debugging purposes.
     *
     * @return the root node if this tree, or <tt>null</tt> if the tree is
     */
    PriorityNode<E> root();
}
