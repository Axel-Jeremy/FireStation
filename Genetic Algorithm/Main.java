import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.List;
import java.io.FileNotFoundException;

public class Main {
	public static void main(String[] args) {
		Scanner sc;
		// Ukuran Peta n*m
		int n = 0; 
		int m = 0;

		int p = 0; // Banyak Fire Station
		int h = 0; // Banyak Rumah
		int t = 0; // Banyak pohon
		int[][] map = null; // Grid peta
		List<Coordinate> houseLocations = new ArrayList<>(); // Daftar lokasi rumah

		try {
			// input dari file input.txt
			sc = new Scanner(new File("input_medium.txt")); // Nama file input di ubah di sini

			// ukuran peta
			n = sc.nextInt();
			m = sc.nextInt();
			map = new int[m][n];

			// banyak fire station
			p = sc.nextInt();

			// banyak rumah
			h = sc.nextInt();

			// banyak pohon
			t = sc.nextInt();

			// input koordinat rumah
			for (int i = 0; i < h; i++) {
				int x = sc.nextInt();
				int y = sc.nextInt();
				map[m - y][x - 1] = 1;
				houseLocations.add(new Coordinate(m - y, x - 1));
			}

			// input koordinat pohon
			for (int i = 0; i < t; i++) {
				int x = sc.nextInt();
				int y = sc.nextInt();
				map[m - y][x - 1] = 2;
			}
			sc.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		int loop = Integer.parseInt(args[0]);// berapa kali algogen dijalankan
		double total = 0;
		Random init = new Random(); // random generator untuk membuat seed
		int bestFitness = Integer.MAX_VALUE;
		Individual bestState = null;
		long seed = init.nextLong() % 1000; // simpan seed sebagai seed untuk random generator
		Random gen = new Random(seed); // random generator untuk algogen-nya

		for (int ct = 1; ct <= loop; ct++) {
			// System.out.println("===================\nRun: "+ct);
			int maxCapacity = p, totalGeneration = 0, maxPopulationSize = 0;
			double crossoverRate = 0.0, mutationRate = 0.0, elitismPct = 0.0;

			try { // baca data parameter genetik
				sc = new Scanner(new File("param.txt"));
				totalGeneration = sc.nextInt();
				maxPopulationSize = sc.nextInt();
				crossoverRate = sc.nextDouble(); 
				mutationRate = sc.nextDouble(); 
				elitismPct = sc.nextDouble();
			} catch (Exception e) {
				e.printStackTrace();
			}
			// gen (random generator) dikirim ke algogen, jadi hanya menggunakan satu
			// generator untuk keseluruhan algo
			FireStationGA ga = new FireStationGA(gen, totalGeneration, maxPopulationSize, elitismPct, crossoverRate,
					mutationRate, maxCapacity);
			Individual.setMap(map);
			Individual.setBanyakFirestation(p);
			Individual.setHouseLocation(houseLocations);
			Individual res = ga.run(); // ambil yg terbaik

			// System.out.println("current: " + res.fitness);
			total += res.fitness;

			if (bestFitness > res.fitness) {
				bestFitness = res.fitness;
				bestState = res;
			}
		}
		// System.out.printf("Best fitness %d\n",bestFitness);
		// System.out.printf("Avg. fitness %.3f\n",total/loop);

		System.out.println("======================================");
		System.out.println("Seed: " + seed);
		System.out.println("======================================");

		System.out.printf("Best F: %.5f\n", ((1.0 * bestFitness) / (1.0 * h)));
		System.out.printf("p: %d, Average: %.5f\n", p, ((1.0 * bestFitness) / (1.0 * h)));
		System.out.println("======================================");

		System.out.println("Best Fire Station Coordinates (x, y):");
		System.out.print(bestState);
		System.out.println("======================================");
	}
}
