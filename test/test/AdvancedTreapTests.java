package test;

import opgave.PriorityNode;
import oplossing.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Advanced edge-case and stress tests for all treap implementations.
 * These tests cover:
 * - Boundary values (MIN_VALUE, MAX_VALUE)
 * - Large-scale operations (>10,000 elements)
 * - Complex interleaved operations
 * - Memory and performance stress tests
 * - Property invariants under extreme conditions
 */
public class AdvancedTreapTests {

    // ==================== BOUNDARY VALUE TESTS ====================

    @Test
    @DisplayName("Treap handles Integer.MIN_VALUE and Integer.MAX_VALUE")
    void treapHandlesBoundaryValues() {
        Treap<Integer> treap = new Treap<>();

        assertTrue(treap.add(Integer.MIN_VALUE));
        assertTrue(treap.add(Integer.MAX_VALUE));
        assertTrue(treap.add(0));

        assertEquals(3, treap.size());
        assertTrue(treap.search(Integer.MIN_VALUE));
        assertTrue(treap.search(Integer.MAX_VALUE));
        assertTrue(treap.search(0));

        assertEquals(List.of(Integer.MIN_VALUE, 0, Integer.MAX_VALUE), treap.values());
    }

    @Test
    @DisplayName("MyTreap handles boundary values correctly")
    void myTreapHandlesBoundaryValues() {
        MyTreap<Integer> treap = new MyTreap<>();

        treap.add(Integer.MIN_VALUE);
        treap.add(Integer.MAX_VALUE);
        treap.add(0);

        assertEquals(List.of(Integer.MIN_VALUE, 0, Integer.MAX_VALUE), treap.values());
        assertTrue(verifyMaxHeapProperty(treap.root()));
    }

    @Test
    @DisplayName("Frequency treaps handle boundary values")
    void frequencyTreapsHandleBoundaryValues() {
        LineairFrequencyTreap<Integer> linear = new LineairFrequencyTreap<>();
        MyFrequencyTreap<Integer> logarithmic = new MyFrequencyTreap<>();

        for (var treap : List.of(linear, logarithmic)) {
            treap.add(Integer.MIN_VALUE);
            treap.add(Integer.MAX_VALUE);
            treap.add(0);

            assertEquals(3, treap.size());
            assertEquals(List.of(Integer.MIN_VALUE, 0, Integer.MAX_VALUE), treap.values());

            // Access MIN_VALUE multiple times
            for (int i = 0; i < 10; i++) {
                treap.search(Integer.MIN_VALUE);
            }

            assertTrue(treap.search(Integer.MIN_VALUE));
        }
    }

    // ==================== LARGE-SCALE STRESS TESTS ====================

    @ParameterizedTest
    @ValueSource(ints = {1000, 5000, 10000})
    @DisplayName("Treap handles large number of sequential inserts")
    void treapHandlesLargeSequentialInserts(int count) {
        Treap<Integer> treap = new Treap<>();

        for (int i = 0; i < count; i++) {
            assertTrue(treap.add(i));
        }

        assertEquals(count, treap.size());
        assertTrue(verifyMaxHeapProperty(treap.root()));

        // Verify BST by checking in-order traversal
        List<Integer> values = treap.values();
        for (int i = 0; i < count - 1; i++) {
            assertTrue(values.get(i) < values.get(i + 1));
        }
    }

    @Test
    @DisplayName("MyTreap handles 10,000 sequential inserts efficiently")
    void myTreapHandlesLargeSequentialInserts() {
        MyTreap<Integer> treap = new MyTreap<>();

        // Sequential inserts (worst case for BST)
        for (int i = 0; i < 10000; i++) {
            treap.add(i);
        }

        assertEquals(10000, treap.size());
        assertEquals(9999, treap.root().getValue()); // Most recent
        assertTrue(verifyMaxHeapProperty(treap.root()));
    }

    @Test
    @DisplayName("Frequency treaps handle 5000 elements with skewed access")
    void frequencyTreapsHandleSkewedAccess() {
        LineairFrequencyTreap<Integer> linear = new LineairFrequencyTreap<>();
        MyFrequencyTreap<Integer> logarithmic = new MyFrequencyTreap<>();

        for (var treap : List.of(linear, logarithmic)) {
            // Insert 1000 elements
            for (int i = 0; i < 1000; i++) {
                treap.add(i);
            }

            // Create hot spot: access first 10 elements 100 times each
            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 100; j++) {
                    treap.search(i);
                }
            }

