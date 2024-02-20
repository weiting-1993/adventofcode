package year2023.day7;

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
    private static final String INPUT_FILE_PATH = "src/main/java/year2023/day7/input.txt";
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
            LinkedHashMap<String, HashMap<String, Integer>> cardHandBidAndRankingMapWithoutWildcardConsideration = createCardHandBidAndRankingMap(input, false);
            LinkedHashMap<String, HashMap<String, Integer>> cardHandBidAndRankingMapWithWildcardConsideration = createCardHandBidAndRankingMap(input, true);
            long totalWinningsWithoutWildcardConsideration = 0;
            long totalWinningsWitWildcardConsideration = 0;

            for (HashMap<String, Integer> map: cardHandBidAndRankingMapWithoutWildcardConsideration.values()) {
                totalWinningsWithoutWildcardConsideration += (long) map.get("bid") * map.get("rank");
            }

            for (HashMap<String, Integer> map: cardHandBidAndRankingMapWithWildcardConsideration.values()) {
                totalWinningsWitWildcardConsideration += (long) map.get("bid") * map.get("rank");
            }

            System.out.println("Part 1: " + totalWinningsWithoutWildcardConsideration);
            System.out.println("Part 2: " + totalWinningsWitWildcardConsideration);
        } catch (IOException e) {
            logger.log(Level.WARNING, "An error occurred", e);
        }
    }

    private static LinkedHashMap<String, HashMap<String, Integer>> createCardHandBidAndRankingMap(List<String> input, boolean considerWildCard)
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
            int handType = considerWildCard ? determineHandTypeConsideringWildCard(cardLabels) : determineHandType(cardLabels);

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
        mergedMap.putAll(sortMapByKey(highCardHandMap, considerWildCard));
        mergedMap.putAll(sortMapByKey(onePairHandMap, considerWildCard));
        mergedMap.putAll(sortMapByKey(twoPairsHandMap, considerWildCard));
        mergedMap.putAll(sortMapByKey(threeKindHandMap, considerWildCard));
        mergedMap.putAll(sortMapByKey(fullHouseHandMap, considerWildCard));
        mergedMap.putAll(sortMapByKey(fourKindHandMap, considerWildCard));
        mergedMap.putAll(sortMapByKey(fiveKindHandMap, considerWildCard));

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
        List<String> groupedCardHand = groupSortedString(sortedCardHand);

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

    private static int determineHandTypeConsideringWildCard(String cardHand)
    {
        String sortedCardHand = sortString(cardHand);
        List<String> groupedCardHand = groupSortedString(sortedCardHand);
        int groupedCardHandSize = groupedCardHand.size();
        int charJOccurrenceCounts = countJOccurrences(sortedCardHand);
        String firstStringGroup = groupedCardHand.getFirst();

        switch (groupedCardHandSize) {
            case 5:
                if (charJOccurrenceCounts == 1) {
                    return ONE_PAIR_TYPE;
                } else {
                    return HIGH_CARD_TYPE;
                }
            case 4:
                if (List.of(1, 2).contains(charJOccurrenceCounts)) {
                    return THREE_KIND_TYPE;
                } else {
                    return ONE_PAIR_TYPE;
                }
            case 3:
                if (firstStringGroup.length() == 3 && charJOccurrenceCounts == 0) {
                    return THREE_KIND_TYPE;
                } else if ((firstStringGroup.length() == 3 && List.of(1, 3).contains(charJOccurrenceCounts))
                    || (firstStringGroup.length() == 2 && charJOccurrenceCounts == 2)) {
                    return FOUR_KIND_TYPE;
                } else if (firstStringGroup.length() == 2 && charJOccurrenceCounts == 1) {
                    return FULL_HOUSE_TYPE;
                } else {
                    return TWO_PAIRS_TYPE;
                }
            case 2:
                if (firstStringGroup.length() == 4 && charJOccurrenceCounts == 0) {
                    return FOUR_KIND_TYPE;
                } else if (List.of(1, 2, 3, 4).contains(charJOccurrenceCounts)) {
                    return FIVE_KIND_TYPE;
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

    private static List<String> groupSortedString(String str) {
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
                prevChar = currentChar;
            }
        }

        groupedStrings.add(currentGroup.toString());
        groupedStrings.sort(new StringLengthComparator());

        return groupedStrings;
    }

    public static int countJOccurrences(String str) {
        int count = 0;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == 'J') {
                count++;
            }
        }

        return count;
    }

    private static LinkedHashMap<String, Integer> sortMapByKey(HashMap<String, Integer> map, boolean considerWildCard)
    {
        return map.entrySet().stream().sorted(new HashMapKeyComparator(considerWildCard)).collect(
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
        private final boolean considerWildcard;

        public HashMapKeyComparator(boolean considerWildcard) {
            this.considerWildcard = considerWildcard;
        }
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

        protected int getCharacterStrength(char c) {
            return switch (c) {
                case 'A' -> 14;
                case 'K' -> 13;
                case 'Q' -> 12;
                case 'J' -> considerWildcard ? 1 : 11;
                case 'T' -> 10;
                default -> Character.getNumericValue(c);
            };
        }
    }

    static class StringLengthComparator implements Comparator<String> {
        @Override
        public int compare(String str1, String str2) {
            // Compare strings based on their lengths (longest first)
            return Integer.compare(str2.length(), str1.length());
        }
    }
}
