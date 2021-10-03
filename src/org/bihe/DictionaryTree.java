package org.bihe;


import java.util.NoSuchElementException;

public class DictionaryTree {
    /**
     * Class Node for saving values in DictionaryTree
     */
    private static class Node {
        private char value;
        private Node mostLeft;
        private Node rightSibling;
        private Node parent;
        private boolean dataFlag;

        public Node(char value) {
            this.value = value;
            this.dataFlag = false;
        }

        public Node() {
            this.dataFlag = false;
        }

        @Override
        public String toString() {
            return "Value: " + ((this.value == Character.MIN_VALUE) ? "Null" : this.value) + " Parent: " + ((this.parent == null) ? "Null" : this.parent.value);
        }

        /**
         * @return true if Node is leaf and doesn't have right sibling
         */
        public boolean isAlone() {
            return this.mostLeft == null && this.rightSibling == null && this.parent.mostLeft == this;
        }
    }

    private Node root;

    //Constructor
    public DictionaryTree() {
        root = new Node();
    }

    //--------------------------------Insert Methods------------------------------
    /**
     * inserts word by using insertChar method
     *
     * @param word
     */
    public void insert(String word) {
        insert(word, root);
    }

    private void insert(String word, Node node) {
        if(word.contains("?")||word.contains("*")||word.contains("~")){
            throw new IllegalArgumentException();
        }
        for (int i = 0; i < word.length(); i++) {
            node = insertChar(word.charAt(i), node);
        }
        node.dataFlag = true;
    }

    /**
     * inserting characters one by one for inserting word
     *
     * @param ch
     * @param node
     * @return Node that use insert next char
     */
    private Node insertChar(char ch, Node node) {
        if (node.mostLeft == null) {
            Node newNode = new Node(ch);
            node.mostLeft = newNode;
            newNode.parent = node;
            return newNode;
        } else {
            node = node.mostLeft;
            while (node.rightSibling != null || node.value == ch) {
                if (node.value == ch) {
                    return node;
                }
                node = node.rightSibling;
            }
            Node newNode = new Node(ch);
            node.rightSibling = newNode;
            newNode.parent = node.parent;
            return newNode;
        }
    }
    //-------------------------------------------------------------------------------

    //----------------------------------Delete Methods-------------------------------
    /**
     * Deletes given word
     *
     * @param word
     */
    public void delete(String word) {
        delete(word, root);
    }

    private void delete(String word, Node node) {
        if (isEmpty(node) || !simpleSearch(word, node)) {
            throw new NoSuchElementException();
        }
        Node org = node;
        Node prevSib = null;
        for (int i = 0; i < word.length(); i++) {
            node = node.mostLeft;
            while (node != null) {
                if (node.value == word.charAt(i)) {
                    break;
                }
                prevSib = node;
                node = node.rightSibling;
            }
        }
        if (node.mostLeft != null) {
            node.dataFlag = false;
            return;
        }
        while (node.isAlone()) {
            node = node.parent;
            node.mostLeft = null;
            if (node == org) {
                return;
            }
        }
        if (node.parent.mostLeft == node) {
            node.parent.mostLeft = node.rightSibling;
        } else {
            prevSib.rightSibling = node.rightSibling;
        }
        node.rightSibling = null;
        node.parent = null;
    }
    //-----------------------------------------------------------------------

    //-----------------------------Search Methods----------------------------
    /**
     * Simple search for checking given node is in tree or not
     *
     * @param word
     * @return true if this tree contains given word
     */
    public boolean simpleSearch(String word) {
        return simpleSearch(word, root);
    }

    private boolean simpleSearch(String word, Node node) {
        for (int i = 0; i < word.length(); i++) {
            node = node.mostLeft;
            while (node != null) {
                if (node.value == word.charAt(i)) {
                    break;
                }
                node = node.rightSibling;
            }
            if (node == null) {
                return false;
            }
        }
        return node.dataFlag;
    }

