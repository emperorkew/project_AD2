package opgave;

import java.util.List;

/**
 * A simple search tree with support for basic actions and a direct view
 * on the nodes in the tree through requesting the root node.
 * Your self-balancing tree should at least implement this interface.
 *
 * @param <E> the type of elements that can be contained in this tree
 */
public interface SearchTree<E extends Comparable<E>> {

    /**
     * Return the number of elements in this tree.
     *
     * @return the number of elements in this tree
     */
    int size();

    /**
     * Search for the given element in the tree.
     *
     * @param o element whose presence in this tree is to be tested.
     * @return <tt>true</tt> if the tree contains the specified element.
     */
    boolean search(E o);

    /**
     * Ensure that this tree contains the specified element.
     *
     * @param o element whose presence in this collection is to be ensured
     * @return <tt>true</tt> if the elements in the tree changed as a result of
     * the call.
     */
    boolean add(E o);

    /**
     * Ensure that this tree does not contain the specified element.
     *
     * @param e element to be removed from the tree, if present
     * @return <tt>true</tt> if the element was removed as result of this call
     */
    boolean remove(E e);

    /**
     * Access the internal tree structure. This method is intended for testing and debugging purposes.
     *
     * @return the root node if this tree, or <tt>null</tt> if the tree is
     */
    Node<E> root();

    /**
     * Access a <b>sorted</b> list of all values in this tree.
     * This list should be constructed efficiëntly in O(n) time by traversing the tree in-order.
     *
     * @return a list with all included values included in this tree
     */
    List<E> values();
}
