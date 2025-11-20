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
}