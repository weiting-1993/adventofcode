package day_7;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class CamelCardsWinningCalculator
{
    private static final String INPUT_FILE_PATH = "src/main/java/day_7/input.txt";
    private static final Logger logger = Logger.getLogger(CamelCardsWinningCalculator.class.getName());
    private static final int FIVE_KIND_TYPE = 7; //1 (5)
    private static final int FOUR_KIND_TYPE = 6; //2 (4,1)
    private static final int FULL_HOUSE_TYPE = 5; //2 (3,2)
    private static final int THREE_KIND_TYPE = 4; //3 (3,1,1)
    private static final int TWO_PAIRS_TYPE = 3; //3 (2,2,1)
    private static final int ONE_PAIR_TYPE = 2; //4 (2,1,1,1)
    private static final int HIGH_CARD_TYPE = 1; //5 (1,1,1,1,1)

    public static void main(String[] args)
    {
        try {
            List<String> input = Files.readAllLines(Paths.get(INPUT_FILE_PATH));
            LinkedHashMap<String, HashMap<String, Integer>> cardHandBidAndRankingMap = createCardHandBidAndRankingMap(input);
            long totalWinnings = 0;

            for (HashMap<String, Integer> map: cardHandBidAndRankingMap.values()) {
                totalWinnings += (long) map.get("bid") * map.get("rank");
            }

            System.out.println("Part 1: " + totalWinnings);
        } catch (IOException e) {
            logger.log(Level.WARNING, "An error occurred", e);
        }
    }

    private static LinkedHashMap<String, HashMap<String, Integer>> createCardHandBidAndRankingMap(List<String> input)
    {
        LinkedHashMap<String, HashMap<String, Integer>> cardHandBidAndRankingMap = new LinkedHashMap<>();
        HashMap<String, Integer> fiveKindHandMap = new HashMap<>();
        HashMap<String, Integer> fourKindHandMap = new HashMap<>();
        HashMap<String, Integer> fullHouseHandMap = new HashMap<>();
        HashMap<String, Integer> threeKindHandMap = new HashMap<>();
        HashMap<String, Integer> twoPairsHandMap = new HashMap<>();
        HashMap<String, Integer> onePairHandMap = new HashMap<>();
        HashMap<String, Integer> highCardHandMap = new HashMap<>();

        for (String line: input) {
            String[] handInfo = line.trim().split(" ");
            String cardLabels = handInfo[0];
            int bid = Integer.parseInt(handInfo[1]);
            int handType = determineHandType(cardLabels);

            switch (handType) {
                case FIVE_KIND_TYPE -> fiveKindHandMap.put(cardLabels, bid);
                case FOUR_KIND_TYPE-> fourKindHandMap.put(cardLabels, bid);
                case FULL_HOUSE_TYPE -> fullHouseHandMap.put(cardLabels, bid);
                case THREE_KIND_TYPE -> threeKindHandMap.put(cardLabels, bid);
                case TWO_PAIRS_TYPE -> twoPairsHandMap.put(cardLabels, bid);
                case ONE_PAIR_TYPE -> onePairHandMap.put(cardLabels, bid);
                default -> highCardHandMap.put(cardLabels, bid);
            }
        }

        LinkedHashMap<String, Integer> mergedMap = new LinkedHashMap<>();
        mergedMap.putAll(sortMapByKey(highCardHandMap));
        mergedMap.putAll(sortMapByKey(onePairHandMap));
        mergedMap.putAll(sortMapByKey(twoPairsHandMap));
        mergedMap.putAll(sortMapByKey(threeKindHandMap));
        mergedMap.putAll(sortMapByKey(fullHouseHandMap));
        mergedMap.putAll(sortMapByKey(fourKindHandMap));
        mergedMap.putAll(sortMapByKey(fiveKindHandMap));

        int ranking = 1;

        for (Map.Entry<String, Integer> hand: mergedMap.entrySet()) {
            HashMap<String, Integer> handBidAndRankMap = new HashMap<>();
            handBidAndRankMap.put("bid", hand.getValue());
            handBidAndRankMap.put("rank", ranking);
            cardHandBidAndRankingMap.put(hand.getKey(), handBidAndRankMap);
            ranking++;
        }

        return cardHandBidAndRankingMap;
    }

    private static int determineHandType(String cardHand)
    {
        String sortedCardHand = sortString(cardHand);
        List<String> groupedCardHand = groupString(sortedCardHand);

        int groupedCardHandSize = groupedCardHand.size();

        switch (groupedCardHandSize) {
            case 5:
                return HIGH_CARD_TYPE;
            case 4:
                return ONE_PAIR_TYPE;
            case 3:
                if (groupedCardHand.get(0).length() == 3 || groupedCardHand.get(1).length() == 3 || groupedCardHand.get(2).length() == 3) {
                    return THREE_KIND_TYPE;
                } else {
                    return TWO_PAIRS_TYPE;
                }
            case 2:
                if (groupedCardHand.get(0).length() == 4 || groupedCardHand.get(1).length() == 4) {
                    return FOUR_KIND_TYPE;
                } else {
                    return FULL_HOUSE_TYPE;
                }
            default:
                return FIVE_KIND_TYPE;
        }
    }

    private static String sortString(String str) {
        char[] charArray = str.toCharArray();
        Arrays.sort(charArray);
        return new String(charArray);
    }

    private static List<String> groupString(String str) {
        List<String> groupedStrings = new ArrayList<>();

        StringBuilder currentGroup = new StringBuilder();
        char prevChar = str.charAt(0);
        currentGroup.append(prevChar);

        for (int i = 1; i < str.length(); i++) {
            char currentChar = str.charAt(i);
            if (currentChar == prevChar) {
                currentGroup.append(currentChar);
            } else {
                groupedStrings.add(currentGroup.toString());
                currentGroup.setLength(0);
                currentGroup.append(currentChar);
            }
            prevChar = currentChar;
        }

        groupedStrings.add(currentGroup.toString());

        return groupedStrings;
    }

    private static LinkedHashMap<String, Integer> sortMapByKey(HashMap<String, Integer> map)
    {
        return map.entrySet().stream().sorted(new HashMapKeyComparator()).collect(
            Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue,
                (oldValue, newValue) -> oldValue,
                LinkedHashMap::new
            )
        );
    }

    static class HashMapKeyComparator implements Comparator<Map.Entry<String, Integer>>
    {
        @Override
        public int compare(Map.Entry<String, Integer> map1, Map.Entry<String, Integer> map2) {
            String key1 = map1.getKey();
            String key2 = map2.getKey();
            int minLength = Math.min(key1.length(), key2.length());

            for (int i = 0; i < minLength; i++) {
                char char1 = key1.charAt(i);
                char char2 = key2.charAt(i);

                if (char1 != char2) {
                    return getCharacterStrength(char1) - getCharacterStrength(char2);
                }
            }

            return key1.length() - key2.length();
        }

        private int getCharacterStrength(char c) {
            return switch (c) {
                case 'A' -> 14;
                case 'K' -> 13;
                case 'Q' -> 12;
                case 'J' -> 11;
                case 'T' -> 10;
                default -> Character.getNumericValue(c);
            };
        }
    }
}
