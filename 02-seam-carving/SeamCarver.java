
import java.awt.Color;
// import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.Picture;

public class SeamCarver {
    private static final double MAX_ENERGY = 1000.0;
    private int[][]    pic;
    private double[][] energy;
    private boolean    isTransposed;
    private int width, height;

    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        if (picture == null) throw new NullPointerException("null argument");
        width  = picture.width();
        height = picture.height();
        energy = new double[height][width];
        pic    = new int[height][width];
        isTransposed = false;

        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++)
                pic[r][c] = picture.get(c, r).getRGB();
        }

        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++)
                energy[r][c] = energy(c, r);
        }
    }

    // current picture
    public Picture picture() {
        if (isTransposed) transpose();
        Picture newPic = new Picture(width, height);
        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++)
                newPic.set(c, r, new Color(pic[r][c]));
        }
        return newPic;
    }

    // width of current picture
    public int width() {
        return width;
    }

    // height of current picture
    public int height() {
        return height;
    }

    private int getRed(int color) {
        return ((color >> 16) & 0xFF);
    }

    private int getGreen(int color) {
        return ((color >> 8) & 0xFF);
    }

    private int getBlue(int color) {
        return (color & 0xFF);
    }

    private int deltaSquared(int colorPixA, int colorPixB) {
        int r = getRed(colorPixA) - getRed(colorPixB);
        int g = getGreen(colorPixA) - getGreen(colorPixB);
        int b = getBlue(colorPixA) - getBlue(colorPixB);
        return (r * r + g * g + b * b);
    }

    // energy of pixel at column x and row y
    public double energy(int x, int y) {
        if (isTransposed) transpose();
        return picEnergy(x, y);
    }

    private double picEnergy(int c, int r) {
        if (!validIndices(r, c))
            throw new IndexOutOfBoundsException("index out of bounds");
        if (c == 0 || c == (width - 1) || r == 0 || r == (height - 1))
            return MAX_ENERGY;
        else
            return Math.sqrt(deltaSquared(pic[r - 1][c], pic[r + 1][c]) +
                             deltaSquared(pic[r][c - 1], pic[r][c + 1]));
    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        if (!isTransposed) transpose();
        return findSeam();
    }

    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        if (isTransposed) transpose();
        return findSeam();
    }

    // find a vertical seam
    private int[] findSeam() {
        double[] lastEnergy = new double[width];
        double[] currEnergy = new double[width];
        int[] minEnergyPath = new int[height];
        int[][] edgeTo      = new int[height][width];

        if (width == 1) {
            for (int r = 0; r < height; r++)
                minEnergyPath[r] = 0;
        } else {
            for (int c = 0; c < width; c++)
                lastEnergy[c] = energy[0][c];

            for (int r = 1; r < height; r++) {
                // Case col = 0
                if (lastEnergy[1] < lastEnergy[0]) {
                    currEnergy[0] = lastEnergy[1] + energy[r][0];
                    edgeTo[r][0]  = 1;
                } else {
                    currEnergy[0] = lastEnergy[0] + energy[r][0];
                    edgeTo[r][0]  = 0;
                }

                // Case 0 < col < width-1
                for (int c = 1; c < (width - 1); c++) {
                    currEnergy[c] = lastEnergy[c - 1];
                    edgeTo[r][c]  = c - 1;
                    if (lastEnergy[c] < currEnergy[c]) {
                        currEnergy[c] = lastEnergy[c];
                        edgeTo[r][c]  = c;
                    }
                    if (lastEnergy[c + 1] < currEnergy[c]) {
                        currEnergy[c] = lastEnergy[c + 1];
                        edgeTo[r][c]  = c + 1;
                    }
                    currEnergy[c] += energy[r][c];
                }

                // Case col = width-1
                if (lastEnergy[width - 2] <= lastEnergy[width - 1]) {
                    currEnergy[width - 1] = lastEnergy[width - 2] +
                                               energy[r][width - 1];
                    edgeTo[r][width - 1] = width - 2;
                } else {
                    currEnergy[width - 1] = lastEnergy[width - 1] +
                                               energy[r][width - 1];
                    edgeTo[r][width - 1] = width - 1;
                }
                lastEnergy = currEnergy.clone();
            }

            minEnergyPath[height - 1] = 0;
            for (int c = 1; c < width; c++)
                if (currEnergy[c] < currEnergy[minEnergyPath[height - 1]])
                    minEnergyPath[height - 1] = c;
            for (int r = height - 1; 0 < r; r--)
                minEnergyPath[r - 1] = edgeTo[r][minEnergyPath[r]];
        }
        return minEnergyPath;
    }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        if (!isTransposed) transpose();
        removeSeam(seam);
    }

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        if (isTransposed) transpose();
        removeSeam(seam);
    }

    // remove vertical seam
    private void removeSeam(int[] seam) {
        validateSeam(seam);
        for (int r = 0; r < height; r++) {
            System.arraycopy(pic[r], seam[r] + 1,
                             pic[r], seam[r], width - (seam[r] + 1));
        }
        width--;
        for (int r = 0; r < height; r++)
            for (int c = seam[r] - 1; c < seam[r] + 2; c++)
                if (validIndices(r, c)) energy[r][c] = picEnergy(c, r);
    }

    // validate vertical seam
    private void validateSeam(int[] seam) {
        if (seam == null) throw new NullPointerException("null argument");
        if (seam.length != height)
            throw new IllegalArgumentException("wrong length array");

        if (width <= 1) throw new IllegalArgumentException("can't remove seam");

        if (seam[0] < 0 || width <= seam[0])
            throw new IllegalArgumentException("invalid seam");
        int prev = seam[0];
        int curr;
        for (int i = 1; i < height; i++) {
            curr = seam[i];
            if (curr < 0 || width <= curr)
                throw new IllegalArgumentException("invalid seam");
            if ((curr - 1) != prev && curr != prev && (curr + 1) != prev)
                throw new IllegalArgumentException("invalid seam");
            prev = curr;
        }
    }

    private boolean validIndices(int r, int c) {
        if (c < 0 || (width - 1) < c || r < 0 || (height - 1) < r) return false;
        return true;
    }

    private void transpose() {
        int[][]    transPic = new int[width][height];
        double[][] transEne = new double[width][height];
        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                transPic[c][r] = pic[r][c];
                transEne[c][r] = energy[r][c];
            }
        }
        pic    = transPic;
        energy = transEne;
        isTransposed = !isTransposed;

        int temp = width;
        width    = height;
        height   = temp;
    }

    // public static void main(String[] args) {}
}
