package oplossing;

import opgave.Node;

public class Top<E extends Comparable<E>> implements Node<E> {

    private final E value;
    private Top<E> left;
    private Top<E> right;

    public Top(E value) {
        this.value = value;
        this.left = null;
        this.right = null;
    }

    @Override
    public E getValue() {
        return this.value;
    }

    @Override
    public Node<E> getLeft() {
        return this.left;
    }

    @Override
    public Node<E> getRight() {
        return this.right;
    }

    public void setLeft(Top<E> left) {
        this.left = left;
    }

    public void setRight(Top<E> right) {
        this.right = right;
    }
}
