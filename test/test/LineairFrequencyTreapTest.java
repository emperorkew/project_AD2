package test;

import opgave.PriorityNode;
import oplossing.LineairFrequencyTreap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class LineairFrequencyTreapTest {

    private LineairFrequencyTreap<Integer> treap;

    @BeforeEach
    void setUp() {
        treap = new LineairFrequencyTreap<>();
    }

    // === Basic Operations ===

    @Test
    @DisplayName("New treap should have size zero")
    void newTreapShouldHaveSizeZero() {
        assertEquals(0, treap.size());
        assertTrue(treap.isEmpty());
    }

    @Test
    @DisplayName("Add should return true for new elements")
    void addShouldReturnTrueForNewElements() {
        assertTrue(treap.add(5));
        assertEquals(1, treap.size());
    }

    @Test
    @DisplayName("Add should return false for duplicate elements")
    void addShouldReturnFalseForDuplicates() {
        treap.add(5);
        assertFalse(treap.add(5));
        assertEquals(1, treap.size());
    }

    @Test
    @DisplayName("Add should return false for null")
    void addShouldReturnFalseForNull() {
        assertFalse(treap.add(null));
        assertEquals(0, treap.size());
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
    }

    @Test
    @DisplayName("Remove should return false for non-existing elements")
    void removeShouldReturnFalseForNonExisting() {
        treap.add(5);
        assertFalse(treap.remove(10));
        assertFalse(treap.remove(null));
        assertEquals(1, treap.size());
    }

    @Test
    @DisplayName("Values should return elements in sorted order")
    void valuesShouldReturnSortedOrder() {
        treap.add(5);
        treap.add(3);
        treap.add(7);
        treap.add(1);
        treap.add(9);

        assertEquals(List.of(1, 3, 5, 7, 9), treap.values());
    }

    // === Priority Tests ===

    @Test
    @DisplayName("New elements should have initial priority of 1")
    void newElementsShouldHaveInitialPriorityOne() {
        treap.add(5);
        assertEquals(1, treap.root().getPriority());
    }

    @Test
    @DisplayName("Search should increase priority by 1")
    void searchShouldIncreasePriorityByOne() {
        treap.add(5);
        long initialPriority = treap.root().getPriority();

        treap.search(5);

        assertEquals(initialPriority + 1, treap.root().getPriority());
    }

    @Test
    @DisplayName("Multiple searches should increase priority linearly")
    void multipleSearchesShouldIncreasePriorityLinearly() {
        treap.add(5);

        for (int i = 0; i < 10; i++) {
            treap.search(5);
        }

        assertEquals(11, treap.root().getPriority()); // 1 initial + 10 searches
    }

    @Test
    @DisplayName("Add existing element should increase priority")
    void addExistingShouldIncreasePriority() {
        treap.add(5);
        long initialPriority = treap.root().getPriority();

        treap.add(5); // Should increase priority, not add

        assertEquals(1, treap.size());
        assertEquals(initialPriority + 1, treap.root().getPriority());
    }

    @Test
    @DisplayName("Frequently accessed node should rise to root")
    void frequentlyAccessedNodeShouldRiseToRoot() {
        // Add elements
        treap.add(5);
        treap.add(3);
        treap.add(7);
        treap.add(1);
        treap.add(9);

        // Access 1 many times
        for (int i = 0; i < 20; i++) {
            treap.search(1);
        }

        // 1 should now be the root
        assertEquals(1, treap.root().getValue());
        assertEquals(21, treap.root().getPriority()); // 1 initial + 20 searches
    }

    // === Property Tests ===

    @Test
    @DisplayName("Treap should maintain BST property after searches")
    void treapShouldMaintainBSTPropertyAfterSearches() {
        treap.add(5);
        treap.add(3);
        treap.add(7);
        treap.add(1);
        treap.add(9);

        // Perform many searches
        for (int i = 0; i < 10; i++) {
            treap.search(1);
            treap.search(9);
        }

        // BST property should still hold
        List<Integer> values = treap.values();
        for (int i = 0; i < values.size() - 1; i++) {
            assertTrue(values.get(i) < values.get(i + 1));
        }
    }

    @Test
    @DisplayName("Treap should maintain heap property after searches")
    void treapShouldMaintainHeapPropertyAfterSearches() {
        treap.add(5);
        treap.add(3);
        treap.add(7);
        treap.add(1);
        treap.add(9);

        // Perform many searches
        for (int i = 0; i < 10; i++) {
            treap.search(1);
            treap.search(9);
        }

        // Heap property should still hold
        assertTrue(verifyHeapProperty(treap.root()));
    }

    @Test
    @DisplayName("Treap should maintain properties after removes")
    void treapShouldMaintainPropertiesAfterRemoves() {
        for (int i = 1; i <= 10; i++) {
            treap.add(i);
        }

        // Increase some priorities
        for (int i = 0; i < 5; i++) {
            treap.search(3);
            treap.search(7);
        }

        // Remove some elements
        treap.remove(5);
        treap.remove(2);
        treap.remove(8);

        // Check BST property
        List<Integer> values = treap.values();
        for (int i = 0; i < values.size() - 1; i++) {
            assertTrue(values.get(i) < values.get(i + 1));
        }

        // Check heap property
        assertTrue(verifyHeapProperty(treap.root()));
    }

    // === Edge Cases ===

    @Test
    @DisplayName("Remove all elements should result in empty treap")
    void removeAllShouldResultInEmptyTreap() {
        treap.add(5);
        treap.add(3);
        treap.add(7);

        treap.remove(5);
        treap.remove(3);
        treap.remove(7);

        assertEquals(0, treap.size());
        assertTrue(treap.isEmpty());
        assertNull(treap.root());
    }

    @Test
    @DisplayName("Search on empty treap should return false")
    void searchOnEmptyTreapShouldReturnFalse() {
        assertFalse(treap.search(5));
    }

    @Test
    @DisplayName("Remove from empty treap should return false")
    void removeFromEmptyTreapShouldReturnFalse() {
        assertFalse(treap.remove(5));
    }

    @Test
    @DisplayName("Single element operations")
    void singleElementOperations() {
        treap.add(42);
        assertEquals(1, treap.size());
        assertEquals(42, treap.root().getValue());

        treap.search(42);
        assertEquals(2, treap.root().getPriority());

        treap.remove(42);
        assertEquals(0, treap.size());
        assertNull(treap.root());
    }

    // === Stress Tests ===

    @Test
    @DisplayName("Stress test with many elements")
    void stressTestWithManyElements() {
        // Add 100 elements
        for (int i = 1; i <= 100; i++) {
            treap.add(i);
        }
        assertEquals(100, treap.size());

        // Access element 50 many times
        for (int i = 0; i < 100; i++) {
            treap.search(50);
        }

        // 50 should be root
        assertEquals(50, treap.root().getValue());
        assertEquals(101, treap.root().getPriority());

        // Properties should hold
        assertTrue(verifyHeapProperty(treap.root()));

        // BST property
        List<Integer> values = treap.values();
        assertEquals(100, values.size());
        for (int i = 0; i < values.size() - 1; i++) {
            assertTrue(values.get(i) < values.get(i + 1));
        }
    }

    @Test
    @DisplayName("Multiple hot spots should order by frequency")
    void multipleHotSpotsShouldOrderByFrequency() {
        // Add elements
        for (int i = 1; i <= 10; i++) {
            treap.add(i);
        }

        // Create hot spots with different frequencies
        for (int i = 0; i < 30; i++) treap.search(5);  // 31 total
        for (int i = 0; i < 20; i++) treap.search(3);  // 21 total
        for (int i = 0; i < 10; i++) treap.search(7);  // 11 total

        // 5 should be root (highest priority)
        assertEquals(5, treap.root().getValue());
        assertEquals(31, treap.root().getPriority());

        // Verify heap property ensures correct ordering
        assertTrue(verifyHeapProperty(treap.root()));
    }

    @Test
    @DisplayName("Add and remove stress test")
    void addAndRemoveStressTest() {
        // Add 100 elements
        for (int i = 1; i <= 100; i++) {
            treap.add(i);
        }

        // Remove all
        for (int i = 1; i <= 100; i++) {
            assertTrue(treap.remove(i));
        }

        assertEquals(0, treap.size());
        assertNull(treap.root());
    }

    // === Rotation Tests ===

    /**
     * Test that left rotate works correctly by doing a series of inserts
     * that should trigger a left rotate.
     */
    @Test
    @DisplayName("Left rotate is executed correctly on insert")
    void testLeftRotateOnInsert() {
        LineairFrequencyTreap<Integer> treap = new LineairFrequencyTreap<>();

        // Add elements in ascending order
        // This should trigger left rotates
        for (int i = 1; i <= 10; i++) {
            treap.add(i);
            assertTrue(verifyHeapProperty(treap.root()),
                    "Heap property violated after insert " + i);
            assertTrue(verifyBSTProperty(treap.root()),
                    "BST property violated after insert " + i);
        }

        assertEquals(List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10), treap.values());
    }

    /**
     * Test that right rotate works correctly by doing a series of inserts
     * that should trigger a right rotate.
     */
    @Test
    @DisplayName("Right rotate is executed correctly on insert")
    void testRightRotateOnInsert() {
        LineairFrequencyTreap<Integer> treap = new LineairFrequencyTreap<>();

        // Add elements in descending order
        // This should trigger right rotates
        for (int i = 10; i >= 1; i--) {
            treap.add(i);
            assertTrue(verifyHeapProperty(treap.root()),
                    "Heap property violated after insert " + i);
            assertTrue(verifyBSTProperty(treap.root()),
                    "BST property violated after insert " + i);
        }

        assertEquals(List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10), treap.values());
    }

    /**
     * Test that rotations on delete work correctly by removing the root.
     */
    @Test
    @DisplayName("Rotations on delete of root")
    void testRotationsOnDeleteRoot() {
        LineairFrequencyTreap<Integer> treap = new LineairFrequencyTreap<>();

        // Build a tree
        for (int i : List.of(50, 25, 75, 10, 30, 60, 90)) {
            treap.add(i);
        }

        // Remove the root multiple times
        while (!treap.isEmpty()) {
            PriorityNode<Integer> root = treap.root();
            int rootValue = root.getValue();

            assertTrue(treap.remove(rootValue));

            if (!treap.isEmpty()) {
                assertTrue(verifyHeapProperty(treap.root()),
                        "Heap property violated after delete of " + rootValue);
                assertTrue(verifyBSTProperty(treap.root()),
                        "BST property violated after delete of " + rootValue);
            }
        }

        assertTrue(treap.isEmpty());
    }

    // === Helper Methods ===

    private boolean verifyHeapProperty(PriorityNode<Integer> node) {
        if (node == null) {
            return true;
        }

        PriorityNode<Integer> left = node.getLeft();
        PriorityNode<Integer> right = node.getRight();

        if (left != null && left.getPriority() > node.getPriority()) {
            return false;
        }
        if (right != null && right.getPriority() > node.getPriority()) {
            return false;
        }

        return verifyHeapProperty(left) && verifyHeapProperty(right);
    }

    /**
     * Verifies the BST property: left < parent < right
     */
    private boolean verifyBSTProperty(PriorityNode<Integer> node) {
        return verifyBSTProperty(node, null, null);
    }

    private boolean verifyBSTProperty(PriorityNode<Integer> node, Integer min, Integer max) {
        if (node == null) {
            return true;
        }

        int value = node.getValue();

        if (min != null && value <= min) {
            return false;
        }
        if (max != null && value >= max) {
            return false;
        }

        return verifyBSTProperty(node.getLeft(), min, value) &&
                verifyBSTProperty(node.getRight(), value, max);
    }
}