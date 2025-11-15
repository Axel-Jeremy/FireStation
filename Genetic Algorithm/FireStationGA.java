import java.util.Random;
import java.util.ArrayList;
import java.util.List;

public class FireStationGA {
    Random MyRand;
    public int maxPopulationSize;
    public double elitismPct;
    public double crossoverRate;
    public double mutationRate;
    public int totalGeneration;
    int maxCapacity;


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

    public Individual run() {
        int generation = 1;
        // buat populasi awal
        Population currentPop = new Population(MyRand, this.maxCapacity, this.maxPopulationSize, this.elitismPct);
        currentPop.randomPopulation(); // populasi diisi individu random
        currentPop.computeAllFitnesses(); // hitung seluruh fitnessnya

        // algogen mulai di sini
        while (terminate(generation) == false) { // jika belum memenuhi kriteria terminasi
            // buat populasi awal dengan elitism, bbrp individu terbaik dari populasi
            // ebelumnya sudah masuk
            Population newPop = currentPop.getNewPopulationWElit();
            while (newPop.isFilled() == false) { // selain elitism, sisanya diisi dengan crossover
                Individual[] parents = currentPop.selectParentByRank(); // pilih parent
                if (this.MyRand.nextDouble() < this.crossoverRate) { // apakah terjadi kawin silang?
                    Individual[] child = parents[0].doCrossover(parents[1]); // jika ya, crossover kedua parent untuk
                                                                             // mendapatkan satu anak
                    for (int i = 0; i < child.length; i++) {
                        if (this.MyRand.nextDouble() < this.mutationRate) { // apakah terjadi mutasi?
                            child[i].doMutation();
                            // newPop.addIndividual(child[i]);
                            // System.out.println(this.mutationRate);
                        }
                        // System.out.println(i);
                    }
                    for (int i = 0; i < child.length; i++) {
                        // if (this.MyRand.nextDouble() < this.mutationRate) { // apakah terjadi mutasi?
                        newPop.addIndividual(child[i]); // masukkan anak ke dalam populasi
                        // }
                    }
                }
            }
            generation++; // sudah ada generasi baru
            currentPop = newPop; // generasi baru menggantikan generasi sebelumnya
            currentPop.computeAllFitnesses(); // hitung fitness generasi baru
        }
        return currentPop.getBestIdv(); // return individu terbaik dari generasi terakhir
    }

    public boolean terminate(int generation) {
        if (generation >= this.totalGeneration)
            return true;
        else
            return false;
        // or by running time
        // or population not changed
    }
}
