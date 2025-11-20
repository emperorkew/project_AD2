package oplossing;

import opgave.Node;

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