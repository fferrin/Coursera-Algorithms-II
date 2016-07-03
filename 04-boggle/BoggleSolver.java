
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.StdOut;

public class BoggleSolver {
    private fastTST      dict;
    private SET<String>  words;
    private boolean[][]  marked;
    private int          M, N;

    // Initializes the data structure using the given array of strings
    // as the dictionary.
    // (You can assume each word in the dictionary contains only the
    // uppercase letters A through Z.)
    public BoggleSolver(String[] dictionary) {
        dict = new fastTST();
        for (String word : dictionary)
            dict.add(word);
    }

    // Returns the set of all valid words in the given
    // Boggle board, as an Iterable.
    public Iterable<String> getAllValidWords(BoggleBoard board) {
        words = new SET<>();
        M = board.rows();
        N = board.cols();
        marked = new boolean[M][N];
        String s = "";
        for (int r = 0; r < M; r++)
            for (int c = 0; c < N; c++)
                getValid(board, s, r, c);
        return words;
    }

    private void getValid(BoggleBoard board, String word, int i, int j) {
        if (validIndices(i, j)) {
            if (!marked[i][j]) {
                marked[i][j] = true;
                char c = board.getLetter(i, j);
                word = addCharToString(word, c);
                if (dict.putIntoStack(c)) {
                    if (dict.isWord() && !words.contains(word) && validWord(word))
                        words.add(word);
                    getValid(board, word, i-1, j-1);
                    getValid(board, word, i-1, j);
                    getValid(board, word, i-1, j+1);
                    getValid(board, word, i, j-1);
                    getValid(board, word, i, j+1);
                    getValid(board, word, i+1, j-1);
                    getValid(board, word, i+1, j);
                    getValid(board, word, i+1, j+1);
                    dict.cleanStack();
                }
                word = remCharFromString(word, c);
                marked[i][j] = false;
            }
        }
    }

    private boolean validIndices(int i, int j) {
        if (i < 0 || M <= i || j < 0 || N <= j) return false;
        return true;
    }

    private String addCharToString(String s, char c) {
        s += c;
        if (c == 'Q') s += 'U';
        return s;
    }

    private String remCharFromString(String s, char c) {
        if (c == 'Q') return s.substring(0, s.length() - 2);
        else          return s.substring(0, s.length() - 1);
    }

    private boolean validWord(String s) {
        return (2 < s.length());
    }
    // Returns the score of the given word if it is in the
    // dictionary, zero otherwise.
    // (You can assume the word contains only the uppercase
    // letters A through Z.)
    public int scoreOf(String word) {
        if (!dict.contains(word)) return 0;
        int len = word.length();
        if      (len <= 2) return 0;
        else if (len <= 4) return 1;
        else if (len == 5) return 2;
        else if (len == 6) return 3;
        else if (len == 7) return 5;
        else               return 11;
    }

    public static void main(String[] args) {
        In in = new In(args[0]);
        String[] dictionary = in.readAllStrings();
        BoggleSolver solver = new BoggleSolver(dictionary);
        BoggleBoard board = new BoggleBoard(args[1]);
        int score = 0;
        for (String word : solver.getAllValidWords(board)) {
            StdOut.println(word);
            score += solver.scoreOf(word);
        }
        StdOut.println("Score = " + score);
    }
}
