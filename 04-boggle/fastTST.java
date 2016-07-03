
/******************************************************************************
 *  Compilation:  javac fastTST.java
 *  Execution:    java fastTST < words.txt
 *  Dependencies:
 ******************************************************************************/

import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;

public class fastTST {
    private int  N;      // size
    private Node root;   // root of fastTST
    private Stack<Node> stack;

    private static class Node {
        private char    c;                 // character
        private boolean isString;
        private Node    left, mid, right;  // left, middle, and right subtries
    }

    /**
     * Initializes an empty string symbol table.
     */
    public fastTST() {
        stack = new Stack<>();
    }

    /**
     * Returns the number of strings in the set.
     * @return the number of strings in the set
     */
    public int size() {
        return N;
    }

    public boolean contains(String key) {
        Node x = get(root, key, 0);
        if (x == null) return false;
        return x.isString;
    }

    public boolean isWord() {
        return stack.peek().isString;
    }

    // return subtrie corresponding to given key
    private Node get(Node x, String key, int d) {
        if (key == null)       throw new NullPointerException();
        if (key.length() == 0) throw new IllegalArgumentException("key must have length >= 1");
        if (x == null) return null;
        char c = key.charAt(d);
        if      (c < x.c)              return get(x.left,  key, d);
        else if (c > x.c)              return get(x.right, key, d);
        else if (d < key.length() - 1) return get(x.mid,   key, d+1);
        else                           return x;
    }

    /**
     * Inserts the key-value pair into the symbol table, overwriting the old value
     * with the new value if the key is already in the symbol table.
     * If the value is <tt>null</tt>, this effectively deletes the key from the symbol table.
     * @param key the key
     * @param val the value
     * @throws NullPointerException if <tt>key</tt> is <tt>null</tt>
     */
    public void add(String key) {
        if (!contains(key)) N++;
        root = add(root, key, 0);
    }

    private Node add(Node x, String key, int d) {
        char c = key.charAt(d);
        if (x == null) {
            x = new Node();
            x.c = c;
            x.isString = false;
        }
        if      (c < x.c)              x.left  = add(x.left,  key, d);
        else if (c > x.c)              x.right = add(x.right, key, d);
        else if (d < key.length() - 1) x.mid   = add(x.mid,   key, d+1);
        else                           x.isString = true;
        return x;
    }

    public boolean putIntoStack(char c) {
        Node x;
        if (stack.isEmpty()) x = searchFor(root, c, true);
        else                 x = searchFor(stack.peek(), c, false);
        if (x == null) return false;
        else {
            stack.push(x);
            if (c == 'Q') {
                x = searchFor(stack.peek(), 'U', false);
                if (x == null) stack.pop();
                else           stack.push(x);
            }
            return true;
        }
    }

    private Node searchFor(Node x, char c, boolean searchInThisNode) {
        if (x == null) return null;
        Node next;
        if (searchInThisNode)    next = x;
        else if (x.mid == null) return null;
        else                    next = x.mid;
        if      (c < next.c) return searchFor(next.left,  c, true);
        else if (c > next.c) return searchFor(next.right, c, true);
        else                 return next;
    }

    public void cleanStack() {
        if (!stack.isEmpty()) {
            Node node = stack.pop();
            if (!stack.isEmpty() && node.c == 'U' && stack.peek().c == 'Q') stack.pop();
        }
    }

    public void printStack() {
        for (Node n : stack)
            StdOut.printf("%c ", n.c);
        StdOut.println();
    }
}
