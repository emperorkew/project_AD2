package oplossing;

import java.util.IdentityHashMap;
import java.util.Map;

/**
 * A Treap that logarithmically increases node priority on each access.
 * Provides balanced performance between frequently and infrequently accessed nodes.
 * <p>
 * Priority calculation:
 * - Initial priority: 0 (new nodes start at priority 0, representing log₂(1) = 0)
 * - Priority = log₂(accessCount) × 1,000,000
 * - Each search/add of existing element increments access count, recalculates priority
 * - First accesses have high impact, then diminishing returns
 * - Prevents extreme dominance by hot nodes
 * <p>
 * Performance characteristics:
 * Time complexity:
 * - O(log n) expected for search and add with rebalancing (bubble-up after priority increase)
 * - O(log n) expected for remove (searches twice: once for cleanup, once for removal)
 * - Worst case O(n) if priorities become pathologically ordered
 * - O(n) for values() traversal
 * - O(1) for priority calculation (cached for powers of 2, otherwise fast log computation)
 * Space complexity:
 * - O(n) for the tree structure
 * - O(n) for accessCounts IdentityHashMap (stores access count for each node)
 * - O(log n) expected auxiliary space for search and add (path array for bubble-up)
 * - O(1) auxiliary space for remove (only local variables)
 * - O(n) auxiliary space for values()
 * <p>
 * Why logarithmic scaling?
 * - Better balance: Very frequent nodes are less dominant than with linear scaling
 * - Prevents starvation: Infrequently accessed nodes can catch up relatively quickly
 * - Diminishing returns: Doubling access count only adds constant priority
 * <p>
 * Comparison example:
 * - Node with 1000 accesses: log₂(1000) ≈ 10 (priority ~10M)
 * - Node with 10 accesses: log₂(10) ≈ 3.3 (priority ~3.3M)
 * - To match priority, node needs ~50 more accesses (log₂(60) ≈ 6)
 * - With linear scaling, it would need 990 more accesses!
 * <p>
 * Highly optimized implementation:
 * - Reuses a single path array to eliminate allocations (zero GC pressure)
 * - IdentityHashMap for O(1) access count lookups (reference equality only)
 * - Precomputed log values for common access counts (powers of 2: 1, 2, 4, 8, 16, 32, 64, 128, 256)
 * - Fast path for powers of 2: bit manipulation instead of Math.log()
 * - Math.round() for proper floating-point precision
 * <p>
 * Use cases:
 * - Balanced caching where both hot and warm data matter
 * - Scenarios with Zipf-like distributions (web caching, word frequency)
 * - When you want frequency-based optimization without extreme imbalance
 * <p>
 * Trade-offs vs linear frequency:
 * - Pro: Better balance, no starvation, more predictable performance
 * - Pro: Extremely hot nodes don't monopolize the tree structure
 * - Con: Slightly more complex priority calculation
 * - Con: HashMap overhead for access count tracking
 *
 * @param <E> the type of elements maintained by this treap must be Comparable
 * @author Remco Marien
 */
public class MyFrequencyTreap<E extends Comparable<E>> extends Treap<E> {

    private static final double INV_LN2 = 1.4426950408889634;  // 1 / ln(2)
    private static final long SCALE = 1_000_000L;

    // Precomputed log priorities for common access counts (powers of 2: 1, 2, 4, 8, 16, 32, 64, 128, 256)
    private static final long[] LOG_CACHE = {
        0L,         // log2(1) * 1M
        1_000_000L, // log2(2) * 1M
        2_000_000L, // log2(4) * 1M
        3_000_000L, // log2(8) * 1M
        4_000_000L, // log2(16) * 1M
        5_000_000L, // log2(32) * 1M
        6_000_000L, // log2(64) * 1M
        7_000_000L, // log2(128) * 1M
        8_000_000L  // log2(256) * 1M
    };

