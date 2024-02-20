package year2023.day8;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NetworkNavigator
{
    private static final String INPUT_FILE_PATH = "src/main/java/year2023/day8/input.txt";
    private static final Logger logger = Logger.getLogger(NetworkNavigator.class.getName());
    private static final String RIGHT = "R";
    private static final String LEFT = "L";
    private static final String START_POSITION = "AAA";
    private static final String FINAL_POSITION = "ZZZ";


    public static void main(String[] args)
    {
        try {
            List<String> input = Files.readAllLines(Paths.get(INPUT_FILE_PATH));
            String directionSequence = input.removeFirst().trim();
            input.removeFirst(); // remove the empty line

            System.out.println("Part 1: " + calculateTotalStepsForReachingFinalPosition(directionSequence, createNetworkNavigationMap(input)));
            System.out.println("Part 2: " + calculateTotalStepsForReachingFinalPositionWithSimultaneousMoves(directionSequence, createNetworkNavigationMap(input)));
        } catch (IOException e) {
            logger.log(Level.WARNING, "An error occurred", e);
        }
    }


    private static HashMap<String, HashMap<String, String>> createNetworkNavigationMap(List<String> input)
    {
        HashMap<String, HashMap<String, String>> networkNaviMap = new HashMap<>();
        for (String line: input) {
            HashMap<String, String> destMap = new HashMap<>();
            String[] lineSplit = line.replaceAll("[() ]", "").split("=");
            String startPosition = lineSplit[0].trim();
            String[] destinationArray = lineSplit[1].trim().split(",");

            destMap.put(LEFT, destinationArray[0]);
            destMap.put(RIGHT, destinationArray[1]);
            networkNaviMap.put(startPosition, destMap);
        }

        return networkNaviMap;
    }

    private static int calculateTotalStepsForReachingFinalPosition(String directionSequence, HashMap<String, HashMap<String, String>> map)
    {
        int stepCount = 0;
        boolean reachFinalPosition = false;
        String startPosition = START_POSITION;
        String finalPosition;
        char[] directions = directionSequence.toCharArray();
        int i = 0;

        while (!reachFinalPosition) {
            String currentDirection = String.valueOf(directions[i]);
            String reachedDestination = map.get(startPosition).get(currentDirection);
            finalPosition = reachedDestination;
            startPosition = reachedDestination;
            stepCount++;
            i++;

            if (Objects.equals(finalPosition, FINAL_POSITION)) {
                reachFinalPosition = true;
            }

            if (!reachFinalPosition && i == directions.length) {
                i = 0;
            }
        }

        return stepCount;
    }

    private static long calculateTotalStepsForReachingFinalPositionWithSimultaneousMoves(String directionSequence, HashMap<String, HashMap<String, String>> map)
    {
        List<String> startPositions = new ArrayList<>(map.keySet().stream().filter(str -> str.endsWith("A")).toList());
        List<Long> reachedZNodeCounts = new ArrayList<>();

        for (String startPosition: startPositions) {
            reachedZNodeCounts.add(calculateTotalStepsForReachingNodeEndsWithZ(directionSequence, map, startPosition));
        }

        return calculateLCM(reachedZNodeCounts);
    }

    private static long calculateTotalStepsForReachingNodeEndsWithZ(
        String directionSequence, HashMap<String, HashMap<String, String>> map, String startNode
    ) {
        long stepCount = 0;
        boolean reachFinalPosition = false;
        String startPosition = startNode;
        String finalPosition;
        char[] directions = directionSequence.toCharArray();
        int i = 0;

        while (!reachFinalPosition) {
            String currentDirection = String.valueOf(directions[i]);
            String reachedDestination = map.get(startPosition).get(currentDirection);
            finalPosition = reachedDestination;
            startPosition = reachedDestination;
            stepCount++;
            i++;

            if (finalPosition.endsWith("Z")) {
                reachFinalPosition = true;
            }

            if (!reachFinalPosition && i == directions.length) {
                i = 0;
            }
        }

        return stepCount;
    }

    // Method to calculate the LCM of two integers
    private static long calculateLCM(long a, long b) {
        return (a * b) / calculateGCD(a, b);
    }

    // Method to calculate the Greatest Common Divisor (GCD) of two integers
    private static long calculateGCD(long a, long b) {
        while (b != 0) {
            long temp = b;
            b = a % b;
            a = temp;
        }

        return a;
    }

    // Method to calculate the LCM of multiple integers
    private static long calculateLCM(List<Long> numbers) {
        long lcm = numbers.removeFirst();
        for (long number: numbers) {
            lcm = calculateLCM(lcm, number);
        }

        return lcm;
    }
}
