public class ResizeDemo {
    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("Usage: java ResizeDemo <imagefile> <removeCols> <removeRows>");
            return;
        }
        edu.princeton.cs.algs4.Picture input = new edu.princeton.cs.algs4.Picture(args[0]);
        int removeCols = Integer.parseInt(args[1]);
        int removeRows = Integer.parseInt(args[2]);

        SeamCarver sc = new SeamCarver(input);

        for (int i = 0; i < removeCols; i++) sc.removeVerticalSeam(sc.findVerticalSeam());
        for (int i = 0; i < removeRows; i++)   sc.removeHorizontalSeam(sc.findHorizontalSeam());

        sc.picture().show(); // display result
    }
}
