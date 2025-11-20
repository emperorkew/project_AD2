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
    }

    protected void setRoot(Top<E> root) {
        this.root = root;
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

        Top<E> parent = null;
        Top<E> current = root;
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

        Top<E> replacement = deleteNode(current);

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
    public Node<E> root() {
        return root;
    }

    @Override
    public List<E> values() {
        List<E> result = new ArrayList<>(size);
        // Iterative in-order traversal to save O(h) stack space
        Top<E> current = root;
        Top<E>[] stack = (Top<E>[]) new Top[32];
        int top = -1;

        while (current != null || top >= 0) {
            while (current != null) {
                if (++top >= stack.length) {
                    Top<E>[] newStack = (Top<E>[]) new Top[stack.length * 2];
                    System.arraycopy(stack, 0, newStack, 0, stack.length);
                    stack = newStack;
                }
                stack[top] = current;
                current = current.getLeft();
            }
            current = stack[top--];
            result.add(current.getValue());
            current = current.getRight();
        }
        return result;
    }
}