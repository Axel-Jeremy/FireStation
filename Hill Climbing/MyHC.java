import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class MyHC {
    static Random rnd;

    // peta input, berisi posisi rumah dan pohon
    static int[][] map; // 0: jalan kosong, 1: rumah, 2: pohon

    static int banyakFireStation; // banyak firestation dari input (p)
    static int banyakRumah; // banyak rumah dari input (h)
    static List<Coordinate> houseLocations = new ArrayList<>();
    // Direction vectors, untuk bfs
    static int dRow[] = { -1, 0, 1, 0 };
    static int dCol[] = { 0, 1, 0, -1 };

    public MyHC(long seed, List<Coordinate> houseLocations, int banyakFireStation, int banyakRumah, int[][] map){
        rnd = new Random(seed);
        this.houseLocations = houseLocations;
        this.banyakFireStation = banyakFireStation;
        this.banyakRumah = banyakRumah;
        this.map = map;
    }

    // objective function (minimizing)
    // itung jarak terdekat (shortest path) dari setiap firestation ke rumah
    private double f(StationLocation[] fireStation) {
        int m = map.length;
        int n = map[0].length;

        // Peta jarak, diisi dengan tak hingga
        int[][] dist = new int[m][n];
        for (int[] row : dist) {
            Arrays.fill(row, Integer.MAX_VALUE);
        }

        // Queue Multi-Source BFS
        Queue<Coordinate> q = new LinkedList<>();

        // Tambahkan semua stasiun sebagai sumber
        for (StationLocation station : fireStation) {
            int r = station.getX();
            int c = station.getY();

            // Pastikan stasiun valid (dalam peta dan bukan di pohon)
            // Stasiun bisa di jalan (0) atau di rumah (1)
            if (isValid(r, c)) {
                if (dist[r][c] == Integer.MAX_VALUE) { // Hindari duplikat jika 2 stasiun di 1 titik
                    dist[r][c] = 0;
                    q.offer(new Coordinate(r, c, 0));
                }
            }
        }

        // BFS (hanya di jalan, sel '0')
        while (!q.isEmpty()) {
            Coordinate curr = q.poll();
            int r = curr.getX();
            int c = curr.getY();
            int d = curr.getDistance();

            for (int i = 0; i < 4; i++) {
                int newRow = r + dRow[i];
                int newCol = c + dCol[i];

                // Cek di map, cuma jalan (0), dan belum dikunjungi (dist masih MAX)
                if (isValid(newRow, newCol)
                        && dist[newRow][newCol] == Integer.MAX_VALUE) {
                    dist[newRow][newCol] = d + 1;
                    q.offer(new Coordinate(newRow, newCol, d + 1));
                }
            }
        }

        // itung total biaya dari rumah yang sudah disimpan
        int totalCost = 0;
        for (Coordinate house : houseLocations) {
            int r = house.getX();
            int c = house.getY();

            // Ambil jarak langsung ke sel rumah,
            // karena BFS kita sekarang bisa berjalan di atas rumah (1)
            int costToThisHouse = dist[r][c];

            // Cek jika rumah ini benar-benar terisolasi (dikelilingi pohon)
            if (costToThisHouse == Integer.MAX_VALUE) {
                return Integer.MAX_VALUE; // State tidak valid, beri penalti tertinggi
            }

            // Jika stasiun di atas rumah, costToThisHouse == 0
            // Jika stasiun 5 langkah, costToThisHouse == 5
            // Tidak perlu +1, karena jaraknya sudah dihitung ke sel rumah
            totalCost += costToThisHouse;
        }

        return totalCost;
    }

    // Function cek masih dalam batas length dan (row,col) adalah jalan kosong
    private boolean isValid(int row, int col) {
        return row >= 0 && row < map.length
                && col >= 0 && col < map[0].length
                && map[row][col] != 2;
    }

    // Mencari neighbor state di antara [-stepSize, stepSize]
    private StationLocation[] getNeighbor(int x, int y, double stepSize) {
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
    private StationLocation[] generateRandomCoordinates() {
        // array buat simpen koordinat random sebanyak n firestation
        StationLocation[] stationCoordinates = new StationLocation[banyakFireStation]; 

        for (int i = 0; i < stationCoordinates.length; i++)
            stationCoordinates[i] = new StationLocation(-1, -1);

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

    private boolean isValidCoordinate(int x, int y) { // cek x dan y
        if (map[x][y] != 2) // kalo jalan kosong (bukan pohon/rumah)
            return true; // valid
        return false; // else, tidak valid
    }

    private boolean notChosenYet(int x, int y, StationLocation[] neighborCoordinates) { // cek apakah x dan y udah
                                                                                       // dipilih
        for (int i = 0; i < neighborCoordinates.length; i++) {
            if (x == neighborCoordinates[i].getX()
                    && y == neighborCoordinates[i].getY())
                return false;
        }
        return true; // x dan y belum dipilih
    }

    private boolean isNotOutOfBound(StationLocation station) { // [x][y] cek masih dalam jangkauan map
        int x = station.getX();
        int y = station.getY();

        return x >= 0 && x < map.length && y >= 0 && y < map[0].length;
    }

    static StationLocation[] deepCopy(StationLocation[] original) {
        StationLocation[] copy = new StationLocation[original.length];
        for (int i = 0; i < original.length; i++) {
            copy[i] = new StationLocation(original[i].getX(), original[i].getY());
        }
        return copy;
    }

    /**
     * Hill Climbing
     * 
     * @param stepSize berapa jauh "lompat" ke tetangga
     * @param maxIter  iterasi maksimum
     * @return x dengan f(x) terbesar
     */
    private StationLocation[] hillClimbing(double stepSize, int maxIter) {
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
                topNeighborStates = deepCopy(bestState);
                topNeighborStates[randomIdx].setX(neighborStates[0].getX());
                topNeighborStates[randomIdx].setY(neighborStates[0].getY());
                topF = f(topNeighborStates);
            }

            StationLocation[] rightNeighborStates = null;
            double rightF = Integer.MAX_VALUE;
            if (isNotOutOfBound(neighborStates[1])) {
                rightNeighborStates = deepCopy(bestState);
                rightNeighborStates[randomIdx].setX(neighborStates[1].getX());
                rightNeighborStates[randomIdx].setY(neighborStates[1].getY());
                rightF = f(rightNeighborStates);
            }

            StationLocation[] bottomNeighborStates = null;
            double bottomF = Integer.MAX_VALUE;
            if (isNotOutOfBound(neighborStates[2])) {
                bottomNeighborStates = deepCopy(bestState);
                bottomNeighborStates[randomIdx].setX(neighborStates[2].getX());
                bottomNeighborStates[randomIdx].setY(neighborStates[2].getY());
                bottomF = f(bottomNeighborStates);
            }

            StationLocation[] leftNeighborStates = null;
            double leftF = Integer.MAX_VALUE;
            if (isNotOutOfBound(neighborStates[3])) {
                leftNeighborStates = deepCopy(bestState);
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

            // ambil yang paling kecil
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
    public StationLocation[] randomRestartHC(int nRestarts, double step, int iter) {
        StationLocation[] bestState = new StationLocation[banyakFireStation];

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
        System.out.println("======================================");
        System.out.println("best:" + bestF);
        System.out.printf("p: %d average: %.5f\n", banyakFireStation, ((1.0 * bestF) / (1.0 * banyakRumah)));
        System.out.println("======================================");
        return bestState;
    }
}

// =========================================================================================
// public static void main(String[] args) {
// Scanner sc;
// int n = 0;
// int m = 0;
// int p = 0;
// int h = 0;
// int t = 0;
// boolean[][] visited;

// try { // input dari file input.txt
// sc = new Scanner(new File("input.txt"));

// // ukuran peta
// n = sc.nextInt();
// m = sc.nextInt();

// map = new int[m][n];
// visited = new boolean[m][n];

// // banyak fire station
// p = sc.nextInt();

// // banyak rumah
// h = sc.nextInt();

// // banyak pohon
// t = sc.nextInt();

// // input koordinat rumah
// for (int i = 0; i < h; i++) {
// int x = sc.nextInt();
// int y = sc.nextInt();
// map[m - y][x - 1] = 1;
// houseLocations.add(new Coordinate(m - y, x - 1));
// }

// // input koordinat pohon
// for (int i = 0; i < t; i++) {
// int x = sc.nextInt();
// int y = sc.nextInt();
// map[m - y][x - 1] = 2;
// }
// sc.close();
// } catch (FileNotFoundException e) {
// e.printStackTrace();
// }

// System.out.println("------------------");

// banyakFireStation = p;
// banyakRumah = h;
// StationLocation[] bestState = randomRestartHC(1000, 10.0,
// Integer.parseInt(args[0]));

// System.out.println("Best all fire station coordinates (x, y):");
// for (int i = 0; i < bestState.length; i++) {
// System.out.print(bestState[i]);
// }
// }