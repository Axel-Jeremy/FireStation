import java.util.*;

public class MySA {
    static Random rnd;

    // peta input, berisi posisi rumah dan pohon
    static int[][] map; // 0: jalan kosong, 1: rumah, 2: pohon

    static int banyakFireStation; // banyak firestation dari input (p)
    static int banyakRumah; // banyak rumah dari input (h)
    static List<Coordinate> houseLocations = new ArrayList<>();
    // Direction vectors, untuk bfs
    static int dRow[] = { -1, 0, 1, 0 };
    static int dCol[] = { 0, 1, 0, -1 };

    public MySA(long seed, List<Coordinate> houseLocations, int banyakFireStation, int banyakRumah, int[][] map) {
        rnd = new Random(seed);
        this.houseLocations = houseLocations;
        this.banyakFireStation = banyakFireStation;
        this.banyakRumah = banyakRumah;
        this.map = map;
    }

    // objective function (minimizing)
    // itung jarak terdekat (shortest path) dari setiap firestation ke rumah
    public double f(StationLocation[] fireStation) {
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

                // Cek di koordinat baru di map, dan belum dikunjungi (dist masih MAX)
                if (isValid(newRow, newCol)
                        && dist[newRow][newCol] == Integer.MAX_VALUE) {
                    dist[newRow][newCol] = d + 1;

                    if(map[newRow][newCol] == 0)
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

    static StationLocation[] deepCopy(StationLocation[] original) {
        StationLocation[] copy = new StationLocation[original.length];
        for (int i = 0; i < original.length; i++) {
            copy[i] = new StationLocation(original[i].getX(), original[i].getY());
        }
        return copy;
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
    public static StationLocation[] generateRandomCoordinates() {
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
                                                                                          // sudah dipilih, random
                                                                                          // lagi
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

    private static boolean isValidCoordinate(int x, int y) { // cek x dan y
        if (map[x][y] == 0) // kalo jalan kosong (bukan pohon/rumah)
            return true; // valid
        return false; // else, tidak valid
    }

    private static boolean notChosenYet(int x, int y, StationLocation[] neighborCoordinates) { // cek apakah x dan y
                                                                                               // udah
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

    // // pastikan x ada diantara MAX_X dan MIN_X;
    // static double clamp(double x) {
    // x = Math.max(MIN_X, x);
    // x = Math.min(x, MAX_X);
    // return x;
    // }

    public StationLocation[] simulatedAnnealing(double t0, double cooling, double stopping_temp, double stepSize) {
        StationLocation[] randPos = generateRandomCoordinates(); // posisi awal random
        double currentF = f(randPos);

        StationLocation[] bestState = randPos;
        StationLocation[] currentState = randPos;
        double bestF = currentF;

        double currentStepSize = stepSize;

        double T = t0; // schedule(t)
        while (true) { // sampai lebih kecil dari stopping_temp atau bisa diiterasi juga
            if (T < stopping_temp)
                break;
            // successor state: "perturbation" via gaussian (mean = 0, deviasi = stepSize)
            // getneighbor\

            currentStepSize = stepSize * (T / t0);
            if (currentStepSize < 1.0)
                currentStepSize = 1.0; // jangan sampai 0

            int randomIdx = rnd.nextInt(banyakFireStation); // dari banyak firestation, pilih 1 random

            StationLocation[] neighborStates = getNeighbor(currentState[randomIdx].getX(),
                    currentState[randomIdx].getY(),
                    currentStepSize);

            // buat neighbor state-nya
            StationLocation[] topNeighborStates = null;
            double topF = Integer.MAX_VALUE;
            if (isNotOutOfBound(neighborStates[0])
                    && isValidCoordinate(neighborStates[0].getX(), neighborStates[0].getY())) {
                topNeighborStates = deepCopy(currentState); // GUNAKAN DEEP COPY
                topNeighborStates[randomIdx].setX(neighborStates[0].getX());
                topNeighborStates[randomIdx].setY(neighborStates[0].getY());
                topF = f(topNeighborStates);
            }

            StationLocation[] rightNeighborStates = null;
            double rightF = Integer.MAX_VALUE;
            if (isNotOutOfBound(neighborStates[1])
                    && isValidCoordinate(neighborStates[1].getX(), neighborStates[1].getY())) {
                rightNeighborStates = deepCopy(currentState); // GUNAKAN DEEP COPY
                rightNeighborStates[randomIdx].setX(neighborStates[1].getX());
                rightNeighborStates[randomIdx].setY(neighborStates[1].getY());
                rightF = f(rightNeighborStates);
            }

            StationLocation[] bottomNeighborStates = null;
            double bottomF = Integer.MAX_VALUE;
            if (isNotOutOfBound(neighborStates[2])
                    && isValidCoordinate(neighborStates[2].getX(), neighborStates[2].getY())) {
                bottomNeighborStates = deepCopy(currentState); // GUNAKAN DEEP COPY
                bottomNeighborStates[randomIdx].setX(neighborStates[2].getX());
                bottomNeighborStates[randomIdx].setY(neighborStates[2].getY());
                bottomF = f(bottomNeighborStates);
            }

            StationLocation[] leftNeighborStates = null;
            double leftF = Integer.MAX_VALUE;
            if (isNotOutOfBound(neighborStates[3])
                    && isValidCoordinate(neighborStates[3].getX(), neighborStates[3].getY())) {
                leftNeighborStates = deepCopy(currentState); // GUNAKAN DEEP COPY
                leftNeighborStates[randomIdx].setX(neighborStates[3].getX());
                leftNeighborStates[randomIdx].setY(neighborStates[3].getY());
                leftF = f(leftNeighborStates);
            }

            // keluarkan firestation ketika terpojok
            if (topNeighborStates == null && rightNeighborStates == null && bottomNeighborStates == null
                    && leftNeighborStates == null) {
                T *= cooling;
                continue;
            }

            StationLocation[] successorFireStation = null;
            int randomSuccessorIdx = rnd.nextInt(4);

            while (successorFireStation == null) {
                randomSuccessorIdx = rnd.nextInt(4);

                switch (randomSuccessorIdx) {
                    case 0:
                        successorFireStation = topNeighborStates;
                        break;
                    case 1:
                        successorFireStation = rightNeighborStates;
                        break;
                    case 2:
                        successorFireStation = bottomNeighborStates;
                        break;
                    case 3:
                        successorFireStation = leftNeighborStates;
                        break;
                }
            }

            double successorF = f(successorFireStation); // hitung f()-nya
            double deltaE = successorF - currentF; // hitung delta
            // karena cari minimal total cost jadi deltaE < 0
            if ((deltaE < 0) || (rnd.nextDouble() <= Math.exp(-deltaE / T))) { // kriteria acceptance
                currentState = successorFireStation; // pindah karena lebih baik
                currentF = successorF;
                // update current state dan f(current) jadi lebih kecil
                if (currentF < bestF) { // simpan terbaik
                    bestF = currentF;
                    bestState = currentState;
                }
            }
            T *= cooling; // turunkan suhu
        }
        // return currentX;
        return bestState;
    }

    // run: SA 100 0.999 0.0001 0.1
    // public static void main(String[] args) {
    // Scanner sc = new Scanner(System.in);

    // // ukuran peta
    // int n = sc.nextInt();
    // int m = sc.nextInt();

    // map = new int[m][n];
    // boolean[][] visited = new boolean[m][n];

    // // banyak fire station
    // int p = sc.nextInt();

    // // banyak rumah
    // int h = sc.nextInt();

    // // banyak pohon
    // int t = sc.nextInt();

    // // input koordinat rumah
    // for (int i = 0; i < h; i++) {
    // int x = sc.nextInt();
    // int y = sc.nextInt();
    // map[m - y][x - 1] = 1;
    // }

    // // input koordinat pohon
    // for (int i = 0; i < t; i++) {
    // int x = sc.nextInt();
    // int y = sc.nextInt();
    // map[m - y][x - 1] = 2;
    // }
    // sc.close();

    // banyakFireStation = p;
    // banyakRumah = h;

    // double starting_temp = Double.parseDouble(args[0]);
    // double cooling_rate = Double.parseDouble(args[1]);
    // double stopping_temp = Double.parseDouble(args[2]);
    // double stepSize = Double.parseDouble(args[3]);
    // int runs = Integer.parseInt(args[4]);
    // int i = 1;

    // StationLocation[] bestState = generateRandomCoordinates();
    // double bestF = f(bestState);

    // while (i++ <= runs) { // lakukan sebanyak runs kali
    // System.out.printf("Run %d\n", i - 1);
    // // hasil SA terbaik
    // StationLocation[] currentState = simulatedAnnealing(starting_temp,
    // cooling_rate, stopping_temp, stepSize);
    // double currentF = f(currentState); // hitung f(x) dari hasil SA
    // System.out.printf("Simulated Annealing result:\n");
    // System.out.printf("Current f = %.5f\n", ((1.0 * currentF) / (1.0 *
    // banyakRumah)));

    // System.out.println("Current fire station coordinates (x, y):");
    // for (int k = 0; k < currentState.length; k++) {
    // System.out.print("(");
    // for (int j = 0; j < currentState[k].length; j++) {
    // System.out.print(currentState[k][j] + "");
    // }
    // System.out.println(")");
    // }
    // System.out.println("----------------------------------------------------------");

    // if (currentF < bestF) { // simpan f(x) terbaik;
    // bestF = currentF;
    // bestState = currentState;
    // }
    // }

    // System.out.printf("Simulated Annealing BEST:\n");
    // System.out.printf("Best f = %.5f\n", ((1.0 * bestF) / (1.0 * banyakRumah)));

    // System.out.println("Best fire station coordinates (x, y):");
    // for (int k = 0; k < bestState.length; k++) {
    // System.out.print("(");
    // for (int j = 0; j < bestState[k].length; j++) {
    // System.out.print(bestState[k][j] + "");
    // }
    // System.out.println(")");
    // }
    // System.out.println("----------------------------------------------------------");
    // }
}