// File: BoggleSolver.java
import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;
import java.util.List;

/**
 * BoggleSolver: finds all valid words on a BoggleBoard using a given dictionary.
 *
 * Implementation notes:
 *  - Uses a TrieSET for dictionary membership and prefix checking.
 *  - DFS from each board cell; prune using hasPrefix().
 *  - Handles the special 'Qu' tile: BoggleBoard.getLetter(i,j) returns 'Q' to denote "Qu".
 */
public class BoggleSolver {
    private final TrieSET dict;

    // Constructor: build trie from dictionary array
    public BoggleSolver(String[] dictionary) {
        if (dictionary == null) throw new IllegalArgumentException("dictionary is null");
        dict = new TrieSET();
        for (String w : dictionary) {
            if (w != null && w.length() > 0)
                dict.add(w);
        }
    }

    // Returns all valid words in the given Boggle board
    public Iterable<String> getAllValidWords(BoggleBoard board) {
        if (board == null) throw new IllegalArgumentException("board is null");
        int R = board.rows();
        int C = board.cols();
        boolean[][] marked = new boolean[R][C];
        Set<String> result = new HashSet<>();

        StringBuilder sb = new StringBuilder();
        for (int r = 0; r < R; r++) {
            for (int c = 0; c < C; c++) {
                dfs(board, r, c, marked, sb, result);
            }
        }
        return result;
    }

    // Returns score of the word (0 if not in dictionary)
    public int scoreOf(String word) {
        if (word == null) throw new IllegalArgumentException("word is null");
        if (!dict.contains(word)) return 0;
        int L = word.length();
        if (L <= 4) return 1;
        if (L == 5) return 2;
        if (L == 6) return 3;
        if (L == 7) return 5;
        return 11;
    }

    // DFS recursive search
    private void dfs(BoggleBoard board, int r, int c, boolean[][] marked, StringBuilder sb, Set<String> result) {
        // append letter (handle 'Q' as "QU")
        char ch = board.getLetter(r, c);
        int prevLen = sb.length();
        if (ch == 'Q') {
            sb.append("QU");
        } else {
            sb.append(ch);
        }

        // prune: if there is no word in dictionary with this prefix, backtrack
        if (!dict.hasPrefix(sb.toString())) {
            sb.setLength(prevLen);
            return;
        }

        // if word length >= 3 and in dictionary, add
        if (sb.length() >= 3 && dict.contains(sb.toString())) {
            result.add(sb.toString());
        }

        // explore neighbors
        marked[r][c] = true;
        int R = board.rows();
        int C = board.cols();
        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                if (dr == 0 && dc == 0) continue;
                int nr = r + dr;
                int nc = c + dc;
                if (0 <= nr && nr < R && 0 <= nc && nc < C && !marked[nr][nc]) {
                    dfs(board, nr, nc, marked, sb, result);
                }
            }
        }
        marked[r][c] = false;
        sb.setLength(prevLen);
    }

}
