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

    public MyTreap() {
        super();
        this.insertionCounter = 0;
    }

    /**
     * Generates time-based priority using insertion counter.
     * Each new element gets a higher priority than the previous one.
     *
     * @param element the element being inserted (unused, priority is based on insertion order only)
     * @return the priority value (current insertionCounter, then incremented)
     */
    @Override
    protected long generatePriority(E element) {
        return insertionCounter++;
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
