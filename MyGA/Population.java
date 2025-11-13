import java.util.Random;
import java.util.ArrayList;
import java.util.*;

public class Population {
    public ArrayList<Individual> population;
    private int maxPopulationSize;
    private int populationSize = 0;
    public double elitismPct;
    // ArrayList<Item> listOfItems;
    int maxCapacity;
    int sumRank = 0;
    Random MyRand;
    public int banyakFireStation; // banyak firestation yang di deklarasi
    static int[][] map;

    public Population(Random MyRand, /* ArrayList<Item> listOfItems, */ int maxCapacity, int maxPopulationSize,
            double elitismPct) {
        // skala 0-1
        this.MyRand = MyRand; // menggunakan random generator dari luar
        this.maxPopulationSize = maxPopulationSize;
        this.population = new ArrayList<Individual>();
        this.elitismPct = elitismPct;
        // this.listOfItems = listOfItems;
        this.maxCapacity = maxCapacity;
        this.banyakFireStation = maxCapacity;

        for (int i = 1; i <= this.maxPopulationSize; i++)
            this.sumRank = this.sumRank + i;
    }

    public void randomPopulation() {
        for (int i = 0; i < this.maxPopulationSize; i++) {
            this.addIndividual(new Individual(this.MyRand, this.banyakFireStation));
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
            // panggil setFitness jika belum dihitung
            if (individu.fitness == Integer.MAX_VALUE) {
                individu.setFitness(individu.chromosome);
            }
        }
        // Urutkan populasi berdasarkan fitness terkecil
        Collections.sort(this.population);
    }

    // Get new Population With Elitism
    public Population getNewPopulationWElit() {
        // Panggil konstarktor population baru
        Population newPop = new Population(this.MyRand, this.maxCapacity, this.populationSize, this.elitismPct);

        int n = (int) (this.elitismPct * this.maxPopulationSize);

        // System.out.println(n);
        // System.out.println(this.population.size());

        // Ambil n terbaik
        for (int i = 0; i < n; i++) {
            // System.out.println(i);
            // boolean res = newPop.addIndividual(this.population.get(i));
            newPop.addIndividual(this.population.get(i));
        }
        return newPop;
    }

    public boolean isFilled() {
        return this.maxPopulationSize == this.populationSize;
    }

    /*
     * public Individual[] selectParent() { //rank selection
     * Individual[] parents = new Individual[2];
     * this.population.sort((idv1,idv2) -> idv1.compareTo(idv2));
     * int top = this.population.size()+1;
     * for (int i=0;i<this.population.size();i++) {
     * ((Individual)this.population.get(i)).parentProbability =
     * (1.0*top)/this.sumRank;
     * }
     * for (int n = 0;n<2;n++) {
     * int i=-1;
     * double prob = this.MyRand.nextDouble();
     * double sum = 0.0;
     * do {
     * i++;
     * sum = sum + this.population.get(i).parentProbability;
     * } while(sum<prob);
     * parents[n] = this.population.get(i);
     * }
     * return parents;
     * }
     */

    // pemilihan 2 parent dengan teknik roulette wheel
    public Individual[] selectParent() {
        Individual[] parents = new Individual[2];

        // this.population.sort((idv1,idv2) -> idv1.compareTo(idv2));
        //int top = this.population.size() + 1;

        long sumfitness = 0;
        for (int i = 0; i < this.population.size(); i++) {
            sumfitness = sumfitness + this.population.get(i).fitness;
        }
        // System.out.println(sumfitness);
        for (int i = 0; i < this.population.size(); i++) {
            ((Individual) this.population.get(i)).parentProbability = (1.0 * this.population.get(i).fitness) / sumfitness;
        }

        // Kode untuk roulette
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