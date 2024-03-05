package year2023.day12;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class HotSpringArrangementCalculator
{
    private static final String INPUT_FILE_PATH = "src/main/java/year2023/day12/input.txt";
    private static final Logger logger = Logger.getLogger(HotSpringArrangementCalculator.class.getName());
    public static void main(String[] args)
    {
        try {
            var allLines = Files.readAllLines(Paths.get(INPUT_FILE_PATH));
            var partOneCount = 0;
            for (var line: allLines) {
                partOneCount += getNumberOfPossibleArrangements(line);
            }

            System.out.println("Part 1:" + partOneCount);
        } catch (IOException e) {
            logger.log(Level.WARNING, "An error occurred while trying to read a file.", e);
        }
    }

    private static int getNumberOfPossibleArrangements(String line)
    {
        var inputs = line.trim().split(" ");
        var conditionRecord = inputs[0];
        var hotSpringCondition = Arrays.stream(inputs[1].split(",")).mapToInt(Integer::parseInt).boxed().toList();
        var possibleArrangements = generateAllPossibleArrangements(conditionRecord);

        var validArrangements = possibleArrangements.stream().filter(arrangement -> {
            var hotSpringArrangements = getHotSpringArrangements(arrangement);
            return Objects.equals(hotSpringArrangements, hotSpringCondition);
        }).toList();

        return validArrangements.size();
    }

    private static List<Integer> getHotSpringArrangements(String input)
    {
        var pattern = Pattern.compile("#+");
        var matcher = pattern.matcher(input);
        var arrangementList = new ArrayList<Integer>();

        while (matcher.find()) {
            var arrangement = matcher.group();
            arrangementList.add(arrangement.length());
        }

        return arrangementList;
    }

    private static List<String> generateAllPossibleArrangements(String input)
    {
        var result = new ArrayList<String>();
        generateAllPossibleArrangementsHelper(input.toCharArray(), 0, result);
        return result;
    }


    private static void generateAllPossibleArrangementsHelper (char[] chars, int index, List<String> result)
    {
        if (index == chars.length) {
            result.add(new String(chars));
            return;
        }

        if (chars[index] == '?') {
            chars[index] = '.';
            generateAllPossibleArrangementsHelper(chars, index + 1, result);
            chars[index] = '#';
            generateAllPossibleArrangementsHelper(chars, index + 1, result);
            chars[index] = '?';
        } else {
            generateAllPossibleArrangementsHelper(chars, index + 1, result);
        }
    }
}
