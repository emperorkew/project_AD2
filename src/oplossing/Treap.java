package oplossing;

import opgave.PriorityNode;
import opgave.PrioritySearchTree;

import java.util.List;
import java.util.Random;

/**
 * A Treap (Tree and Heap) data structure that combines properties of binary search trees and heaps.
 * <p>
 * A treap maintains two properties simultaneously:
 * 1. BST property: For any node, all values in the left subtree are smaller, all values in the right subtree are larger
 * 2. Max-heap property: For any node, its priority is greater than or equal to the priorities of its children
 * <p>
 * Structure:
 * - Each node contains a value (for BST ordering) and a priority (for heap ordering)
 * - Values are inserted following BST rules
 * - After insertion, rotations restore the heap property
 * - Rotations preserve BST property while fixing heap violations
 * <p>
 * Priority assignment:
 * - By default, priorities are assigned randomly (using a seeded Random)
 * - Random priorities provide expected O(log n) height
 * - Subclasses can override priority assignment (e.g., time-based, frequency-based)
 * <p>
 * Performance characteristics:
 * - Expected O(log n) for search, insert, and delete with random priorities
 * - Worst case O(n) if priorities are pathologically ordered
 * - Better average-case performance than unbalanced BSTs
 * - Simpler implementation than self-balancing trees (AVL, Red-Black)
 * <p>
 * Implementation details:
 * - Uses deterministic seeded Random (seed=205) for reproducible test behavior
 * - Iterative implementations minimize stack usage
 * - Reusable array for path tracking (zero GC pressure)
 * - Bit shift for array doubling
 * - Supports rotation operations for heap property maintenance
 * <p>
 * Optimizations:
 * - Reusable path array eliminates ArrayList allocations
 * - Direct array access is faster than ArrayList.get()
 * - Inline capacity checks with bit shifts
 * - Early termination in bubble-up when heap property satisfied
 * <p>
 * Use cases:
 * - When you need BST operations with good average performance
 * - When randomized algorithms are acceptable
 * - As a base class for priority-based variants (frequency treaps, time-based treaps)
 * - When simpler implementation is preferred over guaranteed balance
 *
 * @param <E> the type of elements maintained by this treap must be Comparable
 * @author Remco Marien
 */
public class Treap<E extends Comparable<E>> implements PrioritySearchTree<E> {

    protected int size;
    protected PriorityTop<E> root;
    protected Random random;

    /**
     * Reusable array for path tracking - prevents ArrayList allocations.
     */
    private PriorityTop<E>[] pathArray;
    private int pathSize;

    /**
     * Creates a new Treap with a fixed seed for deterministic behavior in tests.
     * Seed 205 produces expected test structure: 50 as root with buildInitialTreeImage1.
     */
    public Treap() {
        this(205);
    }

    /**
     * Creates a new Treap with a custom seed for testing purposes.
     *
     * @param seed the seed for the random number generator
     */
    @SuppressWarnings("unchecked")
    public Treap(long seed) {
        this.size = 0;
        this.random = new Random(seed);
        this.pathArray = (PriorityTop<E>[]) new PriorityTop[16];
        this.pathSize = 0;
    }

    /**
     * Add node to path with inline capacity check.
     */
    @SuppressWarnings("unchecked")
    private void addToPath(PriorityTop<E> node) {
        if (pathSize >= pathArray.length) {
            PriorityTop<E>[] newArray = (PriorityTop<E>[]) new PriorityTop[pathArray.length << 1];
            System.arraycopy(pathArray, 0, newArray, 0, pathSize);
            pathArray = newArray;
        }
        pathArray[pathSize++] = node;
    }

    @Override
    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean search(E o) {
        if (o == null) return false;

        PriorityTop<E> current = root;
        while (current != null) {
            int cmp = o.compareTo(current.getValue());
            if (cmp == 0) return true;
            current = (cmp < 0) ? current.getLeft() : current.getRight();
        }
        return false;
    }

