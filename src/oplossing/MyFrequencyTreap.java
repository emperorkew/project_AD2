package oplossing;

/**
 * Een Treap die de prioriteit van nodes logaritmisch verhoogt bij elke toegang.
 * <p>
 * De prioriteit wordt berekend als: Priority = log2(accessCount) * 1M
 * <p>
 * Dit zorgt voor:
 * - Logaritmische schaling: eerste accesses hebben meer impact, diminishing returns
 * - Betere balans: zeer frequente nodes zijn minder dominant dan bij lineaire schaling
 * - Voorkomt starvation: weinig bezochte nodes kunnen relatief snel inhalen
 * <p>
 * Voordelen van logaritmische schaling:
 * - Node met 1000 accesses: log₂(1000) ≈ 10
 * - Node met 10 accesses: log₂(10) ≈ 3.3
 * - Node met 10 accesses heeft ~50 extra accesses nodig om bij te komen (log₂(60) ≈ 6)
 * - Bij lineaire schaling zou dit 990 extra accesses zijn!
 * <p>
 * Highly optimized implementation:
 * - Reuses a single path array to eliminate allocations (zero GC pressure)
 * - Stores access count in upper 32 bits of priority for O(1) retrieval
 * - Uses bit operations for fast access count extraction
 * - Precomputed constants for all mathematical operations
 * - Extracted common logic to reduce code duplication
 */
public class MyFrequencyTreap<E extends Comparable<E>> extends Treap<E> {

    private static final double INV_LN2 = 1.4426950408889634;  // 1 / ln(2)
    private static final long SCALE = 1_000_000L;

    private PriorityTop<E>[] pathArray;
    private int pathSize;

    @SuppressWarnings("unchecked")
    public MyFrequencyTreap() {
        super();
        this.pathArray = (PriorityTop<E>[]) new PriorityTop[64];
    }

    /**
     * Encodes access count and logarithmic priority using bit packing:
     * Upper 32 bits: access count (for O(1) increment)
     * Lower 32 bits: log2(accessCount) * SCALE (for heap ordering)
     */
    private long encodePriority(long accessCount) {
        if (accessCount <= 1) return (1L << 32); // Access count = 1, log priority = 0

        long logPriority = (long) (Math.log(accessCount) * INV_LN2 * SCALE);
        return (accessCount << 32) | (logPriority & 0xFFFFFFFFL);
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
     * Efficiently extracts, increments, and re-encodes in one operation.
     */
    private void incrementAccess(PriorityTop<E> node) {
        long accessCount = (node.getPriority() >>> 32) + 1;  // Extract upper 32 bits and increment
        node.setPriority(encodePriority(accessCount));
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
            root = new PriorityTop<>(o, encodePriority(1));
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
                PriorityTop<E> newNode = new PriorityTop<>(o, encodePriority(1));
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
}