    private PriorityTop<E>[] pathArray;
    private int pathSize;
    private final Map<PriorityTop<E>, Long> accessCounts;

    @SuppressWarnings("unchecked")
    public MyFrequencyTreap() {
        super();
        this.pathArray = (PriorityTop<E>[]) new PriorityTop[64];
        // IdentityHashMap is faster since we only need reference equality
        this.accessCounts = new IdentityHashMap<>();
    }

    /**
     * Calculates logarithmic priority from access count.
     * Uses cache for powers of 2, computes others.
     */
    private static long calculatePriority(long accessCount) {
        if (accessCount <= 1) return 0;

        // Fast path: check if it's a power of 2 in cache range
        if (accessCount <= 256 && (accessCount & (accessCount - 1)) == 0) {
            // Count trailing zeros to get the power: 2^n
            int power = Long.numberOfTrailingZeros(accessCount);
            if (power < LOG_CACHE.length) {
                return LOG_CACHE[power];
            }
        }

        // Fallback: compute logarithm
        return Math.round(Math.log(accessCount) * INV_LN2 * SCALE);
    }

    /**
     * Ensures sufficient capacity and adds a node to the path.
     */
    @SuppressWarnings("unchecked")
    private void addToPath(PriorityTop<E> node) {
        if (pathSize >= pathArray.length) {
            PriorityTop<E>[] newArray = (PriorityTop<E>[]) new PriorityTop[pathArray.length << 1];
            System.arraycopy(pathArray, 0, newArray, 0, pathSize);
            pathArray = newArray;
        }
        pathArray[pathSize++] = node;
    }

    /**
     * Increments access count and updates priority.
     */
    private void incrementAccess(PriorityTop<E> node) {
        long accessCount = accessCounts.getOrDefault(node, 1L) + 1;
        accessCounts.put(node, accessCount);
        node.setPriority(calculatePriority(accessCount));
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
                incrementAccess(current);
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
            root = new PriorityTop<>(o, 0); // Priority 0 for new node
            accessCounts.put(root, 1L); // Access count starts at 1
            size++;
            return true;
        }

        pathSize = 0;
        PriorityTop<E> current = root;

        while (true) {
            addToPath(current);
            int cmp = o.compareTo(current.getValue());

            if (cmp == 0) {
                incrementAccess(current);
                bubbleUpArray();
                return false;
            }

            PriorityTop<E> next = (cmp < 0) ? current.getLeft() : current.getRight();

            if (next == null) {
                PriorityTop<E> newNode = new PriorityTop<>(o, 0); // Priority 0 for new node
                accessCounts.put(newNode, 1L); // Access count starts at 1
                if (cmp < 0) {
                    current.setLeft(newNode);
                } else {
                    current.setRight(newNode);
                }
                addToPath(newNode);
                size++;
                bubbleUpArray();
                return true;
            }

            current = next;
        }
    }

    /**
     * Bubbles up using the reusable path array.
     */
    private void bubbleUpArray() {
        int i = pathSize - 1;

        while (i > 0) {
            PriorityTop<E> node = pathArray[i];
            PriorityTop<E> parent = pathArray[i - 1];

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

            pathArray[i - 1] = newSubtreeRoot;
            i--;
        }
    }

    @Override
    public boolean remove(E e) {
        if (e == null || root == null) return false;

        // Find and store node reference before removal to clean up accessCounts
        PriorityTop<E> current = root;
        PriorityTop<E> nodeToRemove = null;

        while (current != null) {
            int cmp = e.compareTo(current.getValue());
            if (cmp == 0) {
                nodeToRemove = current;
                break;
            }
            current = (cmp < 0) ? current.getLeft() : current.getRight();
        }

        // Perform actual tree removal
        boolean removed = super.remove(e);

        // Clean up accessCounts only if removal was successful
        if (removed && nodeToRemove != null) {
            accessCounts.remove(nodeToRemove);
        }

        return removed;
    }
}
