package year2023.day5;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SeedLocationFinder
{
    private static final String INPUT_FILE_PATH = "src/main/java/year2023/day5/input.txt";
    private static final Logger logger = Logger.getLogger(SeedLocationFinder.class.getName());

    public static void main(String[] args)
    {
        try {
            List<String> input = Files.readAllLines(Paths.get(INPUT_FILE_PATH));
            String[] seeds = input.getFirst().trim().substring("seeds: ".length()).split(" ");
            LinkedHashMap<String, List<LinkedHashMap<String, Long>>> seedNumberMap = createSeedNumberMap(input);

            long minLocationNumberForPart1 = Long.MAX_VALUE;
            long minLocationNumberForPart2 = Long.MAX_VALUE;

            for (String seedNumber: seeds) {
                long locationNumber = getLocationNumber(Long.parseLong(seedNumber), seedNumberMap);

                if (locationNumber < minLocationNumberForPart1) {
                    minLocationNumberForPart1 = locationNumber;
                }
            }

            for (int i = 0; i < seeds.length; i += 2) {
                long seedNumberStart = Long.parseLong(seeds[i]);
                long seedNumberRange = Long.parseLong(seeds[i + 1]);

                for (long seedNumber = seedNumberStart; seedNumber < seedNumberStart + seedNumberRange; seedNumber++) {
                    long locationNumber = getLocationNumber(seedNumber, seedNumberMap);

                    if (locationNumber < minLocationNumberForPart2) {
                        minLocationNumberForPart2 = locationNumber;
                    }
                }
            }

            System.out.println("Part 1:" + minLocationNumberForPart1);
            System.out.println("Part 2:" + minLocationNumberForPart2);
        } catch (IOException e) {
            logger.log(Level.WARNING, "An error occurred", e);
        }
    }

    public static LinkedHashMap<String, List<LinkedHashMap<String, Long>>> createSeedNumberMap(List<String> input)
    {
        LinkedHashMap<String, List<LinkedHashMap<String, Long>>> seedCultivationMap = new LinkedHashMap<>();
        Pattern alphabetPattern = Pattern.compile(".*[a-zA-Z].*");
        int iteration = 0;
        String mapIdentifier = "";

        for (String line: input) {
            if (iteration == 0 || line.trim().isEmpty()) {
                iteration++;
                continue;
            }

            List<LinkedHashMap<String, Long>> mapsForDifferentItems = new ArrayList<>();
            LinkedHashMap<String, Long> mapInfo = new LinkedHashMap<>();

            Matcher alphabetMatcher = alphabetPattern.matcher(line);

            if (alphabetMatcher.find()) {
                String trimmedLine = line.trim();
                mapIdentifier = trimmedLine.substring(0, trimmedLine.length() - 5);

                seedCultivationMap.put(mapIdentifier, mapsForDifferentItems);
                iteration++;
            } else {
                mapsForDifferentItems = seedCultivationMap.get(mapIdentifier);
                String[] numberMappingInfo = line.trim().split(" ");
                long destStart = Long.parseLong(numberMappingInfo[0]);
                long srcStart = Long.parseLong(numberMappingInfo[1]);
                long range = Long.parseLong(numberMappingInfo[2]);

                mapInfo.put("srcStart", srcStart);
                mapInfo.put("srcEnd", srcStart + (range - 1));
                mapInfo.put("destStart", destStart);
                mapInfo.put("destEnd", destStart + (range - 1));

                mapsForDifferentItems.add(mapInfo);

                seedCultivationMap.put(mapIdentifier, mapsForDifferentItems);
            }

            iteration++;
        }

        LinkedHashMap<String, List<LinkedHashMap<String, Long>>> sortedSeedCultivationMap =new LinkedHashMap<>();

        for (Map.Entry<String, List<LinkedHashMap<String, Long>>> map: seedCultivationMap.entrySet()) {
            sortedSeedCultivationMap.put(map.getKey(), sortBySrcStart(map.getValue()));
        }

        return sortedSeedCultivationMap;
    }

    public static List<LinkedHashMap<String, Long>> sortBySrcStart(List<LinkedHashMap<String, Long>> hashMapList)
    {
        hashMapList.sort(Comparator.comparing((HashMap<String, Long> map) -> map.get("srcStart")));

        return  hashMapList;
    }

    public static long getLocationNumber (Long seedNumber, LinkedHashMap<String, List<LinkedHashMap<String, Long>>> map)
    {
        long srcCategory = seedNumber;
        long destCategory = 0;

        for (Map.Entry<String, List<LinkedHashMap<String, Long>>> seedNumberToOtherCategoryNumberMap:map.entrySet()) {
            int iteration = 1;
            for (LinkedHashMap<String, Long> srcDestMap: seedNumberToOtherCategoryNumberMap.getValue()) {
                long srcStart = srcDestMap.get("srcStart");
                long srcEnd = srcDestMap.get("srcEnd");
                long destStart = srcDestMap.get("destStart");

                if (srcCategory >= srcStart && srcCategory <= srcEnd) {
                    destCategory = destStart + (srcCategory - srcStart);
                    srcCategory = destCategory;
                    break;
                }

                if (iteration == seedNumberToOtherCategoryNumberMap.getValue().size() && destCategory == 0) {
                    destCategory = srcCategory;
                    break;
                }

                iteration++;
            }
        }

        return destCategory;
    }
}
