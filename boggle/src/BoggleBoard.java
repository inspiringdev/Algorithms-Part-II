// File: BoggleBoard.java
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class BoggleBoard {
    private final int m;        // rows
    private final int n;        // cols
    private final char[][] board;
    private static final Random random = new Random();

    // the 16 Hasbro dice (1992 version)
    private static final String[] HASBRO = {
            "AAEEGN", "ABBJOO", "ACHOPS", "AFFKPS",
            "AOOTTW", "CIMOTU", "DEILRX", "DELRVY",
            "DISTTY", "EEGHNW", "EEINSU", "EHRTVW",
            "EIOSST", "ELRTTY", "HIMNQU", "HLNNRZ"
    };

    // create random 4x4 board using Hasbro dice
    public BoggleBoard() {
        this(4, 4);
        // shuffle dice in-place
        for (int i = 0; i < HASBRO.length; i++) {
            int r = i + random.nextInt(HASBRO.length - i);
            String tmp = HASBRO[i];
            HASBRO[i] = HASBRO[r];
            HASBRO[r] = tmp;
        }
        // roll dice
        int k = 0;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++, k++) {
                String faces = HASBRO[k];
                board[i][j] = faces.charAt(random.nextInt(faces.length()));
            }
        }
    }

    // create random m-by-n board, letters chosen uniformly
    public BoggleBoard(int m, int n) {
        if (m <= 0 || n <= 0) throw new IllegalArgumentException("Invalid dimensions");
        this.m = m;
        this.n = n;
        board = new char[m][n];
        for (int i = 0; i < m; i++)
            for (int j = 0; j < n; j++)
                board[i][j] = (char) ('A' + random.nextInt(26));
    }

    // construct from 2D char array
    public BoggleBoard(char[][] a) {
        if (a == null || a.length == 0 || a[0].length == 0)
            throw new IllegalArgumentException("array is empty");
        m = a.length;
        n = a[0].length;
        board = new char[m][n];
        for (int i = 0; i < m; i++) {
            if (a[i].length != n) throw new IllegalArgumentException("ragged array");
            for (int j = 0; j < n; j++)
                board[i][j] = a[i][j];
        }
    }

    // construct from file (supports "m n" header OR just n×n letters; "Qu" or "Q" ok)
    public BoggleBoard(String filename) {
        if (filename == null) throw new IllegalArgumentException("filename is null");
        List<String> tokens = new ArrayList<>();
        try (Scanner sc = new Scanner(new File(filename))) {
            sc.useDelimiter("\\s+");
            while (sc.hasNext()) tokens.add(sc.next());
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("Cannot open file: " + filename, e);
        }
        if (tokens.size() < 2) throw new IllegalArgumentException("file too small");

        int rows, cols, startIdx = 0;

        // Try to parse first two tokens as ints (rows, cols)
        Integer maybeM = tryParseInt(tokens.get(0));
        Integer maybeN = tryParseInt(tokens.get(1));
        if (maybeM != null && maybeN != null) {
            rows = maybeM;
            cols = maybeN;
            startIdx = 2;
            if (rows <= 0 || cols <= 0) throw new IllegalArgumentException("invalid dimensions");
            if (tokens.size() - startIdx != rows * cols)
                throw new IllegalArgumentException("expected " + (rows * cols) + " letters but found " + (tokens.size() - startIdx));
        } else {
            // No header; assume square board using all tokens
            int total = tokens.size();
            int size = (int) Math.round(Math.sqrt(total));
            if (size * size != total)
                throw new IllegalArgumentException("Board file must contain an n-by-n grid of letters.");
            rows = cols = size;
            startIdx = 0;
        }

        this.m = rows;
        this.n = cols;
        this.board = new char[m][n];

        int k = startIdx;
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++, k++) {
                String s = tokens.get(k).toUpperCase();
                if ("QU".equals(s)) board[i][j] = 'Q';
                else board[i][j] = s.charAt(0); // assume A–Z
            }
        }
    }

    private static Integer tryParseInt(String s) {
        try { return Integer.parseInt(s); } catch (Exception e) { return null; }
    }

    public int rows() { return m; }
    public int cols() { return n; }

    // 'Q' represents the two-letter sequence "Qu"
    public char getLetter(int i, int j) {
        if (i < 0 || i >= m || j < 0 || j >= n)
            throw new IllegalArgumentException("index out of bounds");
        return board[i][j];
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(m).append(" ").append(n).append("\n");
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                sb.append(board[i][j]).append(' ');
            }
            sb.append('\n');
        }
        return sb.toString();
    }
}
