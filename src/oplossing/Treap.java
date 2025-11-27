package oplossing;

import opgave.PriorityNode;
import opgave.PrioritySearchTree;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Treap<E extends Comparable<E>> implements PrioritySearchTree<E> {

    protected int size;
    protected PriorityTop<E> root;

    public Treap() {
        this.size = 0;
    }

    /**
     * Estimates the height of the tree for pre-allocating collections.
     * Uses log2(n) as baseline for balanced trees with bounds to handle edge cases.
     *
     * @return estimated height, minimum 8, maximum 64
     */
    protected int estimateHeight() {
        if (size == 0) return 8;
        // For balanced tree: log2(n) ≈ log(n)/log(2)
        // Add 50% margin for partially unbalanced trees
        int estimated = (int) (Math.log(size + 1) / Math.log(2) * 1.5);
        return Math.min(Math.max(estimated, 8), 64);
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

        long priority = ThreadLocalRandom.current().nextLong();

        if (root == null) {
            root = new PriorityTop<>(o, priority);
            size++;
            return true;
        }

        // Track the path for bubble-up with adaptive capacity
        List<PriorityTop<E>> path = new ArrayList<>(estimateHeight());
        PriorityTop<E> current = root;
        boolean insertLeft = false;

        // Find the insertion point
        while (current != null) {
            int cmp = o.compareTo(current.getValue());
            if (cmp == 0) return false; // Already exists

            path.add(current);
            insertLeft = cmp < 0;
            current = insertLeft ? current.getLeft() : current.getRight();
        }

        // Insert new node
        PriorityTop<E> newNode = new PriorityTop<>(o, priority);
        PriorityTop<E> parent = path.getLast();
        if (insertLeft) {
            parent.setLeft(newNode);
        } else {
            parent.setRight(newNode);
        }
        path.add(newNode);
        size++;

        // Bubble up to maintain heap property
        bubbleUp(path);
        return true;
    }

    /**
     * Bubble up the last node in a path if its priority exceeds its parent's.
     */
    protected void bubbleUp(List<PriorityTop<E>> path) {
        for (int i = path.size() - 1; i > 0; i--) {
            PriorityTop<E> node = path.get(i);
            PriorityTop<E> parent = path.get(i - 1);

            if (node.getPriority() <= parent.getPriority()) break;

            // Rotate node up
            PriorityTop<E> grandparent = (i > 1) ? path.get(i - 2) : null;
            boolean isLeftChild = parent.getLeft() == node;

            PriorityTop<E> newSubtreeRoot = isLeftChild ? parent.rotateRight() : parent.rotateLeft();

            // Update grandparent or root
            if (grandparent == null) {
                root = newSubtreeRoot;
            } else if (grandparent.getLeft() == parent) {
                grandparent.setLeft(newSubtreeRoot);
            } else {
                grandparent.setRight(newSubtreeRoot);
            }

            path.set(i - 1, newSubtreeRoot);
        }
    }

    @Override
    public boolean remove(E e) {
        if (e == null || root == null) return false;

        // Find a node and its parent
        PriorityTop<E> parent = null;
        PriorityTop<E> current = root;
        boolean isLeftChild = false;

        while (current != null) {
            int cmp = e.compareTo(current.getValue());
            if (cmp == 0) break;

            parent = current;
            if (cmp < 0) {
                current = current.getLeft();
                isLeftChild = true;
            } else {
                current = current.getRight();
                isLeftChild = false;
            }
        }

        if (current == null) return false;

        // Rotate down iteratively until the node is a leaf
        while (current.getLeft() != null || current.getRight() != null) {
            PriorityTop<E> l = current.getLeft();
            PriorityTop<E> r = current.getRight();

            // Determine rotation direction: prefer child with higher priority
            boolean rotateRight = (r == null) || (l != null && l.getPriority() > r.getPriority());
            PriorityTop<E> newSubtreeRoot = rotateRight ? current.rotateRight() : current.rotateLeft();

            // Update parent link
            if (parent == null) {
                root = newSubtreeRoot;
            } else if (isLeftChild) {
                parent.setLeft(newSubtreeRoot);
            } else {
                parent.setRight(newSubtreeRoot);
            }

            parent = newSubtreeRoot;
            isLeftChild = !rotateRight; // After right rotation, current becomes right child; after left rotation, left child
        }

        // Remove leaf node
        if (parent == null) {
            root = null;
        } else if (isLeftChild) {
            parent.setLeft(null);
        } else {
            parent.setRight(null);
        }

        size--;
        return true;
    }

    @Override
    public PriorityNode<E> root() {
        return root;
    }

    @Override
    public List<E> values() {
        return SearchTree.inOrderTraversal(root, size);
    }
}