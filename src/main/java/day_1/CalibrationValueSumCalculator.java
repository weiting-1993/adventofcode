package day_1;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CalibrationValueSumCalculator
{
    private static final Logger logger = Logger.getLogger(CalibrationValueSumCalculator.class.getName());
    private static final HashMap<String, String> numberWordDigitMap = new HashMap<>();

    public static void main(String[] args)
    {
        numberWordDigitMap.put("one", "1");
        numberWordDigitMap.put("two", "2");
        numberWordDigitMap.put("three", "3");
        numberWordDigitMap.put("four", "4");
        numberWordDigitMap.put("five", "5");
        numberWordDigitMap.put("six", "6");
        numberWordDigitMap.put("seven", "7");
        numberWordDigitMap.put("eight", "8");
        numberWordDigitMap.put("nine", "9");

        String filePath = "src/main/java/day_1/input.txt";

        try {
            List<String> lines = Files.readAllLines(Paths.get(filePath));

            int sumOfCalibrationValue = 0;

            for (String line : lines) {
                HashMap<Integer, String> numberIndexMap = createNumberIndexMap(line);
                int firstDigit = getFirstDigit(numberIndexMap);
                int lastDigit = getLastDigit(numberIndexMap);

                sumOfCalibrationValue += firstDigit * 10 + lastDigit;
            }

            System.out.print(sumOfCalibrationValue);
        } catch (IOException e) {
            logger.log(Level.WARNING, "An error occurred", e);
        }
    }

    private static HashMap<Integer, String> createNumberIndexMap(String line)
    {
        HashMap<Integer, String> numberIndexMap = new HashMap<>();

        for (String word: numberWordDigitMap.keySet()) {
            int index = line.indexOf(word);

            while (index != -1) {
                numberIndexMap.put(index, word);
                index = line.indexOf(word, index + 1);
            }
        }

        for (String digit: numberWordDigitMap.values()) {
            int index = line.indexOf(digit);

            while (index != -1) {
                numberIndexMap.put(index, digit);
                index = line.indexOf(digit, index + 1);
            }
        }

        return numberIndexMap;
    }

    private static int getFirstDigit(HashMap<Integer, String> map)
    {
        int lowestIndex = Integer.MAX_VALUE;
        String firstNumber = "";

        for (Integer index: map.keySet()) {
            if (index < lowestIndex) {
                firstNumber = map.get(index);
                lowestIndex = index;
            }
        }

        if (firstNumber.length() != 1) {
            firstNumber = numberWordDigitMap.get(firstNumber);
        }

        return Integer.parseInt(firstNumber);
    }

    private static int getLastDigit(HashMap<Integer, String> map)
    {
        int highestIndex = -1;
        String lastNumber = "";

        for (Integer index: map.keySet()) {
            if (index > highestIndex) {
                lastNumber = map.get(index);
                highestIndex = index;
            }
        }

        if (lastNumber.length() != 1) {
            lastNumber = numberWordDigitMap.get(lastNumber);
        }

        return Integer.parseInt(lastNumber);
    }
}
