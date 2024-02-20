package year2023.day3;

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

public class GearRatioSumCalculator
{
    private static final Logger logger = Logger.getLogger(GearRatioSumCalculator.class.getName());
    public static void main(String[] args)
    {
        String filePath = "src/main/java/year2023/day3/input.txt";

        try {
            List<String> lines = Files.readAllLines(Paths.get(filePath));

            Pattern starPattern = Pattern.compile("\\*");

            int sumOfGearRatio = 0;
            int lengthOfEachLine = lines.getFirst().length();

            for (int i = 0; i < lengthOfEachLine; i++) {
                String currentLine = lines.get(i);
                String preLine = i == 0 ? ".".repeat(lengthOfEachLine) : lines.get(i - 1);
                String postLine = i == lengthOfEachLine - 1 ? ".".repeat(lengthOfEachLine) : lines.get(i + 1);

                Matcher currentLineStarMatcher = starPattern.matcher(currentLine);

                while (currentLineStarMatcher.find()) {
                    int starIndex = currentLineStarMatcher.start();
                    int preStarIndex = starIndex - 1;
                    int postStarIndex = starIndex + 1;
                    List<Integer> indicesOfAdjacentCharInCurrentLine = new ArrayList<>();
                    List<Integer> indicesOfAdjacentCharInAdjacentLine = new ArrayList<>();

                    if (preStarIndex >= 0) {
                        indicesOfAdjacentCharInCurrentLine.add(preStarIndex);
                        indicesOfAdjacentCharInAdjacentLine.add(preStarIndex);
                    }

                    indicesOfAdjacentCharInAdjacentLine.add(starIndex);

                    if (postStarIndex < lengthOfEachLine) {
                        indicesOfAdjacentCharInCurrentLine.add(postStarIndex);
                        indicesOfAdjacentCharInAdjacentLine.add(postStarIndex);
                    }

                    int starAdjacentToNumberCount = 0;

                    List<Integer> indicesOfNumberAdjacentToStarInCurrentLine = new ArrayList<>();
                    List<Integer> indicesOfNumberAdjacentToStarInPreLine = new ArrayList<>();
                    List<Integer> indicesOfNumberAdjacentToStarInPostLine = new ArrayList<>();

                    for (int charIndex: indicesOfAdjacentCharInCurrentLine) {
                        if (Character.isDigit(currentLine.charAt(charIndex))) {
                            starAdjacentToNumberCount++;
                            indicesOfNumberAdjacentToStarInCurrentLine.add(charIndex);
                        }
                    }

                    boolean isPreviousCharDigitInPreLine = false;
                    boolean isPreviousCharDigitInPostLine = false;

                    for (int charIndex: indicesOfAdjacentCharInAdjacentLine) {
                        if (Character.isDigit(preLine.charAt(charIndex)) && !isPreviousCharDigitInPreLine) {
                            starAdjacentToNumberCount++;
                            indicesOfNumberAdjacentToStarInPreLine.add(charIndex);
                        }

                        isPreviousCharDigitInPreLine = charIndex > 0 && Character.isDigit(preLine.charAt(charIndex));

                        if (Character.isDigit(postLine.charAt(charIndex)) && !isPreviousCharDigitInPostLine) {
                            starAdjacentToNumberCount++;
                            indicesOfNumberAdjacentToStarInPostLine.add(charIndex);
                        }

                        isPreviousCharDigitInPostLine = charIndex > 0 && Character.isDigit(postLine.charAt(charIndex));
                    }

                    if (starAdjacentToNumberCount == 2) {
                        int numberInCurrentLine = 1;
                        int numberInPreLine = 1;
                        int numberInPostLine = 1;

                        if (!indicesOfNumberAdjacentToStarInCurrentLine.isEmpty()) {
                            if (indicesOfNumberAdjacentToStarInCurrentLine.size() == 2) {
                                sumOfGearRatio += findNumberInStringWithIndex(
                                    indicesOfNumberAdjacentToStarInCurrentLine.getFirst(), currentLine
                                ) * findNumberInStringWithIndex(
                                    indicesOfNumberAdjacentToStarInCurrentLine.getLast(), currentLine
                                );
                            } else {
                                numberInCurrentLine = findNumberInStringWithIndex(
                                    indicesOfNumberAdjacentToStarInCurrentLine.getFirst(), currentLine
                                );
                            }
                        }

                        if (!indicesOfNumberAdjacentToStarInPreLine.isEmpty()) {
                            if (indicesOfNumberAdjacentToStarInPreLine.size() == 2) {
                                sumOfGearRatio += findNumberInStringWithIndex(
                                    indicesOfNumberAdjacentToStarInPreLine.getFirst(), preLine
                                ) * findNumberInStringWithIndex(
                                    indicesOfNumberAdjacentToStarInPreLine.getLast(), preLine
                                );
                            } else {
                                numberInPreLine = findNumberInStringWithIndex(
                                    indicesOfNumberAdjacentToStarInPreLine.getFirst(), preLine
                                );
                            }
                        }

                        if (!indicesOfNumberAdjacentToStarInPostLine.isEmpty()) {
                            if (indicesOfNumberAdjacentToStarInPostLine.size() == 2) {
                                sumOfGearRatio += findNumberInStringWithIndex(
                                    indicesOfNumberAdjacentToStarInPostLine.getFirst(), postLine
                                ) * findNumberInStringWithIndex(
                                    indicesOfNumberAdjacentToStarInPostLine.getLast(), postLine
                                );
                            } else {
                                numberInPostLine = findNumberInStringWithIndex(
                                    indicesOfNumberAdjacentToStarInPostLine.getFirst(), postLine
                                );
                            }
                        }

                        int ratioGear = numberInCurrentLine * numberInPreLine * numberInPostLine;

                        if (ratioGear != 1) {
                            sumOfGearRatio += ratioGear;
                        }
                    }
                }
            }

            System.out.println(sumOfGearRatio);
        } catch (IOException e) {
            logger.log(Level.WARNING, "An error occurred", e);
        }
    }

    public static int findNumberInStringWithIndex(int index, String line)
    {
        int numberFound = 1;
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(line);

        HashMap<Integer, String> matchedIndexNumberMap = new HashMap<>();

        while (matcher.find()) {
            String number = matcher.group();
            int matchedIndex = matcher.start();

            matchedIndexNumberMap.put(matchedIndex, number);
        }

        for (int matchedIndex: matchedIndexNumberMap.keySet()) {
            if (index >= matchedIndex &&
                index <= (matchedIndex + matchedIndexNumberMap.get(matchedIndex).length() - 1)) {
                numberFound = Integer.parseInt(matchedIndexNumberMap.get(matchedIndex));
                break;
            }
        }

        return numberFound;
    }
}
