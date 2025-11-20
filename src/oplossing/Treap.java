package oplossing;

import opgave.PriorityNode;
import opgave.PrioritySearchTree;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Treap<E extends Comparable<E>> implements PrioritySearchTree<E> {

    protected int size;
    protected PriorityTop<E> root;
    private final Random random;

    public Treap() {
        this.size = 0;
        this.root = null;
        this.random = new Random();
    }

    @Override
    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean search(E o) {
        if (o == null) return false;

        PriorityTop<E> current = root;
        while (current != null) {
            int cmp = o.compareTo(current.getValue());
            if (cmp == 0) return true;
            current = (cmp < 0) ? current.getLeft() : current.getRight();
        }
        return false;
    }

    @Override
    public boolean add(E o) {
        if (o == null) return false;

        int oldSize = size;
        root = addRecursive(root, o, random.nextLong());
        return size > oldSize;
    }

    private PriorityTop<E> addRecursive(PriorityTop<E> node, E value, long priority) {
        if (node == null) {
            size++;
            return new PriorityTop<>(value, priority);
        }

        int cmp = value.compareTo(node.getValue());
        if (cmp == 0) return node;

        if (cmp < 0) {
            node.setLeft(addRecursive(node.getLeft(), value, priority));
            if (node.getLeft().getPriority() > node.getPriority()) {
                node = rotateRight(node);
            }
        } else {
            node.setRight(addRecursive(node.getRight(), value, priority));
            if (node.getRight().getPriority() > node.getPriority()) {
                node = rotateLeft(node);
            }
        }

        return node;
    }

    protected PriorityTop<E> rotateLeft(PriorityTop<E> node) {
        PriorityTop<E> r = node.getRight();
        node.setRight(r.getLeft());
        r.setLeft(node);
        return r;
    }

    protected PriorityTop<E> rotateRight(PriorityTop<E> node) {
        PriorityTop<E> l = node.getLeft();
        node.setLeft(l.getRight());
        l.setRight(node);
        return l;
    }

    @Override
    public boolean remove(E e) {
        if (e == null || root == null) return false;

        int oldSize = size;
        root = removeRecursive(root, e);
        return size < oldSize;
    }

    private PriorityTop<E> removeRecursive(PriorityTop<E> node, E value) {
        if (node == null) return null;

        int cmp = value.compareTo(node.getValue());

        if (cmp < 0) {
            node.setLeft(removeRecursive(node.getLeft(), value));
        } else if (cmp > 0) {
            node.setRight(removeRecursive(node.getRight(), value));
        } else {
            size--;
            node = rotateDown(node);
        }

        return node;
    }

    private PriorityTop<E> rotateDown(PriorityTop<E> node) {
        PriorityTop<E> l = node.getLeft();
        PriorityTop<E> r = node.getRight();

        if (l == null && r == null) return null;
        if (l == null) {
            PriorityTop<E> newRoot = rotateLeft(node);
            newRoot.setLeft(rotateDown(node));
            return newRoot;
        }
        if (r == null) {
            PriorityTop<E> newRoot = rotateRight(node);
            newRoot.setRight(rotateDown(node));
            return newRoot;
        }

        if (l.getPriority() > r.getPriority()) {
            PriorityTop<E> newRoot = rotateRight(node);
            newRoot.setRight(rotateDown(node));
            return newRoot;
        } else {
            PriorityTop<E> newRoot = rotateLeft(node);
            newRoot.setLeft(rotateDown(node));
            return newRoot;
        }
    }

    @Override
    public PriorityNode<E> root() {
        return root;
    }

    @Override
    public List<E> values() {
        List<E> result = new ArrayList<>(size);
        inOrderTraversal(root, result);
        return result;
    }

    private void inOrderTraversal(PriorityTop<E> node, List<E> result) {
        if (node == null) return;
        inOrderTraversal(node.getLeft(), result);
        result.add(node.getValue());
        inOrderTraversal(node.getRight(), result);
    }
}