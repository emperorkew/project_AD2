package test;

import opgave.PriorityNode;
import oplossing.MyTreap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite voor MyTreap - de tijdgestuurde treap waar "recent = belangrijk".
 *
 * Deze tests verifiëren:
 * - Basis treap functionaliteit (add, search, remove)
 * - Time-based priority systeem (insertion counter)
 * - Recent toegevoegde elementen hebben hogere priority
 * - Root is altijd de meest recent toegevoegde node
 * - Heap property wordt gehandhaafd
 */
public class MyTreapTest {

    private MyTreap<Integer> treap;

    @BeforeEach
    void setUp() {
        treap = new MyTreap<>();
    }

    // ==================== BASIC FUNCTIONALITY ====================

    @Test
    @DisplayName("New treap should have size zero")
    void newTreapShouldHaveSizeZero() {
        assertEquals(0, treap.size());
        assertTrue(treap.isEmpty());
        assertEquals(0, treap.getInsertionCounter());
    }

    @Test
    @DisplayName("Add should return true for new elements")
    void addShouldReturnTrueForNewElements() {
        assertTrue(treap.add(5));
        assertEquals(1, treap.size());
        assertEquals(1, treap.getInsertionCounter());
    }

    @Test
    @DisplayName("Add should return false for duplicate elements")
    void addShouldReturnFalseForDuplicates() {
        treap.add(5);
        assertFalse(treap.add(5));
        assertEquals(1, treap.size());
        assertEquals(1, treap.getInsertionCounter()); // Counter doesn't increase on duplicate
    }

    @Test
    @DisplayName("Add should return false for null")
    void addShouldReturnFalseForNull() {
        assertFalse(treap.add(null));
        assertEquals(0, treap.size());
        assertEquals(0, treap.getInsertionCounter());
    }

    @Test
    @DisplayName("Search should find existing elements")
    void searchShouldFindExistingElements() {
        treap.add(5);
        treap.add(3);
        treap.add(7);

        assertTrue(treap.search(5));
        assertTrue(treap.search(3));
        assertTrue(treap.search(7));
    }

    @Test
    @DisplayName("Search should return false for non-existing elements")
    void searchShouldReturnFalseForNonExisting() {
        treap.add(5);
        assertFalse(treap.search(10));
        assertFalse(treap.search(null));
    }

    @Test
    @DisplayName("Remove should return true for existing elements")
    void removeShouldReturnTrueForExisting() {
        treap.add(5);
        treap.add(3);
        treap.add(7);

        assertTrue(treap.remove(5));
        assertEquals(2, treap.size());
        assertFalse(treap.search(5));
    }

    @Test
    @DisplayName("Values should return elements in sorted order")
    void valuesShouldReturnSortedOrder() {
        treap.add(5);
        treap.add(3);
        treap.add(7);
        treap.add(1);
        treap.add(9);

        List<Integer> values = treap.values();
        assertEquals(List.of(1, 3, 5, 7, 9), values);
    }

    // ==================== TIME-BASED PRIORITY ====================

    @Test
    @DisplayName("Insertion counter should increment with each add")
    void insertionCounterShouldIncrement() {
        assertEquals(0, treap.getInsertionCounter());

        treap.add(10);
        assertEquals(1, treap.getInsertionCounter());

        treap.add(20);
        assertEquals(2, treap.getInsertionCounter());

        treap.add(30);
        assertEquals(3, treap.getInsertionCounter());
    }

    @Test
    @DisplayName("Most recent element should become root")
    void mostRecentShouldBecomeRoot() {
        treap.add(100);  // First insert
        assertEquals(100, treap.root().getValue());

        treap.add(50);   // Second insert (more recent)
        assertEquals(50, treap.root().getValue());

        treap.add(150);  // Third insert (most recent)
        assertEquals(150, treap.root().getValue());
    }

    @Test
    @DisplayName("Priority should match insertion order")
    void priorityShouldMatchInsertionOrder() {
        treap.add(100);  // Priority 0
        treap.add(50);   // Priority 1
        treap.add(150);  // Priority 2

        PriorityNode<Integer> node100 = findNode(treap.root(), 100);
        PriorityNode<Integer> node50 = findNode(treap.root(), 50);
        PriorityNode<Integer> node150 = findNode(treap.root(), 150);

        assertEquals(0, node100.getPriority());
        assertEquals(1, node50.getPriority());
        assertEquals(2, node150.getPriority());
    }

    @Test
    @DisplayName("Recent elements should have higher priority than old")
    void recentShouldHaveHigherPriority() {
        treap.add(100);  // Old (priority 0)
        treap.add(200);  // Recent (priority 1)

        PriorityNode<Integer> oldNode = findNode(treap.root(), 100);
        PriorityNode<Integer> recentNode = findNode(treap.root(), 200);

        assertTrue(recentNode.getPriority() > oldNode.getPriority(),
            "Recent element should have higher priority");
    }

