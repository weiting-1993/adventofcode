package day_2;

import day_1.CalibrationValueSumCalculator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CubeConfigurationChecker
{
    private static final String RED_CUBE = "red";

    private static final Integer RED_CUBE_AMOUNT_CONFIG = 12;

    private static final String BLUE_CUBE = "blue";

    private static final Integer BLUE_CUBE_AMOUNT_CONFIG = 14;

    private static final String GREEN_CUBE = "green";

    private static final Integer GREEN_CUBE_AMOUNT_CONFIG = 13;

    private static final Logger logger = Logger.getLogger(CalibrationValueSumCalculator.class.getName());

    public static void main(String[] args)
    {
        String filePath = "src/main/java/day_2/input.txt";

        try {
            HashMap<Integer, List<HashMap<String, Integer>>> allGamesInfoMap = new HashMap<>();
            List<String> lines = Files.readAllLines(Paths.get(filePath));

            for (String line: lines) {
                HashMap<Integer, List<HashMap<String, Integer>>> gameInfo = createGameInformationMap(line);
                allGamesInfoMap.putAll(gameInfo);
            }

            int sumOfPossibleGameIds = 0;
            int sumOfThePowerOfAllGames = 0;

            for (Integer gameId: allGamesInfoMap.keySet()) {
                HashMap<String, Integer> gameInfoWithHighestAmountOfEachCubeColor = getHighestAmountPerCubeColor(allGamesInfoMap.get(gameId));
                int powerOfMinimumSetOfCubes = 1;

                boolean cubeAmountIsPossible = true;

                for (String cubeColor: gameInfoWithHighestAmountOfEachCubeColor.keySet()) {
                    powerOfMinimumSetOfCubes = powerOfMinimumSetOfCubes * gameInfoWithHighestAmountOfEachCubeColor.get(cubeColor);

                    if (Objects.equals(cubeColor, RED_CUBE) && gameInfoWithHighestAmountOfEachCubeColor.get(cubeColor) > RED_CUBE_AMOUNT_CONFIG) {
                        cubeAmountIsPossible = false;
                    }

                    if (Objects.equals(cubeColor, BLUE_CUBE) && gameInfoWithHighestAmountOfEachCubeColor.get(cubeColor) > BLUE_CUBE_AMOUNT_CONFIG) {
                        cubeAmountIsPossible = false;
                    }

                    if (Objects.equals(cubeColor, GREEN_CUBE) && gameInfoWithHighestAmountOfEachCubeColor.get(cubeColor) > GREEN_CUBE_AMOUNT_CONFIG) {
                        cubeAmountIsPossible = false;
                    }
                }

                sumOfThePowerOfAllGames += powerOfMinimumSetOfCubes;

                if (cubeAmountIsPossible) {
                    sumOfPossibleGameIds += gameId;
                }
            }

            System.out.printf("sumOfPossibleGameIds: %d\n", sumOfPossibleGameIds);
            System.out.printf("sumOfThePowerOfAllGames: %d\n", sumOfThePowerOfAllGames);
        } catch (IOException e) {
            logger.log(Level.WARNING, "An error occurred", e);
        }
    }

    private static HashMap<Integer, List<HashMap<String, Integer>>> createGameInformationMap(String line)
    {
        HashMap<Integer, List<HashMap<String, Integer>>> gameInformationMap = new HashMap<>();
        List<HashMap<String, Integer>> gameInformationList = new ArrayList<>();

        String[] gameIdAndInformation = line.split(":");
        String[] cubesInformationCollection = gameIdAndInformation[1].trim().split("; ");

        int gameId = Integer.parseInt(gameIdAndInformation[0].substring(5));

        for (String cubesInformation: cubesInformationCollection) {
            HashMap<String, Integer> cubeColorAmountMap = new HashMap<>();
            String[] cubesColorAndAmountCollection = cubesInformation.split(", ");

            for (String cubesColorAndAmount: cubesColorAndAmountCollection) {
                String[] cubeColorAndAmount = cubesColorAndAmount.split(" ");
                cubeColorAmountMap.put(cubeColorAndAmount[1], Integer.parseInt(cubeColorAndAmount[0]));
            }

            gameInformationList.add(cubeColorAmountMap);
        }

        gameInformationMap.put(gameId, gameInformationList);

        return gameInformationMap;
    }

    private static HashMap<String, Integer> getHighestAmountPerCubeColor(List<HashMap<String, Integer>> gameInformation)
    {
        HashMap<String, Integer> highestAmountPerCubeColorMap = new HashMap<>();
        int highestRedAmount = 0;
        int highestBlueAmount = 0;
        int highestGreenAmount = 0;

        for (HashMap<String, Integer> cubesColorAndAmount: gameInformation) {
            for (String cubeColor: cubesColorAndAmount.keySet()) {
                if (Objects.equals(cubeColor, RED_CUBE) && cubesColorAndAmount.get(cubeColor) > highestRedAmount) {
                    highestRedAmount = cubesColorAndAmount.get(cubeColor);
                }

                if (Objects.equals(cubeColor, BLUE_CUBE) && cubesColorAndAmount.get(cubeColor) > highestBlueAmount) {
                    highestBlueAmount = cubesColorAndAmount.get(cubeColor);
                }

                if (Objects.equals(cubeColor, GREEN_CUBE) && cubesColorAndAmount.get(cubeColor) > highestGreenAmount) {
                    highestGreenAmount = cubesColorAndAmount.get(cubeColor);
                }
            }
        }

        highestAmountPerCubeColorMap.put(RED_CUBE, highestRedAmount);
        highestAmountPerCubeColorMap.put(BLUE_CUBE, highestBlueAmount);
        highestAmountPerCubeColorMap.put(GREEN_CUBE, highestGreenAmount);

        return highestAmountPerCubeColorMap;
    }
}
