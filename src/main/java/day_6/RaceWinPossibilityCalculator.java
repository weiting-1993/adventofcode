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
            List<HashMap<String, Long>> raceList = createRaceList(input);

            long totalWinningPossibilities = 1;

            for (HashMap<String, Long> raceMap: raceList) {
                totalWinningPossibilities *= calculateWinningPossibilities(raceMap);
            }

            System.out.println("Part 1: " + totalWinningPossibilities);
            System.out.println("Part 2: " + calculateWinningPossibilities(createRaceInfo(input)));
        } catch (IOException e) {
            logger.log(Level.WARNING, "An error occurred", e);
        }
    }

    public static List<HashMap<String, Long>> createRaceList(List<String> input)
    {
        List<HashMap<String, Long>> raceMap = new ArrayList<>();

        Pattern digitPattern = Pattern.compile("\\d+");

        String timeOfAllRaces = input.getFirst().split(":")[1].trim();
        String distanceOfAllRaces = input.getLast().split(":")[1].trim();

        Matcher timeMatcher = digitPattern.matcher(timeOfAllRaces);
        Matcher distanceMatcher = digitPattern.matcher(distanceOfAllRaces);

        while (timeMatcher.find() && distanceMatcher.find()) {
            HashMap<String, Long> timeAndDistanceMap = new HashMap<>();

            timeAndDistanceMap.put(RACE_TIME, Long.parseLong(timeMatcher.group()));
            timeAndDistanceMap.put(RACE_DISTANCE, Long.parseLong(distanceMatcher.group()));

            raceMap.add(timeAndDistanceMap);
        }

        return raceMap;
    }

    public static HashMap<String, Long> createRaceInfo(List<String> input)
    {
        HashMap<String, Long> raceMap = new HashMap<>();

        String raceTime = input.getFirst().split(":")[1].trim().replace(" ", "");
        String raceDistanceRecord = input.getLast().split(":")[1].trim().replace(" ", "");

        raceMap.put(RACE_TIME, Long.parseLong(raceTime));
        raceMap.put(RACE_DISTANCE, Long.parseLong(raceDistanceRecord));

        return raceMap;
    }

    public static long calculateWinningPossibilities(HashMap<String, Long> raceMap)
    {
        long winningPossibilities = 0;
        long raceTime = raceMap.get(RACE_TIME);
        long raceDistanceRecord = raceMap.get(RACE_DISTANCE);

        for (long carSpeed = 0; carSpeed <= raceTime; carSpeed++) {
            long timeForMoving = raceTime - carSpeed;
            long movedDistance = timeForMoving * carSpeed;

            if (movedDistance > raceDistanceRecord) {
                winningPossibilities++;
            }
        }

        return winningPossibilities;
    }
}
