package test;

import opgave.PriorityNode;
import oplossing.Treap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TreapTest {

    private Treap<Integer> treap;

    @BeforeEach
    void setUp() {
        treap = new Treap<>();
    }

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
        assertFalse(treap.search(5));
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

        List<Integer> values = treap.values();
        assertEquals(List.of(1, 3, 5, 7, 9), values);
    }

    @Test
    @DisplayName("Treap should maintain BST property")
    void treapShouldMaintainBSTProperty() {
        for (int i = 1; i <= 20; i++) {
            treap.add(i);
        }

        List<Integer> values = treap.values();
        for (int i = 0; i < values.size() - 1; i++) {
            assertTrue(values.get(i) < values.get(i + 1));
        }
    }

    @Test
    @DisplayName("Treap should maintain heap property")
    void treapShouldMaintainHeapProperty() {
        for (int i = 1; i <= 20; i++) {
            treap.add(i);
        }

        // Verify heap property: parent priority >= child priority
        assertTrue(verifyHeapProperty(treap.root()));
    }

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
    @DisplayName("Add many elements and remove them")
    void addAndRemoveManyElements() {
        for (int i = 1; i <= 100; i++) {
            treap.add(i);
        }
        assertEquals(100, treap.size());

        for (int i = 1; i <= 100; i++) {
            assertTrue(treap.remove(i));
        }
        assertEquals(0, treap.size());
    }

    @Test
    @DisplayName("Remove should maintain BST and heap properties")
    void removeShouldMaintainProperties() {
        for (int i = 1; i <= 20; i++) {
            treap.add(i);
        }

        treap.remove(10);
        treap.remove(5);
        treap.remove(15);

        // Check BST property
        List<Integer> values = treap.values();
        for (int i = 0; i < values.size() - 1; i++) {
            assertTrue(values.get(i) < values.get(i + 1));
        }

        // Check heap property
        assertTrue(verifyHeapProperty(treap.root()));
    }

    @Test
    @DisplayName("Root should return PriorityNode with priority")
    void rootShouldReturnPriorityNode() {
        treap.add(5);
        PriorityNode<Integer> root = treap.root();

        assertNotNull(root);
        assertEquals(5, root.getValue());
        // Priority should be a long value (not checking specific value as it's random)
        assertDoesNotThrow(root::getPriority);
    }

    /*
      Test cases based on the Treap operation diagrams from geeksforgeeks.org:
      https://www.geeksforgeeks.org/dsa/treap-a-randomized-binary-search-tree/
     */
        /**
         * Test the initial tree structure:
         * <p>
         *           (50, 15)
         *          /        \
         *     (30, 5)      (70, 10)
         *     /    \
         * (20, 2)  (40, 4)
         * <p>
         * BST property: 20 < 30 < 40 < 50 < 70
         * Max-Heap property: 15 > 5 > 2, 15 > 5 > 4, 15 > 10
         */
        @Test
        @DisplayName("Verify initial tree structure before insert")
        void verifyInitialTreeStructureImage1() {
            Treap<Integer> treap = buildInitialTreeImage1();

            // Verify BST property via in-order traversal
            List<Integer> values = treap.values();
            assertEquals(List.of(20, 30, 40, 50, 70), values);

            // Verify that all elements are present
            assertTrue(treap.search(50));
            assertTrue(treap.search(30));
            assertTrue(treap.search(70));
            assertTrue(treap.search(20));
            assertTrue(treap.search(40));

            assertEquals(5, treap.size());
        }

        /**
         * Test Insert(80) scenario:
         * <p>
         * After BST insert of 80 (with priority 12) as right child of 70:
         *           (50, 15)
         *          /        \
         *     (30, 5)      (70, 10)
         *     /    \            \
         * (20, 2)  (40, 4)     (80, 12)   <-- priority 12 > 10, heap violation!
         * <p>
         * After left rotate on 70:
         *           (50, 15)
         *          /        \
         *     (30, 5)      (80, 12)
         *     /    \       /
         * (20, 2)  (40, 4) (70, 10)
         * <p>
         * Now heap property is restored: 15 > 12 > 10
         */
        @Test
        @DisplayName("Insert(80) with left rotate for heap property")
        void testInsert80WithLeftRotate() {
            Treap<Integer> treap = buildInitialTreeImage1();

            // Add 80 (in the figure it gets priority 12)
            assertTrue(treap.add(80));

            // Verify BST property is maintained
            List<Integer> values = treap.values();
            assertEquals(List.of(20, 30, 40, 50, 70, 80), values);

            // Verify heap property is maintained
            assertTrue(verifyMaxHeapProperty(treap.root()));

            assertEquals(6, treap.size());
        }

        /**
         * Test the final structure after Insert(80):
         * <p>
         *           (50, 15)
         *          /        \
         *     (30, 5)      (80, 12)
         *     /    \       /
         * (20, 2)  (40, 4) (70, 10)
         */
        @Test
        @DisplayName("Verify final structure after insert(80)")
        void verifyFinalStructureAfterInsert80() {
            Treap<Integer> treap = buildTreeAfterInsert80();

            // Verify BST property
            List<Integer> values = treap.values();
            assertEquals(List.of(20, 30, 40, 50, 70, 80), values);

            // Verify heap property
            assertTrue(verifyMaxHeapProperty(treap.root()));

            // Verify all search operations work
            for (int key : List.of(20, 30, 40, 50, 70, 80)) {
                assertTrue(treap.search(key), "Key " + key + " should be found");
            }
        }

        /**
         * Test Delete(50) scenario:
         * <p>
         * Starting tree:
         *           (50, 15)
         *          /        \
         *     (30, 5)      (70, 10)
         *     /    \
         * (20, 2)  (40, 4)
         * <p>
         * Step 1: Set priority of 50 to -INF
         * Step 2: Left rotate (since right child 70 has higher priority than left child 30)
         * Step 3: Right rotate(s) to push 50 down
         * Step 4: Delete 50 when it becomes a leaf
         * <p>
         * Final tree:
         *           (70, 10)
         *          /
         *     (30, 5)
         *     /    \
         * (20, 2)  (40, 4)
         */
        @Test
        @DisplayName("Delete(50) with rotations")
        void testDelete50WithRotations() {
            Treap<Integer> treap = buildInitialTreeImage1();

            // Verify that 50 is present
            assertTrue(treap.search(50));
            assertEquals(5, treap.size());

            // Remove 50
            assertTrue(treap.remove(50));

            // Verify that 50 is gone
            assertFalse(treap.search(50));
            assertEquals(4, treap.size());

            // Verify BST property is maintained
            List<Integer> values = treap.values();
            assertEquals(List.of(20, 30, 40, 70), values);

            // Verify heap property is maintained
            assertTrue(verifyMaxHeapProperty(treap.root()));
        }

        /**
         * Test the final structure after Delete(50):
         * <p>
         *       (70, 10)
         *       /
         *   (30, 5)
         *   /    \
         * (20, 2)  (40, 4)
         */
        @Test
        @DisplayName("Verify final structure after delete(50)")
        void verifyFinalStructureAfterDelete50() {
            Treap<Integer> treap = buildTreeAfterDelete50();

            // Verify BST property
            List<Integer> values = treap.values();
            assertEquals(List.of(20, 30, 40, 70), values);

            // Verify that 50 is no longer present
            assertFalse(treap.search(50));

            // Verify heap property
            assertTrue(verifyMaxHeapProperty(treap.root()));

            assertEquals(4, treap.size());
        }

        /**
         * Test that deleting the root works correctly when root has two children.
         */
        @Test
        @DisplayName("Delete root with two children")
        void testDeleteRootWithTwoChildren() {
            Treap<Integer> treap = buildInitialTreeImage1();

            PriorityNode<Integer> root = treap.root();
            assertNotNull(root);
            assertEquals(50, root.getValue());

            // Remove the root
            assertTrue(treap.remove(50));

            // New root must be one of the original children (70 has highest priority)
            PriorityNode<Integer> newRoot = treap.root();
            assertNotNull(newRoot);

            // Verify properties
            assertTrue(verifyMaxHeapProperty(newRoot));
            assertEquals(List.of(20, 30, 40, 70), treap.values());
        }

        // ==================== COMBINED TESTS ====================

        /**
         * Test the complete sequence: build tree, insert 80, delete 50
         */
        @Test
        @DisplayName("Full sequence: insert(80) followed by delete(50)")
        void testFullSequenceInsertThenDelete() {
            Treap<Integer> treap = buildInitialTreeImage1();

            // Insert 80
            assertTrue(treap.add(80));
            assertEquals(6, treap.size());
            assertTrue(verifyMaxHeapProperty(treap.root()));

            // Delete 50
            assertTrue(treap.remove(50));
            assertEquals(5, treap.size());
            assertFalse(treap.search(50));
            assertTrue(verifyMaxHeapProperty(treap.root()));

            // Verify final result
            assertEquals(List.of(20, 30, 40, 70, 80), treap.values());
        }

        /**
         * Test multiple consecutive deletes
         */
        @Test
        @DisplayName("Multiple consecutive deletes maintain treap properties")
        void testMultipleDeletes() {
            Treap<Integer> treap = buildInitialTreeImage1();
            treap.add(80);

            // Delete in descending key order
            assertTrue(treap.remove(80));
            assertTrue(verifyMaxHeapProperty(treap.root()));

            assertTrue(treap.remove(70));
            assertTrue(verifyMaxHeapProperty(treap.root()));

            assertTrue(treap.remove(50));
            assertTrue(verifyMaxHeapProperty(treap.root()));

            assertTrue(treap.remove(40));
            assertTrue(verifyMaxHeapProperty(treap.root()));

            assertTrue(treap.remove(30));
            assertTrue(verifyMaxHeapProperty(treap.root()));

            assertTrue(treap.remove(20));
            assertNull(treap.root());
            assertTrue(treap.isEmpty());
        }

        // ==================== HELPER METHODS ====================

        /**
         * Builds the initial tree:
         *           (50, 15)
         *          /        \
         *     (30, 5)      (70, 10)
         *     /    \
         * (20, 2)  (40, 4)
         * <p>
         * Note: The exact structure depends on the random priorities.
         * This method adds elements in an order that will likely
         * produce a similar structure.
         */
        private Treap<Integer> buildInitialTreeImage1() {
            Treap<Integer> treap = new Treap<>();
            // Add elements
            treap.add(50);
            treap.add(30);
            treap.add(70);
            treap.add(20);
            treap.add(40);
            return treap;
        }

        /**
         * Builds the tree after insert(80).
         */
        private Treap<Integer> buildTreeAfterInsert80() {
            Treap<Integer> treap = buildInitialTreeImage1();
            treap.add(80);
            return treap;
        }

        /**
         * Builds the final structure after delete(50).
         */
        private Treap<Integer> buildTreeAfterDelete50() {
            Treap<Integer> treap = new Treap<>();
            treap.add(70);
            treap.add(30);
            treap.add(20);
            treap.add(40);
            return treap;
        }

        /**
         * Verifies the max-heap property: parent priority >= child priority
         */
        private boolean verifyMaxHeapProperty(PriorityNode<Integer> node) {
            if (node == null) {
                return true;
            }

            PriorityNode<Integer> left = node.getLeft();
            PriorityNode<Integer> right = node.getRight();

            // Check left child
            if (left != null && left.getPriority() > node.getPriority()) {
                System.err.println("Heap violation: parent " + node.getValue() +
                        " (priority " + node.getPriority() + ") < left child " +
                        left.getValue() + " (priority " + left.getPriority() + ")");
                return false;
            }

            // Check right child
            if (right != null && right.getPriority() > node.getPriority()) {
                System.err.println("Heap violation: parent " + node.getValue() +
                        " (priority " + node.getPriority() + ") < right child " +
                        right.getValue() + " (priority " + right.getPriority() + ")");
                return false;
            }

            return verifyMaxHeapProperty(left) && verifyMaxHeapProperty(right);
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

        // ==================== EXTRA TESTS FOR ROTATIONS ====================

        /**
         * Test that left rotate works correctly by doing a series of inserts
         * that should trigger a left rotate.
         */
        @Test
        @DisplayName("Left rotate is executed correctly on insert")
        void testLeftRotateOnInsert() {
            Treap<Integer> treap = new Treap<>();

            // Add elements in ascending order
            // This should trigger left rotates
            for (int i = 1; i <= 10; i++) {
                treap.add(i);
                assertTrue(verifyMaxHeapProperty(treap.root()),
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
            Treap<Integer> treap = new Treap<>();

            // Add elements in descending order
            // This should trigger right rotates
            for (int i = 10; i >= 1; i--) {
                treap.add(i);
                assertTrue(verifyMaxHeapProperty(treap.root()),
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
            Treap<Integer> treap = new Treap<>();

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
                    assertTrue(verifyMaxHeapProperty(treap.root()),
                            "Heap property violated after delete of " + rootValue);
                    assertTrue(verifyBSTProperty(treap.root()),
                            "BST property violated after delete of " + rootValue);
                }
            }

            assertTrue(treap.isEmpty());
        }
    }