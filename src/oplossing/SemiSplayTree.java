package oplossing;

import java.util.ArrayList;
import java.util.List;

public class SemiSplayTree<E extends Comparable<E>> extends SearchTreeImplemented<E> {

    public SemiSplayTree() {
        super();
    }

    /**
     * Rotate the subtree rooted at node to the right.
     *
     *       node              left
     *       /  \              /  \
     *     left  C    ->      A   node
     *     /  \                   /  \
     *    A    B                 B    C
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
     *
     *     node                right
     *     /  \                /  \
     *    A   right    ->    node  C
     *        /  \           /  \
     *       B    C         A    B
     */
    private Top<E> rotateLeft(Top<E> node) {
        Top<E> right = (Top<E>) node.getRight();
        Top<E> rightLeft = (Top<E>) right.getLeft();

        right.setLeft(node);
        node.setRight(rightLeft);

        return right;
    }

    /**
     * Perform semi-splay operation on the path to the given value.
     * Semi-splaying moves the accessed node closer to the root by performing
     * rotations on every other level of the path.
     */
    private void semiSplay(E value) {
        if (root == null) {
            return;
        }

        // Build path from root to the node (or where it would be)
        List<Top<E>> path = new ArrayList<>();
        Top<E> current = root;

        while (current != null) {
            path.add(current);
            int cmp = value.compareTo(current.getValue());
            if (cmp == 0) {
                break;
            } else if (cmp < 0) {
                current = (Top<E>) current.getLeft();
            } else {
                current = (Top<E>) current.getRight();
            }
        }

        // Perform semi-splaying from bottom to top
        // Process path in groups of 3: grandparent, parent, child
        int i = path.size() - 1;

        while (i >= 2) {
            Top<E> child = path.get(i);
            Top<E> parent = path.get(i - 1);
            Top<E> grandparent = path.get(i - 2);

            // Determine the great-grandparent (if exists) to update the link
            Top<E> greatGrandparent = (i >= 3) ? path.get(i - 3) : null;

            // Perform the appropriate splay step
            Top<E> newSubtreeRoot = splayStep(child, parent, grandparent);

            // Update the link from great-grandparent (or root)
            if (greatGrandparent == null) {
                root = newSubtreeRoot;
            } else if (greatGrandparent.getLeft() == grandparent) {
                greatGrandparent.setLeft(newSubtreeRoot);
            } else {
                greatGrandparent.setRight(newSubtreeRoot);
            }

            // Update the path for next iteration
            path.set(i - 2, newSubtreeRoot);

            // Move up by 2 levels
            i -= 2;
        }

        // Handle remaining single rotation (zig case) if path length is even
        // In standard semi-splay, we don't perform a final zig if only parent remains
        // But some implementations do. Here we skip it for true semi-splay behavior.
    }

    /**
     * Perform a splay step on the triple (child, parent, grandparent).
     * Returns the new root of this subtree.
     */
    private Top<E> splayStep(Top<E> child, Top<E> parent, Top<E> grandparent) {
        boolean parentIsLeft = (grandparent.getLeft() == parent);
        boolean childIsLeft = (parent.getLeft() == child);

        if (parentIsLeft && childIsLeft) {
            // Zig-Zig (left-left): rotate grandparent right, then parent right
            grandparent = rotateRight(grandparent);
            return rotateRight(grandparent);
        } else if (!parentIsLeft && !childIsLeft) {
            // Zig-Zig (right-right): rotate grandparent left, then parent left
            grandparent = rotateLeft(grandparent);
            return rotateLeft(grandparent);
        } else if (!parentIsLeft && childIsLeft) {
            // Zig-Zag (right-left): rotate parent right, then grandparent left
            grandparent.setRight(rotateRight(parent));
            return rotateLeft(grandparent);
        } else {
            // Zig-Zag (left-right): rotate parent left, then grandparent right
            grandparent.setLeft(rotateLeft(parent));
            return rotateRight(grandparent);
        }
    }

    @Override
    public boolean search(E o) {
        if (o == null) {
            return false;
        }

        boolean found = searchRecursive(root, o);

        // Perform semi-splay regardless of whether element was found
        // (splay to the last accessed node)
        if (root != null) {
            semiSplay(o);
        }

        return found;
    }

