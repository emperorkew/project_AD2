package oplossing;

/**
 * Een Treap die de prioriteit van nodes lineair verhoogt bij elke toegang.
 * Frequenter bezochte nodes stijgen naar de top van de boom.
 *
 * Optimized implementation:
 * - Reuses a single path array to avoid repeated allocations
 * - Uses array instead of ArrayList for better cache locality
 * - Manual path tracking without extra list operations
 */
public class LineairFrequencyTreap<E extends Comparable<E>> extends Treap<E> {

    // Reusable path array to avoid allocations on every operation
    private PriorityTop<E>[] pathArray;
    private int pathSize;

    @SuppressWarnings("unchecked")
    public LineairFrequencyTreap() {
        super();
        // Initial capacity for path - will grow if needed
        this.pathArray = (PriorityTop<E>[]) new PriorityTop[64];
        this.pathSize = 0;
    }

    /**
     * Ensures the path array has sufficient capacity.
     * Doubles the size if more space is needed.
     */
    @SuppressWarnings("unchecked")
    private void ensurePathCapacity(int required) {
        if (required > pathArray.length) {
            int newSize = Math.max(required, pathArray.length * 2);
            PriorityTop<E>[] newArray = (PriorityTop<E>[]) new PriorityTop[newSize];
            System.arraycopy(pathArray, 0, newArray, 0, pathSize);
            pathArray = newArray;
        }
    }

    @Override
    public boolean search(E o) {
        if (o == null || root == null) return false;

        // Reset path tracking
        pathSize = 0;
        PriorityTop<E> current = root;

        while (current != null) {
            ensurePathCapacity(pathSize + 1);
            pathArray[pathSize++] = current;

            int cmp = o.compareTo(current.getValue());

            if (cmp == 0) {
                // Found - increase priority linearly and bubble up
                current.setPriority(current.getPriority() + 1);
                bubbleUpArray();
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

        // Reset path tracking
        pathSize = 0;
        PriorityTop<E> current = root;

        while (current != null) {
            ensurePathCapacity(pathSize + 1);
            pathArray[pathSize++] = current;

            int cmp = o.compareTo(current.getValue());

            if (cmp == 0) {
                // Element exists - increase priority (counts as access)
                current.setPriority(current.getPriority() + 1);
                bubbleUpArray();
                return false;
            }

            if (cmp < 0) {
                if (current.getLeft() == null) {
                    PriorityTop<E> newNode = new PriorityTop<>(o, 1);
                    current.setLeft(newNode);
                    ensurePathCapacity(pathSize + 1);
                    pathArray[pathSize++] = newNode;
                    size++;
                    bubbleUpArray();
                    return true;
                }
                current = current.getLeft();
            } else {
                if (current.getRight() == null) {
                    PriorityTop<E> newNode = new PriorityTop<>(o, 1);
                    current.setRight(newNode);
                    ensurePathCapacity(pathSize + 1);
                    pathArray[pathSize++] = newNode;
                    size++;
                    bubbleUpArray();
                    return true;
                }
                current = current.getRight();
            }
        }

        return false;
    }

    /**
     * Bubble up using the reusable path array instead of a List.
     * More efficient as it avoids boxing/unboxing and uses direct array access.
     */
    private void bubbleUpArray() {
        for (int i = pathSize - 1; i > 0; i--) {
            PriorityTop<E> node = pathArray[i];
            PriorityTop<E> parent = pathArray[i - 1];

            if (node.getPriority() <= parent.getPriority()) break;

            // Rotate node up
            PriorityTop<E> grandparent = (i > 1) ? pathArray[i - 2] : null;
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

            pathArray[i - 1] = newSubtreeRoot;
        }
    }
}