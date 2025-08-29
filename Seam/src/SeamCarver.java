import edu.princeton.cs.algs4.Picture;
import java.awt.Color;

public class SeamCarver {
    private Picture picture;
    private double[][] energy; // energy[y][x]

    public SeamCarver(Picture picture) {
        if (picture == null) throw new IllegalArgumentException("Null picture");
        this.picture = new Picture(picture); // defensive copy
        computeEnergy();
    }

    public Picture picture() {
        return new Picture(picture); // defensive copy
    }

    public int width()  { return picture.width();  }
    public int height() { return picture.height(); }

    // recompute full energy table from current picture
    private void computeEnergy() {
        int w = width(), h = height();
        energy = new double[h][w];
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                energy[y][x] = energy(x, y);
            }
        }
    }

    // dual-gradient energy
    public double energy(int x, int y) {
        int w = width(), h = height();
        if (x < 0 || x >= w || y < 0 || y >= h)
            throw new IllegalArgumentException("Coordinates out of range");

        if (x == 0 || x == w - 1 || y == 0 || y == h - 1) return 1000.0;

        int rgbL = picture.get(x - 1, y).getRGB();
        int rgbR = picture.get(x + 1, y).getRGB();
        int rgbU = picture.get(x, y - 1).getRGB();
        int rgbD = picture.get(x, y + 1).getRGB();

        double dx2 = delta(rgbL, rgbR);
        double dy2 = delta(rgbU, rgbD);
        return Math.sqrt(dx2 + dy2);
    }

    private static double delta(int rgb1, int rgb2) {
        int r1 = (rgb1 >> 16) & 0xFF, g1 = (rgb1 >> 8) & 0xFF, b1 = rgb1 & 0xFF;
        int r2 = (rgb2 >> 16) & 0xFF, g2 = (rgb2 >> 8) & 0xFF, b2 = rgb2 & 0xFF;
        int dr = r1 - r2, dg = g1 - g2, db = b1 - b2;
        return dr*dr + dg*dg + db*db;
    }

    // Horizontal seam via transpose trick
    public int[] findHorizontalSeam() {
        transpose();
        int[] seam = findVerticalSeam();
        transpose();
        return seam;
    }

    // Standard DP: O(W*H)
    public int[] findVerticalSeam() {
        int w = width(), h = height();

        double[][] distTo = new double[h][w];
        int[][] edgeTo = new int[h][w];

        for (int y = 0; y < h; y++)
            for (int x = 0; x < w; x++)
                distTo[y][x] = Double.POSITIVE_INFINITY;

        // top row initializes to its own energy
        for (int x = 0; x < w; x++) {
            distTo[0][x] = energy[0][x];
            edgeTo[0][x] = -1;
        }

        for (int y = 0; y < h - 1; y++) {
            for (int x = 0; x < w; x++) {
                double cur = distTo[y][x];
                if (cur == Double.POSITIVE_INFINITY) continue;

                for (int dx = -1; dx <= 1; dx++) {
                    int nx = x + dx;
                    int ny = y + 1;
                    if (nx < 0 || nx >= w) continue;
                    double cand = cur + energy[ny][nx];
                    if (cand < distTo[ny][nx]) {
                        distTo[ny][nx] = cand;
                        edgeTo[ny][nx] = x;
                    }
                }
            }
        }

        // find min in bottom row
        double best = Double.POSITIVE_INFINITY;
        int endx = 0;
        for (int x = 0; x < w; x++) {
            if (distTo[h - 1][x] < best) {
                best = distTo[h - 1][x];
                endx = x;
            }
        }

        int[] seam = new int[h];
        int x = endx;
        for (int y = h - 1; y >= 0; y--) {
            seam[y] = x;
            x = edgeTo[y][x];
        }
        return seam;
    }

    public void removeHorizontalSeam(int[] seam) {
        if (seam == null) throw new IllegalArgumentException("Seam is null");
        if (height() <= 1) throw new IllegalArgumentException("Height <= 1");
        if (seam.length != width()) throw new IllegalArgumentException("Wrong seam length");

        // validate seam (bounds + adjacency)
        for (int x = 0; x < width(); x++) {
            int y = seam[x];
            if (y < 0 || y >= height())
                throw new IllegalArgumentException("Seam entry out of range");
            if (x > 0 && Math.abs(seam[x] - seam[x - 1]) > 1)
                throw new IllegalArgumentException("Adjacent seam entries differ by > 1");
        }

        // reuse vertical removal via transpose
        transpose();
        removeVerticalSeam(seam);
        transpose();
    }

    public void removeVerticalSeam(int[] seam) {
        if (seam == null) throw new IllegalArgumentException("Seam is null");
        if (width() <= 1) throw new IllegalArgumentException("Width <= 1");
        if (seam.length != height()) throw new IllegalArgumentException("Wrong seam length");

        // validate seam (bounds + adjacency)
        for (int y = 0; y < height(); y++) {
            int x = seam[y];
            if (x < 0 || x >= width())
                throw new IllegalArgumentException("Seam entry out of range");
            if (y > 0 && Math.abs(seam[y] - seam[y - 1]) > 1)
                throw new IllegalArgumentException("Adjacent seam entries differ by > 1");
        }

        Picture out = new Picture(width() - 1, height());
        for (int y = 0; y < height(); y++) {
            int rx = seam[y];
            int nx = 0;
            for (int x = 0; x < width(); x++) {
                if (x == rx) continue;
                out.set(nx++, y, picture.get(x, y)); // set uses Color
            }
        }
        picture = out;
        computeEnergy();
    }

    // transpose picture (and recompute energy)
    private void transpose() {
        Picture t = new Picture(height(), width());
        for (int y = 0; y < height(); y++)
            for (int x = 0; x < width(); x++)
                t.set(y, x, picture.get(x, y));
        picture = t;
        computeEnergy();
    }

    // optional test client
    public static void main(String[] args) { }
}
