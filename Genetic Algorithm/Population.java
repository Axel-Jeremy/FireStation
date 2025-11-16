import java.util.*;

/**
 * Mengelola seluruh populasiuntuk GA, untuk membuat populasi awal, mengelola elitism,
 * dan memilih orang tua (parent selection).
 */

public class Population {
    public ArrayList<Individual> population; // list dari semua individu
    private int maxPopulationSize; // Ukuran maksimal dari populasi
    private int populationSize = 0; // Ukuran saat ini dari populasi
    public double elitismPct; // Persentase individu terbaik akan terbawa ke gen selanjutnya
    int maxCapacity; // banyak firestation yang di deklarasi
    int sumRank = 0; // Jumlah total peringkat untuk rank selection
    Random MyRand; 
    static int[][] map;

    /**
     * Konstruktor untuk membuat objek populasi baru (awalnya kosong).
     * @param MyRand Generator acak
     * @param maxCapacity ukuran maksimal
     * @param maxPopulationSize Ukuran populasi yang diinginkan
     * @param elitismPct Persentase elitism
     */
    public Population(Random MyRand, int maxCapacity, int maxPopulationSize, double elitismPct) {
        this.MyRand = MyRand; // menggunakan random generator dari luar
        this.maxPopulationSize = maxPopulationSize;
        this.population = new ArrayList<Individual>();
        this.elitismPct = elitismPct;
        this.maxCapacity = maxCapacity;

        for (int i = 1; i <= this.maxPopulationSize; i++)
            this.sumRank = this.sumRank + i;

    }

    // Mengisi populasi dengan individu acak dan akan digunakan untuk membuat populasi di gen 1
    public void randomPopulation() { 
        for (int i = 0; i < this.maxPopulationSize; i++) {
            this.addIndividual(new Individual(this.MyRand));
        }
    }

    public boolean addIndividual(Individual newIdv) { // masukkan individu baru
        if (this.populationSize >= this.maxPopulationSize)
            return false;
        this.population.add(newIdv);
        this.populationSize++;
        return true;
    }

    public void computeAllFitnesses() { // hitung fitness seluruh individu dalam populasi kemudian urutkan
        for (Individual individu : population) {
            individu.setFitness(individu.chromosome);
        }
        // Lakukan pengurutan populasi dimana fitness terendah di indeks 0 (paling bagus)
        Collections.sort(this.population);
    }

    // Get new Population With Elitism
    public Population getNewPopulationWElit() {
        // Panggil konstarktor population baru
        Population newPop = new Population(this.MyRand, this.maxCapacity, this.populationSize, this.elitismPct);
        int n = (int) (this.elitismPct * this.maxPopulationSize);

        // System.out.println(n);
        // System.out.println(this.population.size());

        // Ambil yang terbaik
        for (int i = 0; i < n; i++) {
            // System.out.println(i);
            // boolean res = newPop.addIndividual(this.population.get(i));
            newPop.addIndividual(this.population.get(i));
        }
        return newPop;
    }

    // Memeriksa apakah populasi sudah penuh atau belum
    public boolean isFilled() {
        return this.maxPopulationSize == this.populationSize;
    }

    // pemilihan 2 parent dengan teknik roulette wheel
    public Individual[] selectParent() {
        Individual[] parents = new Individual[2];
        // this.population.sort((idv1,idv2) -> idv1.compareTo(idv2));

        // int top = this.population.size() + 1;
        long sumfitness = 0;
        for (int i = 0; i < this.population.size(); i++) {
            sumfitness = sumfitness + this.population.get(i).fitness;
        }
        // System.out.println(sumfitness);
        for (int i = 0; i < this.population.size(); i++) {
            ((Individual) this.population.get(i)).parentProbability = 1
                    - ((1.0 * this.population.get(i).fitness) / sumfitness);
        }

        // Kode untuk roulette wheel selection
        for (int n = 0; n < 2; n++) {
            int i = -1;
            double prob = this.MyRand.nextDouble();
            double sum = 0.0;
            do {
                i++;
                sum = sum + this.population.get(i).parentProbability;
            } while (sum < prob);
            parents[n] = this.population.get(i);
        }
        return parents;
    }

    //rank selection
    public Individual[] selectParentByRank() {
        Individual[] parents = new Individual[2];
        int N = this.population.size();

        // sort populasi berdasarkan fitnessnya
        Collections.sort(this.population);

        // tetapkan probabilitas berdasarkan Peringkat (Rank).
        // individu terbaik (index 0) mendapat peringkat N.
        // individu terburuk (index N-1) mendapat peringkat 1.
        if (this.sumRank == 0) { // Pengaman jika sumRank tidak terinisialisasi
            for (int i = 1; i <= N; i++)
                this.sumRank += i;
        }

        for (int i = 0; i < N; i++) {
            // i = 0 adalah yang terbaik, i = N-1 adalah yang terburuk.
            int rank = N - i; // Peringkat terbalik (terbaik = N, terburuk = 1)
            this.population.get(i).parentProbability = (double) rank / this.sumRank;
        }

        // lakukan "Roulette Wheel" pada Peringkat (Rank).
        // loop ini memilih 2 parent.
        for (int n = 0; n < 2; n++) {
            double prob = this.MyRand.nextDouble();
            double cumulativeSum = 0.0;
            int i = 0; // mulai dari individu terbaik

            // loop untuk menemukan parent berdasarkan prob
            while (i < N - 1) { // berhenti di N-1 untuk menghindari out of bounds
                cumulativeSum += this.population.get(i).parentProbability;
                if (cumulativeSum >= prob) {
                    break;
                }
                i++;
            }
            // Jika loop selesai (karena prob sangat tinggi atau error floating point),
            // kita pilih individu terakhir (i akan bernilai N-1).
            parents[n] = this.population.get(i);
        }

        return parents;
    }

    public Individual getBestIdv() { // return individu terbaik
        // int top = this.population.size()+1;
        return this.population.get(0);
    }

    @Override
    public String toString() {
        String res = new String();
        for (int i = 0; i < this.population.size(); i++) {
            res = res + new String(this.population.get(i) + "\n");
        }
        return res;
    }
}
