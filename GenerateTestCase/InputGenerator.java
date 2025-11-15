import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class InputGenerator {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int m = sc.nextInt();
        int n = sc.nextInt();
        String fileName = sc.next();

        Path file = Path.of(fileName);
        try (BufferedWriter bw = Files.newBufferedWriter(file)) {
            writeInput(bw, m, n);
        } catch (IOException e) {
            System.out.println("Error " + e.getMessage());
        }
    }

    public static void writeInput(BufferedWriter bw, int m, int n) throws IOException {
        bw.write(m + " " + n + "\n");
        Random rand = new Random();

        // Jumlah rumah 15 - 20 persen total cell
        int minHousePercent = 15, maxHousePercent = 20;
        int h = (int) ((rand.nextInt((maxHousePercent - minHousePercent + 1)) + minHousePercent) / 100.0 * m * n);

        // Jumlah pohon 20 - 25 persen total cell
        int minTreePercent = 20, maxTreePercent = 25;
        int t = (int) ((rand.nextInt((maxTreePercent - minTreePercent + 1)) + minTreePercent) / 100.0 * m * n);

        // Jumlah fire station adalah 15 - 25 per rumah
        int minFirestation = 15, maxFirestation = 25;
        int p = h / (rand.nextInt((maxFirestation - minFirestation + 1)) + minFirestation);

        bw.write(p + " " + h + " " + t + "\n");

        // Generate maze grid (0 = jalan, 3 = dinding)
        int[][] grid = generateMaze(m, n, rand);

        // Tempat menyimpan posisi rumah dan pohon
        List<StationLocation> housePlacement = new ArrayList<>();
        List<StationLocation> treePlacement = new ArrayList<>();

        // Tempatkan rumah dan pohon hanya pada cell kosong
        placeHousesAndTrees(grid, m, n, h, t, rand, housePlacement, treePlacement);

        // Tulis ke file
        writeToTextFile(bw, housePlacement, treePlacement);
    }

    // -------------------------------------------------------
    // MAZE GENERATOR (RANDOMIZED DFS)
    // -------------------------------------------------------
    public static int[][] generateMaze(int m, int n, Random rand) {
        int[][] grid = new int[m][n];

        // Isi grid awal sebagai tembok
        for (int i = 0; i < m; i++) Arrays.fill(grid[i], 3);

        // Posisi awal harus ganjil agar maze konsisten
        int startX = rand.nextInt(m / 2) * 2 + 1;
        int startY = rand.nextInt(n / 2) * 2 + 1;

        grid[startX][startY] = 0;

        Stack<StationLocation> stack = new Stack<>();
        stack.push(new StationLocation(startX, startY));

        int[] moveX = {-2, 0, 2, 0};
        int[] moveY = {0, 2, 0, -2};

        while (!stack.isEmpty()) {
            StationLocation curr = stack.peek();
            List<Integer> dirs = Arrays.asList(0, 1, 2, 3);
            Collections.shuffle(dirs, rand);

            boolean moved = false;

            for (int d : dirs) {
                int nx = curr.getX() + moveX[d];
                int ny = curr.getY() + moveY[d];

                if (nx > 0 && nx < m - 1 && ny > 0 && ny < n - 1 && grid[nx][ny] == 3) {
                    grid[nx][ny] = 0;
                    grid[curr.getX() + moveX[d] / 2][curr.getY() + moveY[d] / 2] = 0;
                    stack.push(new StationLocation(nx, ny));
                    moved = true;
                    break;
                }
            }

            if (!moved) stack.pop();
        }

        return grid;
    }

    // -------------------------------------------------------
    // PLACE HOUSES AND TREES
    // -------------------------------------------------------
    public static void placeHousesAndTrees(
            int[][] grid,
            int m, int n,
            int h, int t,
            Random rand,
            List<StationLocation> houses,
            List<StationLocation> trees
    ) {
        List<StationLocation> empty = new ArrayList<>();

        // Ambil semua cell kosong (hasil maze)
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (grid[i][j] == 0) {
                    empty.add(new StationLocation(i, j));
                }
            }
        }

        Collections.shuffle(empty, rand);

        if (h + t > empty.size()) {
            throw new RuntimeException("Tidak cukup cell kosong untuk menempatkan rumah + pohon");
        }

        // Tempatkan rumah
        for (int i = 0; i < h; i++) {
            StationLocation p = empty.get(i);
            houses.add(p);
            grid[p.getX()][p.getY()] = 1;
        }

        // Tempatkan pohon
        for (int i = h; i < h + t; i++) {
            StationLocation p = empty.get(i);
            trees.add(p);
            grid[p.getX()][p.getY()] = 2;
        }
    }

    // -------------------------------------------------------
    // WRITE OUTPUT
    // -------------------------------------------------------
    public static void writeToTextFile(BufferedWriter bw, List<StationLocation> housePos, List<StationLocation> treePos)
            throws IOException {

        for (StationLocation pos : housePos) {
            bw.write((pos.getX() + 1) + " " + (pos.getY() + 1) + "\n");
        }
        for (StationLocation pos : treePos) {
            bw.write((pos.getX() + 1) + " " + (pos.getY() + 1) + "\n");
        }
    }
}
