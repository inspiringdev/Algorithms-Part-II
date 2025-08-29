// File: TrieSET.java
import java.util.ArrayList;
import java.util.List;

/**
 * TrieSET: a basic trie set supporting add, contains, and hasPrefix.
 * - Only supports uppercase A-Z strings.
 */
public class TrieSET {
    private static final int R = 26; // A-Z
    private Node root;

    private static class Node {
        Node[] next = new Node[R];
        boolean isWord = false;
    }

    // Add a key (word)
    public void add(String key) {
        if (key == null) throw new IllegalArgumentException("null key");
        root = add(root, key, 0);
    }

    private Node add(Node x, String key, int d) {
        if (x == null) x = new Node();
        if (d == key.length()) {
            x.isWord = true;
            return x;
        }
        char ch = key.charAt(d);
        // Expect uppercase A-Z. If 'Q' could be followed by U in dictionary, that's fine.
        int idx = ch - 'A';
        if (idx < 0 || idx >= R) {
            // ignore non A-Z letters (defensive)
            return x;
        }
        x.next[idx] = add(x.next[idx], key, d + 1);
        return x;
    }

    // check if exact word exists
    public boolean contains(String key) {
        if (key == null) throw new IllegalArgumentException("null key");
        Node x = get(root, key, 0);
        return x != null && x.isWord;
    }

    private Node get(Node x, String key, int d) {
        if (x == null) return null;
        if (d == key.length()) return x;
        char ch = key.charAt(d);
        int idx = ch - 'A';
        if (idx < 0 || idx >= R) return null;
        return get(x.next[idx], key, d + 1);
    }

    // check whether there exists at least one dictionary word with given prefix
    public boolean hasPrefix(String prefix) {
        if (prefix == null) throw new IllegalArgumentException("null prefix");
        Node x = get(root, prefix, 0);
        return x != null && hasAnyDescendant(x);
    }

    private boolean hasAnyDescendant(Node x) {
        if (x == null) return false;
        if (x.isWord) return true;
        for (int i = 0; i < R; i++) {
            if (x.next[i] != null && hasAnyDescendant(x.next[i])) return true;
        }
        return false;
    }

    // optional: collect all keys (for debugging)
    public Iterable<String> keys() {
        List<String> list = new ArrayList<>();
        collect(root, new StringBuilder(), list);
        return list;
    }

    private void collect(Node x, StringBuilder prefix, List<String> list) {
        if (x == null) return;
        if (x.isWord) list.add(prefix.toString());
        for (int c = 0; c < R; c++) {
            if (x.next[c] != null) {
                prefix.append((char) ('A' + c));
                collect(x.next[c], prefix, list);
                prefix.deleteCharAt(prefix.length() - 1);
            }
        }
    }
}
