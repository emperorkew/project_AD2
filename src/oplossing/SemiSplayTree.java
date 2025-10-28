package oplossing;

import opgave.Node;
import opgave.SearchTree;

import java.util.List;

/**
 * implementatie binaire zoekboom
 */
public class SemiSplayTree<E extends Comparable<E>> implements SearchTree<E> {

    private int size;
    private Node<E> root;


    public SemiSplayTree() {
        this.size = 0;
        this.root = null;
    }
    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean search(E o) {
        return false;
    }

    @Override
    public boolean add(E o) {
        return false;
    }

    @Override
    public boolean remove(E e) {
        return false;
    }

    @Override
    public Node<E> root() {
        return this.root;
    }

    @Override
    public List<E> values() {
        return List.of();
    }
}
