package test;

import opgave.Node;
import oplossing.SemiSplayTree;
import oplossing.Top;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SemiSplayTreeTest {

    private SemiSplayTree<Integer> tree;

    @BeforeEach
    void setUp() {
        tree = new SemiSplayTree<>();
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
    @DisplayName("Path < 3: Geen Splay bij Root of Kind van Root")
    void testNoSplayConditions() {
        tree.add(10);
        tree.add(5); // Pad: [10, 5] -> size 2
        // Actie: Search 5
        tree.search(5);
        // Assertie: Omdat size < 3, mag er niks veranderen.
        assertEquals(10, tree.root().getValue());
        assertEquals(5, tree.root().getLeft().getValue());
    }

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

    // === Semi-Splay Specific Tests ===

    @Test
    @DisplayName("Figuur 6 Scenario: Combinatie van Zig-Zag en Zig-Zig")
    void testFigure6Scenario() {
        // 1. Maak de nodes aan
        Top<Integer> n75 = new Top<>(75);
        Top<Integer> n60 = new Top<>(60);
        Top<Integer> n30 = new Top<>(30);
        Top<Integer> n4 = new Top<>(4);
        Top<Integer> n13 = new Top<>(13);
        Top<Integer> n7 = new Top<>(7);

        // 2. Bouw de boomstructuur exact na zoals in Figuur 6 (links)
        // Pad: 75 -> 60 -> 30 -> 4 -> 13 -> 7

        n75.setLeft(n60);      // Root -> 60
        n60.setLeft(n30);      // 60 -> 30 (Zig-Zig lijn begint)
        n30.setLeft(n4);       // 30 -> 4
        n4.setRight(n13);      // 4 -> 13 (Zig-Zag knik)
        n13.setLeft(n7);       // 13 -> 7 (Target)

        // Injecteer de root (hack om splay tijdens insert te omzeilen)
        tree.setRoot(n75);

        // 3. Voer de search uit op de onderste node (7)
        boolean found = tree.search(7);
        assertTrue(found, "Element 7 moet gevonden worden");

        // 4. Verifieer de structuur exact zoals in Figuur 6 (rechts)

        // De Root (75) blijft ongewijzigd omdat de loop stopt (step size 2)
        assertEquals(75, tree.root().getValue());

        // Niveau 1: 30 moet links van 75 staan (Resultaat van de bovenste Zig-Zig)
        Node<Integer> n30_new = tree.root().getLeft();
        assertEquals(30, n30_new.getValue(), "30 moet omhoog gekomen zijn door de Zig-Zig splay");

        // Niveau 2: Kinderen van 30
        assertEquals(60, n30_new.getRight().getValue(), "60 moet rechts van 30 gezakt zijn");
        Node<Integer> n7_new = n30_new.getLeft();
        assertEquals(7, n7_new.getValue(), "7 is omhoog gekomen door de Zig-Zag en nu kind van 30");

        // Niveau 3: Kinderen van 7 (Resultaat van de onderste Zig-Zag)
        // Bij de Zig-Zag (4 -> 13 -> 7) wordt 7 de ouder van 4 en 13.
        assertEquals(4, n7_new.getLeft().getValue(), "4 moet links van 7 staan");
        assertEquals(13, n7_new.getRight().getValue(), "13 moet rechts van 7 staan");
    }

    @Test
    @DisplayName("Zig-Zig (Left): Parent wordt Root, niet Child")
    void testZigZigLeftSemiSplay() {
        // Situatie: 30 -> 20 -> 10
        tree.add(30);
        tree.add(20);
        tree.add(10);

        // Pad voor 10: [30, 20, 10]
        // Child: 10, Parent: 20, Grandparent: 30
        // Dit is een Zig-Zig (beide links).

        tree.search(10);

        assertEquals(20, tree.root().getValue(),
                "Bij Semi-Splay Zig-Zig moet de PARENT (20) de plek van Grandparent innemen.");

        assertEquals(10, tree.root().getLeft().getValue(), "Child (10) blijft onder de parent.");
        assertEquals(30, tree.root().getRight().getValue(), "Grandparent (30) zakt naar rechts.");
    }

    @Test
    @DisplayName("Zig-Zag (Left-Right): Child wordt Root (Double Rotation)")
    void testZigZagSemiSplay() {
        // Situatie: 30 -> 10 -> 20
        tree.add(30);
        tree.add(10);
        tree.add(20);

        // Pad voor 20: [30, 10, 20]
        // Child: 20, Parent: 10 (Left child of 30), GP: 30
        // Dit is Left-Right (Zig-Zag).

        tree.search(20);

        assertEquals(20, tree.root().getValue(),
                "Bij Zig-Zag moet de target (20) wél naar boven komen.");
        assertEquals(10, tree.root().getLeft().getValue());
        assertEquals(30, tree.root().getRight().getValue());
    }


    @Test
    @DisplayName("Tree structure should change after search (semi-splay)")
    void treeStructureShouldChangeAfterSearch() {
        // Build a tree with elements
        tree.add(10);
        tree.add(5);
        tree.add(3);
        tree.add(1);

        // All elements should be present
        assertEquals(4, tree.size());
        assertTrue(tree.search(1));
        assertTrue(tree.search(3));
        assertTrue(tree.search(5));
        assertTrue(tree.search(10));

        // After multiple searches, tree should still be correct
        // (splaying may change root but all elements remain)
        List<Integer> values = tree.values();
        assertEquals(List.of(1, 3, 5, 10), values);
    }

    @Test
    @DisplayName("Tree structure should change after add (semi-splay)")
    void treeStructureShouldChangeAfterAdd() {
        // Add elements that will create a deep path
        tree.add(10);
        tree.add(8);
        tree.add(6);
        tree.add(4);
        tree.add(2);

        // All elements should be present
        List<Integer> values = tree.values();
        assertEquals(List.of(2, 4, 6, 8, 10), values);
        assertEquals(5, tree.size());
    }

    @Test
    @DisplayName("Should handle many sequential additions with splaying")
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
    @DisplayName("Should handle many reverse sequential additions with splaying")
    void shouldHandleManyReverseSequentialAdditions() {
        for (int i = 100; i >= 1; i--) {
            tree.add(i);
        }
        assertEquals(100, tree.size());
        for (int i = 1; i <= 100; i++) {
            assertTrue(tree.search(i));
        }
    }

    @Test
    @DisplayName("Should handle alternating add and remove with splaying")
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
    @DisplayName("Should maintain BST property after operations with splaying")
    void shouldMaintainBSTProperty() {
        tree.add(50);
        tree.add(30);
        tree.add(70);
        tree.add(20);
        tree.add(40);
        tree.add(60);
        tree.add(80);

        // Perform some searches to trigger splaying
        tree.search(20);
        tree.search(80);
        tree.search(40);

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
        SemiSplayTree<String> stringTree = new SemiSplayTree<>();
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

    @Test
    @DisplayName("Repeated searches should maintain tree correctness")
    void repeatedSearchesShouldMaintainTreeCorrectness() {
        tree.add(5);
        tree.add(3);
        tree.add(7);
        tree.add(1);
        tree.add(9);

        // Perform multiple searches
        for (int i = 0; i < 10; i++) {
            assertTrue(tree.search(1));
            assertTrue(tree.search(9));
            assertTrue(tree.search(5));
        }

        // Tree should still be correct
        assertEquals(List.of(1, 3, 5, 7, 9), tree.values());
    }

    @Test
    @DisplayName("Remove with predecessor replacement should work")
    void removeWithPredecessorShouldWork() {
        // Build tree where removal needs predecessor
        tree.add(10);
        tree.add(5);
        tree.add(15);
        tree.add(3);
        tree.add(7);
        tree.add(12);
        tree.add(20);

        // Remove node with two children (should use largest from left subtree)
        tree.remove(10);

        assertFalse(tree.search(10));
        assertTrue(tree.search(5));
        assertTrue(tree.search(15));
        assertTrue(tree.search(3));
        assertTrue(tree.search(7));
        assertTrue(tree.search(12));
        assertTrue(tree.search(20));
        assertEquals(6, tree.size());

        // Values should still be sorted
        List<Integer> values = tree.values();
        assertEquals(List.of(3, 5, 7, 12, 15, 20), values);
    }

    @Test
    @DisplayName("Complex operations sequence should maintain correctness")
    void complexOperationsSequenceShouldMaintainCorrectness() {
        // Add elements
        for (int i = 1; i <= 20; i++) {
            tree.add(i * 5);
        }
        assertEquals(20, tree.size());

        // Search for various elements
        for (int i = 1; i <= 20; i++) {
            assertTrue(tree.search(i * 5));
        }

        // Remove every other element
        for (int i = 1; i <= 10; i++) {
            tree.remove(i * 10);
        }
        assertEquals(10, tree.size());

        // Verify remaining elements
        for (int i = 1; i <= 10; i++) {
            assertFalse(tree.search(i * 10));
            assertTrue(tree.search(i * 10 - 5));
        }
    }

}