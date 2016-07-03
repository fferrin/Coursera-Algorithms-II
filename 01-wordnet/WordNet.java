
import edu.princeton.cs.algs4.ST;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.Digraph;

public class WordNet {
    private SAP sap;
    private Digraph digraph;
    private ST<String, Bag<Integer>> st;

    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        String[] line;
        String[] synList;
        int      id;
        st    = new ST<>();
        In in = new In(synsets);

        while (in.hasNextLine()) {
            line    = in.readLine().split(",");
            id      = Integer.parseInt(line[0]);
            synList = line[1].split(" ");

            for (String s : synList) {
                if (isNoun(s)) st.get(s).add(id);
                else {
                    Bag<Integer> values = new Bag<>();
                    values.add(id);
                    st.put(s, values);
                }
            }
        }

        digraph = new Digraph(st.size());
        in      = new In(hypernyms);
        while (in.hasNextLine()) {
            line = in.readLine().split(",");
            id   = Integer.parseInt(line[0]);
            for (int i = 1; i < line.length; i++)
                digraph.addEdge(id, Integer.parseInt(line[i]));
        }

        sap = new SAP(digraph);
    }

    // returns all WordNet nouns
    public Iterable<String> nouns() {
        return st.keys();
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        return st.contains(word);
    }

    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        return sap.length(st.get(nounA), st.get(nounB));
    }

    // a synset (second field of synsets.txt) that is the common ancestor
    // of nounA and nounB in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        int ancestral = sap.ancestor(st.get(nounA), st.get(nounB));

        for (String s : nouns()) {
            for (int id : st.get(s))
                if (ancestral == id) return s;
        }
        return null;
    }

    // do unit testing of this class
    public static void main(String[] args) {
        WordNet wordnet = new WordNet(args[0], args[1]);
        System.out.println(wordnet.sap("unit_cell", "variable"));
    }
}
