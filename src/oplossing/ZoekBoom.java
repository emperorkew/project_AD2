package oplossing;

import opgave.Node;
import opgave.SearchTree;

import java.util.ArrayList;
import java.util.List;

public class ZoekBoom<E extends Comparable<E>> implements SearchTree<E> {

    private int size;
    private Top<E> root;

    public ZoekBoom() {
        this.size = 0;
        this.root = null;
    }


    @Override
    public int size() {
        return this.size;
    }

    @Override
    public boolean search(E o) {
        if (o == null) {
            return false;
        }
        return searchRecursive(root, o);
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

        if (search(o)) {
            return false; // Element bestaat al
        }

        root = addRecursive(root, o);
        size++;
        return true;
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
        if (e == null || !search(e)) {
            return false;
        }

        root = removeRecursive(root, e);
        size--;
        return true;
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

    private Top<E> findMin(Top<E> node) {
        while (node.getLeft() != null) {
            node = (Top<E>) node.getLeft();
        }
        return node;
    }

    @Override
    public Node<E> root() {
        return this.root;
    }

    @Override
    public List<E> values() {
        List<E> result = new ArrayList<>();
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
