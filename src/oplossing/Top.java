package oplossing;

import opgave.Node;

public class Top<E extends Comparable<E>> implements Node<E> {

    private final E value;
    private Top<E> left;
    private Top<E> right;

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
}