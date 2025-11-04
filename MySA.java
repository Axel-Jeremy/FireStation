import java.util.*;

public class MySA {
    static final Random rnd = new Random();
    static final double MIN_X = -6.0;
    static final double MAX_X = 6.0;

    // objective function (maximizing)
	//f(x)=−0.2x^4 + 1.2x^3 − 1.1x^2 + 0.3x + 0.5sin(19x) + 0.75cos(13x)
    static double f(double x) {
		double term1 = -0.2 * (x*x*x*x);
		double term2 =  1.2 * (x*x*x);
		double term3 = -1.1 * (x*x);
		double term4 =  0.3 * x;
		double term5 =  0.5 * Math.sin(19*x);
		double term6 =  0.75* Math.cos(13*x);
        return term1 + term2 + term3 + term4 + term5 + term6;
    }

    // pastikan x ada diantara MAX_X dan MIN_X;
    static double clamp(double x) {
		x = Math.max(MIN_X, x);
		x = Math.min(x, MAX_X);
        return x;
    }

    static double simulatedAnnealing(double t0, double cooling, double stopping_temp, double stepSize) {
		double currentX = clamp( MIN_X + rnd.nextDouble() * (MAX_X - MIN_X)); //posisi awal random		
        double currentF = f(currentX);
        double bestX = currentX;
        double bestF = currentF;        
        double T = t0;
        while(true) {	//sampai lebih kecil dari stopping_temp atau bisa diiterasi juga
			if (T < stopping_temp) break;
            // successor state: "perturbation" via gaussian (mean = 0, deviasi = stepSize)
			double successorX = clamp(currentX + rnd.nextGaussian() * stepSize); // getneighbor
            double successorF = f(successorX);	//hitung f()-nya
            double deltaE = successorF - currentF; 	//hitung delta
            if ((deltaE>0) || (rnd.nextDouble() <= Math.exp(deltaE/T))) {	//kriteria acceptance 
                currentX = successorX;	//pindah karena lebih baik
                currentF = successorF;	
				if (currentF > bestF) {  //simpan terbaik
					bestF = currentF;  
					bestX = currentX; 
				}
            }
            T = T * cooling; //turunkan suhu 
        }
        //return currentX;
		return bestX;
    }

	//run: SA 100 0.999 0.0001 0.1
    public static void main(String[] args) {
		double starting_temp = Double.parseDouble(args[0]);
		double cooling_rate = Double.parseDouble(args[1]);
		double stopping_temp= Double.parseDouble(args[2]);
		double stepSize = Double.parseDouble(args[3]);
		int runs = Integer.parseInt(args[4]);
		int i = 1;
		double bestX = MIN_X;
		double bestF = f(bestX);
		while (i++<=runs) {		//lakukan sebanyak runs kali
			System.out.printf("Run %d\n",i-1);
			//hasil SA terbaik
			double resX = simulatedAnnealing(starting_temp, cooling_rate, stopping_temp, stepSize);  
			double resF = f(resX);	//dan f()-nya
			System.out.printf("Simulated Annealing result x=%.6f f(x)=%.6f%n", resX, resF);
			System.out.println("----------------------------------------------------------");
			if (resF>bestF) { //simpan f(x) terbaik;
				bestF = resF; 
				bestX = resX; 
			}			
		}
		System.out.printf("Simulated Annealing best x=%.6f f(x)=%.6f%n", bestX, bestF);
    }
}
