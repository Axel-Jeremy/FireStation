import java.util.*;

public class MySA {
	static final Random rnd = new Random();
	// private int[][] firestationCoordinates;
	static int[][] map;
	static int banyakFireStation;
	static int banyakRumah;
	// Direction vectors
	static int dRow[] = { -1, 0, 1, 0 };
	static int dCol[] = { 0, 1, 0, -1 };

	// objective function (maximizing)
	// itung jarak terdekat (shortest path) dari setiap rumah ke firestation
	// terdekat, tambahin
	static double f(int[][] fireStation) {
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

	// Function to check if the given cell is within bounds,
	// and not already visited
	static boolean isValid(int row, int col, boolean[][] visited) {
		return row >= 0 && row < map.length &&
				col >= 0 && col < map[0].length &&
				!visited[row][col] &&
				map[row][col] == 0; /// 0 = kosong
	}

	// BFS function to find the shortest distance
	// from 's' to 'd' in the matrix
	static int shortestPath(int xRow, int yCol, int[][] fireStation) {
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

	// alternatif mencari neighbor state di antara [-stepSize, stepSize]
	static int[][] getNeighbor(int x, int y, double stepSize) {
		int[][] neighborCoordinates = new int[4][2];

		for (int i = 0; i < neighborCoordinates.length; i++) {
			Arrays.fill(neighborCoordinates[i], -1);
		}

		for (int i = 0; i < 4; i++) {
			int stepX = (int) (dRow[i] * stepSize);
			int stepY = (int) (dCol[i] * stepSize);

			if (stepX + x < map.length && stepY + y < map[0].length) {
				neighborCoordinates[i][0] = x + (int) (dRow[i] * stepSize);
				neighborCoordinates[i][1] = y + (int) (dCol[i] * stepSize);
			}
		}
		return neighborCoordinates;
	}

	// generate random coordinate buat koordinat si firestation
	static int[][] generateRandomCoordinates() {
		int[][] stationCoordinates = new int[banyakFireStation][2];
		for (int i = 0; i < stationCoordinates.length; i++) {
			Arrays.fill(stationCoordinates[i], -1);
		}
		int x, y;

		for (int i = 0; i < banyakFireStation; i++) {
			x = rnd.nextInt(map.length);
			y = rnd.nextInt(map[0].length);

			while (!isValidCoordinate(x, y) && !notChosenYet(x, y, stationCoordinates)) {
				x = rnd.nextInt(map.length);
				y = rnd.nextInt(map[0].length);
			}
			stationCoordinates[i][0] = x;
			stationCoordinates[i][1] = y;
		}
		return stationCoordinates;
	}

	static boolean isValidCoordinate(int x, int y) {
		if (map[x][y] == 0)
			return true;
		return false;
	}

	static boolean notChosenYet(int x, int y, int[][] neighborCoordinates) {
		for (int i = 0; i < neighborCoordinates.length; i++) {
			if (x == neighborCoordinates[i][0] && y == neighborCoordinates[i][1])
				return false;
		}
		return true;
	}

	static boolean isNotOutOfBound(int[] arr) { // [x][y]
		int x = arr[0];
		int y = arr[1];

		// Cek tidak ada yg d batas negatif (atas), tidak lewatin batas bawah dan kanan serta kiri peta
		return x >= 0 && x < map.length && y >= 0 && y < map[0].length;
	}

	// // pastikan x ada diantara MAX_X dan MIN_X;
	// static double clamp(double x) {
	// x = Math.max(MIN_X, x);
	// x = Math.min(x, MAX_X);
	// return x;
	// }

	static int[][] simulatedAnnealing(double t0, double cooling, double stopping_temp, double stepSize) {
		int[][] randPos = generateRandomCoordinates(); // posisi awal random
		double currentF = f(randPos);

		int[][] bestState = randPos;
		int[][] currentState = randPos;
		double bestF = currentF;
		double T = t0; // schedule(t)
		while (true) { // sampai lebih kecil dari stopping_temp atau bisa diiterasi juga
			if (T < stopping_temp)
				break;
			// successor state: "perturbation" via gaussian (mean = 0, deviasi = stepSize)
			// getneighbor
			int randomIdx = rnd.nextInt(banyakFireStation); // dari banyak firestation, pilih 1 random
			int[][] neighborStates = getNeighbor(randPos[randomIdx][0], randPos[randomIdx][1], stepSize);
			// buat neighbor state-nya --> bisa gunakan getNeighbor()
			int[][] topNeighborStates = null;
			if (isNotOutOfBound(neighborStates[0])) {
				topNeighborStates = randPos;
				topNeighborStates[randomIdx][0] = neighborStates[0][0];
				topNeighborStates[randomIdx][1] = neighborStates[0][1];
			}

			int[][] rightNeighborStates = null;
			if (isNotOutOfBound(neighborStates[1])) {
				rightNeighborStates = randPos;
				rightNeighborStates[randomIdx][0] = neighborStates[1][0];
				rightNeighborStates[randomIdx][1] = neighborStates[1][1];
			}

			int[][] bottomNeighborStates = null;
			if (isNotOutOfBound(neighborStates[2])) {
				bottomNeighborStates = randPos;
				bottomNeighborStates[randomIdx][0] = neighborStates[2][0];
				bottomNeighborStates[randomIdx][1] = neighborStates[2][1];
			}

			int[][] leftNeighborStates = null;
			if (isNotOutOfBound(neighborStates[3])) {
				leftNeighborStates = randPos;
				leftNeighborStates[randomIdx][0] = neighborStates[3][0];
				leftNeighborStates[randomIdx][1] = neighborStates[3][1];
			}

			// keluarkan firestation ketika terpojok
			if (topNeighborStates == null && rightNeighborStates == null && bottomNeighborStates == null && leftNeighborStates == null){
				T *= cooling;
				continue;
			}

			int[][] successorFireStation = null;
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
			if ((deltaE < 0) || (rnd.nextDouble() <= Math.exp(deltaE / T))) { // kriteria acceptance
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
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);

		// ukuran peta
		int n = sc.nextInt();
		int m = sc.nextInt();

		map = new int[m][n];
		boolean[][] visited = new boolean[m][n];

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

		banyakFireStation = p;
		banyakRumah = h;

		double starting_temp = Double.parseDouble(args[0]);
		double cooling_rate = Double.parseDouble(args[1]);
		double stopping_temp = Double.parseDouble(args[2]);
		double stepSize = Double.parseDouble(args[3]);
		int runs = Integer.parseInt(args[4]);
		int i = 1;

		int[][] bestState = generateRandomCoordinates();
		double bestF = f(bestState);
		while (i++ <= runs) { // lakukan sebanyak runs kali
			System.out.printf("Run %d\n", i - 1);
			// hasil SA terbaik
			int[][] currentState = simulatedAnnealing(starting_temp, cooling_rate, stopping_temp, stepSize);
			double currentF = f(currentState); // hitung f(x) dari hasil SA
			System.out.printf("Simulated Annealing result:\n");
			System.out.printf("Current f = %.5f\n", ((1.0 * currentF) / (1.0 * banyakRumah)));

			System.out.println("Current fire station coordinates (x, y):");
			for (int k = 0; k < currentState.length; k++) {
				System.out.print("(");
				for (int j = 0; j < currentState[k].length; j++) {
					System.out.print(currentState[k][j] + "");
				}
				System.out.println(")");
			}
			System.out.println("----------------------------------------------------------");

			if (currentF < bestF) { // simpan f(x) terbaik;
				bestF = currentF;
				bestState = currentState;
			}
		}

		System.out.printf("Simulated Annealing BEST:\n");
		System.out.printf("Best f = %.5f\n", ((1.0 * bestF) / (1.0 * banyakRumah)));

		System.out.println("Best fire station coordinates (x, y):");
		for (int k = 0; k < bestState.length; k++) {
			System.out.print("(");
			for (int j = 0; j < bestState[k].length; j++) {
				System.out.print(bestState[k][j] + "");
			}
			System.out.println(")");
		}
		System.out.println("----------------------------------------------------------");
	}
}

// Yg davin ubah tadi di bagian:
// 1. If isNotOutOfBound() jadi (deltaE < 0) sblmnya (deltaE > 0)
// 2. ngebalikin yg if topNeighborStates == null dkk nya