package year2023.day11;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;

public class GalaxyDistanceCalculator
{
    private static final String INPUT_FILE_PATH = "src/main/java/year2023/day11/input.txt";
    private static final Logger logger = Logger.getLogger(GalaxyDistanceCalculator.class.getName());
    private static final char GALAXY = '#';

    public static void main(String[] args)
    {
        try {
            var input = Files.readAllLines(Paths.get(INPUT_FILE_PATH));

            var originalCoordinates = findGalaxyCoordinates(input);
            var modifiedCoordinates = expandGalaxyCoordinates(originalCoordinates, input);

            System.out.println("Part 1: " + calculateSumOfLengthBetweenGalaxyPairs(modifiedCoordinates));
        } catch (IOException e) {
            logger.log(Level.WARNING, "An error occurred while trying to read a file.", e);
        }
    }

    private static List<int[]> expandGalaxyCoordinates(List<int[]> galaxyCoordinates, List<String> originalInput)
    {
        var galaxyXCoordinates = galaxyCoordinates.stream()
            .map(coordinatePair -> coordinatePair[0])
            .toList();
        var galaxyYCoordinates = galaxyCoordinates.stream()
            .map(coordinatePair -> coordinatePair[1])
            .toList();
        List<Integer> expandingGalaxyXCoordinates = galaxyXCoordinates;
        List<Integer> expandingGalaxyYCoordinates = galaxyYCoordinates;
        var allXIndices = IntStream.range(0, originalInput.getFirst().toCharArray().length)
            .boxed()
            .toList();
        var allYIndices = IntStream.range(0, originalInput.size())
            .boxed()
            .toList();
        var missingXIndices = allXIndices.stream()
            .filter(index -> !galaxyXCoordinates.contains(index))
            .toList();
        var missingYIndices = allYIndices.stream()
            .filter(index -> !galaxyYCoordinates.contains(index))
            .toList();
        var expandingMissingXIndices = IntStream.range(0, missingXIndices.size())
            .mapToObj(i -> missingXIndices.get(i) + i)
            .toList();
        var expandingMissingYIndices = IntStream.range(0, missingYIndices.size())
            .mapToObj(i -> missingYIndices.get(i) + i)
            .toList();

        for (int x: expandingMissingXIndices) {
            expandingGalaxyXCoordinates = expandingGalaxyXCoordinates.stream()
                .map(xCoordinate -> xCoordinate > x ? xCoordinate + 1 : xCoordinate).toList();
        }

        for (int y: expandingMissingYIndices) {
            expandingGalaxyYCoordinates = expandingGalaxyYCoordinates.stream().map(yCoordinate -> yCoordinate > y ? yCoordinate + 1 : yCoordinate).toList();
        }

        IntStream indices = IntStream.range(0, Math.min(expandingGalaxyXCoordinates.size(), expandingGalaxyYCoordinates.size()));
        List<Integer> finalExpandingGalaxyXCoordinates = expandingGalaxyXCoordinates;
        List<Integer> finalExpandingGalaxyYCoordinates = expandingGalaxyYCoordinates;

        return indices.mapToObj(i -> new int[]{finalExpandingGalaxyXCoordinates.get(i), finalExpandingGalaxyYCoordinates.get(i)}).toList();
    }

    private static List<int[]> findGalaxyCoordinates(List<String> input)
    {
        List<int[]> galaxyCoordinates = new ArrayList<>();
        var lineIndex = 0;

        for (String line: input) {
            var chars = line.toCharArray();
            var charIndex = 0;

            for (char character: chars) {
                if (character == GALAXY || Character.isDigit(character)) {
                    galaxyCoordinates.add(new int[]{charIndex, lineIndex});
                }

                charIndex++;
            }

            lineIndex++;
        }

        return galaxyCoordinates;
    }

    private static int calculateSumOfLengthBetweenGalaxyPairs(List<int[]> expandingGalaxyCoordinates)
    {
        var galaxyIndex = 0;
        var sum = 0;

        for (int[] currentGalaxyCoordinatePair : expandingGalaxyCoordinates) {
            for (var j = galaxyIndex + 1; j < expandingGalaxyCoordinates.size(); j++) {
                var nextGalaxyCoordinatePair = expandingGalaxyCoordinates.get(j);
                sum += Math.abs(currentGalaxyCoordinatePair[0] - nextGalaxyCoordinatePair[0]);
                sum += Math.abs(currentGalaxyCoordinatePair[1] - nextGalaxyCoordinatePair[1]);
            }

            galaxyIndex++;
        }

        return sum;
    }
}
