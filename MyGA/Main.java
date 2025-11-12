import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.io.File;

public class Main {
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);

		// ukuran peta
		int n = sc.nextInt();
		int m = sc.nextInt();
		int[][] map = new int[m][n];

		// boolean[][] visited = new boolean[m][n];

		// banyak fire station
		int p = sc.nextInt();

		// banyak rumah
		int h = sc.nextInt();

		// banyak pohon
		int t = sc.nextInt();

		// input koordinat rumah
		for (int i = 0; i < h; i++) {
			int x = sc.nextInt();
			int y = sc.nextInt();
			map[m - y][x - 1] = 1;
		}

		// input koordinat pohon
		for (int i = 0; i < t; i++) {
			int x = sc.nextInt();
			int y = sc.nextInt();
			map[m - y][x - 1] = 2;
		}
		sc.close();

		Individual.setMap(map);

		int loop = Integer.parseInt(args[0]); // berapa kali algogen dijalankan
		double total = 0;
		Random init = new Random(); // random generator untuk membuat seed
		Individual bestRun = null; // menyimpan individu terbaik

		for (int ct = 1; ct <= loop; ct++) {
			// System.out.println("===================\nRun: "+ct);
			long seed = init.nextLong() % 1000; // simpan seed sebagai seed untuk random generator
			// System.out.println("Seed: "+seed);
			Random gen = new Random(seed); // random generator untuk algogen-nya
			int maxCapacity = 0;
			int totalGeneration = 0;
			int maxPopulationSize = 0;
			double crossoverRate = 0.0;
			double mutationRate = 0.0;
			double elitismPct = 0.0;

			// ArrayList<Item> listOfItems = new ArrayList<Item>();
			// try { //baca data item knapsack
			// sc = new Scanner(new File("input.txt"));
			// maxCapacity = sc.nextInt();
			// for (int i=1;i<=32;i++) {
			// listOfItems.add(new Item(sc.nextInt(), sc.nextInt(),sc.next()));
			// }
			// } catch (FileNotFoundException e) { e.printStackTrace();}

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
					mutationRate, p);
			Individual res = ga.run(); // ambil yg terbaik

			// simpan individu terbaik
			if (bestRun == null || res.fitness < bestRun.fitness) {
				bestRun = res;
			}

			double rata2Cost;

			if (res.fitness == Integer.MAX_VALUE || h == 0) {
				rata2Cost = 0;
			} else {
				rata2Cost = (res.fitness * 1.0 / h);
			}
			total += rata2Cost;
		}
		// SOUT
		if (bestRun != null) {
			double rata2 = 0;
			if (bestRun.fitness != Integer.MAX_VALUE && h > 0) {
				rata2 = bestRun.fitness * 1.0 / h;
			}

			// Fire Station dan Rata rata
			System.out.printf("%d %5.f \n", p, rata2);

			// Koordinat Fire Station
			for (int i = 0; i < p; i++){
				int row = bestRun.chromosome[i].getX();
				int col = bestRun.chromosome[i].getY();

				System.out.printf("%d %d \n", row, col);
			}
		}
	}
}

/*
 * // ???
 * double fit = (1.0 * res.fitness) / 13692887; // kebetulan optimalnya tahu,
 * tapi intinya untuk mencari tahu
 * // seberapa bagus fitnesnya
 * total = total + fit;
 * System.out.printf("%2d: Acc = %.3f (%d) Seed: %d\n", ct, (1.0 * res.fitness)
 * / 13692887, res.fitness, seed);
 * // for (int i=0;i<Integer.SIZE;i++) {
 * // int bit = res.chromosome&(1<<i);
 * // System.out.println(i+" :"+((bit>0)?"1":"0"));
 * // }
 * }
 * System.out.printf("Avg. fitness %.3f\n", total / loop);
 */

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
