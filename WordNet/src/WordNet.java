import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.DirectedCycle;

import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public class WordNet {
    private final Map<String, Set<Integer>> nounToIds; // noun -> set of synset ids
    private final List<String> idToSynset;            // id -> synset (full string)
    private final Digraph G;
    private final SAP sap; // helper for shortest ancestral path

    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        if (synsets == null || hypernyms == null) throw new IllegalArgumentException("Argument is null");
        nounToIds = new HashMap<>();
        idToSynset = new ArrayList<>();

        // Read synsets
        In inSyn = new In(synsets);
        int maxId = -1;
        while (!inSyn.isEmpty()) {
            String line = inSyn.readLine();
            if (line == null || line.length() == 0) continue;
            String[] parts = line.split(",", 3);
            if (parts.length < 2) continue; // robustness; spec says format is correct
            int id = Integer.parseInt(parts[0]);
            if (id > maxId) {
                // ensure list size later
                maxId = id;
            }
            String synset = parts[1];
            // ensure idToSynset size
            while (idToSynset.size() <= id) idToSynset.add(null);
            idToSynset.set(id, synset);

            String[] nouns = synset.split(" ");
            for (String noun : nouns) {
                Set<Integer> ids = nounToIds.get(noun);
                if (ids == null) {
                    ids = new HashSet<>();
                    nounToIds.put(noun, ids);
                }
                ids.add(id);
            }
        }
        inSyn.close();

        // Build digraph with size = idToSynset.size()
        int V = idToSynset.size();
        G = new Digraph(V);

        // Read hypernyms
        In inHyp = new In(hypernyms);
        while (!inHyp.isEmpty()) {
            String line = inHyp.readLine();
            if (line == null || line.length() == 0) continue;
            String[] parts = line.split(",");
            int v = Integer.parseInt(parts[0]);
            for (int i = 1; i < parts.length; i++) {
                if (parts[i].length() == 0) continue;
                int w = Integer.parseInt(parts[i]);
                G.addEdge(v, w);
            }
        }
        inHyp.close();

        // Validate rooted DAG: acyclic and exactly one root (one vertex with outdegree 0)
        DirectedCycle dc = new DirectedCycle(G);
        if (dc.hasCycle()) {
            throw new IllegalArgumentException("Graph has a cycle; not a DAG");
        }

        int roots = 0;
        for (int v = 0; v < G.V(); v++) {
            if (G.outdegree(v) == 0) roots++;
        }
        if (roots != 1) {
            throw new IllegalArgumentException("Graph is not rooted DAG (roots found: " + roots + ")");
        }

        // Optionally check that root is ancestor of all vertices — with single root and DAG this commonly holds,
        // but for strictness we ensure root is reachable from all vertices by BFS on reverse graph.
        // (Not strictly necessary per some interpretations, but safe.)
        // Build reverse graph and check paths from each vertex to root:
        int root = -1;
        for (int v = 0; v < G.V(); v++) if (G.outdegree(v) == 0) root = v;
        // Quick reachability check: run DFS/BFS from every vertex to see if root reachable — but that is O(V*(V+E)).
        // Instead, run DFS on reverse graph from root and check all vertices are reachable.
        edu.princeton.cs.algs4.Digraph reverse = reverse(G);
        boolean[] marked = new boolean[G.V()];
        java.util.Stack<Integer> stack = new java.util.Stack<>();
        stack.push(root);
        marked[root] = true;
        while (!stack.isEmpty()) {
            int x = stack.pop();
            for (int y : reverse.adj(x)) {
                if (!marked[y]) {
                    marked[y] = true;
                    stack.push(y);
                }
            }
        }
        for (int v = 0; v < G.V(); v++) {
            if (!marked[v]) {
                throw new IllegalArgumentException("Graph root is not ancestor of all vertices; not rooted DAG");
            }
        }

        // Construct SAP helper
        sap = new SAP(G);
    }

    // returns all WordNet nouns
    public Iterable<String> nouns() {
        return Collections.unmodifiableSet(nounToIds.keySet());
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        if (word == null) throw new IllegalArgumentException("Argument is null");
        return nounToIds.containsKey(word);
    }

    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        if (nounA == null || nounB == null) throw new IllegalArgumentException("Argument is null");
        if (!isNoun(nounA) || !isNoun(nounB)) throw new IllegalArgumentException("Noun not in WordNet");
        Iterable<Integer> v = nounToIds.get(nounA);
        Iterable<Integer> w = nounToIds.get(nounB);
        return sap.length(v, w);
    }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        if (nounA == null || nounB == null) throw new IllegalArgumentException("Argument is null");
        if (!isNoun(nounA) || !isNoun(nounB)) throw new IllegalArgumentException("Noun not in WordNet");
        Iterable<Integer> v = nounToIds.get(nounA);
        Iterable<Integer> w = nounToIds.get(nounB);
        int ancestor = sap.ancestor(v, w);
        if (ancestor == -1) return null;
        return idToSynset.get(ancestor);
    }

    // do unit testing of this class
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java WordNet synsets.txt hypernyms.txt");
            return;
        }
        WordNet wn = new WordNet(args[0], args[1]);
        // simple interactive tests via stdin not required here; just print counts
        System.out.println("Nouns count: " + ((java.util.Set<String>)wn.nouns()).size());
    }

    // helper to produce reverse digraph
    private static Digraph reverse(Digraph G) {
        Digraph R = new Digraph(G.V());
        for (int v = 0; v < G.V(); v++) {
            for (int w : G.adj(v)) {
                R.addEdge(w, v);
            }
        }
        return R;
    }
}
