import java.util.Arrays;
import java.util.Comparator;

/**
 * CircularSuffixArray: builds sorted array of circular suffixes of a String s.
 * Constructor is O(n log n) using Java sort with a comparator that compares cyclically.
 */
public class CircularSuffixArray {
    private final int n;
    private final Integer[] index; // index[i] = starting index in original string of i-th sorted suffix
    private final char[] sChars;

    // circular suffix array of s
    public CircularSuffixArray(String s) {
        if (s == null) throw new IllegalArgumentException("null string");
        n = s.length();
        sChars = s.toCharArray();
        index = new Integer[n];
        for (int i = 0; i < n; i++) index[i] = i;

        // comparator: compare suffixes starting at a and b by comparing up to n characters cyclically
        Comparator<Integer> cmp = new Comparator<Integer>() {
            @Override
            public int compare(Integer a, Integer b) {
                if (a.equals(b)) return 0;
                int i = a;
                int j = b;
                for (int k = 0; k < n; k++) {
                    char ca = sChars[i];
                    char cb = sChars[j];
                    if (ca < cb) return -1;
                    if (ca > cb) return 1;
                    i++; if (i == n) i = 0;
                    j++; if (j == n) j = 0;
                }
                return 0; // equal (should not happen unless all chars same)
            }
        };

        Arrays.sort(index, cmp);
    }

    // length of s
    public int length() {
        return n;
    }

    // returns index of ith sorted suffix
    public int index(int i) {
        if (i < 0 || i >= n) throw new IllegalArgumentException("index out of range");
        return index[i];
    }

    // unit testing
    public static void main(String[] args) {
        String s = (args.length == 0) ? "ABRACADABRA!" : args[0];
        CircularSuffixArray csa = new CircularSuffixArray(s);
        System.out.println("length = " + csa.length());
        for (int i = 0; i < csa.length(); i++) {
            System.out.printf("%2d: %2d\n", i, csa.index(i));
        }
    }
}
