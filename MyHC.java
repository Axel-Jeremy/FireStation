import java.util.*;

public class MyHC {
    static final Random rnd = new Random();
    static int MIN = Integer.MAX_VALUE; //
    // private int[][] firestationCoordinates;
    static int[][] map;
    private int banyakFireStation;
    private int banyakRumah;
    // Direction vectors
    static int dRow[] = { -1, 0, 1, 0 };
    static int dCol[] = { 0, 1, 0, -1 };

    public MyHC(int[][] map, int banyakFireStation, int banyakRumah) {
        MyHC.map = map;
        this.banyakFireStation = banyakFireStation;
        this.banyakRumah = banyakRumah;
    }

    // objective function (maximizing)
    // itung jarak terdekat (shortest path) dari setiap rumah ke firestation
    // terdekat, tambahin
    private double f(int[][] fireStation) {
        int totalCost = 0;

        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                if (map[i][j] == 1) {
                    // do bfs
                    totalCost += shortestPath(i, j, fireStation);
                }
            }
        }

        // for (int i = 0; i < banyakRumah; i++) {
        // // do bfs
        // int cost = 0;
        // totalCost += cost;
        // }
        System.out.println(totalCost);
        return totalCost;
    }

    // Function to check if the given cell is within bounds,
    // and not already visited
    private boolean isValid(int row, int col, boolean[][] visited) {
        return row >= 0 && row < map.length &&
                col >= 0 && col < map[0].length &&
                !visited[row][col] &&
                map[row][col] == 0; /// 0 = kosong
    }

    // BFS function to find the shortest distance
    // from 's' to 'd' in the matrix
    private int shortestPath(int xRow, int yCol, int[][] fireStation) {
        int n = map.length;
        int m = map[0].length;

        // Direction vectors for moving: up, down, left, right
        int[] dRow = { -1, 1, 0, 0 };
        int[] dCol = { 0, 0, -1, 1 };

        // Visited matrix to keep track of explored cells
        boolean[][] visited = new boolean[map.length][map[0].length];

        // Queue to perform BFS: stores {row, col, distance}
        Queue<int[]> q = new LinkedList<>();

        // Find the source 's' in the matrix
        // and start BFS from it
        // for (int i = 0; i < n; i++) {
        // for (int j = 0; j < m; j++) {
        // if (map[i][j] == 1) {
        // q.offer(new int[] { i, j, 0 });
        // visited[i][j] = true;
        // break;
        // }
        // }
        // }

        q.offer(new int[] { xRow, yCol, 0 });
        visited[xRow][yCol] = true;

        // Standard BFS loop
        while (!q.isEmpty()) {

            int[] curr = q.poll();

            int row = curr[0];
            int col = curr[1];
            int dist = curr[2];

            if (!notChosenYet(row, col, fireStation)) { // kalo sampe firestation, return distancenya
                return dist;
            }

            // Explore all four adjacent directions
            for (int i = 0; i < 4; i++) {
                int newRow = row + dRow[i];
                int newCol = col + dCol[i];

                // If new cell is valid and can be visited
                if (isValid(newRow, newCol, visited)) {

                    // Mark the new cell as visited
                    visited[newRow][newCol] = true;

                    // Push the new cell with updated distance
                    q.offer(new int[] { newRow, newCol, dist + 1 });
                }
            }
        }

        // If no path to destination is found, return max value
        return Integer.MAX_VALUE;
    }

    // // pastikan x ada diantara MAX_X dan MIN_X;
    // static double clamp(double x) {
    // x = Math.max(MIN_X, x);
    // x = Math.min(x, MAX_X);
    // return x;
    // }

    // alternatif mencari neighbor state di antara [-stepSize, stepSize]
    private int[][] getNeighbor(int x, int y, double stepSize) {
        int[][] neighborCoordinates = new int[4][2];

        for (int i = 0; i < neighborCoordinates.length; i++) {
            Arrays.fill(neighborCoordinates[i], -1);
        }

        for (int i = 0; i < 4; i++) {
            int stepX = (int) (dRow[i] * stepSize);
            int stepY = (int) (dCol[i] * stepSize);

            if (stepX + x < map.length && stepY + y < map[0].length) {
                neighborCoordinates[i][0] = x + (int) (dRow[i] * stepSize);
                neighborCoordinates[i][1] = y + (int) (dCol[i] * stepSize);
            }
        }
        return neighborCoordinates;
    }

    // generate random coordinate buat koordinat si firestation
    private int[][] generateRandomCoordinates() {
        int[][] stationCoordinates = new int[banyakFireStation][2];
        for (int i = 0; i < stationCoordinates.length; i++) {
            Arrays.fill(stationCoordinates[i], -1);
        }
        // [x1][y1] - coord firestation1
        // [x2][y2] - coord firestation2

        int x;
        int y;
        for (int i = 0; i < banyakFireStation; i++) {
            x = rnd.nextInt(map.length);
            y = rnd.nextInt(map.length);

            while (!isValidCoordinate(x, y) && !notChosenYet(x, y, stationCoordinates)) {
                x = rnd.nextInt(map.length);
                y = rnd.nextInt(map.length);
            }

            stationCoordinates[i][0] = x;
            stationCoordinates[i][1] = y;
        }
        return stationCoordinates;
    }

    private boolean isValidCoordinate(int x, int y) {
        if (map[x][y] == 0)
            return true;
        return false;
    }

    private boolean notChosenYet(int x, int y, int[][] neighborCoordinates) {
        for (int i = 0; i < neighborCoordinates.length; i++) {
            if (x == neighborCoordinates[i][0] && y == neighborCoordinates[i][1])
                return false;
        }
        return true;
    }

    /**
     * Hill Climbing
     * 
     * @param stepSize berapa jauh "lompat" ke tetangga
     * @param maxIter  iterasi maksimum
     * @return x dengan f(x) terbesar
     */
    private int[][] hillClimbing(double stepSize, int maxIter) {
        int[][] randPos = generateRandomCoordinates(); // posisi awal: random
        // System.out.printf("Initial posistion: %.6f\n", randPos);

        int[][] bestState = randPos;
        double bestF = f(bestState); // hitung f(x)-nya

        for (int it = 1; it <= maxIter; it++) { // lakukan sampai maxIter
            int randomIdx = rnd.nextInt(banyakFireStation); // dari banyak firestation, pilih 1 random
            int[][] neighborStates = getNeighbor(randPos[randomIdx][0], randPos[randomIdx][1], stepSize);

            // buat neighbor state-nya --> bisa gunakan getNeighbor()
            int[][] topNeighborStates = { neighborStates[0] };
            randPos[randomIdx][0] = neighborStates[0][0];
            randPos[randomIdx][1] = neighborStates[0][1];

            double topF = f(randPos);

            int[][] rightNeighborStates = { neighborStates[1] };
            randPos[randomIdx][0] = neighborStates[1][0];
            randPos[randomIdx][1] = neighborStates[1][1];

            double rightF = f(randPos);

            int[][] bottomNeighborStates = { neighborStates[2] };
            randPos[randomIdx][0] = neighborStates[2][0];
            randPos[randomIdx][1] = neighborStates[2][1];

            double bottomF = f(randPos);

            int[][] leftNeighborStates = { neighborStates[3] };
            randPos[randomIdx][0] = neighborStates[3][0];
            randPos[randomIdx][1] = neighborStates[3][1];

            double leftF = f(randPos);

            // int[][] rightNeighborStates = { neighborStates[1] };
            // int[][] bottomNeighborStates = { neighborStates[2] };
            // int[][] leftNeighborStates = { neighborStates[3] };

            // double topF = f(topNeighborStates); // hitung f()-nya
            // double rightF = f(rightNeighborStates);
            // double bottomF = f(bottomNeighborStates);
            // double leftF = f(leftNeighborStates);

            double minF = Math.min(Math.min(topF, rightF), Math.min(bottomF, leftF));

            // jika ada tetangga yang lebih baik, pindah ke tetangga tersebut
            if (minF < bestF) {
                if (topF == minF) {
                    bestState = topNeighborStates;
                    bestF = topF;
                    randPos[randomIdx][0] = neighborStates[0][0];
                    randPos[randomIdx][1] = neighborStates[0][1];
                } else if (rightF == minF) {
                    bestState = rightNeighborStates;
                    bestF = rightF;
                    randPos[randomIdx][0] = neighborStates[0][0];
                    randPos[randomIdx][1] = neighborStates[0][1];
                } else if (bottomF == minF) {
                    bestState = bottomNeighborStates;
                    bestF = bottomF;
                    randPos[randomIdx][0] = neighborStates[0][0];
                    randPos[randomIdx][1] = neighborStates[0][1];
                } else {
                    bestState = leftNeighborStates;
                    bestF = leftF;
                    randPos[randomIdx][0] = neighborStates[0][0];
                    randPos[randomIdx][1] = neighborStates[0][1];
                }
            }
            // Jika tetangga tidak ada yang lebih baik
            else {
                stepSize = stepSize * 0.5; // kurangi stepsize
            }
        }
        return bestState;
    }

    /**
     * random restart hill climbing
     * 
     * @param nRestarts berapa kali melakukan hill climbing
     * @param step      berapa jauh "lompat" ke tetangga
     * @param runs      iterasi maksimum
     * @return x dengan f(x) terbesar
     */
    private int[][] randomRestartHC(int nRestarts, double step, int iter) {
        int[][] bestState = new int[banyakFireStation][2];
        for (int i = 0; i < bestState.length; i++) {
            Arrays.fill(bestState[i], -1);
        }

        double bestF = Integer.MAX_VALUE; // hitung f(x)-nya

        for (int r = 1; r <= nRestarts; r++) { // ulangi nRestarts kali
            int[][] bestCurrentState = hillClimbing(step, iter); // state terbaik hasil HC
            double currentF = f(bestCurrentState); // f(x)-nya
            if (currentF < bestF && currentF > 0) { // simpan f(x) terbaik;
                bestF = currentF;
                bestState = bestCurrentState;
            }
        }
        // System.out.println(bestF);
        System.out.printf("p: %d average: %.5f\n", banyakFireStation, (bestF / (1.0 * banyakFireStation)));
        return bestState;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // ukuran peta
        int n = sc.nextInt();
        int m = sc.nextInt();

        int[][] map = new int[m][n];
        boolean[][] visited = new boolean[m][n];

        // banyak fire station
        int p = sc.nextInt();

        // banyak rumah
        int h = sc.nextInt();

        // banyak pohon
        int t = sc.nextInt();

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

        System.out.println("------------------");

        MyHC rrhc = new MyHC(map, p, h);
        int[][] bestState = rrhc.randomRestartHC(20, 20.0, 10);

        for (int i = 0; i < bestState.length; i++) {
            for (int j = 0; j < bestState[i].length; j++) {
                System.out.print(bestState[i][j] + " ");
            }
            System.out.println();
        }
    }
}