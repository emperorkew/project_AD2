package oplossing;

import opgave.PriorityNode;
import opgave.PrioritySearchTree;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Treap<E extends Comparable<E>> implements PrioritySearchTree<E> {

    private int size;
    private PriorityTop<E> root;
    private final Random random;

    public Treap() {
        this.size = 0;
        this.root = null;
        this.random = new Random();
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

        PriorityTop<E> current = root;
        while (current != null) {
            int cmp = o.compareTo(current.getValue());
            if (cmp == 0) {
                return true;
            }
            if (cmp < 0) {
                current = (PriorityTop<E>) current.getLeft();
            } else {
                current = (PriorityTop<E>) current.getRight();
            }
        }
        return false;
    }

    @Override
    public boolean add(E o) {
        if (o == null) {
            return false;
        }

        long priority = random.nextLong();

        if (root == null) {
            root = new PriorityTop<>(o, priority);
            size++;
            return true;
        }

        // Zoek de juiste positie en houd het pad bij voor rotaties
        PriorityTop<E> current = root;
        PriorityTop<E> parent = null;
        List<PriorityTop<E>> path = new ArrayList<>();
        List<Boolean> directions = new ArrayList<>(); // true = left, false = right

        while (current != null) {
            int cmp = o.compareTo(current.getValue());

            if (cmp == 0) {
                return false; // Element bestaat al
            }

            path.add(current);
            parent = current;

            if (cmp < 0) {
                directions.add(true);
                current = (PriorityTop<E>) current.getLeft();
            } else {
                directions.add(false);
                current = (PriorityTop<E>) current.getRight();
            }
        }

        // Voeg de nieuwe node toe
        PriorityTop<E> newNode = new PriorityTop<>(o, priority);
        if (directions.get(directions.size() - 1)) {
            parent.setLeft(newNode);
        } else {
            parent.setRight(newNode);
        }
        size++;

        // Herstel heap-eigenschap door rotaties
        bubbleUp(newNode, path, directions);

        return true;
    }

    /**
     * Roteert de nieuwe node omhoog tot de heap-eigenschap is hersteld.
     */
    private void bubbleUp(PriorityTop<E> node, List<PriorityTop<E>> path, List<Boolean> directions) {
        for (int i = path.size() - 1; i >= 0; i--) {
            PriorityTop<E> parent = path.get(i);

            // Check of heap-eigenschap al geldt (max-heap: parent heeft hogere prioriteit)
            if (parent.getPriority() >= node.getPriority()) {
                break;
            }

            // Roteer node omhoog
            PriorityTop<E> grandparent = (i > 0) ? path.get(i - 1) : null;
            boolean isLeftChild = directions.get(i);

            if (isLeftChild) {
                rotateRight(parent, grandparent, i > 0 ? directions.get(i - 1) : false);
            } else {
                rotateLeft(parent, grandparent, i > 0 ? directions.get(i - 1) : false);
            }

            // Update path voor volgende iteratie
            if (grandparent != null) {
                if (directions.get(i - 1)) {
                    node = (PriorityTop<E>) grandparent.getLeft();
                } else {
                    node = (PriorityTop<E>) grandparent.getRight();
                }
            } else {
                node = root;
            }
        }
    }

    /**
     * Linker rotatie: rechterkind wordt parent.
     */
    private void rotateLeft(PriorityTop<E> node, PriorityTop<E> parent, boolean isLeftChild) {
        PriorityTop<E> rightChild = (PriorityTop<E>) node.getRight();
        node.setRight((PriorityTop<E>) rightChild.getLeft());
        rightChild.setLeft(node);

        if (parent == null) {
            root = rightChild;
        } else if (isLeftChild) {
            parent.setLeft(rightChild);
        } else {
            parent.setRight(rightChild);
        }
    }

    /**
     * Rechter rotatie: linkerkind wordt parent.
     */
    private void rotateRight(PriorityTop<E> node, PriorityTop<E> parent, boolean isLeftChild) {
        PriorityTop<E> leftChild = (PriorityTop<E>) node.getLeft();
        node.setLeft((PriorityTop<E>) leftChild.getRight());
        leftChild.setRight(node);

        if (parent == null) {
            root = leftChild;
        } else if (isLeftChild) {
            parent.setLeft(leftChild);
        } else {
            parent.setRight(leftChild);
        }
    }

    @Override
    public boolean remove(E e) {
        if (e == null || root == null) {
            return false;
        }

        // Zoek het te verwijderen element en zijn parent
        PriorityTop<E> parent = null;
        PriorityTop<E> current = root;
        boolean isLeftChild = false;

        while (current != null) {
            int comparison = e.compareTo(current.getValue());

            if (comparison == 0) {
                break;
            } else if (comparison < 0) {
                parent = current;
                current = (PriorityTop<E>) current.getLeft();
                isLeftChild = true;
            } else {
                parent = current;
                current = (PriorityTop<E>) current.getRight();
                isLeftChild = false;
            }
        }

        if (current == null) {
            return false; // Element niet gevonden
        }

        // Roteer de node naar beneden tot het een blad is
        while (current.getLeft() != null || current.getRight() != null) {
            PriorityTop<E> leftChild = (PriorityTop<E>) current.getLeft();
            PriorityTop<E> rightChild = (PriorityTop<E>) current.getRight();

            // Bepaal welk kind omhoog moet (hoogste prioriteit)
            if (leftChild == null) {
                // Alleen rechterkind: roteer links
                rotateLeft(current, parent, isLeftChild);
                parent = rightChild;
                isLeftChild = true;
            } else if (rightChild == null) {
                // Alleen linkerkind: roteer rechts
                rotateRight(current, parent, isLeftChild);
                parent = leftChild;
                isLeftChild = false;
            } else if (leftChild.getPriority() > rightChild.getPriority()) {
                // Linkerkind heeft hogere prioriteit: roteer rechts
                rotateRight(current, parent, isLeftChild);
                parent = leftChild;
                isLeftChild = false;
            } else {
                // Rechterkind heeft hogere prioriteit: roteer links
                rotateLeft(current, parent, isLeftChild);
                parent = rightChild;
                isLeftChild = true;
            }
        }

        // Verwijder de node (nu een blad)
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
        return this.root;
    }

    @Override
    public List<E> values() {
        List<E> result = new ArrayList<>(size);
        inOrderTraversal(root, result);
        return result;
    }

    private void inOrderTraversal(PriorityTop<E> node, List<E> result) {
        if (node == null) {
            return;
        }

        inOrderTraversal((PriorityTop<E>) node.getLeft(), result);
        result.add(node.getValue());
        inOrderTraversal((PriorityTop<E>) node.getRight(), result);
    }
}