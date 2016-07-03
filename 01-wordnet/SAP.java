
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.BreadthFirstDirectedPaths;

public class SAP {
    private static final int INFINITY = Integer.MAX_VALUE;
    private Digraph digraph;
    private int     length;
    private int     ancestor;
    private BreadthFirstDirectedPaths bfs;

    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        digraph = new Digraph(G);
    }

    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        if (ancestor(v, w) < 0) return -1;
        return length;
    }

    // a common ancestor of v and w that participates in a shortest
    // ancestral path; -1 if no such path
    public int ancestor(int v, int w) {
        bfs = new BreadthFirstDirectedPaths(digraph, w);
        ancestorRecursive(v, 0);

        return ancestor;
    }

    private void ancestorRecursive(int v, int dist) {
        int aux = bfs.distTo(v);

        if (aux < length) {
            this.length   = aux + dist;
            this.ancestor = v;
        }

        for (int adj : digraph.adj(v))
            ancestorRecursive(adj, ++dist);
    }

    // length of shortest ancestral path between any vertex in v and any
    // vertex in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        if (ancestor(v, w) < 0) return -1;
        return length;

    }

    // a common ancestor that participates in shortest ancestral path;
    // -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        length   = INFINITY;
        ancestor = -1;

        for (int v1 : v) for (int v2 : w) ancestor(v1, v2);

        return ancestor;
    }

    // do unit testing of this class
    public static void main(String[] args) {
        // In in = new In(args[0]);
        // Digraph G = new Digraph(in);
        // // System.out.println(G.toString());
        // SAP sap = new SAP(G);
        // while (!StdIn.isEmpty()) {
        //     int v = StdIn.readInt();
        //     int w = StdIn.readInt();
        //     int length   = sap.length(v, w);
        //     int ancestor = sap.ancestor(v, w);
        //     StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        // }
    }
}
