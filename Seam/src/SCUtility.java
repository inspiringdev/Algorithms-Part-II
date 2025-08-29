import edu.princeton.cs.algs4.Picture;
import java.awt.Color;

public class SCUtility {
    public static Picture toEnergyPicture(SeamCarver sc) {
        int w = sc.width();
        int h = sc.height();
        Picture pic = new Picture(w, h);

        double maxEnergy = 0.0;
        for (int y = 0; y < h; y++)
            for (int x = 0; x < w; x++)
                maxEnergy = Math.max(maxEnergy, sc.energy(x, y));

        if (maxEnergy == 0.0) maxEnergy = 1.0; // avoid divide-by-zero

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int gray = (int) Math.round(255.0 * sc.energy(x, y) / maxEnergy);
                if (gray < 0) gray = 0;
                if (gray > 255) gray = 255;
                pic.set(x, y, new Color(gray, gray, gray));
            }
        }
        return pic;
    }
}
