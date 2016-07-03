
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;

public class CircularSuffixArray {
    private String        string;
    private List<Integer> sortSuffixes;

    // circular suffix array of s
    public CircularSuffixArray(String s) {
        if (s == null) throw new NullPointerException("null argument");
        string = s;
        sortSuffixes = new ArrayList<>();

        for (int i = 0; i < length(); i++)
            sortSuffixes.add((length() - 1) - i);

        Collections.sort(sortSuffixes, suffixesOrder());
    }

    private class SuffixesOrder implements Comparator<Integer> {
        public int compare(Integer i, Integer j) {
            return compare(i, j, 0);
        }

        private int compare(int i, int j, int len) {
            if (i == length()) i = 0;
            if (j == length()) j = 0;

            if (length() == len++) return 1;
            if (string.charAt(i) != string.charAt(j))
                return string.charAt(i) - string.charAt(j);
            else return compare(i+1, j+1, len);
        }
    }

    // length of s
    public int length() {
        return string.length();
    }

    // returns index of ith sorted suffix
    public int index(int i) {
        if (i < 0 || (length() -1) < i)
            throw new IndexOutOfBoundsException("index out of bounds");
        return sortSuffixes.get(i);
    }

    private Comparator<Integer> suffixesOrder() {
        return new SuffixesOrder();
    }

    // unit testing of the methods (optional)
    public static void main(String[] args) {
    }
}