    /**
     * Type 1 search -> input must contain one question mark. inserts results in the given DictionaryTree
     *
     * @param input
     * @param dt
     */
    private void searchType1(String input, DictionaryTree dt) {
        int index = input.indexOf("?");
        Node node = root;
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < index; i++) {
            node = getNodeForSearchType1(input, node, s, i);
            if (node == null) {
                return;
            }
        }
        node = node.mostLeft;
        while (node != null) {
            StringBuilder s2 = new StringBuilder(s);
            s2.append(node.value);
            Node node1 = node;
            for (int i = index + 1; i < input.length(); i++) {
                if (node1 != null) {
                    node1 = getNodeForSearchType1(input, node1, s2, i);
                }
            }
            if (node1 != null && node1.dataFlag) {
                dt.insert(s2.toString());
            }
            node = node.rightSibling;
        }
    }

    /**
     * Type 1 search without input DictionaryTree
     *
     * @param input
     * @return DictionaryTree that has results of search
     */
    public DictionaryTree searchType1(String input) {
        DictionaryTree dt = new DictionaryTree();
        searchType1(input, dt);
        return dt;
    }

    /**
     * Checking input string and given node for appending char to given StringBuilder if equals with Node value
     *
     * @param input
     * @param node
     * @param s
     * @param i
     * @return Node for next step
     */
    private Node getNodeForSearchType1(String input, Node node, StringBuilder s, int i) {
        char inChar = input.charAt(i);
        node = node.mostLeft;
        while (node != null) {
            if (node.value == inChar) {
                s.append(inChar);
                break;
            }
            node = node.rightSibling;
        }
        return node;
    }

    /**
     * Type 2 Search -> input must contain one "~" at the beginning. inserts results in the given DictionaryTree
     *
     * @param input
     * @return results as a DictionaryTree
     */
    public DictionaryTree searchType2(String input) {
        DictionaryTree dt = new DictionaryTree();
        String word = input.substring(1);
        if (simpleSearch(word)) {
            dt.insert(word);
        }
        searchType1("?" + word, dt);
        searchType1(word + "?", dt);
        for (int i = 0; i < word.length(); i++) {
            searchType1(word.replace(word.charAt(i), '?'), dt);
        }

        if(word.length()>1){
            for (int i = 0; i < word.length(); i++) {
                String part1 = word.substring(0,i);
                String part2 = word.substring(i+1);
                String conc = part1.concat(part2);
                if (simpleSearch(conc)){
                    dt.insert(conc);
                }
            }
        }

        return dt;
    }

    /**
     * Type 3 Search -> input must contain "*" at the end and return results as a DictionaryTree
     *
     * @param input
     * @return results as a DictionaryTree
     */
    public DictionaryTree searchType3(String input) {
        DictionaryTree dt = new DictionaryTree();
        String subWord = input.substring(0, input.indexOf("*"));
        Node node = root;
        for (int i = 0; i < subWord.length(); i++) {
            node = node.mostLeft;
            while (node != null) {
                if (node.value == subWord.charAt(i)) {
                    break;
                }
                node = node.rightSibling;
            }
            if (node == null) {
                return dt;
            }
        }
        DictionaryTree temp = new DictionaryTree();
        while (!isEmpty(node)) {
            temp.insert(getFirst(node));
        }
        while (!temp.isEmpty()) {
            String s = temp.getFirst();
            insert(subWord + s);
            dt.insert(subWord + s);
        }
        return dt;
    }

    /**
     * Type 4 search -> input must have a "*" at the beginning. inserts results in the DictionaryTree
     *
     * @param input
     * @return DictionaryTree as a result
     */
    public DictionaryTree searchType4(String input) {
        DictionaryTree dt = new DictionaryTree();
        int numOfSubWord = input.length() - 1;
        String subWord = input.substring(1);
        DictionaryTree temp = new DictionaryTree();
        while (!isEmpty()) {
            temp.insert(getFirst());
        }
        while (!temp.isEmpty()) {
            String s = temp.getFirst();
            insert(s);
            if (s.length() >= numOfSubWord && s.substring(s.length() - numOfSubWord).equals(subWord)) {
                dt.insert(s);
            }
        }
        return dt;
    }

    public DictionaryTree searchType5(String input) {
        DictionaryTree dt;
        int index = input.indexOf("*");
        String subWord1 = input.substring(0, index + 1);
        String subWord2 = input.substring(index);
        DictionaryTree temp = searchType3(subWord1);
        dt = temp.searchType4(subWord2);
        return dt;
    }

    //---------------------------------------------------------------------------

    //-------------------------------GetFirst Methods----------------------------
    /**
     * Gets first element of DictionaryTree and delete it
     *
     * @return first word
     */
    public String getFirst() {
        return getFirst(root);
    }

    private String getFirst(Node node) {
        Node org = node;
        StringBuilder s = new StringBuilder();
        while (node.mostLeft != null) {
            node = node.mostLeft;
            s.append(node.value);
            if (node.dataFlag) {
                break;
            }
        }
        String first = s.toString();
        delete(first, org);
        return first;
    }

    public String getFirstWithOutDelete() {
        Node node = root;
        StringBuilder s = new StringBuilder();
        while (node.mostLeft != null) {
            node = node.mostLeft;
            s.append(node.value);
            if (node.dataFlag) {
                break;
            }
        }
        return s.toString();
    }
    //------------------------------------------------------------------------

    //------------------------------isEmpty methods---------------------------
    public boolean isEmpty() {
        return isEmpty(root);
    }

    private boolean isEmpty(Node node) {
        return node.mostLeft == null;
    }
    //-------------------------------------------------------------------------

    //---------------------Methods For printing and Sorting--------------------
    /**
     * Print all words till DictionaryTree Not empty
     */
    public void printWordsWithDelete() {
        while (!isEmpty()) {
            String word = getFirst();
            System.out.println(word);
        }
    }

    /**
     * Sorting DictionaryTree in another DictionaryTree
     * @return Sorted DictionaryTree
     */
    public DictionaryTree sort() {
        DictionaryTree sorted = new DictionaryTree();
        String min;
        while (!isEmpty()){
            min = min();
            sorted.insert(min);
            delete(min);
        }
        return sorted;
    }

    /**
     * @return minimum word in the tree
     */
    public String min(){
        DictionaryTree dTemp = new DictionaryTree();
        String s = getFirst();
        dTemp.insert(s);
        String tmp = s;
        boolean notEmpty;
        while((notEmpty = !isEmpty()) || (tmp.compareToIgnoreCase(s) > 0)){
            if(tmp.compareToIgnoreCase(s) > 0){
                tmp = s;
            }
            if(notEmpty){
                s = getFirst();
                dTemp.insert(s);
            }

        }
        while(!dTemp.isEmpty()){
            insert(dTemp.getFirst());
        }
        return tmp;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        DictionaryTree tmp = new DictionaryTree();
        while (!isEmpty()){
            String s = getFirst();
            sb.append(s+"\n");
            tmp.insert(s);
        }
        while (!tmp.isEmpty()){
            insert(tmp.getFirst());
        }
        return sb.toString();
    }

    //-------------------------------------------------------------
}
