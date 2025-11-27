package test;

import opgave.PriorityNode;
import oplossing.MyFrequencyTreap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MyFrequencyTreapTest {

    private MyFrequencyTreap<Integer> treap;

    @BeforeEach
    void setUp() {
        treap = new MyFrequencyTreap<>();
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

    // === Logarithmic Priority Tests ===

    @Test
    @DisplayName("New elements should have initial priority of log2(1) = 0")
    void newElementsShouldHaveInitialPriorityZero() {
        treap.add(5);
        // log2(1) * 1M = 0
        assertEquals(0, treap.root().getPriority());
    }

    @Test
    @DisplayName("Search should increase priority logarithmically")
    void searchShouldIncreasePriorityLogarithmically() {
        treap.add(5);

        // Initial: accessCount=1, priority=0
        assertEquals(0, treap.root().getPriority());

        // After 1 search: accessCount=2, priority=log2(2)*1M=1M
        treap.search(5);
        assertEquals(1_000_000, treap.root().getPriority());

        // After 2 more searches: accessCount=4, priority=log2(4)*1M=2M
        treap.search(5);
        treap.search(5);
        assertEquals(2_000_000, treap.root().getPriority());
    }

    @Test
    @DisplayName("Logarithmic scaling shows diminishing returns")
    void logarithmicScalingShowsDiminishingReturns() {
        treap.add(5);

        // Search to get accessCount = 2 (priority = 1M)
        treap.search(5);
        long priority2 = treap.root().getPriority();

        // Search to get accessCount = 4 (priority = 2M)
        treap.search(5);
        treap.search(5);
        long priority4 = treap.root().getPriority();

        // Search to get accessCount = 8 (priority = 3M)
        for (int i = 0; i < 4; i++) treap.search(5);
        long priority8 = treap.root().getPriority();

        // Verify logarithmic increase: each doubling adds exactly 1M
        long delta1 = priority4 - priority2; // Should be 1M
        long delta2 = priority8 - priority4; // Should be 1M

        assertEquals(1_000_000, delta1);
        assertEquals(1_000_000, delta2);
    }

    @Test
    @DisplayName("Add existing element should increase priority logarithmically")
    void addExistingShouldIncreasePriorityLogarithmically() {
        treap.add(5);
        long initialPriority = treap.root().getPriority();

        treap.add(5); // Should increase accessCount to 2

        assertEquals(1, treap.size());
        // Priority should now be log2(2) * 1M = 1M
        assertTrue(treap.root().getPriority() > initialPriority);
        assertEquals(1_000_000, treap.root().getPriority());
    }

    @Test
    @DisplayName("Frequently accessed node should rise to root with logarithmic priority")
    void frequentlyAccessedNodeShouldRiseToRoot() {
        // Add elements
        treap.add(5);
        treap.add(3);
        treap.add(7);
        treap.add(1);
        treap.add(9);

        // Access 1 many times (16 searches -> accessCount = 17, log2(17) ≈ 4.09)
        for (int i = 0; i < 16; i++) {
            treap.search(1);
        }

        // 1 should now be the root or close to it
        assertEquals(1, treap.root().getValue());

        // Verify priority is logarithmic (log2(17) * 1M ≈ 4.09M)
        long expectedPriority = (long) (Math.log(17) / Math.log(2) * 1_000_000);
        assertTrue(Math.abs(treap.root().getPriority() - expectedPriority) < 1000);
    }

    @Test
    @DisplayName("Logarithmic priority prevents extreme imbalance")
    void logarithmicPriorityPreventsExtremeImbalance() {
        // Add elements
        for (int i = 1; i <= 10; i++) {
            treap.add(i);
        }

        // Heavily access element 5 (1000 times)
        for (int i = 0; i < 1000; i++) {
            treap.search(5);
        }

        // With logarithmic scaling, priority should be log2(1001) * 1M ≈ 9.97M
        // Much more moderate than linear 1001
        long priority = treap.root().getPriority();

        // Priority should be around 10M (log2(1001) ≈ 9.97)
        assertTrue(priority < 11_000_000);
        assertTrue(priority > 9_000_000);
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

    // === Comparison with Linear Scaling ===

    @Test
    @DisplayName("Logarithmic requires more accesses to overtake than linear")
    void logarithmicRequiresMoreAccessesToOvertake() {
        // Add two elements
        treap.add(10);
        treap.add(20);

        // Element 20 starts with priority 0
        // Access 10 once (accessCount=2, priority=1M)
        treap.search(10);

        // With linear scaling, 20 would need 2 accesses to tie
        // With logarithmic, 20 needs count=4 to match (since log2(4)=2=log2(2)+1 more than log2(2))
        // But to demonstrate overtaking, let's give 20 more searches to surpass 10
        treap.search(20);
        treap.search(20);
        treap.search(20);

        // After 3 searches on 20: accessCount=4, priority=log2(4)*1M=2M
        // 10 has accessCount=2, priority=log2(2)*1M=1M
        // 20 has now overtaken 10
        long priority10 = findNodePriority(treap.root(), 10);
        long priority20 = findNodePriority(treap.root(), 20);

        assertTrue(priority20 > priority10); // 20 should have overtaken 10
        assertEquals(1_000_000, priority10); // log2(2) * 1M
        assertEquals(2_000_000, priority20); // log2(4) * 1M
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
        // After 1 search: accessCount=2, priority=log2(2)*1M=1M
        assertEquals(1_000_000, treap.root().getPriority());

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

        // Access element 50 many times (128 searches -> accessCount=129)
        for (int i = 0; i < 128; i++) {
            treap.search(50);
        }

        // 50 should be root or very high
        assertEquals(50, treap.root().getValue());

        // log2(129) ≈ 7.01, so priority ≈ 7M
        assertTrue(treap.root().getPriority() > 6_900_000);
        assertTrue(treap.root().getPriority() < 7_100_000);

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
    @DisplayName("Multiple hot spots should order by logarithmic frequency")
    void multipleHotSpotsShouldOrderByLogarithmicFrequency() {
        // Add elements
        for (int i = 1; i <= 10; i++) {
            treap.add(i);
        }

        // Create hot spots with different frequencies
        // 32 accesses -> accessCount=33, log2(33)≈5.04, priority≈5M
        for (int i = 0; i < 32; i++) treap.search(5);

        // 16 accesses -> accessCount=17, log2(17)≈4.09, priority≈4M
        for (int i = 0; i < 16; i++) treap.search(3);

        // 8 accesses -> accessCount=9, log2(9)≈3.17, priority≈3M
        for (int i = 0; i < 8; i++) treap.search(7);

        // 5 should be root (highest priority)
        assertEquals(5, treap.root().getValue());

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

    @Test
    @DisplayName("Zipf-like access pattern test")
    void zipfLikeAccessPatternTest() {
        // Add elements
        for (int i = 1; i <= 10; i++) {
            treap.add(i);
        }

        // Simulate Zipf distribution: element 1 accessed most, 2 half as much, etc.
        for (int i = 0; i < 64; i++) treap.search(1);  // 65 total
        for (int i = 0; i < 32; i++) treap.search(2);  // 33 total
        for (int i = 0; i < 16; i++) treap.search(3);  // 17 total
        for (int i = 0; i < 8; i++) treap.search(4);   // 9 total

        // With logarithmic scaling, differences are moderated:
        // 1: log2(65)≈6.02
        // 2: log2(33)≈5.04
        // 3: log2(17)≈4.09
        // 4: log2(9)≈3.17

        // Element 1 should be root
        assertEquals(1, treap.root().getValue());

        // Tree should still be reasonably balanced despite skewed access
        assertTrue(verifyHeapProperty(treap.root()));

        // All elements should still be accessible
        for (int i = 1; i <= 10; i++) {
            assertTrue(treap.search(i));
        }
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

    private long findNodePriority(PriorityNode<Integer> node, int value) {
        if (node == null) {
            return -1;
        }
        if (node.getValue() == value) {
            return node.getPriority();
        }
        if (value < node.getValue()) {
            return findNodePriority(node.getLeft(), value);
        } else {
            return findNodePriority(node.getRight(), value);
        }
    }
}
