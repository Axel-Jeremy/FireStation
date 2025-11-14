import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

/**
 * Kelas ini membuat file input untuk masalah fire station.
 * Versi 2.0: Menjamin semua rumah dapat dijangkau (tidak terisolasi oleh pohon)
 * dengan menempatkan rumah HANYA di "Largest Connected Component" (LCC)
 * dari jalan yang tersedia setelah pohon ditempatkan.
 *
 * Bergantung pada kelas Coordinate.java
 */
public class InputGenerator {

    private static final Random rnd = new Random();
    private static int[] dRow = { -1, 0, 1, 0 };
    private static int[] dCol = { 0, 1, 0, -1 };

    /**
     * Helper untuk menghasilkan angka random dalam range [min, max] (inklusif).
     */
    private static int randInt(int min, int max) {
        if (min > max) {
            // Tukar jika min > max
            int temp = min;
            min = max;
            max = temp;
        } else if (min == max) {
            return min;
        }
        return rnd.nextInt((max - min) + 1) + min;
    }

    /**
     * Menjalankan BFS untuk menemukan satu komponen terhubung dari sel non-pohon.
     * * @param r_start Baris awal
     * @param c_start Kolom awal
     * @param map     Peta (dengan pohon sudah ditempatkan)
     * @param visited Peta kunjungan
     * @return Daftar koordinat (col, row) dalam komponen ini
     */
    private static List<Coordinate> findComponent(int r_start, int c_start, int[][] map, boolean[][] visited) {
        int m_height = map.length;
        int n_width = map[0].length;
        List<Coordinate> component = new ArrayList<>();
        Queue<Coordinate> q = new LinkedList<>();

        q.offer(new Coordinate(c_start, r_start));
        visited[r_start][c_start] = true;
        component.add(new Coordinate(c_start, r_start));

        while (!q.isEmpty()) {
            Coordinate curr = q.poll();
            int c = curr.getX(); // col
            int r = curr.getY(); // row

            for (int i = 0; i < 4; i++) {
                int newRow = r + dRow[i];
                int newCol = c + dCol[i];

                // Cek dalam batas, bukan pohon, dan belum dikunjungi
                if (newRow >= 0 && newRow < m_height && newCol >= 0 && newCol < n_width &&
                        map[newRow][newCol] != 2 && !visited[newRow][newCol]) {
                    
                    visited[newRow][newCol] = true;
                    Coordinate newCoord = new Coordinate(newCol, newRow);
                    component.add(newCoord);
                    q.offer(newCoord);
                }
            }
        }
        return component;
    }

    /**
     * Menemukan komponen terhubung terbesar (LCC) dari sel non-pohon (0 atau 1).
     * * @param map Peta (dengan pohon sudah ditempatkan)
     * @return Daftar koordinat (col, row) di LCC
     */
    private static List<Coordinate> findLargestConnectedComponent(int[][] map) {
        int m_height = map.length;
        int n_width = map[0].length;
        boolean[][] visited = new boolean[m_height][n_width];
        List<Coordinate> largestComponent = new ArrayList<>();

        for (int r = 0; r < m_height; r++) {
            for (int c = 0; c < n_width; c++) {
                // Jika bukan pohon dan belum dikunjungi, mulai pencarian komponen baru
                if (map[r][c] != 2 && !visited[r][c]) {
                    List<Coordinate> currentComponent = findComponent(r, c, map, visited);
                    if (currentComponent.size() > largestComponent.size()) {
                        largestComponent = currentComponent;
                    }
                }
            }
        }
        return largestComponent;
    }

