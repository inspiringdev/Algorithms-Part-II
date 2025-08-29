import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

/**
 * Burrows-Wheeler transform and inverse transform.
 *
 * transform:
 *   read entire input as a String s (sequence of chars),
 *   build CircularSuffixArray on s,
 *   write int 'first' (32 bits), then write last column characters in order (each 8 bits)
 *
 * inverseTransform:
 *   read int first (32 bits), read n chars (each 8 bits) to build t[],
 *   construct next[] using key-indexed counting,
 *   follow next[] starting at first to output original string.
 */
public class BurrowsWheeler {

    // apply Burrows-Wheeler transform
    public static void transform() {
        // read all input as bytes -> build char array
        StringBuilder sb = new StringBuilder();
        while (!BinaryStdIn.isEmpty()) {
            char c = BinaryStdIn.readChar(); // reads 8-bit byte as char
            sb.append(c);
        }
        String s = sb.toString();
        int n = s.length();
        if (n == 0) {
            // write first = 0 and nothing else
            BinaryStdOut.write(0, 32);
            BinaryStdOut.flush();
            return;
        }

        CircularSuffixArray csa = new CircularSuffixArray(s);
        // find first: row where original suffix at 0 appears
        int first = -1;
        for (int i = 0; i < n; i++) {
            if (csa.index(i) == 0) { first = i; break; }
        }
        BinaryStdOut.write(first, 32);

        // write last column: for each sorted suffix at position i, the preceding character is s[(index[i]+n-1)%n]
        for (int i = 0; i < n; i++) {
            int idx = csa.index(i);
            char last = s.charAt((idx + n - 1) % n);
            BinaryStdOut.write(last);
        }
        BinaryStdOut.flush();
    }

    // apply Burrows-Wheeler inverse transform
    public static void inverseTransform() {
        // read first (32 bits)
        if (BinaryStdIn.isEmpty()) return; // nothing
        int first = BinaryStdIn.readInt(32);

        // read remaining bytes into t[]
        java.util.List<Character> list = new java.util.ArrayList<>();
        while (!BinaryStdIn.isEmpty()) {
            char c = BinaryStdIn.readChar();
            list.add(c);
        }
        int n = list.size();
        if (n == 0) {
            BinaryStdOut.flush();
            return;
        }
        char[] t = new char[n];
        for (int i = 0; i < n; i++) t[i] = list.get(i);

        // key-indexed counting to compute starting positions for each char (alphabet size R = 256)
        int R = 256;
        int[] count = new int[R + 1];
        for (int i = 0; i < n; i++) count[((int) t[i]) + 1]++;

        for (int r = 0; r < R; r++) count[r + 1] += count[r];

        // next array
        int[] next = new int[n];
        // for stable mapping: for each i from 0..n-1, place i at position count[t[i]] and increment count[t[i]]
        for (int i = 0; i < n; i++) {
            int c = (int) t[i];
            int pos = count[c]++;
            next[pos] = i;
        }

        // now reconstruct by starting at next[first]
        int idx = first;
        for (int i = 0; i < n; i++) {
            char c = t[idx];
            BinaryStdOut.write(c);
            idx = next[idx];
        }
        BinaryStdOut.flush();
    }

    // main: args[0] "-" => transform; "+" => inverseTransform
    public static void main(String[] args) {
        if (args.length == 0) {
            throw new IllegalArgumentException("usage: BurrowsWheeler - (transform) or + (inverse)");
        }
        if (args[0].equals("-")) transform();
        else if (args[0].equals("+")) inverseTransform();
        else throw new IllegalArgumentException("first arg must be '-' or '+'");
    }
}
