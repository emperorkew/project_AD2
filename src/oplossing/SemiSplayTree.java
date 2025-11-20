package oplossing;

import java.util.ArrayList;
import java.util.List;

public class SemiSplayTree<E extends Comparable<E>> extends SearchTreeImplemented<E> {

    public SemiSplayTree() {
        super();
    }

    /**
     * Rotate the subtree rooted at node to the right.
     */
    private Top<E> rotateRight(Top<E> node) {
        Top<E> left = (Top<E>) node.getLeft();
        Top<E> leftRight = (Top<E>) left.getRight();

        left.setRight(node);
        node.setLeft(leftRight);

        return left;
    }

    /**
     * Rotate the subtree rooted at node to the left.
     */
    private Top<E> rotateLeft(Top<E> node) {
        Top<E> right = (Top<E>) node.getRight();
        Top<E> rightLeft = (Top<E>) right.getLeft();

        right.setLeft(node);
        node.setRight(rightLeft);

        return right;
    }

    /**
     * Perform semi-splay operation on the given path.
     * The path should be ordered from root to the accessed node.
     */
    private void semiSplayPath(List<Top<E>> path) {
        if (path.size() < 3) {
            return;
        }

        int i = path.size() - 1;

        while (i >= 2) {
            Top<E> child = path.get(i);
            Top<E> parent = path.get(i - 1);
            Top<E> grandparent = path.get(i - 2);
            Top<E> greatGrandparent = (i >= 3) ? path.get(i - 3) : null;

            Top<E> newSubtreeRoot = splayStep(child, parent, grandparent);

            if (greatGrandparent == null) {
                root = newSubtreeRoot;
            } else if (greatGrandparent.getLeft() == grandparent) {
                greatGrandparent.setLeft(newSubtreeRoot);
            } else {
                greatGrandparent.setRight(newSubtreeRoot);
            }

            path.set(i - 2, newSubtreeRoot);
            i -= 2;
        }
    }

    /**
     * Perform a splay step on the triple (child, parent, grandparent).
     */
    private Top<E> splayStep(Top<E> child, Top<E> parent, Top<E> grandparent) {
        boolean parentIsLeft = (grandparent.getLeft() == parent);
        boolean childIsLeft = (parent.getLeft() == child);

        if (parentIsLeft && childIsLeft) {
            grandparent = rotateRight(grandparent);
            return rotateRight(grandparent);
        } else if (!parentIsLeft && !childIsLeft) {
            grandparent = rotateLeft(grandparent);
            return rotateLeft(grandparent);
        } else if (!parentIsLeft && childIsLeft) {
            grandparent.setRight(rotateRight(parent));
            return rotateLeft(grandparent);
        } else {
            grandparent.setLeft(rotateLeft(parent));
            return rotateRight(grandparent);
        }
    }

    @Override
    public boolean search(E o) {
        if (o == null || root == null) {
            return false;
        }

        // Build path while searching - single traversal
        List<Top<E>> path = new ArrayList<>();
        Top<E> current = root;
        boolean found = false;

        while (current != null) {
            path.add(current);
            int cmp = o.compareTo(current.getValue());

            if (cmp == 0) {
                found = true;
                break;
            } else if (cmp < 0) {
                current = (Top<E>) current.getLeft();
            } else {
                current = (Top<E>) current.getRight();
            }
        }

        // Splay using the path we already built
        semiSplayPath(path);

        return found;
    }

    @Override
    public boolean add(E o) {
        if (o == null) {
            return false;
        }

        if (root == null) {
            root = new Top<>(o);
            incrementSize();
            return true;
        }

        // Build path while adding - single traversal
        List<Top<E>> path = new ArrayList<>();
        Top<E> current = root;

        while (true) {
            path.add(current);
            int cmp = o.compareTo(current.getValue());

            if (cmp == 0) {
                // Element exists, splay to it and return false
                semiSplayPath(path);
                return false;
            } else if (cmp < 0) {
                if (current.getLeft() == null) {
                    Top<E> newNode = new Top<>(o);
                    current.setLeft(newNode);
                    path.add(newNode);
                    incrementSize();
                    semiSplayPath(path);
                    return true;
                }
                current = (Top<E>) current.getLeft();
            } else {
                if (current.getRight() == null) {
                    Top<E> newNode = new Top<>(o);
                    current.setRight(newNode);
                    path.add(newNode);
                    incrementSize();
                    semiSplayPath(path);
                    return true;
                }
                current = (Top<E>) current.getRight();
            }
        }
    }

    @Override
    public boolean remove(E e) {
        if (e == null || root == null) {
            return false;
        }

        // Build path while finding the node - single traversal
        List<Top<E>> path = new ArrayList<>();
        Top<E> current = root;
        Top<E> parent = null;
        boolean isLeftChild = false;

        while (current != null) {
            int cmp = e.compareTo(current.getValue());

            if (cmp == 0) {
                break;
            }

            path.add(current);
            parent = current;

            if (cmp < 0) {
                current = (Top<E>) current.getLeft();
                isLeftChild = true;
            } else {
                current = (Top<E>) current.getRight();
                isLeftChild = false;
            }
        }

        if (current == null) {
            // Element not found, splay to last accessed node
            semiSplayPath(path);
            return false;
        }

        // Remove the node and get replacement
        Top<E> replacement = deleteNode(current);

        // Update parent link
        if (parent == null) {
            root = replacement;
        } else if (isLeftChild) {
            parent.setLeft(replacement);
        } else {
            parent.setRight(replacement);
        }

        decrementSize();

        // Splay to parent (or replacement if it exists)
        if (!path.isEmpty()) {
            semiSplayPath(path);
        }

        return true;
    }

    /**
     * Delete a node and return its replacement.
     */
    private Top<E> deleteNode(Top<E> node) {
        if (node.getLeft() == null) {
            return (Top<E>) node.getRight();
        } else if (node.getRight() == null) {
            return (Top<E>) node.getLeft();
        }

        // Two children: find and remove inorder successor
        Top<E> successorParent = node;
        Top<E> successor = (Top<E>) node.getRight();

        while (successor.getLeft() != null) {
            successorParent = successor;
            successor = (Top<E>) successor.getLeft();
        }

        // Remove successor from its current position
        if (successorParent == node) {
            successorParent.setRight((Top<E>) successor.getRight());
        } else {
            successorParent.setLeft((Top<E>) successor.getRight());
        }

        // Replace node with successor
        successor.setLeft((Top<E>) node.getLeft());
        successor.setRight((Top<E>) node.getRight());

        return successor;
    }
}