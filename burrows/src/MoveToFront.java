import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

/**
 * Move-to-front encoding/decoding.
 * Usage:
 *   java MoveToFront -   (encode from BinaryStdIn to BinaryStdOut)
 *   java MoveToFront +   (decode)
 */
public class MoveToFront {

    // apply move-to-front encoding, reading from standard input and writing to standard output
    public static void encode() {
        // initialize sequence of 256 extended ASCII characters
        char[] seq = new char[256];
        for (int i = 0; i < 256; i++) seq[i] = (char) i;

        while (!BinaryStdIn.isEmpty()) {
            char c = BinaryStdIn.readChar(); // reads 8-bit char
            // find index of c in seq
            int idx = 0;
            while (seq[idx] != c) idx++;
            // write index as 8-bit value
            BinaryStdOut.write((char) idx);
            // move to front
            if (idx != 0) {
                char temp = seq[idx];
                System.arraycopy(seq, 0, seq, 1, idx);
                seq[0] = temp;
            }
        }
        BinaryStdOut.flush();
    }

    // apply move-to-front decoding, reading from standard input and writing to standard output
    public static void decode() {
        // initialize sequence
        char[] seq = new char[256];
        for (int i = 0; i < 256; i++) seq[i] = (char) i;

        while (!BinaryStdIn.isEmpty()) {
            int idx = BinaryStdIn.readChar(); // read 8-bit index as char then promoted to int 0..255
            char c = seq[idx];
            BinaryStdOut.write(c);
            // move to front
            if (idx != 0) {
                char temp = seq[idx];
                System.arraycopy(seq, 0, seq, 1, idx);
                seq[0] = temp;
            }
        }
        BinaryStdOut.flush();
    }

    // main: args[0] "-" => encode; "+" => decode
    public static void main(String[] args) {
        if (args.length == 0) {
            throw new IllegalArgumentException("usage: MoveToFront - (encode) or + (decode)");
        }
        if (args[0].equals("-")) encode();
        else if (args[0].equals("+")) decode();
        else throw new IllegalArgumentException("first arg must be '-' or '+'");
    }
}
