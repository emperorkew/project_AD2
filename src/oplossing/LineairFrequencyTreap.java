package oplossing;

/**
 * A Treap that linearly increases node priority on each access.
 * More frequently accessed nodes rise to the top of the tree.
 * <p>
 * Priority strategy:
 * - Initial priority: 1 (new nodes start at priority 1)
 * - Priority increases linearly with each access (priority++)
 * - Each search/add of existing element: priority += 1
 * - Frequently accessed nodes get proportionally higher priority
 * - Ideal for scenarios where access frequency strongly correlates with importance
 * <p>
 * Performance characteristics:
 * Time complexity:
 * - O(log n) expected for search and add with rebalancing (bubble-up after priority increase)
 * - O(log n) expected for remove (inherited from Treap)
 * - Worst case O(n) if priorities become pathologically ordered
 * - O(n) for values() traversal
 * Space complexity:
 * - O(n) for the tree structure
 * - O(log n) expected auxiliary space for search and add (path array for bubble-up)
 * - O(1) auxiliary space for remove (inherited from Treap)
 * - O(n) auxiliary space for values()
 * <p>
 * Highly optimized implementation:
 * - Reuses a single path array to eliminate allocations (zero GC pressure)
 * - Inlined capacity checks for better performance
 * - Minimized method calls and redundant operations
 * - Bit shift for array doubling (x << 1 instead of x * 2)
 * <p>
 * Use cases:
 * - Caching with strong locality (80/20 rule)
 * - Hot-spot optimization where few elements dominate access patterns
 * - Scenarios where recent AND frequent access both matter
 * <p>
 * Trade-offs vs logarithmic frequency:
 * - Pro: Simple linear scaling, very hot nodes become extremely accessible
 * - Con: Can cause extreme imbalance with skewed access patterns
 * - Con: Less-accessed nodes may become unreachable in pathological cases
 *
 * @param <E> the type of elements maintained by this treap must be Comparable
 * @author Remco Marien
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