package com.taberstorm.gmtools;

/**
 * Created by taber on 8/9/2015.
 */
public class DoubleCircleLinkedList {
    Node head;
    Node tail;
    Node currentNode;
    private class Node {
        public String name;
        public int sort;
        public Node next;
        public Node previous;
        Node(String name, int sort) {
            this.name = name;
            this.sort = sort;
        }

        public void setSort(int sort) {
            this.sort = sort;
        }
        public void setName() {
            this.name = name;
        }
        public String getName() {
            return name;
        }
        public int getSort() {
            return sort;
        }
    }
}
