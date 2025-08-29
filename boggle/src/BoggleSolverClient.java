// File: BoggleSolverClient.java
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class BoggleSolverClient {
    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.out.println("Usage: java BoggleSolverClient <dictionary-file> <board-file>");
            return;
        }
        // read dictionary
        List<String> dict = new ArrayList<>();
        try (Scanner sc = new Scanner(new File(args[0]))) {
            while (sc.hasNext()) dict.add(sc.next().toUpperCase());
        }
        BoggleSolver solver = new BoggleSolver(dict.toArray(new String[0]));

        // read board
        BoggleBoard board = new BoggleBoard(args[1]);

        int score = 0;
        for (String w : solver.getAllValidWords(board)) {
            System.out.println(w);
            score += solver.scoreOf(w);
        }
        System.out.println("Score = " + score);
    }
}
