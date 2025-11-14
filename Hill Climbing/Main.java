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
        try {
            // input dari file input.txt
            sc = new Scanner(new File("input.txt"));

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


        MyHC hillClimbing = new MyHC();
        hillClimbing.banyakFireStation = p;
        hillClimbing.banyakRumah = h;
        hillClimbing.map = map;

        StationLocation[] bestState = hillClimbing.randomRestartHC(1000, 10.0, Integer.parseInt(args[0]));

        System.out.println("Best Fire Station Coordinates (x, y):");
        for (int i = 0; i < bestState.length; i++) {
            System.out.print(bestState[i]);
        }
    }
}