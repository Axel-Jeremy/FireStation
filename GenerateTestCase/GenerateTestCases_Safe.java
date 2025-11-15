import java.io.PrintWriter;
import java.io.IOException;
import java.util.Random;
import java.util.List;
import java.util.ArrayList;

public class GenerateTestCases_Safe {

    // 0 = Jalan, 1 = Rumah, 2 = Pohon
    static int[][] grid;
    static int m_map, n_map;
    
    // Vektor 4-arah (atas, kanan, bawah, kiri)
    static int[] dRow = {-1, 0, 1, 0};
    static int[] dCol = {0, 1, 0, -1};

    public static void main(String[] args) {
        // (i) Kecil: 20x20, 4 Stasiun, 50 Rumah, 90 Pohon
        generateFile("input_kecil_aman.txt", 20, 20, 4, 50, 90);

        // (ii) Menengah: 40x40, 15 Stasiun, 300 Rumah, 350 Pohon
        generateFile("input_menengah_aman.txt", 40, 40, 15, 300, 350);
        
        // (iii) Besar: 80x80, 50 Stasiun, 1000 Rumah, 1400 Pohon
        generateFile("input_besar_aman.txt", 80, 80, 50, 1000, 1400);

        System.out.println("Sukses! File test case 'aman' telah dibuat.");
    }

    /**
     * Helper class untuk menyimpan koordinat 0-based (row, col)
     */
    private static class Coord {
        int r, c;
        Coord(int r, int c) { this.r = r; this.c = c; }
        
        // Konversi ke format output 1-based (x, y)
        public String toOutputString() {
            int x = c + 1;
            int y = m_map - r;
            return x + " " + y;
        }
    }

    /**
     * Cek apakah koordinat (r, c) berada di dalam peta
     */
    private static boolean isValid(int r, int c) {
        return r >= 0 && r < m_map && c >= 0 && c < n_map;
    }

    /**
     * Menghitung berapa banyak jalan (sel 0) di sebelah sebuah rumah
     */
    private static int countOpenPaths(int r_house, int c_house) {
        int openPaths = 0;
        for (int i = 0; i < 4; i++) {
            int nr = r_house + dRow[i];
            int nc = c_house + dCol[i];
            
            // Cek jika tetangga adalah jalan (0)
            if (isValid(nr, nc) && grid[nr][nc] == 0) {
                openPaths++;
            }
        }
        return openPaths;
    }

    /**
     * Cek apakah aman menempatkan pohon di (r, c).
     * Tidak aman jika menempatkan pohon ini akan mengisolasi rumah.
     */
    private static boolean isSafeToPlaceTree(int r_tree, int c_tree) {
        // Cek 4 tetangga dari lokasi pohon
        for (int i = 0; i < 4; i++) {
            int nr = r_tree + dRow[i];
            int nc = c_tree + dCol[i];
            
            // Jika tetangga adalah rumah
            if (isValid(nr, nc) && grid[nr][nc] == 1) {
                // Cek berapa sisa jalan rumah itu
                if (countOpenPaths(nr, nc) <= 1) {
                    // Jika sisa jalannya hanya 1 (yaitu sel r_tree, c_tree ini),
                    // maka menempatkan pohon di sini akan MENGISOLASI rumah.
                    return false; // Tidak aman
                }
            }
        }
        return true; // Aman
    }


    private static void generateFile(String fileName, int m, int n, int p, int h, int t) {
        System.out.println("Membuat file: " + fileName + "...");
        
        m_map = m;
        n_map = n;
        grid = new int[m][n]; // 0 = Jalan (default)
        
        Random rand = new Random();
        List<Coord> houseList = new ArrayList<>();
        List<Coord> treeList = new ArrayList<>();
        
        int maxAttempts = m * n * 10; // Batas percobaan

        // 1. Tempatkan Rumah (h)
        for (int i = 0; i < h; i++) {
            int attempts = 0;
            while (attempts < maxAttempts) {
                int r = rand.nextInt(m);
                int c = rand.nextInt(n);
                
                if (grid[r][c] == 0) { // Jika masih jalan
                    grid[r][c] = 1; // Jadi rumah
                    houseList.add(new Coord(r, c));
                    break;
                }
                attempts++;
            }
             if (attempts >= maxAttempts) {
                System.err.println("Peringatan: Gagal menempatkan rumah ke-" + (i+1) + " di " + fileName);
            }
        }

        // 2. Tempatkan Pohon (t)
        for (int i = 0; i < t; i++) {
            int attempts = 0;
            while (attempts < maxAttempts) {
                int r = rand.nextInt(m);
                int c = rand.nextInt(n);
                
                // Cek 1: Apakah sel ini kosong (jalan)?
                if (grid[r][c] == 0) {
                    // Cek 2: Apakah aman menempatkan pohon di sini?
                    if (isSafeToPlaceTree(r, c)) {
                        grid[r][c] = 2; // Jadi pohon
                        treeList.add(new Coord(r, c));
                        break;
                    }
                }
                attempts++;
            }
            if (attempts >= maxAttempts) {
                System.err.println("Peringatan: Gagal menempatkan pohon ke-" + (i+1) + " di " + fileName);
            }
        }
        
        // 3. Tulis ke File
        try (PrintWriter writer = new PrintWriter(fileName, "UTF-8")) {
            // Tulis header
            writer.println(n + " " + m);
            writer.println(p);
            writer.println(houseList.size()); // Jumlah rumah yang sukses ditempatkan
            writer.println(treeList.size());  // Jumlah pohon yang sukses ditempatkan
            
            // Tulis koordinat rumah
            for (Coord coord : houseList) {
                writer.println(coord.toOutputString());
            }
            
            // Tulis koordinat pohon
            for (Coord coord : treeList) {
                writer.println(coord.toOutputString());
            }
            
            System.out.println("File " + fileName + " berhasil dibuat.");

        } catch (IOException e) {
            System.err.println("Error saat menulis file " + fileName + ": " + e.getMessage());
        }
    }
}