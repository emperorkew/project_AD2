package oplossing;

import java.util.ArrayList;
import java.util.List;

/**
 * Een Treap die de prioriteit van nodes lineair verhoogt bij elke toegang.
 * Frequenter bezochte nodes stijgen naar de top van de boom.
 */
public class LineairFrequencyTreap<E extends Comparable<E>> extends Treap<E> {

    @Override
    public boolean search(E o) {
        if (o == null || root == null) return false;

        // Track path for bubble-up after priority increase (capacity 32 covers ~4B nodes)
        List<PriorityTop<E>> path = new ArrayList<>(32);
        PriorityTop<E> current = root;

        while (current != null) {
            path.add(current);
            int cmp = o.compareTo(current.getValue());

            if (cmp == 0) {
                // Found - increase priority linearly and bubble up
                current.setPriority(current.getPriority() + 1);
                bubbleUp(path);
                return true;
            }
            current = (cmp < 0) ? current.getLeft() : current.getRight();
        }

        return false;
    }

    @Override
    public boolean add(E o) {
        if (o == null) return false;

        if (root == null) {
            root = new PriorityTop<>(o, 1);
            size++;
            return true;
        }

        // Track path for bubble-up (capacity 32 covers ~4B nodes)
        List<PriorityTop<E>> path = new ArrayList<>(32);
        PriorityTop<E> current = root;

        while (current != null) {
            path.add(current);
            int cmp = o.compareTo(current.getValue());

            if (cmp == 0) {
                // Element exists - increase priority (counts as access)
                current.setPriority(current.getPriority() + 1);
                bubbleUp(path);
                return false;
            }

            if (cmp < 0) {
                if (current.getLeft() == null) {
                    PriorityTop<E> newNode = new PriorityTop<>(o, 1);
                    current.setLeft(newNode);
                    path.add(newNode);
                    size++;
                    bubbleUp(path);
                    return true;
                }
                current = current.getLeft();
            } else {
                if (current.getRight() == null) {
                    PriorityTop<E> newNode = new PriorityTop<>(o, 1);
                    current.setRight(newNode);
                    path.add(newNode);
                    size++;
                    bubbleUp(path);
                    return true;
                }
                current = current.getRight();
            }
        }

        return false;
    }

    /**
     * Bubble up the last node in path if its priority exceeds its parent's.
     */
    private void bubbleUp(List<PriorityTop<E>> path) {
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

            // Update path for next iteration
            path.set(i - 1, newSubtreeRoot);
        }
    }
}