    /**
     * Metode utama untuk mengenerate dan menulis file input.
     * @param n_width  Lebar peta (kolom)
     * @param m_height Tinggi peta (baris)
     * @param filename Nama file output (cth: "input.txt")
     */
    public static void generateInput(int n_width, int m_height, String filename) {
        
        System.out.println("Membuat file (V2): " + filename + "...");
        
        long totalCells = (long) n_width * m_height;
        int[][] map = new int[m_height][n_width]; // 0=jalan, 1=rumah, 2=pohon

        // 1. Hitung jumlah rumah (h) [15-20% dari total sel]
        int h_houses_target = randInt((int) (0.15 * totalCells), (int) (0.20 * totalCells));

        // 2. Hitung jumlah pohon (t) [20-25% dari total sel]
        int t_trees = randInt((int) (0.20 * totalCells), (int) (0.25 * totalCells));

        // 3. Buat daftar semua koordinat yang mungkin untuk diacak
        List<Coordinate> allCoords = new ArrayList<>();
        for (int r = 0; r < m_height; r++) {
            for (int c = 0; c < n_width; c++) {
                allCoords.add(new Coordinate(c, r)); // Simpan sebagai (col, row)
            }
        }
        Collections.shuffle(allCoords, rnd);

        // 4. Tempatkan Pohon (2) terlebih dahulu
        List<Coordinate> treeCoords = new ArrayList<>();
        for (int i = 0; i < t_trees; i++) {
            Coordinate tree = allCoords.get(i);
            map[tree.getY()][tree.getX()] = 2; // Tandai di peta
            treeCoords.add(tree);
        }

        // 5. Temukan Jaringan Jalan Terbesar (LCC)
        List<Coordinate> lccCells = findLargestConnectedComponent(map);

        if (lccCells.isEmpty()) {
            System.err.println("  PERINGATAN: Tidak ada sel yang dapat dijangkau! Peta ini kosong.");
            return;
        }

        // 6. Sesuaikan jumlah rumah jika LCC lebih kecil dari target
        int h_houses_actual = h_houses_target;
        if (h_houses_target > lccCells.size()) {
            System.err.println("  PERINGATAN: Target rumah (" + h_houses_target + ") > LCC (" + lccCells.size() + "). Mengurangi jumlah rumah.");
            h_houses_actual = lccCells.size(); // Hanya bisa menempatkan sebanyak sel di LCC
        }

        // 7. Pilih lokasi rumah dari LCC
        Collections.shuffle(lccCells, rnd);
        List<Coordinate> houseCoords = new ArrayList<>(lccCells.subList(0, h_houses_actual));
        
        // (Opsional, tapi penting) Perbarui peta dengan lokasi rumah
        for (Coordinate house : houseCoords) {
             map[house.getY()][house.getX()] = 1;
        }

        // 8. Hitung jumlah stasiun (p) [15-25 rumah / stasiun]
        int housesPerStation = randInt(15, 25);
        int p_stations = (int) Math.ceil((double) h_houses_actual / housesPerStation);
        if (p_stations == 0 && h_houses_actual > 0) {
            p_stations = 1;
        }
        
        // 9. Tulis ke file
        try (PrintWriter writer = new PrintWriter(new File(filename))) {
            
            writer.println(n_width + " " + m_height);
            writer.println(p_stations);
            writer.println(h_houses_actual); // Tulis jumlah rumah aktual
            writer.println(t_trees);

            // --- Tulis Koordinat Rumah ---
            // Konversi dari internal (0-based, top-left) (col, row)
            // ke format input (1-based, bottom-left) (x, y)
            for (Coordinate coord : houseCoords) {
                int c = coord.getX(); // col (0 hingga n-1)
                int r = coord.getY(); // row (0 hingga m-1)
                int x_out = c + 1; 
                int y_out = m_height - r; 
                writer.println(x_out + " " + y_out);
            }

            // --- Tulis Koordinat Pohon ---
            for (Coordinate coord : treeCoords) {
                int c = coord.getX();
                int r = coord.getY();
                int x_out = c + 1;
                int y_out = m_height - r;
                writer.println(x_out + " " + y_out);
            }

            System.out.println("  Selesai: " + filename);
            System.out.println("    Ukuran: " + n_width + "x" + m_height);
            System.out.println("    Stasiun (p): " + p_stations);
            System.out.println("    Rumah (h): " + h_houses_actual);
            System.out.println("    Pohon (t): " + t_trees);
            System.out.println("    Ukuran LCC: " + lccCells.size());
            System.out.println("-------------------------");

        } catch (FileNotFoundException e) {
            System.err.println("Error: Tidak dapat menulis ke file: " + e.getMessage());
        }
    }

    /**
     * Main method untuk menjalankan generator.
     */
    public static void main(String[] args) {
        generateInput(20, 20, "input_small.txt");
        generateInput(40, 40, "input_medium.txt");
        generateInput(80, 80, "input_large.txt");
    }
}