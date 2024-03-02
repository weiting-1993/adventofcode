package year2023.day11;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

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
            var modifiedCoordinatesWith2TimesExpansion = expandGalaxyCoordinates(originalCoordinates, input, 2);
            var modifiedCoordinatesWith1millionTimesExpansion = expandGalaxyCoordinates(originalCoordinates, input, 1_000_000);

            System.out.println("Part 1: " + calculateSumOfLengthBetweenGalaxyPairs(modifiedCoordinatesWith2TimesExpansion));
            System.out.println("Part 2: " + calculateSumOfLengthBetweenGalaxyPairs(modifiedCoordinatesWith1millionTimesExpansion));
        } catch (IOException e) {
            logger.log(Level.WARNING, "An error occurred while trying to read a file.", e);
        }
    }

    private static List<long[]> expandGalaxyCoordinates(List<long[]> galaxyCoordinates, List<String> originalInput, long expandingRatio)
    {
        var expandingSpaceInBetween = expandingRatio - 1;
        var galaxyXCoordinates = galaxyCoordinates.stream()
            .map(coordinatePair -> coordinatePair[0])
            .toList();
        var galaxyYCoordinates = galaxyCoordinates.stream()
            .map(coordinatePair -> coordinatePair[1])
            .toList();
        List<Long> expandingGalaxyXCoordinates = galaxyXCoordinates;
        List<Long> expandingGalaxyYCoordinates = galaxyYCoordinates;
        var allXIndices = LongStream.range(0, originalInput.getFirst().toCharArray().length)
            .boxed()
            .toList();
        var allYIndices = LongStream.range(0, originalInput.size())
            .boxed()
            .toList();
        var missingXIndices = allXIndices.stream()
            .filter(index -> !galaxyXCoordinates.contains(index))
            .toList();
        var missingYIndices = allYIndices.stream()
            .filter(index -> !galaxyYCoordinates.contains(index))
            .toList();
        var expandingMissingXIndices = IntStream.range(0, missingXIndices.size())
            .mapToObj(i -> missingXIndices.get(i) + i * expandingSpaceInBetween)
            .toList();
        var expandingMissingYIndices = IntStream.range(0, missingYIndices.size())
            .mapToObj(i -> missingYIndices.get(i) + i * expandingSpaceInBetween)
            .toList();

        for (long x: expandingMissingXIndices) {
            expandingGalaxyXCoordinates = expandingGalaxyXCoordinates.stream()
                .map(xCoordinate -> xCoordinate > x ? xCoordinate + expandingSpaceInBetween : xCoordinate).toList();
        }

        for (long y: expandingMissingYIndices) {
            expandingGalaxyYCoordinates = expandingGalaxyYCoordinates.stream().map(yCoordinate -> yCoordinate > y ? yCoordinate + expandingSpaceInBetween : yCoordinate).toList();
        }

        LongStream indices = LongStream.range(0, Math.min(expandingGalaxyXCoordinates.size(), expandingGalaxyYCoordinates.size()));
        List<Long> finalExpandingGalaxyXCoordinates = expandingGalaxyXCoordinates;
        List<Long> finalExpandingGalaxyYCoordinates = expandingGalaxyYCoordinates;

        return indices.mapToObj(i -> new long[]{finalExpandingGalaxyXCoordinates.get((int)i), finalExpandingGalaxyYCoordinates.get((int)i)}).toList();
    }

    private static List<long[]> findGalaxyCoordinates(List<String> input)
    {
        List<long[]> galaxyCoordinates = new ArrayList<>();
        var lineIndex = 0;

        for (String line: input) {
            var chars = line.toCharArray();
            var charIndex = 0;

            for (char character: chars) {
                if (character == GALAXY || Character.isDigit(character)) {
                    galaxyCoordinates.add(new long[]{charIndex, lineIndex});
                }

                charIndex++;
            }

            lineIndex++;
        }

        return galaxyCoordinates;
    }

    private static long calculateSumOfLengthBetweenGalaxyPairs(List<long[]> expandingGalaxyCoordinates)
    {
        var galaxyIndex = 0;
        var sum = 0L;

        for (long[] currentGalaxyCoordinatePair : expandingGalaxyCoordinates) {
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
