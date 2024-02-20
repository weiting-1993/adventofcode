package year2023.day4;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CardPointSumCalculator
{
    private static final String INPUT_FILE_PATH = "src/main/java/year2023/day4/input.txt";
    private static final Logger logger = Logger.getLogger(CardPointSumCalculator.class.getName());
    public static void main(String[] args)
    {
        try {
            List<String> input = Files.readAllLines(Paths.get(INPUT_FILE_PATH));
            HashMap<Integer, HashMap<String, List<Integer>>> allCards = createAllCardsMap(input);
            HashMap<Integer, Integer> cardAndItsInstancesCountMap = createAllCardsAndItsTotalInstanceCountMap(allCards);

            int sumOfCardPoints = 0;
            int totalScratchcardsCount = 0;

            for (Map.Entry<Integer, HashMap<String, List<Integer>>> card: allCards.entrySet()) {
                int matchesCount = calculateCardTotalMatchesCount(card.getValue());

                if (matchesCount > 0) {
                    sumOfCardPoints += (int) Math.pow(2, matchesCount - 1);
                }
            }

            for (Map.Entry<Integer, Integer> cardAndItsInstancesCount: cardAndItsInstancesCountMap.entrySet()) {
                totalScratchcardsCount += cardAndItsInstancesCount.getValue();
            }

            System.out.println("sumOfCardPoints:" + sumOfCardPoints);
            System.out.println("totalScratchcardsCount:" + totalScratchcardsCount);
        } catch (IOException e) {
            logger.log(Level.WARNING, "An error occurred", e);
        }
    }

    public static HashMap<Integer, HashMap<String, List<Integer>>> createAllCardsMap(List<String> input)
    {
        HashMap<Integer, HashMap<String, List<Integer>>> allCardsMap = new HashMap<>();
        Pattern numberPattern = Pattern.compile("\\d+");

        for (String card: input) {
            HashMap<String, List<Integer>> winningAndPlayingNumberMap = new HashMap<>();
            String[] cardIdentifierAndNumbers = card.split(":");
            int cardIdentifier = Integer.parseInt(cardIdentifierAndNumbers[0].replace(" ", "").substring(4));
            String[] winningAndPlayNumberArray = cardIdentifierAndNumbers[1].trim().split("\\|");
            String winningNumbers = winningAndPlayNumberArray[0].trim();
            String playingNumbers = winningAndPlayNumberArray[1].trim();

            Matcher winningNumberMatcher = numberPattern.matcher(winningNumbers);
            Matcher playingNumberMatcher = numberPattern.matcher(playingNumbers);

            List<Integer> winningNumberList = new ArrayList<>();
            List<Integer> playingNumberList = new ArrayList<>();

            while (winningNumberMatcher.find()) {
                String number = winningNumberMatcher.group();
                winningNumberList.add(Integer.parseInt(number));
            }

            while (playingNumberMatcher.find()) {
                String number = playingNumberMatcher.group();
                playingNumberList.add(Integer.parseInt(number));
            }

            winningAndPlayingNumberMap.put("winningNumbers", winningNumberList);
            winningAndPlayingNumberMap.put("playingNumbers", playingNumberList);
            allCardsMap.put(cardIdentifier, winningAndPlayingNumberMap);
        }

        return allCardsMap;
    }

    public static int calculateCardTotalMatchesCount(HashMap<String, List<Integer>> cardNumbers) {
        int matchesNumberCount = 0;

        for (int playingNumber: cardNumbers.get("playingNumbers")) {
            if (cardNumbers.get("winningNumbers").contains(playingNumber)) {
                matchesNumberCount++;
            }
        }

        return matchesNumberCount;
    }

    public static HashMap<Integer, Integer> createAllCardsAndItsTotalInstanceCountMap(HashMap<Integer, HashMap<String, List<Integer>>> allCardsMap)
    {
        HashMap<Integer, Integer> allCardsAndItsTotalInstanceCountMap = new HashMap<>();

        int originalCardsSize = allCardsMap.size();

        for (int cardIdentifier: allCardsMap.keySet()) {
            allCardsAndItsTotalInstanceCountMap.put(cardIdentifier, 1);
        }

        for (int cardIdentifier: allCardsMap.keySet()) {
            int matchesAmount = calculateCardTotalMatchesCount(allCardsMap.get(cardIdentifier));

            for (int iteration = 1; iteration <= matchesAmount; iteration++) {
                int cardIdentifierForCopy = cardIdentifier + iteration;

                if (cardIdentifierForCopy > originalCardsSize) {
                    break;
                }

                allCardsAndItsTotalInstanceCountMap.put(
                    cardIdentifierForCopy,
                    allCardsAndItsTotalInstanceCountMap.get(cardIdentifierForCopy) + allCardsAndItsTotalInstanceCountMap.get(cardIdentifier)
                );
            }
        }

        return allCardsAndItsTotalInstanceCountMap;
    }
}
