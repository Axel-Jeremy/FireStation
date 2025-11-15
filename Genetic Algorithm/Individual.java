import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

public class Individual implements Comparable<Individual> {
    public StationLocation[] chromosome; // kromosom adalah array of bit, integer diperlakukan seperti array berisi
                                         // (bit) 0/1
    public int fitness; // nilai fitnessnya
    public Random MyRand; // random generator dikirim dari luar untuk membuat invididu acal
    public double parentProbability; // probabilitas individu ini terpilih sbg parent
    static int banyakFireStation; // banyak firestation yang di deklarasi
    static int[][] map;

    private static final int[] dRow = { 0, 0, 1, -1 };
    private static final int[] dCol = { 1, -1, 0, 0 };
    public static List<Coordinate> houseLocations = new ArrayList<>();

    // membuat individu acak
    public Individual(Random MyRand) {
        this.MyRand = MyRand;
        // this.banyakFireStation = banyakFireStation;
        this.chromosome = generateRandomCoordinates();

        // System.out.println("=================");
        // for(int i = 0; i < chromosome.length; i++){
        // System.out.println(chromosome[i]);
        // }

        this.fitness = setFitness(chromosome);
        this.parentProbability = 0;

    }

    // Method untuk Peta yang akan digunakan
    public static void setMap(int[][] map) {
        Individual.map = map;
    }

    // Method untuk p yang akan digunakan
    public static void setBanyakFirestation(int banyakFireStation) {
        Individual.banyakFireStation = banyakFireStation;
    }

    public static void setHouseLocation(List<Coordinate> houseLocations) {
        Individual.houseLocations = houseLocations;
    }

    // membuat individu baru berdasarkan kromosom dari luar
    public Individual(Random MyRand, StationLocation[] chromosome) {
        this.MyRand = MyRand;
        this.chromosome = chromosome;

        this.fitness = setFitness(chromosome);
        this.parentProbability = 0;
    }

    // generate random coordinate buat koordinat si firestation
    public StationLocation[] generateRandomCoordinates() {
        StationLocation[] stationCoordinates = new StationLocation[banyakFireStation];
        Arrays.fill(stationCoordinates, new StationLocation(-1, -1));
        // for(StationLocation station:stationCoordinates) System.out.println(station);
        // [x1][y1] - coord firestation1
        // [x2][y2] - coord firestation2

        int x = -1;
        int y = -1;
        for (int i = 0; i < banyakFireStation; i++) {
            x = MyRand.nextInt(map.length);
            y = MyRand.nextInt(map[0].length);

            // System.out.println(x + "------" + y);
            while (!isValidCoordinate(x, y) && !notChosenYet(x, y, stationCoordinates)) {
                x = MyRand.nextInt(map.length);
                y = MyRand.nextInt(map[0].length);
            }

            stationCoordinates[i] = new StationLocation(x, y);
            // System.out.println(stationCoordinates[i]);
        }
        return stationCoordinates;
    }

    static boolean isValidCoordinate(int x, int y) {
        if (map[x][y] == 0)
            return true;
        return false;
    }

    static boolean notChosenYet(int x, int y, StationLocation[] neighborCoordinates) {
        for (int i = 0; i < neighborCoordinates.length; i++) {
            if (x == neighborCoordinates[i].getX() && y == neighborCoordinates[i].getY())
                return false;
        }
        return true;
    }

    static boolean isValid(int row, int col) {
        return row >= 0 && row < map.length &&
                col >= 0 && col < map[0].length &&
                map[row][col] != 2; /// 0 = kosong
    }

