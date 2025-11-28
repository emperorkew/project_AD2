package oplossing;

/**
 * Tijdgestuurde Treap: "recent toegevoegd is het belangrijkst"
 * <p>
 * Deze implementatie is geoptimaliseerd voor log-achtige data en monitoring scenarios:
 * - Constant nieuwe entries toevoegen met stijgende timestamps
 * - Vooral zoeken in de nieuwste data (recent logs, monitoring, sliding window)
 * <p>
 * Priority-strategie:
 * - Nieuwere items krijgen HOGERE priority (via insertionCounter)
 * - Priority = -insertionTime → recente inserts komen dicht bij de root
 * - Oude data zakt automatisch naar beneden in de boom
 * <p>
 * Voordelen:
 * - Recente elementen zijn O(log n) maar meestal veel sneller bereikbaar
 * - Ideaal voor "laatste N elementen" queries
 * - Ideaal voor "recentste groter dan X" queries
 * - Sliding window operations zijn zeer efficiënt
 * - Oude rommel heeft geen negatieve invloed op performance
 * <p>
 * Use cases:
 * - Log monitoring en analyse
 * - Time-series data met focus op recente waarden
 * - Event streams met tijdelijke relevantie
 * - Cache-achtige structuren waar nieuw = belangrijk
 */
public class
MyTreap<E extends Comparable<E>> extends Treap<E> {

    /**
     * Globale insertion counter - stijgt met elke insert.
     * Gebruikt als priority: hogere waarde = recenter = hogere priority in max-heap.
     */
    private long insertionCounter;

    public MyTreap() {
        super();
        this.insertionCounter = 0;
    }

    /**
     * Constructor met custom seed voor deterministische tests.
     */
    public MyTreap(long seed) {
        super(seed);
        this.insertionCounter = 0;
    }

    @Override
    public boolean add(E o) {
        if (o == null) return false;

        // Genereer priority: hogere counter = recenter = hogere priority
        long priority = insertionCounter++;

        if (root == null) {
            root = new PriorityTop<>(o, priority);
            size++;
            return true;
        }

        // Standaard treap insertion met gegenereerde priority
        java.util.List<PriorityTop<E>> path = new java.util.ArrayList<>(estimateHeight());
        PriorityTop<E> current = root;
        boolean insertLeft = false;

        while (current != null) {
            int cmp = o.compareTo(current.getValue());
            if (cmp == 0) return false; // Element bestaat al

            path.add(current);
            insertLeft = cmp < 0;
            current = insertLeft ? current.getLeft() : current.getRight();
        }

        // Insert nieuwe node met time-based priority
        PriorityTop<E> newNode = new PriorityTop<>(o, priority);
        PriorityTop<E> parent = path.get(path.size() - 1);
        if (insertLeft) {
            parent.setLeft(newNode);
        } else {
            parent.setRight(newNode);
        }
        path.add(newNode);
        size++;

        // Bubble up - recente nodes stijgen naar top
        bubbleUp(path);
        return true;
    }

    /**
     * Geeft de insertion counter terug - nuttig voor debugging en statistieken.
     */
    public long getInsertionCounter() {
        return insertionCounter;
    }

    /**
     * Reset de insertion counter - gebruik met voorzichtigheid!
     * Dit kan de heap property verstoren voor bestaande nodes.
     */
    public void resetCounter() {
        this.insertionCounter = 0;
    }
}