    @Test
    @DisplayName("Root should always be the most recently added element")
    void rootShouldAlwaysBeRecent() {
        int[] insertions = {50, 25, 75, 10, 40, 60, 90};

        for (int value : insertions) {
            treap.add(value);
            assertEquals(value, treap.root().getValue(),
                "Root should be the most recently added element: " + value);
        }
    }

    // ==================== HEAP PROPERTY ====================

    @Test
    @DisplayName("Max-heap property should be maintained")
    void maxHeapPropertyShouldBeMaintained() {
        // Add many elements
        for (int i = 0; i < 20; i++) {
            treap.add(i * 10);
        }

        assertTrue(verifyMaxHeapProperty(treap.root()),
            "Max-heap property should be maintained");
    }

    @Test
    @DisplayName("Heap property should be maintained after multiple operations")
    void heapPropertyAfterOperations() {
        treap.add(50);
        treap.add(30);
        treap.add(70);
        treap.add(20);
        treap.add(40);

        assertTrue(verifyMaxHeapProperty(treap.root()));

        treap.remove(30);
        assertTrue(verifyMaxHeapProperty(treap.root()));

        treap.add(60);
        assertTrue(verifyMaxHeapProperty(treap.root()));
    }

    // ==================== BST PROPERTY ====================

    @Test
    @DisplayName("BST property should be maintained")
    void bstPropertyShouldBeMaintained() {
        treap.add(50);
        treap.add(30);
        treap.add(70);
        treap.add(20);
        treap.add(40);
        treap.add(60);
        treap.add(80);

        assertTrue(verifyBSTProperty(treap.root()));
    }

    // ==================== EDGE CASES ====================

    @Test
    @DisplayName("Single element tree should work correctly")
    void singleElementTree() {
        treap.add(42);

        assertEquals(1, treap.size());
        assertEquals(42, treap.root().getValue());
        assertEquals(0, treap.root().getPriority());
        assertTrue(treap.search(42));
    }

    @Test
    @DisplayName("Sequential insertion should maintain properties")
    void sequentialInsertion() {
        // Simulate log entries being added sequentially
        for (int i = 1; i <= 100; i++) {
            treap.add(i);
        }

        assertEquals(100, treap.size());
        assertEquals(100, treap.root().getValue()); // Most recent
        assertEquals(99, treap.root().getPriority()); // Counter - 1

        assertTrue(verifyMaxHeapProperty(treap.root()));
        assertTrue(verifyBSTProperty(treap.root()));
    }

    @Test
    @DisplayName("Reset counter should work")
    void resetCounterShouldWork() {
        treap.add(10);
        treap.add(20);
        assertEquals(2, treap.getInsertionCounter());

        treap.resetCounter();
        assertEquals(0, treap.getInsertionCounter());
    }

    // ==================== USE CASE SIMULATION ====================

    @Test
    @DisplayName("Log monitoring scenario: recent logs are accessible")
    void logMonitoringScenario() {
        // Simulate adding 10 log entries
        for (int i = 1; i <= 10; i++) {
            treap.add(i * 100);
        }

        // Most recent log should be root
        assertEquals(1000, treap.root().getValue());
        assertEquals(9, treap.root().getPriority());

        // All logs should be searchable
        for (int i = 1; i <= 10; i++) {
            assertTrue(treap.search(i * 100));
        }
    }

    @Test
    @DisplayName("Sliding window scenario: new entries replace old")
    void slidingWindowScenario() {
        // Initial window
        treap.add(100);
        treap.add(200);
        treap.add(300);

        assertEquals(300, treap.root().getValue());

        // Add more recent entries
        treap.add(400);
        treap.add(500);

        assertEquals(500, treap.root().getValue());
        assertEquals(4, treap.root().getPriority());
    }

    // ==================== HELPER METHODS ====================

    private PriorityNode<Integer> findNode(PriorityNode<Integer> node, int value) {
        if (node == null) return null;
        if (node.getValue() == value) return node;

        PriorityNode<Integer> left = findNode(node.getLeft(), value);
        if (left != null) return left;

        return findNode(node.getRight(), value);
    }

    private boolean verifyMaxHeapProperty(PriorityNode<Integer> node) {
        if (node == null) return true;

        PriorityNode<Integer> left = node.getLeft();
        PriorityNode<Integer> right = node.getRight();

        if (left != null && left.getPriority() > node.getPriority()) {
            return false;
        }
        if (right != null && right.getPriority() > node.getPriority()) {
            return false;
        }

        return verifyMaxHeapProperty(left) && verifyMaxHeapProperty(right);
    }

    private boolean verifyBSTProperty(PriorityNode<Integer> node) {
        return verifyBSTProperty(node, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    private boolean verifyBSTProperty(PriorityNode<Integer> node, int min, int max) {
        if (node == null) return true;

        if (node.getValue() <= min || node.getValue() >= max) {
            return false;
        }

        return verifyBSTProperty(node.getLeft(), min, node.getValue()) &&
               verifyBSTProperty(node.getRight(), node.getValue(), max);
    }
}
