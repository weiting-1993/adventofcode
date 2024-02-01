package day_1;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CalibrationValueSumCalculator
{
    private static final Logger logger = Logger.getLogger(CalibrationValueSumCalculator.class.getName());

    public static void main(String[] args) {
        String filePath = "src/main/java/day_1/input.txt";

        try {
            List<String> lines = Files.readAllLines(Paths.get(filePath));

            int sumOfCalibrationValue = 0;
            Pattern twoDigitsPattern = Pattern.compile(".*\\d.*\\d.*");
            Pattern oneDigitPattern = Pattern.compile(".*\\d.*");

            for (String line : lines) {
                Matcher twoDigitsMatcher = twoDigitsPattern.matcher(line);
                Matcher oneDigitMatcher = oneDigitPattern.matcher(line);

                boolean twoDigitsMatchFound = twoDigitsMatcher.find();
                boolean oneDigitMatchFound = oneDigitMatcher.find();

                if (oneDigitMatchFound) {
                    char[] lineCharArray = line.toCharArray();

                    int firstDigit = 0;

                    for (char character: lineCharArray) {
                        if (Character.isDigit(character)) {
                            firstDigit = Character.getNumericValue(character);
                            sumOfCalibrationValue += firstDigit * 10;
                            break;
                        }
                    }

                    if (twoDigitsMatchFound) {
                        for (int i = 0; i < lineCharArray.length; i++) {
                            int character = lineCharArray[lineCharArray.length - 1 - i];
                            if (Character.isDigit(character)) {
                                sumOfCalibrationValue += Character.getNumericValue(character);
                                break;
                            }
                        }
                    } else {
                        sumOfCalibrationValue += firstDigit;
                    }
                }
            }

            System.out.print(sumOfCalibrationValue);
        } catch (IOException e) {
            logger.log(Level.WARNING, "An error occurred", e);
        }
    }
}
