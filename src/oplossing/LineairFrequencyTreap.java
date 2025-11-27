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

        // Track path for bubble-up after priority increase
        List<PriorityTop<E>> path = new ArrayList<>(estimateHeight());
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

        // Track path for bubble-up with adaptive capacity
        List<PriorityTop<E>> path = new ArrayList<>(estimateHeight());
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
}