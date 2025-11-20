package oplossing;

import opgave.Node;
import opgave.SearchTree;

import java.util.ArrayList;
import java.util.List;

public class SearchTreeImplemented<E extends Comparable<E>> implements SearchTree<E> {

    private int size;
    protected Top<E> root;

    public SearchTreeImplemented() {
        this.size = 0;
        this.root = null;
    }

    protected void setRoot(Top<E> root) {
        this.root = root;
    }

    protected void incrementSize() {
        this.size++;
    }

    protected void decrementSize() {
        this.size--;
    }


    @Override
    public int size() {
        return this.size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean search(E o) {
        if (o == null) {
            return false;
        }

        // Iterative search - O(1) space instead of O(h) recursive stack
        Top<E> current = root;
        while (current != null) {
            int cmp = o.compareTo(current.getValue());
            if (cmp == 0) {
                return true;
            }
            current = (cmp < 0) ? (Top<E>) current.getLeft() : (Top<E>) current.getRight();
        }
        return false;
    }

    @Override
    public boolean add(E o) {
        if (o == null) {
            return false;
        }

        if (root == null) {
            root = new Top<>(o);
            size++;
            return true;
        }

        // Single traversal - cache child references to avoid double getter calls
        Top<E> current = root;
        while (true) {
            int cmp = o.compareTo(current.getValue());

            if (cmp == 0) {
                return false; // Element exists
            }

            if (cmp < 0) {
                Top<E> left = (Top<E>) current.getLeft();
                if (left == null) {
                    current.setLeft(new Top<>(o));
                    size++;
                    return true;
                }
                current = left;
            } else {
                Top<E> right = (Top<E>) current.getRight();
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
        if (e == null || root == null) {
            return false;
        }

        // Zoek het te verwijderen element en zijn parent in één traversal
        Top<E> parent = null;
        Top<E> current = root;
        boolean isLeftChild = false;

        while (current != null) {
            int comparison = e.compareTo(current.getValue());

            if (comparison == 0) {
                break; // Gevonden
            } else if (comparison < 0) {
                parent = current;
                current = (Top<E>) current.getLeft();
                isLeftChild = true;
            } else {
                parent = current;
                current = (Top<E>) current.getRight();
                isLeftChild = false;
            }
        }

        if (current == null) {
            return false; // Element niet gevonden
        }

        // Verwijder de node
        Top<E> replacement = deleteNode(current);

        // Update de parent link
        if (parent == null) {
            root = replacement;
        } else if (isLeftChild) {
            parent.setLeft(replacement);
        } else {
            parent.setRight(replacement);
        }

        size--;
        return true;
    }

    /**
     * Verwijdert een node en retourneert de vervangende node.
     */
    private Top<E> deleteNode(Top<E> node) {
        // Geval 1: geen kinderen of 1 kind
        if (node.getLeft() == null) {
            return (Top<E>) node.getRight();
        } else if (node.getRight() == null) {
            return (Top<E>) node.getLeft();
        }

        // Geval 2: twee kinderen - vind en verwijder inorder successor
        Top<E> successorParent = node;
        Top<E> successor = (Top<E>) node.getRight();

        while (successor.getLeft() != null) {
            successorParent = successor;
            successor = (Top<E>) successor.getLeft();
        }

        // Verwijder successor uit zijn huidige positie
        if (successorParent == node) {
            successorParent.setRight((Top<E>) successor.getRight());
        } else {
            successorParent.setLeft((Top<E>) successor.getRight());
        }

        // Vervang node door successor
        successor.setLeft((Top<E>) node.getLeft());
        successor.setRight((Top<E>) node.getRight());

        return successor;
    }

    @Override
    public Node<E> root() {
        return this.root;
    }

    @Override
    public List<E> values() {
        // Pre-allocate exact size to avoid dynamic resizing
        List<E> result = new ArrayList<>(size);
        inOrderTraversal(root, result);
        return result;
    }

    private void inOrderTraversal(Top<E> node, List<E> result) {
        if (node == null) {
            return;
        }

        inOrderTraversal((Top<E>) node.getLeft(), result);
        result.add(node.getValue());
        inOrderTraversal((Top<E>) node.getRight(), result);
    }
}
