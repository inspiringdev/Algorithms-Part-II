public class Outcast {
    private final WordNet wordnet;

    public Outcast(WordNet wordnet) {
        if (wordnet == null) throw new IllegalArgumentException("Argument is null");
        this.wordnet = wordnet;
    }

    // given an array of WordNet nouns, return an outcast
    public String outcast(String[] nouns) {
        if (nouns == null) throw new IllegalArgumentException("Argument is null");
        if (nouns.length < 1) throw new IllegalArgumentException("Must provide at least one noun");
        String outcast = null;
        int maxDist = -1;
        for (String noun : nouns) {
            if (noun == null) throw new IllegalArgumentException("Array contains null");
            if (!wordnet.isNoun(noun)) throw new IllegalArgumentException("Not a WordNet noun: " + noun);
            int sum = 0;
            for (String other : nouns) {
                sum += wordnet.distance(noun, other);
            }
            if (sum > maxDist) {
                maxDist = sum;
                outcast = noun;
            }
        }
        return outcast;
    }

    // test client
    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("Usage: java Outcast synsets.txt hypernyms.txt outcastX.txt ...");
            return;
        }
        WordNet wn = new WordNet(args[0], args[1]);
        Outcast oc = new Outcast(wn);
        for (int t = 2; t < args.length; t++) {
            edu.princeton.cs.algs4.In in = new edu.princeton.cs.algs4.In(args[t]);
            String[] nouns = in.readAllStrings();
            String out = oc.outcast(nouns);
            System.out.println(args[t] + ": " + out);
        }
    }
}
