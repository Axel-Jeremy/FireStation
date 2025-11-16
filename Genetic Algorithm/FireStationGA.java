import java.util.Random;
import java.util.ArrayList;
import java.util.List;

// Kontroler dari GA yang aakan mengatur GA dan menjalankan crossover serta mutasi
public class FireStationGA {
    Random MyRand; // Generator angka acak
    public int maxPopulationSize; // Ukuran maksimal populasi
    public double elitismPct; // Persentase elitism
    public double crossoverRate; // Probabilitas cross over
    public double mutationRate; // Probabtilitas mutasi
    public int totalGeneration; // Jumlah generasi yang akan dijalankan
    int maxCapacity;

    /**
     * Konstruktor
     * @param MyRand Generator angka acak
     * @param totalGeneration Jumlah generasi
     * @param maxPopulationSize Ukuran maksimal populasi
     * @param elitismPct Persentase elitism
     * @param crossoverRate Probabilitas cross over
     * @param mutationRate Probabilitas mutasi
     * @param maxCapacity
     */
    public FireStationGA(Random MyRand, int totalGeneration, int maxPopulationSize, double elitismPct,
            double crossoverRate, double mutationRate, int maxCapacity) {
        this.MyRand = MyRand; // MyRand adalah random generator yang dikirim dari luar
        this.totalGeneration = totalGeneration;
        this.maxPopulationSize = maxPopulationSize;
        this.elitismPct = elitismPct;
        this.crossoverRate = crossoverRate;
        this.mutationRate = mutationRate;
        this.maxCapacity = maxCapacity;
    }

    // Menjalankan proses GA secara keseluruhan
    public Individual run() {
        int generation = 1;

        // buat populasi awal
        Population currentPop = new Population(MyRand, this.maxCapacity, this.maxPopulationSize, this.elitismPct);
        currentPop.randomPopulation(); // populasi diisi individu random
        currentPop.computeAllFitnesses(); // hitung seluruh fitnessnya

        // algogen mulai di sini
        while (terminate(generation) == false) { // jika belum memenuhi kriteria terminasi
            // buat populasi awal dengan elitism, bbrp individu terbaik dari populasi sebelumnya
            Population newPop = currentPop.getNewPopulationWElit();
            while (newPop.isFilled() == false) { // selain elitism, sisanya diisi dengan crossover
                Individual[] parents = currentPop.selectParentByRank(); // pilih parent
                if (this.MyRand.nextDouble() < this.crossoverRate) { // apakah terjadi kawin silang?
                    Individual[] child = parents[0].doCrossover(parents[1]); // jika ya, crossover kedua parent untuk
                                                                             // mendapatkan satu anak
                    for (int i = 0; i < child.length; i++) {
                        if (this.MyRand.nextDouble() < this.mutationRate) { // apakah terjadi mutasi?
                            child[i].doMutation();
                        }
                    }
                    for (int i = 0; i < child.length; i++) {
                        newPop.addIndividual(child[i]); // masukkan anak ke dalam populasi
                    }
                }
            }
            generation++; // sudah ada generasi baru
            currentPop = newPop; // generasi baru menggantikan generasi sebelumnya
            currentPop.computeAllFitnesses(); // hitung fitness generasi baru
        }
        return currentPop.getBestIdv(); // return individu terbaik dari generasi terakhir
    }

    // Berhenti ketika jumlah generasi sudah mencapai target
    public boolean terminate(int generation) {
        if (generation >= this.totalGeneration)
            return true;
        else
            return false;
    }
}
