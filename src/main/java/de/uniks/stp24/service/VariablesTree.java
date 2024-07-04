package de.uniks.stp24.service;

import java.util.ArrayList;
import java.util.List;

/*
Generic class made for tree to handle variables
 */

public class VariablesTree<T> {
    private Node<T> root;

    public VariablesTree(String key){
        this.root = new Node<>(key);
    }

    public Node<T> getRoot(){
        return root;
    }

    /*
    Inner class for Nodes
     */
    public static class Node<T>{
        private String key;
        private T value;
        private List<Node<T>> children;

        /*
        Constructor for children
         */
        public Node(String key){
            this.key = key;
            this.children = new ArrayList<>();
        }

        /*
        Constructor for leaf nodes
         */
        public Node(T value){
            this.value = value;
        }

        public String getKey(){
            return key;
        }

        public T getValue(){
            return value;
        }

        public void setValue(T value){
            this.value = value;
        }

        public List<Node<T>> getChildren(){
            return children;
        }

        public void addChild(Node<T> child){
            children.add(child);
        }
    }

}
