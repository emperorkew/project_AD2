package oplossing;

import opgave.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * A standard Binary Search Tree implementation that serves as a base class for specialized tree variants.
 * <p>
 * This class provides iterative implementations of core BST operations to minimize stack space usage.
 * It implements the SearchTree interface and can be extended by self-balancing tree variants
 * such as SemiSplayTree.
 * <p>
 * Key characteristics:
 * - Iterative search, add, and remove operations (no recursion)
 * - Uses inorder successor for node deletion with two children
 * - Maintains size tracking with protected increment/decrement methods for subclasses
 * - Iterative in-order traversal for values() method
 * <p>
 * Time complexity:
 * - Average case O(log n) for search, add, and remove operations
 * - Worst case O(n) for degenerate (linear) trees
 * <p>
 * Space complexity:
 * - O(n) for the tree structure
 * - O(1) auxiliary space for most operations (iterative approach)
 * - O(h) auxiliary space for values() traversal (where h is tree height)
 *
 * @param <E> the type of elements maintained by this tree must be Comparable
 * @author Remco Marien
 */
public class SearchTree<E extends Comparable<E>> implements opgave.SearchTree<E> {

    private int size;
    protected Top<E> root;

    /**
     * Result of finding a node in the tree containing the node, its parent, and traversal path.
     * Used by subclasses that need the path (e.g., SemiSplayTree).
     */
    protected record FindResult<E extends Comparable<E>>(
            Top<E> node,
            Top<E> parent,
            boolean isLeftChild,
            List<Top<E>> path
    ) {}

    /**
     * Lightweight result of finding a node without path tracking.
     * Used by operations that don't need the traversal path.
     */
    private record SimpleFindResult<E extends Comparable<E>>(
            Top<E> node,
            Top<E> parent,
            boolean isLeftChild
    ) {}

    public SearchTree() {
        this.size = 0;
    }

    /**
     * Estimates the height of the tree for pre-allocating collections.
     * Uses log2(n) as baseline for balanced trees with bounds to handle edge cases.
     *
     * @return estimated height, minimum 8, maximum 64
     */
    private int estimateHeight() {
        if (size == 0) return 8;
        // For balanced tree: log2(n) ≈ log(n)/log(2)
        // Add 50% margin for partially unbalanced trees
        int estimated = (int) (Math.log(size + 1) / Math.log(2) * 1.5);
        return Math.min(Math.max(estimated, 8), 64);
    }

    /**
     * Finds a node in the tree and returns traversal information.
     *
     * @param e the element to find
     * @return FindResult containing the node (or null if not found), parent, isLeftChild flag, and path
     */
    protected FindResult<E> findNode(E e) {
        List<Top<E>> path = new ArrayList<>(estimateHeight());
        Top<E> current = root;
        Top<E> parent = null;
        boolean isLeftChild = false;

        while (current != null) {
            int cmp = e.compareTo(current.getValue());

            if (cmp == 0) break;

            path.add(current);
            parent = current;

            if (cmp < 0) {
                current = current.getLeft();
                isLeftChild = true;
            } else {
                current = current.getRight();
                isLeftChild = false;
            }
        }

        return new FindResult<>(current, parent, isLeftChild, path);
    }

    /**
     * Finds a node without tracking the path. More efficient for operations that don't need the path.
     */
    private SimpleFindResult<E> findNodeSimple(E e) {
        Top<E> current = root;
        Top<E> parent = null;
        boolean isLeftChild = false;

        while (current != null) {
            int cmp = e.compareTo(current.getValue());
            if (cmp == 0) break;

            parent = current;
            isLeftChild = cmp < 0;
            current = isLeftChild ? current.getLeft() : current.getRight();
        }

        return new SimpleFindResult<>(current, parent, isLeftChild);
    }

    public void setRoot(Top<E> root) {
        this.root = root;
    }

    @Override
    public Node<E> root() {
        return root;
    }

    protected void incrementSize() {
        size++;
    }

    protected void decrementSize() {
        size--;
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

        Top<E> current = root;
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

        if (root == null) {
            root = new Top<>(o);
            size++;
            return true;
        }

        Top<E> current = root;
        while (true) {
            int cmp = o.compareTo(current.getValue());

            if (cmp == 0) return false;

            if (cmp < 0) {
                Top<E> left = current.getLeft();
                if (left == null) {
                    current.setLeft(new Top<>(o));
                    size++;
                    return true;
                }
                current = left;
            } else {
                Top<E> right = current.getRight();
                if (right == null) {
                    current.setRight(new Top<>(o));
                    size++;
                    return true;
                }
                current = right;
            }
        }
    }

    @Override
    public boolean remove(E e) {
        if (e == null || root == null) return false;

        SimpleFindResult<E> result = findNodeSimple(e);

        if (result.node() == null) return false;

        Top<E> replacement = deleteNode(result.node());

        if (result.parent() == null) {
            root = replacement;
        } else if (result.isLeftChild()) {
            result.parent().setLeft(replacement);
        } else {
            result.parent().setRight(replacement);
        }

        size--;
        return true;
    }

    private Top<E> deleteNode(Top<E> node) {
        if (node.getLeft() == null) return node.getRight();
        if (node.getRight() == null) return node.getLeft();

        // Two children: find inorder successor
        Top<E> successorParent = node;
        Top<E> successor = node.getRight();

        while (successor.getLeft() != null) {
            successorParent = successor;
            successor = successor.getLeft();
        }

        if (successorParent == node) {
            successorParent.setRight(successor.getRight());
        } else {
            successorParent.setLeft(successor.getRight());
        }

        successor.setLeft(node.getLeft());
        successor.setRight(node.getRight());

        return successor;
    }

    @Override
    public List<E> values() {
        return inOrderTraversal(root, size);
    }

    /**
     * Performs an iterative in-order traversal of a tree rooted at the given node.
     *
     * @param root the root node of the tree
     * @param size the expected size (for list pre-allocation)
     * @param <E>  the element type
     * @param <N>  the node type (Top or subclass like PriorityTop)
     * @return list of values in sorted order
     */
    protected static <E extends Comparable<E>, N extends Top<E>> List<E> inOrderTraversal(N root, int size) {
        List<E> result = new ArrayList<>(size);
        Top<E> current = root;
        // Estimate stack size based on tree size (log2(n) * 1.5, bounded between 8 and 64)
        int stackCapacity = size == 0 ? 8 : Math.min(Math.max((int) (Math.log(size + 1) / Math.log(2) * 1.5), 8), 64);
        List<Top<E>> stack = new ArrayList<>(stackCapacity);

        while (current != null || !stack.isEmpty()) {
            while (current != null) {
                stack.add(current);
                current = current.getLeft();
            }
            current = stack.removeLast();
            result.add(current.getValue());
            current = current.getRight();
        }
        return result;
    }
}