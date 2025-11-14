import java.util.*;
import java.io.File;
import java.io.FileNotFoundException;

public class Main {
    public static void main(String[] args) {
        Scanner sc;
        // Ukuran Peta n*m
        int n = 0;
        int m = 0;

        int p = 0; // Banyak Fire Station
        int h = 0; // Banyak Rumah
        int t = 0; // Banyak pohon
        boolean[][] visited;
        int[][] map = null;
        List<Coordinate> houseLocations = new ArrayList<>();

        try {
            // input dari file input.txt
            sc = new Scanner(new File("input_large.txt"));

            // ukuran peta
            n = sc.nextInt();
            m = sc.nextInt();
            map = new int[m][n];
            visited = new boolean[m][n];

            // banyak fire station
            p = sc.nextInt();

            // banyak rumah
            h = sc.nextInt();

            // banyak pohon
            t = sc.nextInt();

            // input koordinat rumah
            for (int i = 0; i < h; i++) {
                int x = sc.nextInt();
                int y = sc.nextInt();
                map[m - y][x - 1] = 1;
                houseLocations.add(new Coordinate(m - y, x - 1));
            }

            // input koordinat pohon
            for (int i = 0; i < t; i++) {
                int x = sc.nextInt();
                int y = sc.nextInt();
                map[m - y][x - 1] = 2;
            }
            sc.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        System.out.println("------------------");

        Random init = new Random(); // random generator untuk membuat seed
        long seed = init.nextLong() % 1000; // simpan seed sebagai seed untuk random generator
        Random gen = new Random(seed); // random generator Hill Climbing

        MyHC hillClimbing = new MyHC(seed, houseLocations, p, h, map);

        StationLocation[] bestState = hillClimbing.randomRestartHC(1000, 5.0, Integer.parseInt(args[0]));

        System.out.println("Seed: " + seed);
        System.out.println("======================================");
        System.out.println("Best Fire Station Coordinates (x, y):");
        for (int i = 0; i < bestState.length; i++) {
            System.out.printf("Firestation #%d : %s",i+1, bestState[i]);
        }
    }
}