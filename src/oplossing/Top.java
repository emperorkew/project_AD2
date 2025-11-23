package oplossing;

import opgave.Node;

/**
 * A node implementation for binary search trees that supports tree rotation operations.
 * <p>
 * This class represents a single node in a binary search tree, storing a value and
 * references to left and right children. It implements the Node interface and provides
 * additional rotation methods used by self-balancing tree variants.
 * <p>
 * Key features:
 * - Immutable value (set at construction)
 * - Mutable left and right child references
 * - Left and right rotation operations for tree balancing
 * <p>
 * Rotation operations:
 * - rotateLeft(): promotes right child to root, current node becomes left child
 * - rotateRight(): promotes left child to root, current node becomes right child
 * <p>
 * Both rotations return the new subtree root and maintain BST ordering invariants.
 *
 * @param <E> the type of element stored in this node, must be Comparable
 * @author Remco Marien
 */
public class Top<E extends Comparable<E>> implements Node<E> {

    private final E value;
    private Top<E> left; //null als geen left child by default
    private Top<E> right; //null als geen right child by default

    public Top(E value) {
        this.value = value;
    }

    @Override
    public E getValue() {
        return value;
    }

    @Override
    public Top<E> getLeft() {
        return left;
    }

    @Override
    public Top<E> getRight() {
        return right;
    }

    public void setLeft(Top<E> left) {
        this.left = left;
    }

    public void setRight(Top<E> right) {
        this.right = right;
    }

    public Top<E> rotateLeft() {
        Top<E> r = this.right;
        this.right = r.left;
        r.left = this;
        return r;
    }

    public Top<E> rotateRight() {
        Top<E> l = this.left;
        this.left = l.right;
        l.right = this;
        return l;
    }
}