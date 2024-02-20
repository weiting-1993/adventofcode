package year2023.day9;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OasisReportAnalyser
{
    private static final String INPUT_FILE_PATH = "src/main/java/year2023/day9/input.txt";
    private static final Logger logger = Logger.getLogger(OasisReportAnalyser.class.getName());
    private static final String NEXT_RECORD = "next";
    private static final String PREV_RECORD = "previous";


    public static void main(String[] args)
    {
        try {
            List<String> input = Files.readAllLines(Paths.get(INPUT_FILE_PATH));
            List<Integer> nextExtrapolatedValueOfEachHistory = new ArrayList<>();
            List<Integer> previousExtrapolatedValueOfEachHistory = new ArrayList<>();

            for (String line: input) {
                List<Integer> historicalData = Arrays.stream(line.trim().split(" ")).map(Integer::parseInt).toList();
                int nextExtrapolatedValue = calculateExtrapolatedValue(historicalData, NEXT_RECORD);
                int prevExtrapolatedValue = calculateExtrapolatedValue(historicalData, PREV_RECORD);
                nextExtrapolatedValueOfEachHistory.add(nextExtrapolatedValue);
                previousExtrapolatedValueOfEachHistory.add(prevExtrapolatedValue);
            }

            System.out.println("Part 1: " + nextExtrapolatedValueOfEachHistory.stream().mapToInt(Integer::intValue).sum());
            System.out.println("Part 2: " + previousExtrapolatedValueOfEachHistory.stream().mapToInt(Integer::intValue).sum());
        } catch (IOException e) {
            logger.log(Level.WARNING, "An error occurred while trying to read a file.", e);
        }
    }

    public static int calculateExtrapolatedValue(List<Integer> historicalData, String when)
    {
        List<List<Integer>> differenceListCollection = new ArrayList<>();
        boolean finishCalculatingTheDifference = false;
        List<Integer> evaluatedData = historicalData;

        while (!finishCalculatingTheDifference) {
            List<Integer> differences = calculateDifferences(evaluatedData);
            if (differences.stream().filter(value -> value != 0).toList().isEmpty()) {
                finishCalculatingTheDifference = true;
            } else {
                differenceListCollection.add(differences);
                evaluatedData = differences;
            }
        }

        int extrapolatedValue = when.equals(NEXT_RECORD) ? historicalData.getLast() : historicalData.getFirst();
        int accumulatedValue = 0;

        for (int i = differenceListCollection.size() - 1; i >= 0; i--) {
            if (when.equals(NEXT_RECORD)) {
                accumulatedValue += differenceListCollection.get(i).getLast();
            } else {
                accumulatedValue = differenceListCollection.get(i).getFirst() - accumulatedValue;
            }
        }

        return when.equals(NEXT_RECORD) ? extrapolatedValue + accumulatedValue : extrapolatedValue - accumulatedValue;
    }

    public static List<Integer> calculateDifferences(List<Integer> originalList) {
        List<Integer> differenceList = new ArrayList<>();
        for (int i = 0; i < originalList.size() - 1; i++) {
            int current = originalList.get(i);
            int next = originalList.get(i + 1);
            differenceList.add(next - current);
        }

        return differenceList;
    }
}