    // menghitung fitness dengan masukan list of item dan kapasitas knapsack
    public int setFitness(StationLocation[] fireStation) {
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

                    if(map[newRow][newCol] == 0)
                    q.offer(new Coordinate(newRow, newCol, d + 1));
                }
            }
        }

        // itung total biaya dari rumah yang sudah disimpan
        int totalCost = 0;
        int unreachedHouse = 0;
        for (Coordinate house : houseLocations) {
            int r = house.getX();
            int c = house.getY();

            // Ambil jarak langsung ke sel rumah,
            // karena BFS kita sekarang bisa berjalan di atas rumah (1)
            int costToThisHouse = dist[r][c];

            // Cek jika rumah ini tidak terjangkau oleh firestation
            if (costToThisHouse == Integer.MAX_VALUE) {
                unreachedHouse++;
            } else {
                // Jika stasiun di atas rumah, costToThisHouse == 0
                // Jika stasiun 5 langkah, costToThisHouse == 5
                // Tidak perlu +1, karena jaraknya sudah dihitung ke sel rumah
                totalCost += costToThisHouse;
            }
        }
        return totalCost + unreachedHouse * (map.length * map[0].length);
    }

    public void doMutation() {
        // Pilih 1 fireStation untuk dimutasi
        // random : 0 - (n-1)
        int mutation = this.MyRand.nextInt(banyakFireStation);

        // Pilih x / y untuk dimutasi
        int i = this.MyRand.nextInt(2);

        // i == 0 : mutasi x, i == 1 : mutasi y
        if (i == 0)
            this.chromosome[mutation].setX((this.chromosome[mutation].getX() + 1) % map.length);
        else
            this.chromosome[mutation].setY((this.chromosome[mutation].getY() + 1) % map.length);
    }

    // single point crossover
    // di sini hanya menghasilkan satu anak, crossover harusnya menghasilkan dua
    // anak
    // kemudian pilihannya bisa diambil anak terbaik saja, atau kedua anak masuk ke
    // dalam populasi berikutnya
    public Individual[] doCrossover(Individual other) {
        // 1. Buat array kromosom BARU untuk anak-anak
        StationLocation[] child1Chromosome = new StationLocation[banyakFireStation];
        StationLocation[] child2Chromosome = new StationLocation[banyakFireStation];

        // 2. Tentukan titik potong (logika Anda sudah OK)
        int rangeIndex = (int) (Math.ceil(((banyakFireStation * 1.0) / 3.0)));
        int potongan = this.MyRand.nextInt(rangeIndex) + rangeIndex;

        // 3. Lakukan crossover dengan menyalin gen (DEEP COPY)
        for (int i = 0; i < banyakFireStation; i++) {
            if (i <= potongan) {
                // Anak 1 mengambil dari Parent 1 (this)
                child1Chromosome[i] = new StationLocation(this.chromosome[i].getX(), this.chromosome[i].getY());
                // Anak 2 mengambil dari Parent 2 (other)
                child2Chromosome[i] = new StationLocation(other.chromosome[i].getX(), other.chromosome[i].getY());
            } else {
                // Anak 1 mengambil dari Parent 2 (other)
                child1Chromosome[i] = new StationLocation(other.chromosome[i].getX(), other.chromosome[i].getY());
                // Anak 2 mengambil dari Parent 1 (this)
                child2Chromosome[i] = new StationLocation(this.chromosome[i].getX(), this.chromosome[i].getY());
            }
        }

        // 4. Buat objek Individual BARU dengan kromosom baru yang aman
        Individual child1 = new Individual(this.MyRand, child1Chromosome);
        Individual child2 = new Individual(this.MyRand, child2Chromosome);

        return new Individual[] { child1, child2 };
    }

    /*
     * public Individual doCrossover(Individual other) { //two points crossover
     * Individual child1 = new Individual(this.MyRand,0);
     * Individual child2 = new Individual(this.MyRand,0);
     * int rd1=3, rd2=28;
     * do {
     * rd1 = this.MyRand.nextInt(28)+2;
     * rd2 = this.MyRand.nextInt(28)+2;
     * } while(Math.abs(rd1-rd2)<=2);
     * int pos1 = Math.min(rd1,rd2);
     * int pos2 = Math.max(rd1,rd2);
     * for (int i=0;i<=pos1;i++) {
     * child1.chromosome = child1.chromosome + (this.chromosome & (1<<i));
     * child2.chromosome = child2.chromosome + (other.chromosome & (1<<i));
     * }
     * for (int i=pos1+1;i<=pos2;i++) {
     * child1.chromosome = child1.chromosome + (other.chromosome & (1<<i));
     * child2.chromosome = child2.chromosome + (this.chromosome & (1<<i));
     * }
     * for (int i=pos2+1;i<Integer.SIZE;i++) {
     * child1.chromosome = child1.chromosome + (this.chromosome & (1<<i));
     * child2.chromosome = child2.chromosome + (other.chromosome & (1<<i));
     * }
     * //System.out.println(this);
     * //System.out.println(other);
     * //System.out.println(pos1+" "+pos2);
     * //System.out.println(child1);
     * //System.out.println(child2);
     * int choose = this.MyRand.nextInt(2);
     * //System.out.println(choose);
     * //System.out.println("-----");
     * if (choose==0) return child1;
     * else return child2;
     * //return child;
     * }
     */

    @Override
    public int compareTo(Individual other) {
        if (this.fitness > other.fitness)
            return 1;
        else if (this.fitness < other.fitness)
            return -1;
        else
            return 0;
    }

    @Override
    public String toString() {
        String res = "";
        for (int i = 0; i < chromosome.length; i++) {
            res += String.format("Firestation #%d : %s",i+1, chromosome[i]);
        }

        return res;
    }
}