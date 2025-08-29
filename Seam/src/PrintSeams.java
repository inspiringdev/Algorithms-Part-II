public class PrintSeams {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java PrintSeams <imagefile>");
            return;
        }
        edu.princeton.cs.algs4.Picture picture = new edu.princeton.cs.algs4.Picture(args[0]);
        SeamCarver sc = new SeamCarver(picture);

        int[] v = sc.findVerticalSeam();
        int[] h = sc.findHorizontalSeam();

        System.out.print("Vertical seam (x per y): ");
        for (int x : v) System.out.print(x + " ");
        System.out.println();

        System.out.print("Horizontal seam (y per x): ");
        for (int y : h) System.out.print(y + " ");
        System.out.println();
    }
}
