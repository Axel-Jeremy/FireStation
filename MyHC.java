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
                    int cost = shortestPath(i, j, fireStation);
                    if (cost == -1) return Integer.MAX_VALUE;

                    totalCost += cost;
                }
            }
        }
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
        return -1;
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
            y = rnd.nextInt(map[0].length);

            while (!isValidCoordinate(x, y) && !notChosenYet(x, y, stationCoordinates)) {
                x = rnd.nextInt(map.length);
                y = rnd.nextInt(map[0].length);
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

    private boolean isNotOutOfBound(int[] arr) { // [x][y]
        int x = arr[0];
        int y = arr[1];

        return x >= map.length && x < 0 && y >= map[0].length && y < 0;
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
            int[][] topNeighborStates = null;
            double topF = Integer.MAX_VALUE;
            if (isNotOutOfBound(neighborStates[0])) {
                topNeighborStates = randPos;
                topNeighborStates[randomIdx][0] = neighborStates[0][0];
                topNeighborStates[randomIdx][1] = neighborStates[0][1];
                topF = f(topNeighborStates);
            }

            int[][] rightNeighborStates = null;
            double rightF = Integer.MAX_VALUE;
            if (isNotOutOfBound(neighborStates[1])) {
                rightNeighborStates = randPos;
                rightNeighborStates[randomIdx][0] = neighborStates[1][0];
                rightNeighborStates[randomIdx][1] = neighborStates[1][1];
                rightF = f(rightNeighborStates);
            }

            int[][] bottomNeighborStates = null;
            double bottomF = Integer.MAX_VALUE;
            if (isNotOutOfBound(neighborStates[2])) {
                bottomNeighborStates = randPos;
                bottomNeighborStates[randomIdx][0] = neighborStates[2][0];
                bottomNeighborStates[randomIdx][1] = neighborStates[2][1];
                bottomF = f(bottomNeighborStates);
            }

            int[][] leftNeighborStates = null;
            double leftF = Integer.MAX_VALUE;
            if (isNotOutOfBound(neighborStates[3])) {
                leftNeighborStates = randPos;
                leftNeighborStates[randomIdx][0] = neighborStates[3][0];
                leftNeighborStates[randomIdx][1] = neighborStates[3][1];
                leftF = f(leftNeighborStates);
            }

            if (topF < 0)
                topF = Integer.MAX_VALUE;
            if (rightF < 0)
                rightF = Integer.MAX_VALUE;
            if (bottomF < 0)
                bottomF = Integer.MAX_VALUE;
            if (leftF < 0)
                leftF = Integer.MAX_VALUE;

            double minF = Math.min(Math.min(topF, rightF), Math.min(bottomF, leftF));

            // jika ada tetangga yang lebih baik, pindah ke tetangga tersebut
            if (minF < bestF) {
                bestF = minF;
                if (topF == minF) {
                    bestState = topNeighborStates;
                } else if (rightF == minF) {
                    bestState = rightNeighborStates;
                } else if (bottomF == minF) {
                    bestState = bottomNeighborStates;
                } else {
                    bestState = leftNeighborStates;
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
            System.out.println("currentF: " + currentF);
            if (currentF < bestF) { // simpan f(x) terbaik;
                bestF = currentF;
                bestState = bestCurrentState;
            }
        }
        System.out.println("best:" + bestF);
        System.out.printf("p: %d average: %.5f\n", banyakFireStation, ((1.0 * bestF) / (1.0 * banyakRumah)));
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
        int[][] bestState = rrhc.randomRestartHC(1000, 10.0, 100);

        for (int i = 0; i < bestState.length; i++) {
            for (int j = 0; j < bestState[i].length; j++) {
                System.out.print(bestState[i][j] + " ");
            }
            System.out.println();
        }
    }
}