package day_6;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RaceWinPossibilityCalculator
{
    private static final String INPUT_FILE_PATH = "src/main/java/day_6/input.txt";
    private static final Logger logger = Logger.getLogger(RaceWinPossibilityCalculator.class.getName());
    private static final String RACE_TIME = "time";
    private static final String RACE_DISTANCE = "distance";

    public static void main(String[] args)
    {
        try {
            List<String> input = Files.readAllLines(Paths.get(INPUT_FILE_PATH));
            List<HashMap<String, Integer>> raceList = createRaceList(input);

            int totalWinningPossibilities = 1;

            for (HashMap<String, Integer> raceMap: raceList) {
                totalWinningPossibilities *= calculateWinningPossibilities(raceMap);
            }

            System.out.println("Part 1: " + totalWinningPossibilities);
        } catch (IOException e) {
            logger.log(Level.WARNING, "An error occurred", e);
        }
    }

    public static List<HashMap<String, Integer>> createRaceList(List<String> input)
    {
        List<HashMap<String, Integer>> raceMap = new ArrayList<>();

        Pattern digitPattern = Pattern.compile("\\d+");

        String timeOfAllRaces = input.getFirst().split(":")[1].trim();
        String distanceOfAllRaces = input.getLast().split(":")[1].trim();

        Matcher timeMatcher = digitPattern.matcher(timeOfAllRaces);
        Matcher distanceMatcher = digitPattern.matcher(distanceOfAllRaces);

        while (timeMatcher.find() && distanceMatcher.find()) {
            HashMap<String, Integer> timeAndDistanceMap = new HashMap<>();

            timeAndDistanceMap.put(RACE_TIME, Integer.parseInt(timeMatcher.group()));
            timeAndDistanceMap.put(RACE_DISTANCE, Integer.parseInt(distanceMatcher.group()));

            raceMap.add(timeAndDistanceMap);
        }

        return raceMap;
    }

    public static int calculateWinningPossibilities(HashMap<String, Integer> raceMap)
    {
        int winningPossibilities = 0;
        int raceTime = raceMap.get(RACE_TIME);
        int raceDistanceRecord = raceMap.get(RACE_DISTANCE);

        for (int carSpeed = 0; carSpeed <= raceTime; carSpeed++) {
            int timeForMoving = raceTime - carSpeed;
            int movedDistance = timeForMoving * carSpeed;

            if (movedDistance > raceDistanceRecord) {
                winningPossibilities++;
            }
        }

        return winningPossibilities;
    }
}
