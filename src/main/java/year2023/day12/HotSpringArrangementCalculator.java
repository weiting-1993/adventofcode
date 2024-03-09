package year2023.day12;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class HotSpringArrangementCalculator
{
    private static final String INPUT_FILE_PATH = "src/main/java/year2023/day12/input.txt";
    private static final Logger logger = Logger.getLogger(HotSpringArrangementCalculator.class.getName());
    private static final Map<String, String> possibleArrangementCache = new HashMap<>();
    private static final Map<List<Integer>, String> rulesPatternCache = new HashMap<>();
    private static final Map<String, Long> cache = new HashMap<>();

    public static void main(String[] args)
    {
        try {
            var allLines = Files.readAllLines(Paths.get(INPUT_FILE_PATH));
            var partOneCount = 0L;
            var partTwoCount = 0L;
            for (var line: allLines) {
                partOneCount += countValidArrangements(line);
            }

            System.out.println("Part 1:" + partOneCount);

            for (var line: allLines) {
                partTwoCount += countValidArrangementsForUnfoldedRecord(line);
            }

            System.out.println("Part 2:" + partTwoCount);
        } catch (IOException e) {
            logger.log(Level.WARNING, "An error occurred while trying to read a file.", e);
        }
    }

    private static long countValidArrangementsForUnfoldedRecord(String line)
    {
        var parts = line.trim().split(" ");
        var originalRecord = parts[0];
        var singleUnfoldedRecord = "?" + originalRecord;
        var unfoldedRecord = String.join("?", Collections.nCopies(5, originalRecord));
        var rules = Arrays.stream(parts[1].split(",")).mapToInt(Integer::parseInt).boxed().toList();
        var unfoldedRules = IntStream.range(0, 5).mapToObj(i -> rules).flatMap(List::stream).toList();
        long possibleArrangements;

        possibleArrangements = checkDamagedSpringData(unfoldedRecord.chars().mapToObj(c -> (char)c).toList(), unfoldedRules);

        return possibleArrangements;
    }

    private static long countValidArrangements(String line)
    {
        var inputs = line.trim().split(" ");
        var record = inputs[0];
        var hotSpringRules = Arrays.stream(inputs[1].split(",")).mapToInt(Integer::parseInt).boxed().collect(Collectors.toList());
        var possibleArrangements = generatePossibleValidArrangements(record, hotSpringRules);

        return possibleArrangements.size();
    }

    private static List<String> generatePossibleValidArrangements(String record, List<Integer> rules)
    {
        var result = new ArrayList<String>();
        generatePossibleValidArrangementsHelper(record.toCharArray(), 0, result, rules);
        return result;
    }


    private static void generatePossibleValidArrangementsHelper(char[] chars, int index, List<String> result, List<Integer> rules)
    {
        var key = new String(chars) + rules.toString();
        if (possibleArrangementCache.containsKey(key)) {
            result.add(possibleArrangementCache.get(key));
            return;
        }

        if (index == chars.length) {
            result.add(new String(chars));
            possibleArrangementCache.put(key, new String(chars));
            return;
        }

        if (chars[index] == '?') {
            chars[index] = '.';
            if (isValidRecord(new String(chars), rules)) {
                generatePossibleValidArrangementsHelper(chars, index + 1, result, rules);
            }

            chars[index] = '#';
            if (isValidRecord(new String(chars), rules)) {
                generatePossibleValidArrangementsHelper(chars, index + 1, result, rules);
            }

            chars[index] = '?';
        } else {
            if (isValidRecord(new String(chars), rules)) {
                generatePossibleValidArrangementsHelper(chars, index + 1, result, rules);
            }
        }
    }

    private static boolean isValidRecord(String record, List<Integer> rules) {
        long totalExpectedSpringsAmount = rules.stream().reduce(Integer::sum).orElse(0);
        long totalPossibleSpringsAmount = record.chars().filter( c -> c == '?').count();
        long totalExistingSpringsAmount = record.chars().filter( c -> c == '#').count();
        if (totalPossibleSpringsAmount == 0L && totalExistingSpringsAmount != totalExpectedSpringsAmount) {
            return false;
        }
        if (totalPossibleSpringsAmount != 0L && (totalExistingSpringsAmount + totalPossibleSpringsAmount) < totalExpectedSpringsAmount) {
            return false;
        }

        var regex = "";

        if (rulesPatternCache.containsKey(rules)) {
            regex = rulesPatternCache.get(rules);
        } else {
            regex = createValidSpringArrangementPattern(rules);
            rulesPatternCache.put(rules, regex);
        }

        var pattern = Pattern.compile(regex);
        var matcher = pattern.matcher(record);

        return matcher.find();
    }

    private static String createValidSpringArrangementPattern (List<Integer> rules) {
        var rulesSize = rules.size();
        var pattern = new StringBuilder("^[\\.?]*");

        for (var i = 0; i < rulesSize; i++) {
            if (i == rulesSize - 1) {
                pattern.append(String.format("[#?]{%d}[\\.?]*$", rules.get(i)));
                continue;
            }

            pattern.append(String.format("[#?]{%d}[\\.?]+", rules.get(i)));
        }

        return pattern.toString();
    }

    // this solution is credited to https://github.com/Da9el00/Advent-of-Code-2023/blob/main/src/Java/adventofcode/day12/Day12Part2.java
    protected static long checkDamagedSpringData(List<Character> record, List<Integer> damagedSpringsAmountList){
        long result = 0;
        String cacheKey = record.toString() + damagedSpringsAmountList.toString();

        if (cache.containsKey(cacheKey)) {
            return cache.get(cacheKey);
        }

        if (damagedSpringsAmountList.isEmpty()) {
            long computedResult = record.contains('#') ? 0 : 1;

            cache.put(cacheKey, computedResult);

            return computedResult;
        }

        int currentDamageSpringsAmount = damagedSpringsAmountList.getFirst();
        List<Integer> remainingDamagedSpringsAmount = damagedSpringsAmountList.subList(1, damagedSpringsAmountList.size());

        /*
        the record should be at least the size of the sum of the damagedSpringsAmount plus the ground in between the springs
        why plus 1 is because the record can be exactly the same size of the rules ,and we want to check it at least once
         */
        for (int i = 0; i < record.size() - sum(damagedSpringsAmountList) - (damagedSpringsAmountList.size() - 1) + 1; i++) {
            if (record.subList(0, i).contains('#')) {
                break;
            }

            int nxt = i + currentDamageSpringsAmount;
            if (nxt <= record.size() && record.subList(i, nxt).stream().noneMatch(ch -> ch == '.') &&
                (nxt == record.size() || record.get(nxt) != '#')) {
                List<Character> nextRecord =  nxt + 1 < record.size() ? record.subList(nxt + 1, record.size()) :List.of();
                result += checkDamagedSpringData(nextRecord, remainingDamagedSpringsAmount);
            }
        }
        // Cache the result before returning
        cache.put(cacheKey, result);
        return result;
    }

    public static int sum(List<Integer> values) {
        return values.stream().reduce(Integer::sum).orElse(0);
    }
}
