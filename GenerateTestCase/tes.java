import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;

public class tes {
    public static void main(String[] args) {
        Scanner sc;
        // Ukuran Peta n*m
        int n = 0;
        int m = 0;

        int p = 0; // Banyak Fire Station
        int h = 0; // Banyak Rumah
        int t = 0; // Banyak pohon
        boolean[][] visited;
        String[][] map;

        try {
            // input dari file input.txt
            sc = new Scanner(new File("input.txt"));

            // ukuran peta
            n = sc.nextInt();
            m = sc.nextInt();
            map = new String[m][n];

            for (String row[] : map) {
                Arrays.fill(row, String.format("%3s", "."));
            }
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
                map[m - y][x - 1] = String.format("%3s", "H");
            }

            // input koordinat pohon
            for (int i = 0; i < t; i++) {
                int x = sc.nextInt();
                int y = sc.nextInt();
                map[m - y][x - 1] = String.format("%3s", "T");
            }
            sc.close();

            for(int i = 0; i < map.length; i++){
                for(int j = 0; j < map[0].length; j++){
                    System.out.print(map[i][j] + " ");
                }System.out.println();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
