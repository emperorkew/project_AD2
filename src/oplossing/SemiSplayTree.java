package oplossing;

import java.util.ArrayList;
import java.util.List;

/**
 * A Semi-Splay Tree implementation that extends SearchTreeImplemented.
 *
 * Semi-splay trees are a variant of splay trees that perform partial splaying operations
 * to improve tree balance while reducing the overhead of full splay operations.
 *
 * This implementation uses bottom-up splaying: the path from root to the accessed node
 * is first collected, then splay rotations are performed from the bottom up toward the root.
 *
 * Key characteristics:
 * - After each operation (search, add, remove), a semi-splay is performed on the access path
 * - Zig-zig case: performs ONE rotation at the grandparent (vs two in full splay)
 * - Zig-zag case: performs TWO rotations (same as full splay)
 * - Splaying stops when fewer than 3 nodes remain in the path
 *
 * Time complexity:
 * - Amortized O(log n) for search, add, and remove operations
 * - Worst case O(n) for individual operations
 *
 * Space complexity:
 * - O(n) for the tree structure
 * - O(h) auxiliary space for operations (where h is tree height)
 *
 * @param <E> the type of elements maintained by this tree, must be Comparable
 * @author Remco Marien
 */
public class SemiSplayTree<E extends Comparable<E>> extends SearchTreeImplemented<E> {

    public SemiSplayTree() {
        super();
    }

    private void semiSplayPath(List<Top<E>> path) {
        if (path.size() < 3) return; //No splay needed when path is too short

        int i = path.size() - 1; //index of the last node in the path

        while (i >= 2) { // Splay until root
            Top<E> child = path.get(i);
            Top<E> parent = path.get(i - 1);
            Top<E> grandparent = path.get(i - 2);
            Top<E> greatGrandparent = (i >= 3) ? path.get(i - 3) : null;

            Top<E> newSubtreeRoot = splayStep(child, parent, grandparent);

            // Update grandparent or root
            if (greatGrandparent == null) {
                root = newSubtreeRoot;
            } else if (greatGrandparent.getLeft() == grandparent) {
                greatGrandparent.setLeft(newSubtreeRoot);
            } else {
                greatGrandparent.setRight(newSubtreeRoot);
            }
            // -2 to exclude parent and grandparent which have been rotated
            path.set(i - 2, newSubtreeRoot);
            i -= 2;
        }
    }

    // Returns the new root of the subtree
    private Top<E> splayStep(Top<E> child, Top<E> parent, Top<E> grandparent) {
        boolean parentIsLeft = grandparent.getLeft() == parent;
        boolean childIsLeft = parent.getLeft() == child;

        if (parentIsLeft == childIsLeft) {
            // Zig-zig: ONE rotation at grandparent
            return parentIsLeft ? grandparent.rotateRight() : grandparent.rotateLeft();
        } else {
            // Zig-zag: TWO rotations
            if (parentIsLeft) {
                grandparent.setLeft(parent.rotateLeft());
                return grandparent.rotateRight();
            } else {
                grandparent.setRight(parent.rotateRight());
                return grandparent.rotateLeft();
            }
        }
    }

    @Override
    public boolean search(E o) {
        // safety check
        if (o == null || root == null) return false;

        // Track path for bubble-up after priority increase (capacity 32 covers ~4B nodes) --> pre-allocated memory to save stack space
        List<Top<E>> path = new ArrayList<>(32);
        Top<E> current = root;

        // Iterative in-order traversal to save O(h) stack space
        while (current != null) {
            path.add(current);
            int cmp = o.compareTo(current.getValue());

            if (cmp == 0) {
                semiSplayPath(path);
                return true;
            }
            current = (cmp < 0) ? current.getLeft() : current.getRight();
        }

        // Not found
        semiSplayPath(path);
        return false;
    }

    @Override
    public boolean add(E o) {
        // Safety check
        if (o == null) return false;

        // when root is null meaning that the tree is empty, added node will become root
        if (root == null) {
            root = new Top<>(o);
            incrementSize();
            return true;
        }

        // Track path for bubble-up after priority increase (capacity 32 covers ~4B nodes) --> pre-allocated memory to save stack space
        List<Top<E>> path = new ArrayList<>(32);
        Top<E> current = root;

        // Iterative in-order traversal to save O(h) stack space
        while (true) {
            path.add(current);
            int cmp = o.compareTo(current.getValue());

            if (cmp == 0) {
                semiSplayPath(path);
                return false;
            }

            if (cmp < 0) {
                Top<E> left = current.getLeft();
                if (left == null) {
                    Top<E> newNode = new Top<>(o);
                    current.setLeft(newNode);
                    path.add(newNode);
                    incrementSize();
                    semiSplayPath(path);
                    return true;
                }
                current = left;
            } else {
                Top<E> right = current.getRight();
                if (right == null) {
                    Top<E> newNode = new Top<>(o);
                    current.setRight(newNode);
                    path.add(newNode);
                    incrementSize();
                    semiSplayPath(path);
                    return true;
                }
                current = right;
            }
        }
    }

    @Override
    // Remove element e from the tree. Returns true if removed, false if not found.
    public boolean remove(E e) {
        if (e == null || root == null) return false;

        List<Top<E>> path = new ArrayList<>(32);
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

        if (current == null) {
            semiSplayPath(path);
            return false;
        }

        Top<E> replacement = deleteNode(current);

        if (parent == null) {
            root = replacement;
        } else if (isLeftChild) {
            parent.setLeft(replacement);
        } else {
            parent.setRight(replacement);
        }

        decrementSize();
        semiSplayPath(path);

        return true;
    }

    // Delete node and return replacement node.
    private Top<E> deleteNode(Top<E> node) {
        if (node.getLeft() == null) return node.getRight();
        if (node.getRight() == null) return node.getLeft();

        // Two children: find inorder predecessor
        Top<E> predecessorParent = node;
        Top<E> predecessor = node.getLeft();

        while (predecessor.getRight() != null) {
            predecessorParent = predecessor;
            predecessor = predecessor.getRight();
        }

        if (predecessorParent == node) {
            predecessor.setRight(node.getRight());
        } else {
            predecessorParent.setRight(predecessor.getLeft());
            predecessor.setLeft(node.getLeft());
            predecessor.setRight(node.getRight());
        }

        return predecessor;
    }
}