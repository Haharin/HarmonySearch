import org.omg.CORBA.INTERNAL;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

public class Main
{
    private static Random generator = new Random();
    private static boolean biggerIsBetter = false;
    public static void main(String[] args) throws IOException {
        Scanner in = new Scanner(System.in);
        System.out.print("worst criteria (0) average criteria (1): ");
        int criteria = in.nextInt();
        System.out.print("Harmony Memory Size: ");
        int HMS = in.nextInt();
        System.out.print("Dimension for evaluate: ");
        int dimensions = in.nextInt();
//        System.out.print("minimum: ");
//        double randMin = in.nextDouble();
        double randMin = -Math.PI;
//        System.out.print("maximum: ");
//        double randMax = in.nextDouble();
        double randMax = Math.PI;
        System.out.print("shift value: ");
        double shiftValue = in.nextDouble();
        Double[][] harmonyMemory = new Double[HMS][dimensions];


        for (int i = 0; i < HMS; i++){
            for (int j = 0; j < dimensions; j++){
                harmonyMemory[i][j] = getRandomInRange(randMin, randMax);
//                System.out.println(harmonyMemory[i][j]);
            }
        }

        System.out.print("Maximum number of iterations: ");
        long numberOfIterations = in.nextLong();
        System.out.print("Maximum number of repetitions: ");
        long maxNumberOfRepetions = in.nextLong();
        System.out.print("HMCR: ");
        double HMCR = in.nextDouble();
        System.out.print("mutation rate: ");
        double mutationRate = in.nextDouble();
        System.out.print("fix the maximum number of possibles parents? (1 yes, 0 no): ");
        List<Integer> usedParents;
        Integer[] selectedParents = null;
        if (in.nextInt() == 1){
            System.out.print("number of maximum possibles parents: ");
            int numberOfParents = in.nextInt();
            selectedParents = new Integer[numberOfParents];
        };
        long iterations = 0;
        Double[] newHarmony = new Double[dimensions];
        String fileName = criteria + "_" +
                HMS + "_" +
                dimensions + "_" +
                "-PI" + "_" +
                "+PI" + "_" +
                shiftValue + "_" +
                numberOfIterations + "_" +
                maxNumberOfRepetions + "_" +
                HMCR + "_" +
                mutationRate + "_" +
                LocalDateTime.now().toString().replace(':', '-');
        File file = new File("fitnessValue" + fileName);
        File file2 = new File("xValues" + fileName);

        file.createNewFile();
        file2.createNewFile();
        FileWriter writer = new FileWriter(file);
        FileWriter writer2 = new FileWriter(file2);

        long numberOfRepetitions = 0;
        double previousValue = 0.0;
        while (iterations < numberOfIterations && numberOfRepetitions < maxNumberOfRepetions){
            usedParents = new ArrayList<Integer>();
            if(selectedParents != null){
                for(int i = 0; i < selectedParents.length; i++){
                    Integer selected = generator.nextInt(HMS);
                    boolean contains = false;
                    for (Integer selectedParent : selectedParents) {
                        if (selectedParent == selected){
                            contains = true;
                        }
                    }
                    if (contains){
                        i--;
                    }
                    else {
                        selectedParents[i] = selected;
                    }
                }
                for (int j = 0; j < dimensions; j++){
                    if ( generator.nextDouble() < HMCR ){
                        usedParents.add(getRandomFromSelectedParents(selectedParents, usedParents, dimensions - j));
                        newHarmony[j] = harmonyMemory[usedParents.get(usedParents.size() - 1)][j];
                        if ( generator.nextDouble() < mutationRate){
                            newHarmony[j] = mutate(newHarmony[j], shiftValue, randMin, randMax);
//                            newHarmony[j] = newHarmony[j] + getRandomInRange(-shiftValue, shiftValue);
                        }
                    }
                    else{
                        newHarmony[j] = getRandomInRange(randMin, randMax);
                        usedParents.add(-1);
                    }
                }
            }
            else{
                for (int j = 0; j < dimensions; j++){
                    if ( generator.nextDouble() < HMCR){
                        usedParents.add(generator.nextInt(HMS));
                        newHarmony[j] = harmonyMemory[usedParents.get(usedParents.size() - 1)][j];
                        if ( generator.nextDouble() < mutationRate){
                            newHarmony[j] = mutate(newHarmony[j], shiftValue, randMin, randMax);
//                            newHarmony[j] = newHarmony[j] + getRandomInRange(-shiftValue, shiftValue);
                        }
                    }
                    else{
                        newHarmony[j] = getRandomInRange(randMin, randMax);
                    }
                }
            }
            int worstIndex = getWortsMember(harmonyMemory);
            double enoughToReplace;
            if (criteria == 0){
                enoughToReplace = fitnessFunction(harmonyMemory[worstIndex]);
            }
            else {
                enoughToReplace = getAverageFitness(harmonyMemory);
            }

            if (!isFirstBetter(enoughToReplace, fitnessFunction(newHarmony))){
                System.arraycopy(newHarmony, 0, harmonyMemory[worstIndex], 0, dimensions);
            }
            int bestIndex = getBestMember(harmonyMemory);
            double bestValue = fitnessFunction(harmonyMemory[bestIndex]);
            writer.write(iterations + ", " + bestValue + "\n");
            writer2.write(iterations + ", " + Arrays.toString(harmonyMemory[bestIndex]) + "\n");
            if (bestValue == previousValue){
                numberOfRepetitions++;
            }
            else{
                numberOfRepetitions = 0;
                previousValue = bestValue;
            }
            if (iterations % 100 == 0){
                System.out.println(Arrays.toString(harmonyMemory[bestIndex]));
                System.out.println(usedParents);
                System.out.println(bestValue);
            }
            iterations++;
        }
        int bestIndex = getBestMember(harmonyMemory);
        double bestValue = fitnessFunction(harmonyMemory[bestIndex]);
        System.out.println(Arrays.toString(harmonyMemory[bestIndex]));
        System.out.println(bestValue);
        writer.flush();
        writer.close();
        writer2.flush();
        writer2.close();
    }

    private static double getRandomInRange(Double min, Double max){
        return min + generator.nextDouble() * (max - min);
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
            if(!isFirstBetter(current, worst)){
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
            if(isFirstBetter(current, best)){
                best = current;
                n = i;
            }
        }
        return n;
    }

    private static double getAverageFitness(Double[][] memory){
        double summ = 0;
        for (int i = 0; i < memory.length; i++){
            summ += fitnessFunction(memory[0]);
        }
        return summ / memory.length;
    }

    private static boolean isFirstBetter(Double first, Double second){
        if (biggerIsBetter){
            return first >= second;
        }
        else{
            return first <= second;
        }
    }

    private static double mutate (double value, double shift, double min, double max){
        double newValue = value + getRandomInRange(-shift, shift);
        if (newValue < min || newValue > max){
            return  mutate(value, shift, min, max);
        }
        else {
            return newValue;
        }
    }

    private static Integer getRandomFromSelectedParents(Integer[] selectedParents, List<Integer> usedParents, int remain){
        Integer selected = selectedParents[generator.nextInt(selectedParents.length)];
        if (remain <= selectedParents.length - usedParents.size()){
            if (usedParents.contains(selected)){
                selected = getRandomFromSelectedParents(selectedParents, usedParents, remain);
                return selected;
            }
            return selected;
        }
        else {
            return selected;
        }
    }
}

