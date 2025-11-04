import java.util.*;

public class MyHC {
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

	//alternatif mencari neighbor state di antara [-stepSize, stepSize]
	static double getNeighbor(double currentX, double stepSize) {
		double step = (rnd.nextDouble() * 2 * stepSize) - stepSize;
        double newX = currentX + step;
		newX = clamp(newX);
        return newX;
    }
	
	/**
	* Hill Climbing
	* @param stepSize berapa jauh "lompat" ke tetangga
	* @param maxIter iterasi maksimum
	* @return x dengan f(x) terbesar
	*/	
    static double hillClimbing(double stepSize, int maxIter) {
		double randPos = MIN_X + (rnd.nextDouble() * (MAX_X - MIN_X)); 	//posisi awal: random
		System.out.printf("Initial posistion: %.6f\n",randPos);

		double bestX = clamp(randPos); 	//pastikan awalnya di antara MIN_X - MAX_X
        double bestF = f(bestX);			//hitung f(x)-nya
        for (int it=1; it<=maxIter; it++) { //lakukan sampai maxIter
            
			//buat neighbor state-nya --> bisa gunakan getNeighbor()
			double leftX = clamp(bestX - stepSize);	//tetangga kiri
            double rightX = clamp(bestX + stepSize);	//tetangga kanan
            double leftF = f(leftX);			//hitung f()-nya
            double rightF = f(rightX);
			
            if (leftF > bestF) {	//tetangga kiri lebih baik
                bestX = leftX; 
				bestF = leftF;
            } else if (rightF > bestF) { //tetangga kanan lebih baik 
                bestX = rightX; 
				bestF = rightF;
            } else {	//local maximum?
                stepSize = stepSize * 0.5; //kurangi step
            }
        }
        return bestX;
    }

	/**
	* random restart hill climbing
	* @param nRestarts berapa kali melakukan hill climbing
	* @param step berapa jauh "lompat" ke tetangga
	* @param runs iterasi maksimum
	* @return x dengan f(x) terbesar
	*/
    static double randomRestartHC(int nRestarts, double step, int iter) {
        double bestX = MIN_X;	//x terbaik saat ini
        double bestF = f(bestX);	//f(x)-nya
		
        for (int r=1; r<=nRestarts; r++) { //ulangi nRestarts kali
			System.out.printf("Run %d\n",r);
            double x = hillClimbing(step, iter);	//x terbaik hasil HC
            double fx = f(x);	//f(x)-nya
			System.out.printf("Hill Climbing result: best x=%.6f f(x)=%.6f%n",x,fx);
            System.out.println("----------------------------------------------");
			if (fx>bestF) { //simpan f(x) terbaik;
				bestF = fx; 
				bestX = x; 
			}
        }
        return bestX;
    }

    
    public static void main(String[] args) {
        // Random-restart Hill climbing 
        double R2HC = randomRestartHC(20, 0.35, 100);
        System.out.printf("Hill Climbing best x=%.6f f(x)=%.6f%n", R2HC, f(R2HC));

    }
}