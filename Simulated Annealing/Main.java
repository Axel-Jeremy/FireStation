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

        double starting_temp = Double.parseDouble(args[0]);
        double cooling_rate = Double.parseDouble(args[1]);
        double stopping_temp = Double.parseDouble(args[2]);
        double stepSize = Double.parseDouble(args[3]);
        int runs = Integer.parseInt(args[4]);
        int i = 1;

        Random init = new Random(); // random generator untuk membuat seed
        long seed = init.nextLong() % 1000; // simpan seed sebagai seed untuk random generator
        Random gen = new Random(seed); // random generator Hill Climbing

        MySA sa = new MySA(seed, houseLocations, p, h, map);

        StationLocation[] bestState = MySA.generateRandomCoordinates();
        double bestF = sa.f(bestState);

        while (i++ <= runs) { // lakukan sebanyak runs kali
            System.out.printf("Run %d\n", i - 1);
            // hasil SA terbaik
            StationLocation[] currentState = sa.simulatedAnnealing(starting_temp, cooling_rate,
                    stopping_temp, stepSize);
            double currentF = sa.f(currentState); // hitung f(x) dari hasil SA
            System.out.printf("Simulated Annealing result:\n");
            System.out.printf("Current f = %.5f\n", ((1.0 * currentF) / (1.0 * h)));

            System.out.println("Current fire station coordinates (x, y):");
            for (int k = 0; k < currentState.length; k++) {
                System.out.printf("Firestation #%d : %s",k+1, currentState[k]);
            }
            // for (int k = 0; k < currentState.length; k++) {
            // System.out.print("(");
            // for (int j = 0; j < currentState.length; j++) {
            // System.out.print(currentState[k].getX() + ", " + currentState[k].getY());
            // }
            // System.out.println(")");
            // }
            System.out.println("----------------------------------------------------------");

            if (currentF < bestF) { // simpan f(x) terbaik;
                bestF = currentF;
                bestState = currentState;
            }
        }
        System.out.println("Seed: " + seed);
        System.out.println("======================================");
        System.out.printf("Simulated Annealing BEST:\n");
        System.out.printf("Best f = %.5f\n", ((1.0 * bestF) / (1.0 * h)));

        System.out.println("Best fire station coordinates (x, y):");
        for (int k = 0; k < bestState.length; k++) {
                System.out.printf("Firestation #%d : %s",k+1, bestState[k]);
        }
        // for (int k = 0; k < bestState.length; k++) {
        // System.out.print("(");
        // for (int j = 0; j < bestState.length; j++) {
        // System.out.print(bestState[k].getX() + ", " + bestState[k].getY());
        // }
        // System.out.println(")");
        // }
        System.out.println("----------------------------------------------------------");
    }
}