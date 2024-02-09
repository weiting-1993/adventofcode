package day_4;

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

public class CardPointSumCalculator
{
    private static final String INPUT_FILE_PATH = "src/main/java/day_4/input.txt";
    private static final Logger logger = Logger.getLogger(CardPointSumCalculator.class.getName());
    public static void main(String[] args)
    {
        try {
            List<String> input = Files.readAllLines(Paths.get(INPUT_FILE_PATH));
            HashMap<Integer, HashMap<String, List<Integer>>> allCards = createAllCardsMap(input);
            int sumOfCardPoints = 0;

            System.out.println(allCards);

            for (int cardIndicator: allCards.keySet()) {
                sumOfCardPoints += calculateCardPoint(allCards.get(cardIndicator));
            }

            System.out.println(sumOfCardPoints);
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
            String[] cardIndicatorAndNumbers = card.split(":");
            int cardIndicator = Integer.parseInt(cardIndicatorAndNumbers[0].replace(" ", "").substring(4));
            String[] winningAndPlayNumberArray = cardIndicatorAndNumbers[1].trim().split("\\|");
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
            allCardsMap.put(cardIndicator, winningAndPlayingNumberMap);
        }

        return allCardsMap;
    }

    public static int calculateCardPoint(HashMap<String, List<Integer>> cardNumbers) {
        int matchNumberCount = -1;

        for (int playingNumber: cardNumbers.get("playingNumbers")) {
            if (cardNumbers.get("winningNumbers").contains(playingNumber)) {
                matchNumberCount++;
            }
        }

        if (matchNumberCount == -1) {
            return 0;
        }

        return (int) Math.pow(2, matchNumberCount);
    }
}
