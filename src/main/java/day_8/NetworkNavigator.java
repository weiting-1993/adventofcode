package day_8;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NetworkNavigator
{
    private static final String INPUT_FILE_PATH = "src/main/java/day_8/input.txt";
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

            if (!Objects.equals(finalPosition, FINAL_POSITION) && i == directions.length) {
                i = 0;
            }
        }

        return stepCount;
    }
}
