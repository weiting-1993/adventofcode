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

    public static void main(String[] args)
    {
        try {
            List<String> input = Files.readAllLines(Paths.get(INPUT_FILE_PATH));
            List<Integer> extrapolatedValueOfEachHistory = new ArrayList<>();

            for (String line: input) {
                List<Integer> historicalData = Arrays.stream(line.trim().split(" ")).map(Integer::parseInt).toList();
                int extrapolatedValue = calculateExtrapolatedValue(historicalData);
                extrapolatedValueOfEachHistory.add(extrapolatedValue);
            }

            System.out.println("Part 1: " + extrapolatedValueOfEachHistory.stream().mapToInt(Integer::intValue).sum());
        } catch (IOException e) {
            logger.log(Level.WARNING, "An error occurred while trying to read a file.", e);
        }
    }

    public static int calculateExtrapolatedValue(List<Integer> historicalData)
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

        int extrapolatedValue = historicalData.getLast();
        int accumulatedValue = 0;

        for (int i = differenceListCollection.size() - 1; i >= 0; i--) {
            accumulatedValue += differenceListCollection.get(i).getLast();
        }

        return extrapolatedValue + accumulatedValue;
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
