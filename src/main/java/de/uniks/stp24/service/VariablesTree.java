package de.uniks.stp24.service;

import java.util.ArrayList;
import java.util.List;
import de.uniks.stp24.dto.ExplainedVariableDTO;

/*
Generic class made for tree to handle variables
 */

public class VariablesTree<T> {
    public Node<T> root;

    public VariablesTree(String key) {
        this.root = new Node<>(key);
    }

    public Node<T> getRoot() {
        return root;
    }

    /*
    Inner class for Nodes
     */
    public static class Node<T> {
        private String key;
        private T value;
        private List<Node<T>> children;

        /*
        Constructor for children
         */
        public Node(String key) {
            this.key = key;
            this.children = new ArrayList<>();
        }

        /*
        Constructor for leaf nodes
         */
        public Node(String key, T value) {
            this.key = key;
            this.value = value;
            this.children = new ArrayList<>();
        }

        public String getKey() {
            return key;
        }

        public T getValue() {
            return value;
        }

        public void setValue(T value) {
            this.value = value;
        }

        public List<Node<T>> getChildren() {
            return children;
        }

        public void addChild(Node<T> child) {
            children.add(child);
        }
    }

    /*
   Method to print all paths from root
    */
    public void printPaths() {
        List<String> currentPath = new ArrayList<>();
        traverseAndPrint(root, currentPath);
    }

    private void traverseAndPrint(Node<T> node, List<String> currentPath) {
        if (node == null) {
            return;
        }

        currentPath.add(node.getKey());

        if (node.getChildren().isEmpty()) {
            ExplainedVariableDTO tmp = (ExplainedVariableDTO) node.value;
            System.out.println(String.join(" -> ", currentPath) + " => " + tmp.finalValue());
        } else {
            for (Node<T> child : node.getChildren()) {
                traverseAndPrint(child, new ArrayList<>(currentPath));
            }
        }
    }

}