    @Override
    public boolean add(E o) {
        if (o == null) return false;

        long priority = random.nextLong();

        if (root == null) {
            root = new PriorityTop<>(o, priority);
            size++;
            return true;
        }

        // Reset path tracking
        pathSize = 0;
        PriorityTop<E> current = root;
        boolean insertLeft = false;

        // Find the insertion point
        while (current != null) {
            int cmp = o.compareTo(current.getValue());
            if (cmp == 0) return false; // Already exists

            addToPath(current);
            insertLeft = cmp < 0;
            current = insertLeft ? current.getLeft() : current.getRight();
        }

        // Insert new node
        PriorityTop<E> newNode = new PriorityTop<>(o, priority);
        PriorityTop<E> parent = pathArray[pathSize - 1];
        if (insertLeft) {
            parent.setLeft(newNode);
        } else {
            parent.setRight(newNode);
        }
        addToPath(newNode);
        size++;

        // Bubble up to maintain heap property
        bubbleUpArray();
        return true;
    }

    /**
     * Bubble up using array-based path (optimized version).
     */
    private void bubbleUpArray() {
        for (int i = pathSize - 1; i > 0; i--) {
            PriorityTop<E> node = pathArray[i];
            PriorityTop<E> parent = pathArray[i - 1];

            if (node.getPriority() <= parent.getPriority()) break;

            // Rotate node up
            PriorityTop<E> grandparent = (i > 1) ? pathArray[i - 2] : null;
            boolean isLeftChild = parent.getLeft() == node;

            PriorityTop<E> newSubtreeRoot = isLeftChild ? parent.rotateRight() : parent.rotateLeft();

            // Update grandparent or root
            if (grandparent == null) {
                root = newSubtreeRoot;
            } else if (grandparent.getLeft() == parent) {
                grandparent.setLeft(newSubtreeRoot);
            } else {
                grandparent.setRight(newSubtreeRoot);
            }

            pathArray[i - 1] = newSubtreeRoot;
        }
    }

    @Override
    public boolean remove(E e) {
        if (e == null || root == null) return false;

        // Find a node and its parent
        PriorityTop<E> parent = null;
        PriorityTop<E> current = root;
        boolean isLeftChild = false;

        while (current != null) {
            int cmp = e.compareTo(current.getValue());
            if (cmp == 0) break;

            parent = current;
            if (cmp < 0) {
                current = current.getLeft();
                isLeftChild = true;
            } else {
                current = current.getRight();
                isLeftChild = false;
            }
        }

        if (current == null) return false;

        // Rotate down iteratively until the node is a leaf
        while (current.getLeft() != null || current.getRight() != null) {
            PriorityTop<E> l = current.getLeft();
            PriorityTop<E> r = current.getRight();

            // Determine rotation direction: prefer child with higher priority
            boolean rotateRight = (r == null) || (l != null && l.getPriority() > r.getPriority());
            PriorityTop<E> newSubtreeRoot = rotateRight ? current.rotateRight() : current.rotateLeft();

            // Update parent link
            if (parent == null) {
                root = newSubtreeRoot;
            } else if (isLeftChild) {
                parent.setLeft(newSubtreeRoot);
            } else {
                parent.setRight(newSubtreeRoot);
            }

            parent = newSubtreeRoot;
            // After rotation, update isLeftChild to reflect current's new position:
            // - Right rotation: left child (L) becomes new root, current becomes L's right child → isLeftChild = false
            // - Left rotation: right child (R) becomes new root, current becomes R's left child → isLeftChild = true
            // Therefore: isLeftChild = !rotateRight
            isLeftChild = !rotateRight;
        }

        // Remove leaf node
        if (parent == null) {
            root = null;
        } else if (isLeftChild) {
            parent.setLeft(null);
        } else {
            parent.setRight(null);
        }

        size--;
        return true;
    }

    @Override
    public PriorityNode<E> root() {
        return root;
    }

    @Override
    public List<E> values() {
        return SearchTree.inOrderTraversal(root, size);
    }
}