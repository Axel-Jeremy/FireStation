import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class MyHC {
    static final Random rnd = new Random();

    // peta input, berisi posisi rumah dan pohon
    static int[][] map; // 0: jalan kosong, 1: rumah, 2: pohon

    static int banyakFireStation; // banyak firestation dari input (p)
    static int banyakRumah; // banyak rumah dari input (h)

    // Direction vectors, untuk bfs
    static int dRow[] = { -1, 0, 1, 0 };
    static int dCol[] = { 0, 1, 0, -1 };

    // objective function (maximizing)
    // itung jarak terdekat (shortest path)
    // dari setiap rumah ke firestation terdekat, tambahin ke totalcost
    static double f(StationLocation[] fireStation) {
        int totalCost = 0;

        // loop cari rumah
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                if (map[i][j] == 1) { // kalo ketemu rumah, bfs ke firestation terdekat

                    int cost = shortestPath(i, j, fireStation); // jarak terdekat rumah ke salah 1 firestation
                    if (cost == -1)
                        return Integer.MAX_VALUE; // kalo -1 berarti ga ada path dari rumah ke firestation manapun

                    totalCost += cost; // tambahin cost setiap rumah
                }
            }
        }
        return totalCost; // return total jarak dari setiap rumah ke firestation terdekat
    }

    // Function cek masih dalam batas length,
    // dan not already visited
    static boolean isValid(int row, int col, boolean[][] visited) {
        return row >= 0 && row < map.length &&
                col >= 0 && col < map[0].length &&
                !visited[row][col] &&
                map[row][col] == 0; /// 0 = jalan kosong yang bisa dilewati
    }

    // BFS
    // dari rumah koordinat (xRow, yCol) ke firestation terdekat
    static int shortestPath(int xRow, int yCol, StationLocation[] fireStation) {
        int n = map.length;
        int m = map[0].length;

        // Direction vectors buat gerak up, down, left, right
        int[] dRow = { -1, 1, 0, 0 };
        int[] dCol = { 0, 0, -1, 1 };

        // Visited matrix: penanda jalan yang udah pernah dilewatin
        boolean[][] visited = new boolean[map.length][map[0].length];

        // Queue to perform BFS: stores {row, col, distance}
        Queue<Coordinate> q = new LinkedList<>();

        q.offer(new Coordinate(xRow, yCol, 0));
        visited[xRow][yCol] = true;

        // Standard BFS
        while (!q.isEmpty()) {

            Coordinate curr = q.poll();

            int row = curr.getX();
            int col = curr.getY();
            int dist = curr.getDistance();

            if (!notChosenYet(row, col, fireStation)) { // kalo sampe firestation, return distancenya
                return dist;
            }

            // Explore all four adjacent directions pake direction vectors
            for (int i = 0; i < 4; i++) {
                int newRow = row + dRow[i];
                int newCol = col + dCol[i];

                // cek sel baru valid ato ga
                if (isValid(newRow, newCol, visited)) { // kalo valid (ga melebihi batas dan bisa dilewatin)

                    // Mark the new cell as visited
                    visited[newRow][newCol] = true;

                    // Push dengan distance = distance sekarang +1
                    q.offer(new Coordinate(newRow, newCol, dist + 1));
                }
            }
        }
        return -1; // ga ada path dari rumah ke firestation manapun
    }

    // Mencari neighbor state di antara [-stepSize, stepSize]
    static StationLocation[] getNeighbor(int x, int y, double stepSize) {
        StationLocation[] neighborCoordinates = new StationLocation[4]; // array neighbor state, 4 karena neighbor hanya
                                                                        // bisa atas bawah
        // kiri kanan
        for (int i = 0; i < neighborCoordinates.length; i++)
            neighborCoordinates[i] = new StationLocation(-1, -1);

        for (int i = 0; i < 4; i++) {
            int stepX = (int) (dRow[i] * stepSize);
            int stepY = (int) (dCol[i] * stepSize);

            if (stepX + x < map.length && stepY + y < map[0].length) { // cek kalo x dan y sekarang kalo ditambah step
                                                                       // masih dalam jangkauan
                neighborCoordinates[i].setX(x + (int) (dRow[i] * stepSize));
                neighborCoordinates[i].setY(y + (int) (dCol[i] * stepSize));
            }
        }
        return neighborCoordinates; // return neighbor states atas bawah kiri kanan
    }

    // generate random koordinat firestation
    static StationLocation[] generateRandomCoordinates() {
        StationLocation[] stationCoordinates = new StationLocation[banyakFireStation]; // array buat simpen koordinat
                                                                                       // random n firestation

        for (int i = 0; i < stationCoordinates.length; i++)
            stationCoordinates[i] = new StationLocation(-1, -1);
        // Arrays.fill(stationCoordinates, new StationLocation(-1, -1));
        
        // contoh struktur array stationCoordinates yang dibuat
        // [x1][y1] - coord firestation1
        // [x2][y2] - coord firestation2

        int x;
        int y;
        for (int i = 0; i < banyakFireStation; i++) {
            x = rnd.nextInt(map.length); // random x
            y = rnd.nextInt(map[0].length); // random y

            // cek valid & belum dipilih
            while (!isValidCoordinate(x, y) && !notChosenYet(x, y, stationCoordinates)) { // selama belum valid dan
                                                                                          // sudah dipilih, random lagi
                // terus random koordinat sampe nemu yang valid & belom dipilih
                x = rnd.nextInt(map.length);
                y = rnd.nextInt(map[0].length);
            }

            // simpen x dan y yang valid
            stationCoordinates[i].setX(x);
            stationCoordinates[i].setY(y);
        }
        return stationCoordinates; // return koordinat random firestation
    }

    static boolean isValidCoordinate(int x, int y) { // cek x dan y
        if (map[x][y] == 0) // kalo jalan kosong (bukan pohon/rumah)
            return true; // valid
        return false; // else, tidak valid
    }

    static boolean notChosenYet(int x, int y, StationLocation[] neighborCoordinates) { // cek apakah x dan y udah
                                                                                       // dipilih
        for (int i = 0; i < neighborCoordinates.length; i++) {
            if (x == neighborCoordinates[i].getX()
                    && y == neighborCoordinates[i].getY())
                return false;
        }
        return true; // x dan y belum dipilih
    }

    static boolean isNotOutOfBound(StationLocation station) { // [x][y] cek masih dalam jangkauan map
        int x = station.getX();
        int y = station.getY();

        return x >= 0 && x < map.length && y >= 0 && y < map[0].length;
    }

    /**
     * Hill Climbing
     * 
     * @param stepSize berapa jauh "lompat" ke tetangga
     * @param maxIter  iterasi maksimum
     * @return x dengan f(x) terbesar
     */
    static StationLocation[] hillClimbing(double stepSize, int maxIter) {
        StationLocation[] randPos = generateRandomCoordinates(); // posisi awal: random
        // System.out.printf("Initial posistion: %.6f\n", randPos);

        StationLocation[] bestState = randPos;
        double bestF = f(bestState); // hitung f(x)-nya

        for (int it = 1; it <= maxIter; it++) { // lakukan sampai maxIter
            int randomIdx = rnd.nextInt(banyakFireStation); // dari banyak firestation, pilih 1 random
            StationLocation[] neighborStates = getNeighbor(bestState[randomIdx].getX(), bestState[randomIdx].getY(),
                    stepSize);

            // buat neighbor state-nya
            StationLocation[] topNeighborStates = null;
            double topF = Integer.MAX_VALUE;
            if (isNotOutOfBound(neighborStates[0])) {
                topNeighborStates = bestState;
                topNeighborStates[randomIdx].setX(neighborStates[0].getX());
                topNeighborStates[randomIdx].setY(neighborStates[0].getY());
                topF = f(topNeighborStates);
            }

            StationLocation[] rightNeighborStates = null;
            double rightF = Integer.MAX_VALUE;
            if (isNotOutOfBound(neighborStates[1])) {
                rightNeighborStates = bestState;
                rightNeighborStates[randomIdx].setX(neighborStates[1].getX());
                rightNeighborStates[randomIdx].setY(neighborStates[1].getY());
                rightF = f(rightNeighborStates);
            }

            StationLocation[] bottomNeighborStates = null;
            double bottomF = Integer.MAX_VALUE;
            if (isNotOutOfBound(neighborStates[2])) {
                bottomNeighborStates = bestState;
                bottomNeighborStates[randomIdx].setX(neighborStates[2].getX());
                bottomNeighborStates[randomIdx].setY(neighborStates[2].getY());
                bottomF = f(bottomNeighborStates);
            }

            StationLocation[] leftNeighborStates = null;
            double leftF = Integer.MAX_VALUE;
            if (isNotOutOfBound(neighborStates[3])) {
                leftNeighborStates = bestState;
                leftNeighborStates[randomIdx].setX(neighborStates[3].getX());
                leftNeighborStates[randomIdx].setY(neighborStates[3].getY());
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

            double minF = Math.min(Math.min(topF, rightF), Math.min(bottomF, leftF)); // ambil yang paling kecil

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
            // Jika tidak ada tetangga yang lebih baik
            else {
                stepSize = stepSize * 0.5; // kurangi stepsize
            }
        }
        return bestState; // return state terbaik dari hasil hill climbing
    }

    /**
     * random restart hill climbing
     * 
     * @param nRestarts berapa kali melakukan hill climbing
     * @param step      berapa jauh "lompat" ke tetangga
     * @param runs      iterasi maksimum
     * @return x dengan f(x) terbesar
     */
    static StationLocation[] randomRestartHC(int nRestarts, double step, int iter) {
        StationLocation[] bestState = new StationLocation[banyakFireStation];
        // Arrays.fill(bestState, new StationLocation(-1, -1));

        for (int i = 0; i < bestState.length; i++)
            bestState[i] = new StationLocation(-1, -1);

        double bestF = Integer.MAX_VALUE; // hitung f(x)-nya

        for (int r = 1; r <= nRestarts; r++) { // ulangi nRestarts kali
            StationLocation[] bestCurrentState = hillClimbing(step, iter); // state terbaik hasil HC
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

    // public static void main(String[] args) {
    //     Scanner sc;
    //     int n = 0;
    //     int m = 0;
    //     int p = 0;
    //     int h = 0;
    //     int t = 0;
    //     boolean[][] visited;

    //     try { // input dari file input.txt
    //         sc = new Scanner(new File("input.txt"));

    //         // ukuran peta
    //         n = sc.nextInt();
    //         m = sc.nextInt();

    //         map = new int[m][n];
    //         visited = new boolean[m][n];

    //         // banyak fire station
    //         p = sc.nextInt();

    //         // banyak rumah
    //         h = sc.nextInt();

    //         // banyak pohon
    //         t = sc.nextInt();

    //         // input koordinat rumah
    //         for (int i = 0; i < h; i++) {
    //             int x = sc.nextInt();
    //             int y = sc.nextInt();
    //             map[m - y][x - 1] = 1;
    //         }

    //         // input koordinat pohon
    //         for (int i = 0; i < t; i++) {
    //             int x = sc.nextInt();
    //             int y = sc.nextInt();
    //             map[m - y][x - 1] = 2;
    //         }
    //         sc.close();
    //     } catch (FileNotFoundException e) {
    //         e.printStackTrace();
    //     }

    //     System.out.println("------------------");

    //     banyakFireStation = p;
    //     banyakRumah = h;
    //     StationLocation[] bestState = randomRestartHC(1000, 10.0, Integer.parseInt(args[0]));

    //     System.out.println("Best all fire station coordinates (x, y):");
    //     for (int i = 0; i < bestState.length; i++) {
    //         System.out.print(bestState[i]);
    //     }
    // }
}