package test;

import oplossing.SearchTreeImplemented;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SearchTreeImplementedTest {

    private SearchTreeImplemented<Integer> tree;

    @BeforeEach
    void setUp() {
        tree = new SearchTreeImplemented<>();
    }

    // === Size Tests ===

    @Test
    @DisplayName("New tree should have size 0")
    void newTreeShouldHaveSizeZero() {
        assertEquals(0, tree.size());
    }

    @Test
    @DisplayName("Size should increase after adding elements")
    void sizeShouldIncreaseAfterAdding() {
        tree.add(5);
        assertEquals(1, tree.size());
        tree.add(3);
        assertEquals(2, tree.size());
        tree.add(7);
        assertEquals(3, tree.size());
    }

    @Test
    @DisplayName("Size should not increase when adding duplicates")
    void sizeShouldNotIncreaseForDuplicates() {
        tree.add(5);
        tree.add(5);
        assertEquals(1, tree.size());
    }

    @Test
    @DisplayName("Size should decrease after removing elements")
    void sizeShouldDecreaseAfterRemoving() {
        tree.add(5);
        tree.add(3);
        tree.add(7);
        tree.remove(3);
        assertEquals(2, tree.size());
        tree.remove(5);
        assertEquals(1, tree.size());
    }

    // === Add Tests ===

    @Test
    @DisplayName("Add should return true for new elements")
    void addShouldReturnTrueForNewElements() {
        assertTrue(tree.add(5));
        assertTrue(tree.add(3));
        assertTrue(tree.add(7));
    }

    @Test
    @DisplayName("Add should return false for duplicates")
    void addShouldReturnFalseForDuplicates() {
        tree.add(5);
        assertFalse(tree.add(5));
    }

    @Test
    @DisplayName("Add should return false for null")
    void addShouldReturnFalseForNull() {
        assertFalse(tree.add(null));
        assertEquals(0, tree.size());
    }

    @Test
    @DisplayName("Add should set root for first element")
    void addShouldSetRootForFirstElement() {
        tree.add(5);
        assertNotNull(tree.root());
        assertEquals(5, tree.root().getValue());
    }

    // === Search Tests ===

    @Test
    @DisplayName("Search should return true for existing elements")
    void searchShouldReturnTrueForExistingElements() {
        tree.add(5);
        tree.add(3);
        tree.add(7);
        assertTrue(tree.search(5));
        assertTrue(tree.search(3));
        assertTrue(tree.search(7));
    }

    @Test
    @DisplayName("Search should return false for non-existing elements")
    void searchShouldReturnFalseForNonExistingElements() {
        tree.add(5);
        tree.add(3);
        tree.add(7);
        assertFalse(tree.search(1));
        assertFalse(tree.search(10));
        assertFalse(tree.search(4));
    }

    @Test
    @DisplayName("Search should return false for null")
    void searchShouldReturnFalseForNull() {
        tree.add(5);
        assertFalse(tree.search(null));
    }

    @Test
    @DisplayName("Search should return false on empty tree")
    void searchShouldReturnFalseOnEmptyTree() {
        assertFalse(tree.search(5));
    }

    // === Remove Tests ===

    @Test
    @DisplayName("Remove should return true for existing elements")
    void removeShouldReturnTrueForExistingElements() {
        tree.add(5);
        tree.add(3);
        tree.add(7);
        assertTrue(tree.remove(3));
        assertTrue(tree.remove(5));
        assertTrue(tree.remove(7));
    }

    @Test
    @DisplayName("Remove should return false for non-existing elements")
    void removeShouldReturnFalseForNonExistingElements() {
        tree.add(5);
        assertFalse(tree.remove(10));
    }

    @Test
    @DisplayName("Remove should return false for null")
    void removeShouldReturnFalseForNull() {
        tree.add(5);
        assertFalse(tree.remove(null));
    }

    @Test
    @DisplayName("Remove should return false on empty tree")
    void removeShouldReturnFalseOnEmptyTree() {
        assertFalse(tree.remove(5));
    }

    @Test
    @DisplayName("Remove leaf node should work correctly")
    void removeLeafNodeShouldWork() {
        tree.add(5);
        tree.add(3);
        tree.add(7);
        tree.remove(3);
        assertFalse(tree.search(3));
        assertTrue(tree.search(5));
        assertTrue(tree.search(7));
    }

    @Test
    @DisplayName("Remove node with one child should work correctly")
    void removeNodeWithOneChildShouldWork() {
        tree.add(5);
        tree.add(3);
        tree.add(2);
        tree.remove(3);
        assertFalse(tree.search(3));
        assertTrue(tree.search(5));
        assertTrue(tree.search(2));
    }

    @Test
    @DisplayName("Remove node with two children should work correctly")
    void removeNodeWithTwoChildrenShouldWork() {
        tree.add(5);
        tree.add(3);
        tree.add(7);
        tree.add(2);
        tree.add(4);
        tree.remove(3);
        assertFalse(tree.search(3));
        assertTrue(tree.search(5));
        assertTrue(tree.search(7));
        assertTrue(tree.search(2));
        assertTrue(tree.search(4));
    }

    @Test
    @DisplayName("Remove root should work correctly")
    void removeRootShouldWork() {
        tree.add(5);
        tree.add(3);
        tree.add(7);
        tree.remove(5);
        assertFalse(tree.search(5));
        assertTrue(tree.search(3));
        assertTrue(tree.search(7));
        assertEquals(2, tree.size());
    }

    @Test
    @DisplayName("Remove only element should result in empty tree")
    void removeOnlyElementShouldResultInEmptyTree() {
        tree.add(5);
        tree.remove(5);
        assertEquals(0, tree.size());
        assertNull(tree.root());
    }

    // === Values Tests ===

    @Test
    @DisplayName("Values should return empty list for empty tree")
    void valuesShouldReturnEmptyListForEmptyTree() {
        List<Integer> values = tree.values();
        assertTrue(values.isEmpty());
    }

    @Test
    @DisplayName("Values should return sorted list")
    void valuesShouldReturnSortedList() {
        tree.add(5);
        tree.add(3);
        tree.add(7);
        tree.add(1);
        tree.add(9);
        List<Integer> values = tree.values();
        assertEquals(List.of(1, 3, 5, 7, 9), values);
    }

    @Test
    @DisplayName("Values should return correct list after removals")
    void valuesShouldReturnCorrectListAfterRemovals() {
        tree.add(5);
        tree.add(3);
        tree.add(7);
        tree.add(1);
        tree.add(9);
        tree.remove(3);
        tree.remove(9);
        List<Integer> values = tree.values();
        assertEquals(List.of(1, 5, 7), values);
    }

    // === Root Tests ===

    @Test
    @DisplayName("Root should be null for empty tree")
    void rootShouldBeNullForEmptyTree() {
        assertNull(tree.root());
    }

    @Test
    @DisplayName("Root should have correct value")
    void rootShouldHaveCorrectValue() {
        tree.add(5);
        assertEquals(5, tree.root().getValue());
    }

    // === isEmpty Tests ===

    @Test
    @DisplayName("isEmpty should return true for empty tree")
    void isEmptyShouldReturnTrueForEmptyTree() {
        assertTrue(tree.isEmpty());
    }

    @Test
    @DisplayName("isEmpty should return false after adding elements")
    void isEmptyShouldReturnFalseAfterAdding() {
        tree.add(5);
        assertFalse(tree.isEmpty());
    }

    // === Complex Scenarios ===

    @Test
    @DisplayName("Should handle many sequential additions")
    void shouldHandleManySequentialAdditions() {
        for (int i = 1; i <= 100; i++) {
            tree.add(i);
        }
        assertEquals(100, tree.size());
        for (int i = 1; i <= 100; i++) {
            assertTrue(tree.search(i));
        }
    }

    @Test
    @DisplayName("Should handle alternating add and remove")
    void shouldHandleAlternatingAddAndRemove() {
        tree.add(5);
        tree.add(3);
        tree.remove(5);
        tree.add(7);
        tree.remove(3);
        tree.add(1);
        assertEquals(2, tree.size());
        assertFalse(tree.search(5));
        assertFalse(tree.search(3));
        assertTrue(tree.search(7));
        assertTrue(tree.search(1));
    }

    @Test
    @DisplayName("Should maintain BST property after operations")
    void shouldMaintainBSTProperty() {
        tree.add(50);
        tree.add(30);
        tree.add(70);
        tree.add(20);
        tree.add(40);
        tree.add(60);
        tree.add(80);
        tree.remove(30);
        tree.remove(70);
        List<Integer> values = tree.values();
        for (int i = 0; i < values.size() - 1; i++) {
            assertTrue(values.get(i) < values.get(i + 1));
        }
    }

    @Test
    @DisplayName("Should work with String type")
    void shouldWorkWithStringType() {
        SearchTreeImplemented<String> stringTree = new SearchTreeImplemented<>();
        stringTree.add("banana");
        stringTree.add("apple");
        stringTree.add("cherry");
        assertTrue(stringTree.search("apple"));
        assertTrue(stringTree.search("banana"));
        assertTrue(stringTree.search("cherry"));
        List<String> values = stringTree.values();
        assertEquals(List.of("apple", "banana", "cherry"), values);
    }

    @Test
    @DisplayName("Remove all elements should result in empty tree")
    void removeAllElementsShouldResultInEmptyTree() {
        tree.add(5);
        tree.add(3);
        tree.add(7);
        tree.remove(5);
        tree.remove(3);
        tree.remove(7);
        assertEquals(0, tree.size());
        assertNull(tree.root());
        assertTrue(tree.values().isEmpty());
    }
}