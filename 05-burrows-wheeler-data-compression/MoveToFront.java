
import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class MoveToFront {
    private static final int LENGTH = 256;
    // apply move-to-front encoding, reading from standard
    // input and writing to standard output
    public static void encode() {
        // read the input
        String s     = BinaryStdIn.readString();
        char[] input = s.toCharArray();
        char[] seq   = new char[LENGTH];

        // fill array
        for (int i = 0; i < LENGTH; i++)
            seq[i] = (char) i;

        for (char c : input) {
            int tmpIndex = 0;
            char tmpPrevChar = seq[tmpIndex];
            char tmpNextChar = seq[tmpIndex + 1];
            while (tmpPrevChar != c) {
                seq[++tmpIndex] = tmpPrevChar;
                tmpPrevChar = tmpNextChar;
                if (tmpIndex < LENGTH - 1) tmpNextChar = seq[tmpIndex + 1];
            }

            seq[0] = tmpPrevChar;
            BinaryStdOut.write(tmpIndex, 8);
        }
        BinaryStdOut.flush();
    }

    // apply move-to-front decoding, reading from standard
    // input and writing to standard output
    public static void decode() {
        char[] seq = new char[256];

        // fill array
        for (int i = 0; i < 256; i++)
            seq[i] = (char) i;

        while (!BinaryStdIn.isEmpty()) {
            int  index  = (int) BinaryStdIn.readChar();
            char letter = seq[index];
            BinaryStdOut.write(seq[index]);
            for (int i = index; 0 < i; i--)
                seq[i] = seq[i - 1];
            seq[0] = letter;
        }
        BinaryStdOut.flush();
    }

    // if args[0] is '-', apply move-to-front encoding
    // if args[0] is '+', apply move-to-front decoding
    public static void main(String[] args) {
        if      (args[0].equals("-")) encode();
        else if (args[0].equals("+")) decode();
        else throw new IllegalArgumentException("Illegal command line argument");
    }
}
