import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

public class BaseballElimination {
    private final int n;
    private final String[] teams;
    private final Map<String, Integer> teamIndex;
    private final int[] wins;
    private final int[] losses;
    private final int[] remaining;
    private final int[][] games;

    public BaseballElimination(String filename) {
        In in = new In(filename);
        n = in.readInt();
        teams = new String[n];
        teamIndex = new HashMap<>();
        wins = new int[n];
        losses = new int[n];
        remaining = new int[n];
        games = new int[n][n];

        for (int i = 0; i < n; i++) {
            teams[i] = in.readString();
            teamIndex.put(teams[i], i);
            wins[i] = in.readInt();
            losses[i] = in.readInt();
            remaining[i] = in.readInt();
            for (int j = 0; j < n; j++) {
                games[i][j] = in.readInt();
            }
        }
    }

    public int numberOfTeams() {
        return n;
    }

    public Iterable<String> teams() {
        ArrayList<String> list = new ArrayList<>();
        for (String t : teams) list.add(t);
        return list;
    }

    public int wins(String team) {
        return wins[teamIndex.get(team)];
    }

    public int losses(String team) {
        return losses[teamIndex.get(team)];
    }

    public int remaining(String team) {
        return remaining[teamIndex.get(team)];
    }

    public int against(String team1, String team2) {
        return games[teamIndex.get(team1)][teamIndex.get(team2)];
    }

    public boolean isEliminated(String team) {
        return certificateOfElimination(team) != null;
    }

    public Iterable<String> certificateOfElimination(String team) {
        int x = teamIndex.get(team);

        // Trivial elimination
        for (int i = 0; i < n; i++) {
            if (wins[x] + remaining[x] < wins[i]) {
                ArrayList<String> cert = new ArrayList<>();
                cert.add(teams[i]);
                return cert;
            }
        }

        // Build Flow network
        int gameVertices = n * (n - 1) / 2;
        int V = 2 + gameVertices + n; // source + sink + games + teams
        int source = 0;
        int sink = V - 1;

        FlowNetwork network = new FlowNetwork(V);

        int gameNode = 1;
        int totalGames = 0;

        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (i == x || j == x) continue;
                int gamesLeft = games[i][j];
                if (gamesLeft > 0) {
                    network.addEdge(new FlowEdge(source, gameNode, gamesLeft));
                    network.addEdge(new FlowEdge(gameNode, gameVertices + 1 + i, Double.POSITIVE_INFINITY));
                    network.addEdge(new FlowEdge(gameNode, gameVertices + 1 + j, Double.POSITIVE_INFINITY));
                    totalGames += gamesLeft;
                }
                gameNode++;
            }
        }

        for (int i = 0; i < n; i++) {
            if (i == x) continue;
            int capacity = wins[x] + remaining[x] - wins[i];
            if (capacity < 0) capacity = 0;
            network.addEdge(new FlowEdge(gameVertices + 1 + i, sink, capacity));
        }

        FordFulkerson ff = new FordFulkerson(network, source, sink);

        if (ff.value() < totalGames) {
            ArrayList<String> cert = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                if (i != x && ff.inCut(gameVertices + 1 + i)) {
                    cert.add(teams[i]);
                }
            }
            return cert;
        }
        return null;
    }

    public static void main(String[] args) {
        BaseballElimination division = new BaseballElimination(args[0]);
        for (String team : division.teams()) {
            if (division.isEliminated(team)) {
                StdOut.print(team + " is eliminated by the subset R = { ");
                for (String t : division.certificateOfElimination(team)) {
                    StdOut.print(t + " ");
                }
                StdOut.println("}");
            } else {
                StdOut.println(team + " is not eliminated");
            }
        }
    }
}
