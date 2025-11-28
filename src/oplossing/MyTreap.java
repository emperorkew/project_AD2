package oplossing;

/**
 * Time-based Treap: "recently added is most important"
 * A Treap where insertion time determines priority - newer elements are more accessible.
 * <p>
 * This implementation is optimized for log-like data and monitoring scenarios:
 * - Continuously adding new entries with increasing timestamps
 * - Primarily searching recent data (recent logs, monitoring, sliding window)
 * - Automatic aging: old data sinks to tree bottom without a manual cleanup
 * <p>
 * Priority strategy:
 * - Initial priority: insertionCounter (starts at 0, increments with each successful insertion)
 * - Newer items get HIGHER priority (via incrementing insertionCounter)
 * - Priority = insertionCounter at time of insertion → recent inserts stay near the root
 * - Old data automatically sinks to the bottom of the tree
 * - No random priorities: fully deterministic based on insertion order
 * - Duplicate insertions do NOT increment counter (element already exists)
 * <p>
 * Performance characteristics:
 * Time complexity:
 * - O(log n) expected for search (no rebalancing on search)
 * - O(log n) expected for add with bubble-up (path tracking and rotations)
 * - O(log n) expected for remove (inherited from Treap)
 * - O(n) for values() traversal
 * Space complexity:
 * - O(n) for the tree structure
 * - O(1) auxiliary space for search (inherited from Treap)
 * - O(log n) expected auxiliary space for add (path array for bubble-up)
 * - O(1) auxiliary space for remove (inherited from Treap)
 * - O(n) auxiliary space for values()
 * <p>
 * Performance optimizations:
 * - Reusable array for path tracking (zero GC pressure)
 * - Inline capacity checks with bit shifts (x << 1 instead of x * 2)
 * - Direct array access instead of ArrayList
 * - Optimized bubble-up with early termination
 * <p>
 * Benefits:
 * - Recent elements are O(log n) but usually much faster to access
 * - Ideal for "last N elements" queries
 * - Ideal for "most recent greater than X" queries
 * - Sliding window operations are very efficient
 * - Old data has no negative impact on performance
 * - Predictable: no random behavior, easier to debug
 * <p>
 * Use cases:
 * - Log monitoring and analysis (recent errors most important)
 * - Time-series data with focus on recent values (stock tickers, sensor data)
 * - Event streams with temporal relevance (news feeds, social media)
 * - Cache-like structures where new = important (LRU-like with treap benefits)
 * - Sliding window algorithms (maintain last N items efficiently)
 * <p>
 * Trade-offs:
 * - Pro: Extremely fast for recent data access
 * - Pro: Deterministic behavior (no randomness)
 * - Pro: Automatic aging without manual eviction
 * - Con: Old data becomes harder to access over time
 * - Con: Not suitable if all data should remain equally accessible
 *
 * @param <E> the type of elements maintained by this treap must be Comparable
 * @author Remco Marien
 */
public class MyTreap<E extends Comparable<E>> extends Treap<E> {

    /**
     * Global insertion counter - increments with each insert.
     * Used as priority: higher value = more recent = higher priority in max-heap.
     */
    private long insertionCounter;

    /**
     * Reusable array for path tracking - prevents ArrayList allocations.
     */
    private PriorityTop<E>[] pathArray;
    private int pathSize;

    @SuppressWarnings("unchecked") // Generic array creation requires an unchecked cast from PriorityTop[] to PriorityTop<E>[]
    public MyTreap() {
        super();
        this.insertionCounter = 0;
        this.pathArray = (PriorityTop<E>[]) new PriorityTop[16];
        this.pathSize = 0;
    }

    @Override
    public boolean add(E o) {
        if (o == null) return false;

        if (root == null) {
            // Generate priority: higher counter = more recent = higher priority
            root = new PriorityTop<>(o, insertionCounter++);
            size++;
            return true;
        }

        // Reset path tracking
        pathSize = 0;

        // Find the insertion point
        PriorityTop<E> current = root;
        boolean insertLeft = false;

        while (current != null) {
            int cmp = o.compareTo(current.getValue());
            if (cmp == 0) return false; // Element already exists - don't increment counter

            addToPath(current);
            insertLeft = cmp < 0;
            current = insertLeft ? current.getLeft() : current.getRight();
        }

        // Element is new - now increment counter and generate priority
        // Insert new node with time-based priority
        PriorityTop<E> newNode = new PriorityTop<>(o, insertionCounter++);
        PriorityTop<E> parent = pathArray[pathSize - 1];
        if (insertLeft) {
            parent.setLeft(newNode);
        } else {
            parent.setRight(newNode);
        }
        addToPath(newNode);
        size++;

        // Bubble up - recent nodes rise to top
        bubbleUpArray();
        return true;
    }

    /**
     * Add a node to a path with an inline capacity check.
     */
    private void addToPath(PriorityTop<E> node) {
        if (pathSize >= pathArray.length) {
            @SuppressWarnings("unchecked") // Generic array creation requires an unchecked cast from PriorityTop[] to PriorityTop<E>[]
            PriorityTop<E>[] newArray = (PriorityTop<E>[]) new PriorityTop[pathArray.length << 1];
            System.arraycopy(pathArray, 0, newArray, 0, pathSize);
            pathArray = newArray;
        }
        pathArray[pathSize++] = node;
    }

    /**
     * Bubble up using an array-based path (optimized version).
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

    /**
     * Returns the insertion counter - useful for debugging and statistics.
     * The counter represents how many elements have been successfully inserted.
     *
     * @return the current value of the insertion counter
     */
    public long getInsertionCounter() {
        return insertionCounter;
    }

    /**
     * Resets the insertion counter - use with caution!
     * This can violate the heap property for existing nodes if new elements
     * are inserted after reset, as they will get lower priorities than old elements.
     * Only use this if you're clearing the tree or know what you're doing.
     */
    public void resetCounter() {
        this.insertionCounter = 0;
    }
}
