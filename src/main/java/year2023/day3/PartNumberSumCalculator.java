package year2023.day3;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PartNumberSumCalculator
{
    private static final Logger logger = Logger.getLogger(PartNumberSumCalculator.class.getName());
    public static void main(String[] args)
    {
        String filePath = "src/main/java/year2023/day3/input.txt";

        try {
            List<String> lines = Files.readAllLines(Paths.get(filePath));

            Pattern pattern = Pattern.compile("\\d+");

            int sumOfPartNumbers = 0;
            int lengthOfEachLine = lines.getFirst().length();

            for (int i = 0; i < lengthOfEachLine; i++) {
                String currentLine = lines.get(i);
                String previousLine = i == 0 ? ".".repeat(lengthOfEachLine) : lines.get(i - 1);
                String nextLine = i == lengthOfEachLine - 1 ? ".".repeat(lengthOfEachLine) : lines.get(i + 1);

                Matcher matcher = pattern.matcher(currentLine);

                while (matcher.find()) {
                    String number = matcher.group();
                    int numberLength = number.length();
                    int index = matcher.start();
                    List<Integer> indexOfAdjacentCharsInCurrentLine = new ArrayList<>();
                    List<Integer> indexOfAdjacentCharsInAdjacentLine = new ArrayList<>();

                    for (int charIndex = index - 1; charIndex <= index + numberLength; charIndex++) {

                        if (charIndex >= 0 && charIndex < lengthOfEachLine) {
                            indexOfAdjacentCharsInAdjacentLine.add(charIndex);
                        }

                        if (charIndex >= 0 &&
                            (charIndex == index - 1 || (charIndex < lengthOfEachLine && charIndex == index + numberLength))) {
                            indexOfAdjacentCharsInCurrentLine.add(charIndex);
                        }
                    }

                    boolean isNumberAdjacentToSymbol = false;

                    for (int charIndex: indexOfAdjacentCharsInCurrentLine) {
                         if (isNumberAdjacentToSymbol) {
                             break;
                         }

                         isNumberAdjacentToSymbol = isSymbol(currentLine.charAt(charIndex));
                    }

                    for (int charIndex: indexOfAdjacentCharsInAdjacentLine) {
                        if (isNumberAdjacentToSymbol) {
                            break;
                        }

                        isNumberAdjacentToSymbol = isSymbol(previousLine.charAt(charIndex)) || isSymbol(nextLine.charAt(charIndex));
                    }

                    if (isNumberAdjacentToSymbol) {
                        sumOfPartNumbers += Integer.parseInt(number);
                    }
                }
            }

            System.out.println(sumOfPartNumbers);
        } catch (IOException e) {
            logger.log(Level.WARNING, "An error occurred", e);
        }


    }

    public static boolean isSymbol(char character)
    {
        return !Character.isDigit(character) && character != '.';
    }
}
