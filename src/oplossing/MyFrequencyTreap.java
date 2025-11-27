package oplossing;

import java.util.ArrayList;
import java.util.List;

/**
 * Een Treap die de prioriteit van nodes logaritmisch verhoogt bij elke toegang.
 * <p>
 * De prioriteit wordt berekend als: Priority = log2(accessCount)
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
 * Dit is vooral nuttig voor Zipf-achtige access patterns waar enkele elementen
 * zeer vaak worden opgevraagd, maar je wilt voorkomen dat de boom te sterk uit balans raakt.
 */
public class MyFrequencyTreap<E extends Comparable<E>> extends Treap<E> {

    /**
     * Calculates logarithmic priority based on access count.
     * Formula: Priority = log2(accessCount) * 1M
     * <p>
     * The logarithmic scaling provides diminishing returns, which prevents
     * starvation of infrequently accessed nodes while still prioritizing
     * frequently accessed elements.
     *
     * @param accessCount number of times this node has been accessed
     * @return the logarithmic priority value
     */
    private long calculatePriority(long accessCount) {
        if (accessCount <= 0) return 0;
        // log2(x) = ln(x) / ln(2)
        // We multiply by a large constant to maintain precision in long
        return (long) (Math.log(accessCount) / Math.log(2) * 1_000_000);
    }

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
                // Found - increase priority logarithmically
                // We interpret current priority as access count encoded
                long currentAccessCount = decodeAccessCount(current.getPriority());
                long newAccessCount = currentAccessCount + 1;
                current.setPriority(calculatePriority(newAccessCount));
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
            // Initial access count = 1, priority = log2(1) * 1M = 0
            root = new PriorityTop<>(o, calculatePriority(1));
            size++;
            return true;
        }

        // Track path for bubble-up
        List<PriorityTop<E>> path = new ArrayList<>(estimateHeight());
        PriorityTop<E> current = root;

        while (current != null) {
            path.add(current);
            int cmp = o.compareTo(current.getValue());

            if (cmp == 0) {
                // Element exists - increase priority (counts as access)
                long currentAccessCount = decodeAccessCount(current.getPriority());
                long newAccessCount = currentAccessCount + 1;
                current.setPriority(calculatePriority(newAccessCount));
                bubbleUp(path);
                return false;
            }

            if (cmp < 0) {
                if (current.getLeft() == null) {
                    PriorityTop<E> newNode = new PriorityTop<>(o, calculatePriority(1));
                    current.setLeft(newNode);
                    path.add(newNode);
                    size++;
                    bubbleUp(path);
                    return true;
                }
                current = current.getLeft();
            } else {
                if (current.getRight() == null) {
                    PriorityTop<E> newNode = new PriorityTop<>(o, calculatePriority(1));
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
     * Decodes the access count from the priority value.
     * Since priority = log2(accessCount) * 1M, we reverse this:
     * accessCount = 2^(priority / 1M)
     *
     * @param priority the encoded priority
     * @return the decoded access count
     */
    private long decodeAccessCount(long priority) {
        if (priority <= 0) return 1;
        double logValue = priority / 1_000_000.0;
        return Math.max(1, Math.round(Math.pow(2, logValue)));
    }
}
