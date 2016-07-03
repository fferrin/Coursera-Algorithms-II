
import java.util.ArrayList;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;

public class BaseballElimination {
    private ArrayList<String> teams;
    private int     N;
    private int[]   w, l, r;
    private int[][] g;
    private Bag<Integer> R;

    // create a baseball division from given filename in format specified below
    public BaseballElimination(String filename) {
        In in = new In(filename);
        N = in.readInt();

        teams = new ArrayList<>();
        w = new int[N];
        l = new int[N];
        g = new int[N][N];
        r = new int[N];

        for (int i = 0; i < N; i++) {
            teams.add(i, in.readString());
            w[i] = in.readInt();
            l[i] = in.readInt();
            r[i] = in.readInt();

            for (int j = 0; j < N; j++)
                g[i][j] = in.readInt();
        }
    }

    // number of teams
    public int numberOfTeams() {
        return N;
    }

    // all teams
    public Iterable<String> teams() {
        return teams;
    }

    // number of wins for given team
    public int wins(String team) {
        checkTeam(team);
        return w[teams.indexOf(team)];
    }

    // number of losses for given team
    public int losses(String team) {
        checkTeam(team);
        return l[teams.indexOf(team)];
    }

    // number of remaining games for given team
    public int remaining(String team) {
        checkTeam(team);
        return r[teams.indexOf(team)];
    }

    // number of remaining games between team1 and team2
    public int against(String team1, String team2) {
        checkTeam(team1);
        checkTeam(team2);
        return g[teams.indexOf(team1)][teams.indexOf(team2)];
    }

    // is given team eliminated?
    public boolean isEliminated(String team) {
        checkTeam(team);
        return computeR(teams.indexOf(team));
    }

    // subset R of teams that eliminates given team; null if not eliminated
    public Iterable<String> certificateOfElimination(String team) {
        checkTeam(team);
        if (!isEliminated(team)) return null;
        computeR(teams.indexOf(team));
        Bag<String> certificate = new Bag<>();
        for (int i : R)
            certificate.add(teams.get(i));
        return certificate;
    }

    private FlowNetwork flowNetwork(int x, int s, int t) {
        // int V = N*N + N + 2;
        int V = (N*N + N + 4) / 2;
        FlowNetwork fNet = new FlowNetwork(V);

        // Teams from 0 to N-1
        for (int i = 0; i < N; i++)
            if (i != x) fNet.addEdge(new FlowEdge(i, t, w[x] + r[x] - w[i]));

        // Games from N+2 to (N^2 + N + 4)/2
        for (int i = 0; i < N - 1; i++) {
            for (int j = i + 1; j < N; j++) {
                if (i != x && j != x) {
                    int v = (N * i + j) - (i * i + 3 * i)/2 + t;
                    fNet.addEdge(new FlowEdge(s, v, g[i][j]));
                    fNet.addEdge(new FlowEdge(v, i, Double.POSITIVE_INFINITY));
                    fNet.addEdge(new FlowEdge(v, j, Double.POSITIVE_INFINITY));
                }
            }
        }
        return fNet;
    }

    private boolean computeR(int x) {
        R = new Bag<>();
        int s = N;
        int t = N + 1;
        int maxWinsX = w[x] + r[x];

        // Trivial elimination
        for (int i = 0; i < N; i++)
            if (i != x && maxWinsX < w[i])
                R.add(i);

        if (!R.isEmpty()) return true;

        // Nontrivial elimination
        FlowNetwork   FN = flowNetwork(x, s, t);
        FordFulkerson FF = new FordFulkerson(FN, s, t);

        for (int i = 0; i < N; i++)
            if (FF.inCut(i))
                R.add(i);
        return maxWinsX < averageR();
    }

    private double averageR() {
        int totalWins = 0;
        for (int i : R)
            totalWins += w[i] + r[i];
        return (double) totalWins * 1.0 / R.size();
    }

    private void checkTeam(String team) {
        if (!teams.contains(team))
            throw new IllegalArgumentException("invalid team");
    }

    public static void main(String[] args) {
        BaseballElimination division = new BaseballElimination(args[0]);

        for (String team : division.teams()) {
            if (division.isEliminated(team)) {
                StdOut.print(team + " is eliminated by the subset R = { ");
                for (String t : division.certificateOfElimination(team))
                    StdOut.print(t + " ");
                StdOut.println("}");
            } else
                StdOut.println(team + " is not eliminated");
        }
    }
}
