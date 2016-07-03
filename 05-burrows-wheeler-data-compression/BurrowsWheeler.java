
import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class BurrowsWheeler {
    private static final int LENGTH = 256;
    // apply Burrows-Wheeler encoding, reading from standard
    // input and writing to standard output
    public static void encode() {
        // read the input
        String s      = BinaryStdIn.readString();
        char[] input  = s.toCharArray();
        int    len    = input.length;
        char[] output = new char[len];

        CircularSuffixArray CSA = new CircularSuffixArray(s);
        int first = -1;

        for (int i = 0; i < len; i++) {
            if (CSA.index(i) != 0) output[i] = input[CSA.index(i) - 1];
            else {
                output[i] = input[len - 1];
                first = i;
            }
        }

        BinaryStdOut.write(first);
        for (char c : output)
            BinaryStdOut.write(c);
        BinaryStdOut.flush();
    }

    // apply Burrows-Wheeler decoding, reading from standard
    // input and writing to standard output
    public static void decode() {
        int    first = BinaryStdIn.readInt();
        String s     = BinaryStdIn.readString();
        char[] input = s.toCharArray();
        int[]  next  = computeNext(input);

        first = next[first];
        for (int i = 0; i < next.length; i++) {
            BinaryStdOut.write(input[first]);
            first = next[first];
        }
        BinaryStdOut.flush();
    }

    private static int[] computeNext(char[] t) {
        int[] count = new int[LENGTH + 1];
        int[] aux   = new int[t.length];
        // compute frequency counts
        for (char c : t)
            count[(int) c + 1]++;
        // Transform counts to indices.
        for (int i = 0; i < LENGTH - 1; i++)
            count[i + 1] += count[i];
        // Distribute the records.
        for (int i = 0; i < t.length; i++)
            aux[count[(int) t[i]]++] = i;
        return aux;
    }

    // if args[0] is '-', apply Burrows-Wheeler encoding
    // if args[0] is '+', apply Burrows-Wheeler decoding
    public static void main(String[] args) {
        if      (args[0].equals("-")) encode();
        else if (args[0].equals("+")) decode();
        else throw new IllegalArgumentException("Illegal command line argument");
    }
}