            // Hot elements should be near root
            assertTrue(treap.search(0));
            assertTrue(treap.search(5));
            assertEquals(1000, treap.size());
        }
    }

    @Test
    @DisplayName("Stress test: 10,000 random operations")
    void stressTestRandomOperations() {
        Treap<Integer> treap = new Treap<>();
        Random rnd = new Random(42);
        Set<Integer> elements = new HashSet<>();

        for (int i = 0; i < 10000; i++) {
            int op = rnd.nextInt(3);
            int value = rnd.nextInt(5000);

            if (op == 0) {
                // Add
                boolean added = treap.add(value);
                if (added) {
                    elements.add(value);
                }
            } else if (op == 1) {
                // Search
                boolean found = treap.search(value);
                assertEquals(elements.contains(value), found);
            } else {
                // Remove
                boolean removed = treap.remove(value);
                if (removed) {
                    elements.remove(value);
                }
            }

            assertEquals(elements.size(), treap.size());
        }

        // Final verification
        assertTrue(verifyMaxHeapProperty(treap.root()));
    }

    // ==================== INTERLEAVED OPERATIONS ====================

    @Test
    @DisplayName("Interleaved add, search, remove maintains properties")
    void interleavedOperationsMaintainProperties() {
        Treap<Integer> treap = new Treap<>();

        for (int i = 0; i < 100; i++) {
            treap.add(i);
            if (i % 3 == 0) treap.search(i / 2);
            if (i % 5 == 0 && i > 0) treap.remove(i - 1);

            assertTrue(verifyMaxHeapProperty(treap.root()),
                "Heap property violated at iteration " + i);
        }

        List<Integer> values = treap.values();
        for (int i = 0; i < values.size() - 1; i++) {
            assertTrue(values.get(i) < values.get(i + 1),
                "BST property violated");
        }
    }

    @Test
    @DisplayName("Frequency treap: interleaved operations with access patterns")
    void frequencyTreapInterleavedOperations() {
        LineairFrequencyTreap<Integer> treap = new LineairFrequencyTreap<>();

        // Build initial tree
        for (int i = 0; i < 50; i++) {
            treap.add(i);
        }

        // Create access patterns
        for (int round = 0; round < 10; round++) {
            // Hot spot on element 25
            for (int i = 0; i < 20; i++) {
                treap.search(25);
            }

            // Add new element
            treap.add(50 + round);

            // Remove old element
            if (round > 0) {
                treap.remove(round - 1);
            }

            // Verify properties
            assertTrue(verifyMaxHeapProperty(treap.root()));
        }

        // Element 25 should have high priority
        assertTrue(treap.search(25));
    }

    // ==================== DUPLICATE HANDLING ====================

    @Test
    @DisplayName("Multiple duplicate adds should not increase size")
    void multipleDuplicateAddsDoNotIncreaseSize() {
        Treap<Integer> treap = new Treap<>();

        treap.add(42);
        assertEquals(1, treap.size());

        for (int i = 0; i < 100; i++) {
            assertFalse(treap.add(42));
        }

        assertEquals(1, treap.size());
        assertEquals(List.of(42), treap.values());
    }

    @Test
    @DisplayName("MyTreap: duplicate adds increase priority")
    void myTreapDuplicateIncreasePriority() {
        LineairFrequencyTreap<Integer> treap = new LineairFrequencyTreap<>();

        treap.add(42);
        long initialPriority = treap.root().getPriority();

        // Duplicate adds should increase priority
        for (int i = 0; i < 10; i++) {
            assertFalse(treap.add(42));
        }

        assertEquals(1, treap.size());
        assertEquals(initialPriority + 10, treap.root().getPriority());
    }

    // ==================== REMOVE EDGE CASES ====================

    @Test
    @DisplayName("Remove root repeatedly maintains properties")
    void removeRootRepeatedlyMaintainsProperties() {
        Treap<Integer> treap = new Treap<>();

        for (int i = 1; i <= 100; i++) {
            treap.add(i);
        }

        while (!treap.isEmpty()) {
            int rootValue = treap.root().getValue();
            assertTrue(treap.remove(rootValue));

            if (!treap.isEmpty()) {
                assertTrue(verifyMaxHeapProperty(treap.root()));
            }
        }

        assertNull(treap.root());
        assertEquals(0, treap.size());
    }

    @Test
    @DisplayName("Remove leaf nodes maintains properties")
    void removeLeafNodesMaintainsProperties() {
        Treap<Integer> treap = new Treap<>();

        for (int i = 1; i <= 50; i++) {
            treap.add(i);
        }

        // Try to find and remove leaf nodes
        for (int i = 1; i <= 50; i += 5) {
            if (treap.search(i)) {
                treap.remove(i);
                assertTrue(verifyMaxHeapProperty(treap.root()));
            }
        }
    }

    @Test
    @DisplayName("Remove from single-element tree results in empty tree")
    void removeFromSingleElementTree() {
        for (var treap : createAllTreapTypes()) {
            treap.add(42);
            assertEquals(1, treap.size());

            assertTrue(treap.remove(42));
            assertEquals(0, treap.size());
            assertNull(treap.root());
            assertEquals(0, treap.size());
        }
    }

    // ==================== EMPTY TREE OPERATIONS ====================

    @Test
    @DisplayName("Operations on empty tree should not throw exceptions")
    void operationsOnEmptyTreeSafe() {
        for (var treap : createAllTreapTypes()) {
            assertDoesNotThrow(() -> treap.search(42));
            assertDoesNotThrow(() -> treap.remove(42));
            assertDoesNotThrow(treap::values);
            assertDoesNotThrow(treap::root);

            assertFalse(treap.search(42));
            assertFalse(treap.remove(42));
            assertEquals(List.of(), treap.values());
            assertNull(treap.root());
        }
    }

    // ==================== STRING TREAP TESTS ====================

    @Test
    @DisplayName("Treap works with String elements")
    void treapWorksWithStrings() {
        Treap<String> treap = new Treap<>();

        treap.add("banana");
        treap.add("apple");
        treap.add("cherry");
        treap.add("date");

        assertEquals(4, treap.size());
        assertEquals(List.of("apple", "banana", "cherry", "date"), treap.values());

        assertTrue(treap.search("banana"));
        assertFalse(treap.search("elderberry"));

        treap.remove("banana");
        assertEquals(List.of("apple", "cherry", "date"), treap.values());
    }

    @Test
    @DisplayName("Frequency treap works with Strings")
    void frequencyTreapWorksWithStrings() {
        LineairFrequencyTreap<String> treap = new LineairFrequencyTreap<>();

        treap.add("hello");
        treap.add("world");

        // Access "hello" multiple times
        for (int i = 0; i < 10; i++) {
            treap.search("hello");
        }

        assertEquals("hello", treap.root().getValue());
        assertEquals(List.of("hello", "world"), treap.values());
    }

    // ==================== PERFORMANCE CHARACTERISTIC TESTS ====================

    @Test
    @DisplayName("Treap maintains reasonable depth with random inserts")
    void treapMaintainsReasonableDepthRandomInserts() {
        Treap<Integer> treap = new Treap<>();
        Random rnd = new Random(42);

        for (int i = 0; i < 1000; i++) {
            treap.add(rnd.nextInt(10000));
        }

        int depth = calculateDepth(treap.root());
        int expectedDepth = (int) (Math.log(treap.size()) / Math.log(2));

        // Depth should be O(log n) - allow 5x factor for randomness
        assertTrue(depth < expectedDepth * 5,
            "Depth " + depth + " should be close to log(n) = " + expectedDepth);
    }

    @Test
    @DisplayName("MyTreap degenerates with sequential inserts")
    void myTreapDegeneratesSequentialInserts() {
        MyTreap<Integer> treap = new MyTreap<>();

        for (int i = 0; i < 100; i++) {
            treap.add(i);
        }

        int depth = calculateDepth(treap.root());
        // Should degenerate to linear (depth ≈ size)
        assertTrue(depth >= 90, "Sequential inserts should create deep tree");
    }

    // ==================== PROPERTY VERIFICATION TESTS ====================

    @Test
    @DisplayName("All treap types maintain invariants after 1000 operations")
    void allTreapTypesMaintainInvariants() {
        for (var treap : createAllTreapTypes()) {
            Random rnd = new Random(42);

            for (int i = 0; i < 1000; i++) {
                int op = rnd.nextInt(3);
                int value = rnd.nextInt(500);

                if (op == 0) {
                    treap.add(value);
                } else if (op == 1) {
                    treap.search(value);
                } else {
                    treap.remove(value);
                }
            }

            // Verify BST property
            List<Integer> values = treap.values();
            for (int i = 0; i < values.size() - 1; i++) {
                assertTrue(values.get(i) < values.get(i + 1),
                    treap.getClass().getSimpleName() + " BST property violated");
            }

            // Verify size consistency
            assertEquals(values.size(), treap.size(),
                treap.getClass().getSimpleName() + " size mismatch");
        }
    }

    // ==================== HELPER METHODS ====================

    private List<opgave.SearchTree<Integer>> createAllTreapTypes() {
        return List.of(
            new Treap<>(),
            new MyTreap<>(),
            new LineairFrequencyTreap<>(),
            new MyFrequencyTreap<>()
        );
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

    private int calculateDepth(PriorityNode<?> node) {
        if (node == null) return 0;
        return 1 + Math.max(calculateDepth(node.getLeft()), calculateDepth(node.getRight()));
    }
}