import java.util.Random;
import java.util.ArrayList;

public class KnapsackGAcopy {

    Random MyRand;
    public int maxPopulationSize;
    public double elitismPct;
    public double crossoverRate;
    public double mutationRate;
    public int totalGeneration;
    ArrayList<Item> listOfItems;
    int maxCapacity;

    public KnapsackGAcopy(Random MyRand, int totalGeneration, int maxPopulationSize, double elitismPct,
        double crossoverRate, double mutationRate, ArrayList<Item> listOfItems, int maxCapacity) {
        this.MyRand = MyRand;		//MyRand adalah random generator yang dikirim dari luar
        this.totalGeneration = totalGeneration;
        this.maxPopulationSize = maxPopulationSize;
        this.elitismPct = elitismPct;
        this.crossoverRate = crossoverRate;
        this.mutationRate = mutationRate;
        this.listOfItems = listOfItems;
        this.maxCapacity = maxCapacity;
    }

    public Individual run() {
        int generation = 1;
		//buat populasi awal 
        Population currentPop = new Population(MyRand,this.listOfItems, this.maxCapacity, this.maxPopulationSize, this.elitismPct);
        currentPop.randomPopulation();	//populasi diisi individu random
        currentPop.computeAllFitnesses();	//hitung seluruh fitnessnya
        
		//algogen mulai di sini
		while (terminate(generation)==false) {				//jika belum memenuhi kriteria terminasi
			//buat populasi awal dengan elitism, bbrp individu terbaik dari populasi ebelumnya sudah masuk
            Population newPop = currentPop.getNewPopulationWElit(); 	
            while (newPop.isFilled()==false) {							//selain elitism, sisanya diis dengan crossover
                Individual[] parents = currentPop.selectParent();		//pilih parent 
                if (this.MyRand.nextDouble()<this.crossoverRate) {		//apakah terjadi kawin silang?
                    Individual child = parents[0].doCrossover(parents[1]);	//jika ya, crossover kedua parent untuk mendapatkan satu anak															
                    if (this.MyRand.nextDouble()<this.mutationRate) {	//apakah terjadi mutasi?
                        child.doMutation();
                    }
                    newPop.addIndividual(child);	//masukkan anak ke dalam populasi
                }
            }
            generation++;						//sudah ada generasi baru
            currentPop = newPop;				//generasi baru menggantikan generasi sebelumnya
            currentPop.computeAllFitnesses();	//hitung fitness generasi baru
       }
        return currentPop.getBestIdv();			//return individu terbaik dari generasi terakhir
    }

    public boolean terminate(int generation){
        if (generation >= this.totalGeneration) return true;
        else return false;
        //or by running time
        //or population not changed
    }



}

//Note - felix
/* fireStation position
    terdiri dari 8 bit
    4 bit awal convert dari integer ke binnary untuk posisi x
    4 bit berikutnya convert dari inteeger ke binnary untuk posisi y
*/
class fireStationPosition {
    public String coordinate;

    public fireStationPosition(int x, int y){
        this.coordinate = convertIntToBin(x)+convertIntToBin(y);
    }
      
    
    public String convertIntToBin(int temp){
        return Integer.toBinaryString(temp);
    }
}