    private boolean searchRecursive(Top<E> node, E value) {
        if (node == null) {
            return false;
        }

        int comparison = value.compareTo(node.getValue());

        if (comparison == 0) {
            return true;
        } else if (comparison < 0) {
            return searchRecursive((Top<E>) node.getLeft(), value);
        } else {
            return searchRecursive((Top<E>) node.getRight(), value);
        }
    }

    @Override
    public boolean add(E o) {
        if (o == null) {
            return false;
        }

        // Check if element already exists
        if (containsElement(root, o)) {
            // Splay to the existing element
            semiSplay(o);
            return false;
        }

        // Add the element
        root = addRecursive(root, o);
        incrementSize();

        // Splay to the newly added element
        semiSplay(o);

        return true;
    }

    private boolean containsElement(Top<E> node, E value) {
        if (node == null) {
            return false;
        }

        int comparison = value.compareTo(node.getValue());

        if (comparison == 0) {
            return true;
        } else if (comparison < 0) {
            return containsElement((Top<E>) node.getLeft(), value);
        } else {
            return containsElement((Top<E>) node.getRight(), value);
        }
    }

    private Top<E> addRecursive(Top<E> node, E value) {
        if (node == null) {
            return new Top<>(value);
        }

        int comparison = value.compareTo(node.getValue());

        if (comparison < 0) {
            node.setLeft(addRecursive((Top<E>) node.getLeft(), value));
        } else if (comparison > 0) {
            node.setRight(addRecursive((Top<E>) node.getRight(), value));
        }

        return node;
    }

    @Override
    public boolean remove(E e) {
        if (e == null) {
            return false;
        }

        // Check if element exists
        if (!containsElement(root, e)) {
            // Splay to the last accessed node
            if (root != null) {
                semiSplay(e);
            }
            return false;
        }

        // Find the parent of the node to be removed for splaying
        E splayTarget = findSplayTargetAfterRemove(e);

        // Remove the element
        root = removeRecursive(root, e);
        decrementSize();

        // Splay to the appropriate node after removal
        if (root != null && splayTarget != null) {
            semiSplay(splayTarget);
        }

        return true;
    }

    /**
     * Find the value to splay to after removing the given element.
     * This is typically the parent of the removed node.
     */
    private E findSplayTargetAfterRemove(E value) {
        Top<E> current = root;
        Top<E> parent = null;

        while (current != null) {
            int cmp = value.compareTo(current.getValue());
            if (cmp == 0) {
                // Found the node to remove
                // If it has two children, we'll splay to the inorder successor
                if (current.getLeft() != null && current.getRight() != null) {
                    Top<E> successor = findMin((Top<E>) current.getRight());
                    return successor.getValue();
                }
                // Otherwise, splay to parent
                return parent != null ? parent.getValue() : null;
            } else if (cmp < 0) {
                parent = current;
                current = (Top<E>) current.getLeft();
            } else {
                parent = current;
                current = (Top<E>) current.getRight();
            }
        }

        return parent != null ? parent.getValue() : null;
    }

    private Top<E> findMin(Top<E> node) {
        while (node.getLeft() != null) {
            node = (Top<E>) node.getLeft();
        }
        return node;
    }

    private Top<E> removeRecursive(Top<E> node, E value) {
        if (node == null) {
            return null;
        }

        int comparison = value.compareTo(node.getValue());

        if (comparison < 0) {
            node.setLeft(removeRecursive((Top<E>) node.getLeft(), value));
        } else if (comparison > 0) {
            node.setRight(removeRecursive((Top<E>) node.getRight(), value));
        } else {
            // Node gevonden - verwijder het

            // Geval 1: geen kinderen of 1 kind
            if (node.getLeft() == null) {
                return (Top<E>) node.getRight();
            } else if (node.getRight() == null) {
                return (Top<E>) node.getLeft();
            }

            // Geval 2: twee kinderen
            // Vind de kleinste waarde in de rechter subboom (inorder successor)
            Top<E> successor = findMin((Top<E>) node.getRight());

            // Maak een nieuwe node met de waarde van de successor
            Top<E> newNode = new Top<>(successor.getValue());
            newNode.setLeft((Top<E>) node.getLeft());
            newNode.setRight(removeRecursive((Top<E>) node.getRight(), successor.getValue()));

            return newNode;
        }

        return node;
    }
}