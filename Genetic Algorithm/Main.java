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
		int[][] map = null;
		List<Coordinate> houseLocations = new ArrayList<>();

		try {
			// input dari file input.txt
			sc = new Scanner(new File("input.txt"));

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
				crossoverRate = sc.nextDouble(); // skala 0-1
				mutationRate = sc.nextDouble(); // skala 0-1
				elitismPct = sc.nextDouble(); // skala 0-1
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

/*
 * 
 * //Implementasi sederhana untuk masalah knapsack di slide
 * public class Main {
 * 
 * public static void main(String[] args) {
 * Scanner sc = new Scanner(System.in);
 * int loop = Integer.parseInt(args[0]);// sc.nextInt(); berapa kali algogen
 * dijalankan
 * System.out.println(loop);
 * System.out.println("Target: 13692887");
 * double total = 0;
 * Random init = new Random(); //random generator untuk membuat seed
 * for (int ct=1;ct<=loop;ct++) {
 * //System.out.println("===================\nRun: "+ct);
 * long seed = init.nextLong()%1000; //simpan seed sebagai seed untuk random
 * generator
 * //System.out.println("Seed: "+seed);
 * Random gen = new Random(seed); //random generator untuk algogen-nya
 * int maxCapacity=0, totalGeneration=0, maxPopulationSize=0;
 * double crossoverRate=0.0, mutationRate=0.0, elitismPct=0.0;
 * ArrayList<Item> listOfItems = new ArrayList<Item>();
 * try { //baca data item knapsack
 * sc = new Scanner(new File("input.txt"));
 * maxCapacity = sc.nextInt();
 * for (int i=1;i<=32;i++) {
 * listOfItems.add(new Item(sc.nextInt(), sc.nextInt(),sc.next()));
 * }
 * } catch (FileNotFoundException e) { e.printStackTrace();}
 * try { //baca data parameter genetik
 * sc = new Scanner(new File("param.txt"));
 * totalGeneration = sc.nextInt();
 * maxPopulationSize = sc.nextInt();
 * crossoverRate = sc.nextDouble(); //skala 0-1
 * mutationRate = sc.nextDouble(); //skala 0-1
 * elitismPct = sc.nextDouble(); //skala 0-1
 * } catch (FileNotFoundException e) { e.printStackTrace();}
 * //gen (random generator) dikirim ke algogen, jadi hanya menggunakan satu
 * generator untuk keseluruhan algo
 * KnapsackGA ga = new
 * KnapsackGA(gen,totalGeneration,maxPopulationSize,elitismPct, crossoverRate,
 * mutationRate, listOfItems, maxCapacity);
 * Individual res = ga.run(); //ambil yg terbaik
 * double fit = (1.0*res.fitness)/13692887; //kebetulan optimalnya tahu, tapi
 * intinya untuk mencari tahu seberapa bagus fitnesnya
 * total = total + fit;
 * System.out.printf("%2d: Acc = %.3f (%d) Seed: %d\n",ct,(1.0*res.fitness)/
 * 13692887,res.fitness,seed);
 * //for (int i=0;i<Integer.SIZE;i++) {
 * // int bit = res.chromosome&(1<<i);
 * // System.out.println(i+" :"+((bit>0)?"1":"0"));
 * //}
 * }
 * System.out.printf("Avg. fitness %.3f\n",total/loop);
 * 
 * }
 * }
 * 
 * //kode program tidak terpakai, kayaknya buat coba2
 * 
 * //Individual idv = new Individual(gen,1282);
 * //idv.setFitness(listOfItems,maxCapacity);
 * //System.out.println("-----------");
 * //System.out.println(idv);
 * //System.out.println("-----------");
 * 
 * //System.out.println(res.generation);
 * //System.out.println(res.bestindividual);
 * /*
 * int value=0;
 * int weight=0;
 * int sol = 1283;
 * for (int i=0;i<Integer.SIZE;i++) {
 * int bit = sol&(1<<i);
 * System.out.println(i+" :"+((bit>0)?"1":"0"));
 * }
 * for (int i=0;i<Integer.SIZE;i++) {
 * int bit = sol&(1<<i);
 * System.out.println(sol);
 * System.out.println(bit);
 * if (bit!=0) {
 * value = value + listOfItems.get(i).value;
 * weight = weight + listOfItems.get(i).weight;
 * System.out.println(
 * listOfItems.get(i).name+" "+listOfItems.get(i).weight+" "+listOfItems.get(i).
 * value);
 * }
 * }
 * System.out.println(weight+" : "+value);
 */

/*
 * int b = 1283;
 * int c = 832748375;
 * //System.out.println(Integer.SIZE);
 * for (int i=0;i<Integer.SIZE;i++) {
 * int bit = b&(1<<i);
 * System.out.println(i+" :"+bit);
 * }
 * System.out.println("========================");
 * for (int i=0;i<Integer.SIZE;i++) {
 * int bit = c&(1<<i);
 * System.out.println(i+" :"+bit);
 * }
 * System.out.println("========================");
 * //b = b ^ (1<<1);
 * int a=0;
 * int pos = 12;
 * for (int i=0;i<=pos;i++) {
 * a = a + (b & (1<<i));
 * }
 * for (int i=pos+1;i<Integer.SIZE;i++) {
 * a = a + (c & (1<<i));
 * }
 * System.out.println(a);
 * for (int i=0;i<Integer.SIZE;i++) {
 * int bit = a&(1<<i);
 * System.out.println(i+" :"+bit);
 * }
 */
