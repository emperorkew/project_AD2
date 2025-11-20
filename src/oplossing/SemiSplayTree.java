package oplossing;

import java.util.ArrayList;
import java.util.List;

public class SemiSplayTree<E extends Comparable<E>> extends SearchTreeImplemented<E> {

    public SemiSplayTree() {
        super();
    }

    private void semiSplayPath(List<Top<E>> path) {
        if (path.size() < 3) return;

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
        if (o == null || root == null) return false;

        List<Top<E>> path = new ArrayList<>(32);
        Top<E> current = root;

        while (current != null) {
            path.add(current);
            int cmp = o.compareTo(current.getValue());

            if (cmp == 0) {
                semiSplayPath(path);
                return true;
            }
            current = (cmp < 0) ? current.getLeft() : current.getRight();
        }

        semiSplayPath(path);
        return false;
    }

    @Override
    public boolean add(E o) {
        if (o == null) return false;

        if (root == null) {
            root = new Top<>(o);
            incrementSize();
            return true;
        }

        List<Top<E>> path = new ArrayList<>(32);
        Top<E> current = root;

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