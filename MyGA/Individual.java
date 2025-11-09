import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class Individual implements Comparable<Individual> {
    public StationLocation[] chromosome; // kromosom adalah array of bit, integer diperlakukan seperti array berisi
                                         // (bit) 0/1
    public int fitness; // nilai fitnessnya
    public Random MyRand; // random generator dikirim dari luar untuk membuat invididu acal
    public double parentProbability; // probabilitas individu ini terpilih sbg parent
    public int banyakFireStation; // banyak firestation yang di deklarasi
    static int[][] map;

    // membuat individu acak
    public Individual(Random MyRand, int banyakFireStation, int[][] map) {
        this.MyRand = MyRand;
        this.chromosome = generateRandomCoordinates();
        // System.out.println(chromosome);
        this.fitness = Integer.MAX_VALUE;
        this.parentProbability = 0;
        this.banyakFireStation = banyakFireStation;
        this.map = map;
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
        Arrays.fill(stationCoordinates, -1);

        // [x1][y1] - coord firestation1
        // [x2][y2] - coord firestation2

        int x;
        int y;
        for (int i = 0; i < banyakFireStation; i++) {
            x = MyRand.nextInt(map.length);
            y = MyRand.nextInt(map[0].length);

            while (!isValidCoordinate(x, y) && !notChosenYet(x, y, stationCoordinates)) {
                x = MyRand.nextInt(map.length);
                y = MyRand.nextInt(map[0].length);
            }

            StationLocation randomLocation = new StationLocation(x, y);

            stationCoordinates[i] = randomLocation;
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

    static boolean isValid(int row, int col, boolean[][] visited) {
        return row >= 0 && row < map.length &&
                col >= 0 && col < map[0].length &&
                !visited[row][col] &&
                map[row][col] == 0; /// 0 = kosong
    }

    // menghitung fitness dengan masukan list of item dan kapasitas knapsack
    public int setFitness(StationLocation[] fireStation) {
        int totalCost = 0;

        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                if (map[i][j] == 1) {
                    // do bfs
                    int cost = shortestPath(i, j, fireStation);
                    if (cost == -1)
                        return Integer.MAX_VALUE;

                    totalCost += cost;
                }
            }
        }
        return totalCost;
    }

    // BFS function to find the shortest distance
    // from 's' to 'd' in the matrix
    static int shortestPath(int xRow, int yCol, StationLocation[] fireStation) {
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

    public void doMutation() {
        // Pilih 1 fireStation untuk dimutasi
        int mutation = this.MyRand.nextInt(banyakFireStation);

        // Pilih x / y untuk dimutasi
        int i = this.MyRand.nextInt(2);

        // i == 0 : mutasi x, i == 1 : mutasi y
        // Bisa eksperimen di mutasi
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
        Individual child1 = new Individual(this.MyRand, this.banyakFireStation, this.map);
        Individual child2 = new Individual(this.MyRand, this.banyakFireStation, this.map);

        // Menentukan potongan untuk crossover
        int potongan = this.MyRand.nextInt((int)(Math.ceil((banyakFireStation / 3)))) + (banyakFireStation / 3);

        // random(17) + 7
        // 7 - 17+7
        // n/2 random(3) + 3 -> 3-4
        // 1 2 3 | 4 5 6 7 | 8 9

        // System.out.println(pos);

        // Gabungkan parent ke anak
        for (int i = 0; i <= potongan; i++) {
            child1.chromosome[i] = this.chromosome[i];
            child2.chromosome[i] = other.chromosome[i];
        }
        for (int i = potongan + 1; i < banyakFireStation; i++) {
            child1.chromosome[i] = other.chromosome[i];
            child2.chromosome[i] = this.chromosome[i];
        }
        // System.out.println(this);
        // System.out.println(other);
        // System.out.println(child);
        // System.out.println("-----");
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
            return -1;
        else if (this.fitness < other.fitness)
            return 1;
        else
            return 0;
    }

    @Override
    public String toString() {
        String res = "Individual Fitness: " + this.fitness + "\n";
        for(int i = 0; i < chromosome.length; i++){
            res += chromosome[i].toString();
        }

        return res;
    }
}