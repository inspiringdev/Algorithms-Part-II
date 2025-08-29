import edu.princeton.cs.algs4.Digraph;
import java.util.Queue;
import java.util.LinkedList;
import java.util.Arrays;
import java.util.Iterator;

public class SAP {
    private final Digraph G;

    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        if (G == null) throw new IllegalArgumentException("Argument is null");
        // defensive copy to ensure immutability
        this.G = new Digraph(G);
    }

    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        validateVertex(v);
        validateVertex(w);
        int[] res = bfsTwoSources(singletonIterable(v), singletonIterable(w));
        return res[0];
    }

    // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    public int ancestor(int v, int w) {
        validateVertex(v);
        validateVertex(w);
        int[] res = bfsTwoSources(singletonIterable(v), singletonIterable(w));
        return res[1];
    }

    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        if (v == null || w == null) throw new IllegalArgumentException("Argument is null");
        int[] res = bfsTwoSources(v, w);
        return res[0];
    }

    // a common ancestor that participates in shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        if (v == null || w == null) throw new IllegalArgumentException("Argument is null");
        int[] res = bfsTwoSources(v, w);
        return res[1];
    }

    // do unit testing of this class
    public static void main(String[] args) {
        edu.princeton.cs.algs4.In in = new edu.princeton.cs.algs4.In(args[0]);
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);
        while (!edu.princeton.cs.algs4.StdIn.isEmpty()) {
            int v = edu.princeton.cs.algs4.StdIn.readInt();
            int w = edu.princeton.cs.algs4.StdIn.readInt();
            int length = sap.length(v, w);
            int ancestor = sap.ancestor(v, w);
            edu.princeton.cs.algs4.StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        }
    }

    // ---- Private helpers ----

    // BFS from two sets of sources; returns int[]{length, ancestor}
    // length = -1 and ancestor = -1 if no common ancestor found
    private int[] bfsTwoSources(Iterable<Integer> vSources, Iterable<Integer> wSources) {
        if (vSources == null || wSources == null) throw new IllegalArgumentException("Argument is null");

        // validate contents and prepare source arrays
        boolean[] vMarked = new boolean[G.V()];
        boolean[] wMarked = new boolean[G.V()];
        int[] vDist = new int[G.V()];
        int[] wDist = new int[G.V()];
        Arrays.fill(vDist, -1);
        Arrays.fill(wDist, -1);

        Queue<Integer> qV = new LinkedList<>();
        Queue<Integer> qW = new LinkedList<>();

        for (Integer s : vSources) {
            if (s == null) throw new IllegalArgumentException("Iterable contains null");
            validateVertex(s);
            if (!vMarked[s]) {
                vMarked[s] = true;
                vDist[s] = 0;
                qV.add(s);
            }
        }
        for (Integer s : wSources) {
            if (s == null) throw new IllegalArgumentException("Iterable contains null");
            validateVertex(s);
            if (!wMarked[s]) {
                wMarked[s] = true;
                wDist[s] = 0;
                qW.add(s);
            }
        }

        // If any shared source, immediately answer
        int bestLen = Integer.MAX_VALUE;
        int bestAncestor = -1;
        for (int i = 0; i < G.V(); i++) {
            if (vMarked[i] && wMarked[i]) {
                int total = vDist[i] + wDist[i];
                if (total < bestLen) {
                    bestLen = total;
                    bestAncestor = i;
                }
            }
        }

        // We'll perform simultaneous BFS expansions level by level for both source sets.
        // But simpler and still O(V+E): fully BFS from vSources, then BFS from wSources while checking vDist hits.
        // BFS from vSources already done partially; we must expand qV fully first.
        while (!qV.isEmpty()) {
            int x = qV.poll();
            for (int y : G.adj(x)) {
                if (!vMarked[y]) {
                    vMarked[y] = true;
                    vDist[y] = vDist[x] + 1;
                    qV.add(y);
                }
            }
        }

        // BFS from wSources and check against vDist for ancestors
        while (!qW.isEmpty()) {
            int x = qW.poll();
            // Check if this vertex is common
            if (vMarked[x]) {
                int total = vDist[x] + wDist[x];
                if (total < bestLen) {
                    bestLen = total;
                    bestAncestor = x;
                }
            }
            // If current wDist already >= bestLen, we can skip expanding neighbors (prune)
            if (wDist[x] >= bestLen) continue;
            for (int y : G.adj(x)) {
                if (!wMarked[y]) {
                    wMarked[y] = true;
                    wDist[y] = wDist[x] + 1;
                    // Early check
                    if (vMarked[y]) {
                        int total = vDist[y] + wDist[y];
                        if (total < bestLen) {
                            bestLen = total;
                            bestAncestor = y;
                        }
                    }
                    // Only enqueue if promising (wDist < bestLen)
                    if (wDist[y] < bestLen) qW.add(y);
                }
            }
        }

        if (bestAncestor == -1) return new int[]{-1, -1};
        return new int[]{bestLen, bestAncestor};
    }

    // small helper to create iterable over single int
    private Iterable<Integer> singletonIterable(final int x) {
        return new Iterable<Integer>() {
            public Iterator<Integer> iterator() {
                return new Iterator<Integer>() {
                    private boolean used = false;
                    public boolean hasNext() { return !used; }
                    public Integer next() { used = true; return x; }
                    public void remove() { throw new UnsupportedOperationException(); }
                };
            }
        };
    }

    private void validateVertex(int v) {
        if (v < 0 || v >= G.V()) throw new IllegalArgumentException("vertex out of range: " + v);
    }
}
