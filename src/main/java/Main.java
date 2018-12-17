import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class Main
{
    private static Random generator = new Random();
    private static boolean biggerIsBetter = false;
    public static void main(String[] args) {
        //test
        Double[] test = {2.2029, 1.5707, 1.285, 1.9231, 1.7205};
        System.out.println(fitnessFunction(test));
        System.out.println(test.length);
        Double[] test2 = {1.9256, 1.5521};
        System.out.println(fitnessFunction(test2));
        System.out.println(test2.length);
        //test end
        Scanner in = new Scanner(System.in);
        System.out.print("Harmony Memory Size: ");
        int HMS = in.nextInt();
        System.out.print("Dimension for evaluate: ");
        int dimensions = in.nextInt();
        System.out.print("minimum: ");
        int randMin = in.nextInt();
        System.out.print("maximum: ");
        int randMax = in.nextInt();
        System.out.print("shift value: ");
        int shiftValue = in.nextInt();
        Double[][] harmonyMemory = new Double[HMS][dimensions];


        for (int i = 0; i < HMS; i++){
            for (int j = 0; j < dimensions; j++){
                harmonyMemory[i][j] = getRandomInRange(randMin, randMax);
//                System.out.println(harmonyMemory[i][j]);
            }
        }

        System.out.print("Number of iterations: ");
        long numberOfIterations = in.nextLong();
        System.out.print("HMCR: ");
        double HMCR = in.nextDouble();
        System.out.print("mutation rate: ");
        double mutationRate = in.nextDouble();
        long iterations = 0;
        Double[] newHarmony = new Double[dimensions];
        while (iterations < numberOfIterations){
            for (int j = 0; j < dimensions; j++){
                if ( generator.nextDouble() < HMCR){
                    newHarmony[j] = harmonyMemory[generator.nextInt(HMS)][j];
                    if ( generator.nextDouble() < mutationRate){
                        newHarmony[j] = newHarmony[j] + getRandomInRange(-shiftValue, shiftValue);
                    }
                }
                else{
                    newHarmony[j] = getRandomInRange(randMin, randMax);
                }
            }
            int worstIndex = getWortsMember(harmonyMemory);
            double worstValue = fitnessFunction(
                    harmonyMemory[worstIndex]);
            if (!firstBetter(worstValue, fitnessFunction(newHarmony))){
                System.arraycopy(newHarmony, 0, harmonyMemory[worstIndex], 0, dimensions);
            }
            fitnessFunction(harmonyMemory[0]);
            if (iterations % 1000 == 0){
                int bestIndex = getBestMember(harmonyMemory);
                double bestValue = fitnessFunction(harmonyMemory[bestIndex]);
                System.out.println(Arrays.toString(harmonyMemory[bestIndex]));
                System.out.println(bestValue);
            }
            iterations++;
        }
        int bestIndex = getBestMember(harmonyMemory);
        double bestValue = fitnessFunction(harmonyMemory[bestIndex]);
        System.out.println(Arrays.toString(harmonyMemory[bestIndex]));
        System.out.println(bestValue);
    }

    private static double getRandomInRange(Integer min, Integer max){
        return min + generator.nextDouble() * (max - min + 1);
    }

    private static double fitnessFunction(Double[] vector){
        double result = 0.0;
        double m = 1;
        for (int i = 0; i < vector.length; i++){
            double x = vector[i];
            result += Math.sin(x) * Math.pow( Math.sin( ( (double) (i + 1) * Math.pow(x, 2.0)) / Math.PI ), 2.0 );
        }
        return -result;
    }

    private static int getWortsMember(Double[][] memory){
        double worst = fitnessFunction(memory[0]);
        int n = 0;
        for (int i = 1; i < memory.length; i++){
            double current = fitnessFunction(memory[i]);
            if(!firstBetter(current, worst)){
                worst = current;
                n = i;
            }
        }
        return n;
    }

    private static int getBestMember(Double[][] memory){
        double best = fitnessFunction(memory[0]);
        int n = 0;
        for (int i = 1; i < memory.length; i++){
            double current = fitnessFunction(memory[i]);
            if(firstBetter(current, best)){
                best = current;
                n = i;
            }
        }
        return n;
    }

    private static boolean firstBetter(Double first, Double second){
        if (biggerIsBetter){
            return first >= second;
        }
        else{
            return first <= second;
        }
    }
}

