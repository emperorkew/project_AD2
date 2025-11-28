package oplossing;

/**
 * Een Treap die de prioriteit van nodes lineair verhoogt bij elke toegang.
 * Frequenter bezochte nodes stijgen naar de top van de boom.
 *
 * Highly optimized implementation:
 * - Reuses a single path array to eliminate allocations
 * - Inlined capacity checks for better performance
 * - Minimized method calls and redundant operations
 * - Extracted common logic to reduce code duplication
 */
public class LineairFrequencyTreap<E extends Comparable<E>> extends Treap<E> {

    private PriorityTop<E>[] pathArray;
    private int pathSize;

    @SuppressWarnings("unchecked")
    public LineairFrequencyTreap() {
        super();
        this.pathArray = (PriorityTop<E>[]) new PriorityTop[64];
    }

    /**
     * Ensures sufficient capacity and adds a node to the path.
     * Inlined to avoid method call overhead in hot path.
     */
    @SuppressWarnings("unchecked")
    private void addToPath(PriorityTop<E> node) {
        if (pathSize >= pathArray.length) {
            // Grow array - double size
            PriorityTop<E>[] newArray = (PriorityTop<E>[]) new PriorityTop[pathArray.length << 1];
            System.arraycopy(pathArray, 0, newArray, 0, pathSize);
            pathArray = newArray;
        }
        pathArray[pathSize++] = node;
    }

    @Override
    public boolean search(E o) {
        if (o == null || root == null) return false;

        pathSize = 0;
        PriorityTop<E> current = root;

        while (current != null) {
            addToPath(current);
            int cmp = o.compareTo(current.getValue());

            if (cmp == 0) {
                current.setPriority(current.getPriority() + 1);
                bubbleUp();
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

        pathSize = 0;
        PriorityTop<E> current = root;

        while (true) {
            addToPath(current);
            int cmp = o.compareTo(current.getValue());

            if (cmp == 0) {
                current.setPriority(current.getPriority() + 1);
                bubbleUp();
                return false;
            }

            PriorityTop<E> next = (cmp < 0) ? current.getLeft() : current.getRight();

            if (next == null) {
                // Insert new node
                PriorityTop<E> newNode = new PriorityTop<>(o, 1);
                if (cmp < 0) {
                    current.setLeft(newNode);
                } else {
                    current.setRight(newNode);
                }
                addToPath(newNode);
                size++;
                bubbleUp();
                return true;
            }

            current = next;
        }
    }

    /**
     * Bubbles up the last node in the path to maintain heap property.
     * Optimized for minimal overhead and cache-friendly access patterns.
     */
    private void bubbleUp() {
        int i = pathSize - 1;

        while (i > 0) {
            PriorityTop<E> node = pathArray[i];
            PriorityTop<E> parent = pathArray[i - 1];

            // Early exit if heap property satisfied
            if (node.getPriority() <= parent.getPriority()) return;

            // Perform rotation
            boolean isLeftChild = parent.getLeft() == node;
            PriorityTop<E> newSubtreeRoot = isLeftChild ? parent.rotateRight() : parent.rotateLeft();

            // Update parent reference
            if (i == 1) {
                root = newSubtreeRoot;
            } else {
                PriorityTop<E> grandparent = pathArray[i - 2];
                if (grandparent.getLeft() == parent) {
                    grandparent.setLeft(newSubtreeRoot);
                } else {
                    grandparent.setRight(newSubtreeRoot);
                }
            }

            // Update path for next iteration
            pathArray[i - 1] = newSubtreeRoot;
            i--;
        }
    }
}