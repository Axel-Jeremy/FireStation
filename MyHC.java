import java.util.*;

public class MyHC {
    static final Random rnd = new Random();
    static int[][] map;
    // Direction vectors
    static int dRow[] = { -1, 0, 1, 0 };
    static int dCol[] = { 0, 1, 0, -1 };

    public MyHC(int[][] map) {
        MyHC.map = map;
    }

    // objective function (maximizing)
    // itung jarak terdekat (shortest path) dari setiap rumah ke firestation
    // terdekat, tambahin
    static double f(double x) {
        int totalCost = 0;
        for (int i = 0; i < banyakRumah; i++) {
            // do bfs
            int cost;

            totalCost += cost;
        }

        return 0;
    }

    // Function to check if the given cell is within bounds,
    // not a blocked cell ('0'), and not already visited
    static boolean isValid(int row, int col, int n, int m, char[][] mat, boolean[][] visited) {
        return row >= 0 && row < n &&
                col >= 0 && col < m &&
                mat[row][col] != '0' &&
                !visited[row][col];
    }

    // BFS function to find the shortest distance
    // from 's' to 'd' in the matrix
    static int shortestPath(int[][] mat) {

        int n = mat.length;
        int m = mat[0].length;

        // Direction vectors for moving: up, down, left, right
        int[] dRow = { -1, 1, 0, 0 };
        int[] dCol = { 0, 0, -1, 1 };

        // Visited matrix to keep track of explored cells
        boolean[][] visited = new boolean[n][m];

        // Queue to perform BFS: stores {row, col, distance}
        Queue<int[]> q = new LinkedList<>();

        // Find the source 's' in the matrix
        // and start BFS from it
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {

                if (mat[i][j] == 1) {

                    q.offer(new int[] { i, j, 0 });

                    visited[i][j] = true;

                    break;
                }
            }
        }

        // Standard BFS loop
        while (!q.isEmpty()) {

            int[] curr = q.poll();

            int row = curr[0];
            int col = curr[1];
            int dist = curr[2];

            // If destination 'd' is reached, return the distance
            if (mat[row][col] == 3) {
                return dist;
            }

            // Explore all four adjacent directions
            for (int i = 0; i < 4; i++) {

                int newRow = row + dRow[i];
                int newCol = col + dCol[i];

                // If new cell is valid and can be visited
                if (isValid(newRow, newCol, n, m, mat, visited)) {

                    // Mark the new cell as visited
                    visited[newRow][newCol] = true;

                    // Push the new cell with updated distance
                    q.offer(new int[] { newRow, newCol, dist + 1 });
                }
            }
        }

        // If no path to destination is found, return -1
        return -1;
    }

    // pastikan x ada diantara MAX_X dan MIN_X;
    static double clamp(double x) {
        x = Math.max(MIN_X, x);
        x = Math.min(x, MAX_X);
        return x;
    }

    // alternatif mencari neighbor state di antara [-stepSize, stepSize]
    static int[][] getNeighbor(int x, int y) {
        int[][] neighborCoordinates = new int[4][2];
        for (int i = 0; i < 4; i++) {
            neighborCoordinates[i][0] = x + dRow[i];
            neighborCoordinates[i][1] = x + dCol[i];
        }
        return neighborCoordinates;
    }

    // generate random coordinate buat koordinat si firestation
    static int[][] generateRandomCoordinates(int nFirestation) {
        int[][] neighborCoordinates = new int[nFirestation][2];
        Arrays.fill(neighborCoordinates, -1);
        // [x1][y1] - coord firestation1
        // [x2][y2] - coord firestation2
        
        int x;
        int y;
        for (int i = 0; i < nFirestation; i++) {
            x = rnd.nextInt(map.length);
            y = rnd.nextInt(map.length);

            while (!isValidCoordinate(x, y) && !notChosenYet(x, y, neighborCoordinates)) {
                x = rnd.nextInt(map.length);
                y = rnd.nextInt(map.length);
            }

            neighborCoordinates[i][0] = x; 
            neighborCoordinates[i][1] = y; 
        }
        return neighborCoordinates;
    }

    static boolean isValidCoordinate(int x, int y) {
        if (map[x][y] == 0)
            return true;
        return false;
    }

    static boolean notChosenYet(int x, int y, int[][] neighborCoordinates){
        for(int i = 0; i < neighborCoordinates.length; i++){
            if(x == neighborCoordinates[i][0] && y == neighborCoordinates[i][1]) return false;
        }return true;
    }

    /**
     * Hill Climbing
     * 
     * @param stepSize berapa jauh "lompat" ke tetangga
     * @param maxIter  iterasi maksimum
     * @return x dengan f(x) terbesar
     */
    static double hillClimbing(double stepSize, int maxIter) {
        double randPos = MIN_X + (rnd.nextDouble() * (MAX_X - MIN_X)); // posisi awal: random
        System.out.printf("Initial posistion: %.6f\n", randPos);

        double bestX = clamp(randPos); // pastikan awalnya di antara MIN_X - MAX_X
        double bestF = f(bestX); // hitung f(x)-nya
        for (int it = 1; it <= maxIter; it++) { // lakukan sampai maxIter

            // buat neighbor state-nya --> bisa gunakan getNeighbor()
            double leftX = clamp(bestX - stepSize); // tetangga kiri
            double rightX = clamp(bestX + stepSize); // tetangga kanan
            double leftF = f(leftX); // hitung f()-nya
            double rightF = f(rightX);

            if (leftF > bestF) { // tetangga kiri lebih baik
                bestX = leftX;
                bestF = leftF;
            } else if (rightF > bestF) { // tetangga kanan lebih baik
                bestX = rightX;
                bestF = rightF;
            } else { // local maximum?
                stepSize = stepSize * 0.5; // kurangi step
            }
        }
        return bestX;
    }

    /**
     * random restart hill climbing
     * 
     * @param nRestarts berapa kali melakukan hill climbing
     * @param step      berapa jauh "lompat" ke tetangga
     * @param runs      iterasi maksimum
     * @return x dengan f(x) terbesar
     */
    static double randomRestartHC(int nRestarts, double step, int iter) {
        double bestX = MIN_X; // x terbaik saat ini
        double bestF = f(bestX); // f(x)-nya

        for (int r = 1; r <= nRestarts; r++) { // ulangi nRestarts kali
            System.out.printf("Run %d\n", r);
            double x = hillClimbing(step, iter); // x terbaik hasil HC
            double fx = f(x); // f(x)-nya
            System.out.printf("Hill Climbing result: best x=%.6f f(x)=%.6f%n", x, fx);
            System.out.println("----------------------------------------------");
            if (fx > bestF) { // simpan f(x) terbaik;
                bestF = fx;
                bestX = x;
            }
        }
        return bestX;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // ukuran peta
        int m = sc.nextInt();
        int n = sc.nextInt();

        int[][] grid = new int[m][n];
        boolean[][] visited = new boolean[m][n];

        // banyak fire station
        int p = sc.nextInt();

        // banyak rumah
        int h = sc.nextInt();

        // banyak pohon
        int t = sc.nextInt();

        // masukkan rumah
        for (int i = 0; i < h; i++) {
            int x = sc.nextInt();
            int y = sc.nextInt();
            grid[grid.length - y][x - 1] = 1;
        }

        // masukkan pohon
        for (int i = 0; i < t; i++) {
            int x = sc.nextInt();
            int y = sc.nextInt();
            grid[grid.length - y][x - 1] = 2;
        }

        sc.close();
    }
}