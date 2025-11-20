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

        // Track path for bubble-up (capacity 32 covers trees up to ~4B nodes)
        List<PriorityTop<E>> path = new ArrayList<>(32);
        PriorityTop<E> current = root;

        // Find insertion point
        while (current != null) {
            int cmp = o.compareTo(current.getValue());
            if (cmp == 0) return false; // Already exists

            path.add(current);
            current = (cmp < 0) ? current.getLeft() : current.getRight();
        }

        // Insert new node
        PriorityTop<E> newNode = new PriorityTop<>(o, priority);
        PriorityTop<E> parent = path.get(path.size() - 1);
        if (o.compareTo(parent.getValue()) < 0) {
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
     * Bubble up the last node in path if its priority exceeds its parent's.
     */
    protected void bubbleUp(List<PriorityTop<E>> path) {
        for (int i = path.size() - 1; i > 0; i--) {
            PriorityTop<E> node = path.get(i);
            PriorityTop<E> parent = path.get(i - 1);

            if (node.getPriority() <= parent.getPriority()) break;

            // Rotate node up
            PriorityTop<E> grandparent = (i > 1) ? path.get(i - 2) : null;
            boolean isLeftChild = parent.getLeft() == node;

            PriorityTop<E> newSubtreeRoot = isLeftChild ? rotateRight(parent) : rotateLeft(parent);

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

        // Find node and its parent
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

        // Rotate down iteratively until node is a leaf
        while (current.getLeft() != null || current.getRight() != null) {
            PriorityTop<E> l = current.getLeft();
            PriorityTop<E> r = current.getRight();

            boolean goRight = (r == null) || (l != null && l.getPriority() > r.getPriority());
            PriorityTop<E> newSubtreeRoot;

            if (goRight) {
                newSubtreeRoot = rotateRight(current);
                // Update parent link
                if (parent == null) {
                    root = newSubtreeRoot;
                } else if (isLeftChild) {
                    parent.setLeft(newSubtreeRoot);
                } else {
                    parent.setRight(newSubtreeRoot);
                }
                parent = newSubtreeRoot;
                isLeftChild = false; // current is now right child of newSubtreeRoot
            } else {
                newSubtreeRoot = rotateLeft(current);
                // Update parent link
                if (parent == null) {
                    root = newSubtreeRoot;
                } else if (isLeftChild) {
                    parent.setLeft(newSubtreeRoot);
                } else {
                    parent.setRight(newSubtreeRoot);
                }
                parent = newSubtreeRoot;
                isLeftChild = true; // current is now left child of newSubtreeRoot
            }
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
        List<E> result = new ArrayList<>(size);
        // Iterative in-order traversal using Morris-like approach with explicit stack
        PriorityTop<E> current = root;
        PriorityTop<E>[] stack = (PriorityTop<E>[]) new PriorityTop[32];
        int top = -1;

        while (current != null || top >= 0) {
            // Go to leftmost node
            while (current != null) {
                if (++top >= stack.length) {
                    // Resize stack if needed (rare for balanced trees)
                    PriorityTop<E>[] newStack = (PriorityTop<E>[]) new PriorityTop[stack.length * 2];
                    System.arraycopy(stack, 0, newStack, 0, stack.length);
                    stack = newStack;
                }
                stack[top] = current;
                current = current.getLeft();
            }

            // Process node
            current = stack[top--];
            result.add(current.getValue());

            // Move to right subtree
            current = current.getRight();
        }

        return result;
    }
}