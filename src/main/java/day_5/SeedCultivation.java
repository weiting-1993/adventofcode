package day_5;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SeedCultivation
{
    private static final String INPUT_FILE_PATH = "src/main/java/day_5/input.txt";
    private static final Logger logger = Logger.getLogger(SeedCultivation.class.getName());

    public static void main(String[] args)
    {
        try {
            List<String> input = Files.readAllLines(Paths.get(INPUT_FILE_PATH));
            String[] seeds = input.getFirst().trim().substring("seeds: ".length()).split(" ");
            LinkedHashMap<String, List<LinkedHashMap<String, Long>>> seedsCultivationMap = createSeedsCultivationMap(input);
            LinkedHashMap<Long, LinkedHashMap<String, Long>> seedAndItsCorrespondingNumbersMap = createSeedAndItsCorrespondingNumbersMap(seeds, seedsCultivationMap);

            List<Long> locationList = new ArrayList<>();

            for (LinkedHashMap<String, Long> correspondingNumbersMap: seedAndItsCorrespondingNumbersMap.values()) {
                locationList.add(correspondingNumbersMap.get("location"));
            }

            System.out.println("Part 1:" + Collections.min(locationList));
        } catch (IOException e) {
            logger.log(Level.WARNING, "An error occurred", e);
        }
    }

    public static LinkedHashMap<String, List<LinkedHashMap<String, Long>>> createSeedsCultivationMap(List<String> input)
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

    public static LinkedHashMap<Long, LinkedHashMap<String, Long>> createSeedAndItsCorrespondingNumbersMap(
        String[] seeds,
        LinkedHashMap<String, List<LinkedHashMap<String, Long>>> seedsCultivationMap
    ) {
        LinkedHashMap<Long, LinkedHashMap<String, Long>> seedAndItsCorrespondingNumbersMap = new LinkedHashMap<>();

        for (String seed: seeds) {
            long seedNumber = Long.parseLong(seed);
            LinkedHashMap<String, Long> nameAndNumberMap = new LinkedHashMap<>();
            long srcCategory = seedNumber;

            for (Map.Entry<String, List<LinkedHashMap<String, Long>>> seedCultivationMap:seedsCultivationMap.entrySet()) {
                String name = seedCultivationMap.getKey().split("-")[2];

                int iteration = 1;
                for (LinkedHashMap<String, Long> srcDestMap: seedCultivationMap.getValue()) {
                    if (nameAndNumberMap.containsKey(name)) break;

                    long destCategory = 0;
                    long srcStart = srcDestMap.get("srcStart");
                    long srcEnd = srcDestMap.get("srcEnd");
                    long destStart = srcDestMap.get("destStart");

                    if (srcCategory >= srcStart && srcCategory <= srcEnd) {
                        destCategory = destStart + (srcCategory - srcStart);
                        srcCategory = destCategory;
                        nameAndNumberMap.put(name, destCategory);
                    }

                    if (iteration == seedCultivationMap.getValue().size() && destCategory == 0) {
                        destCategory = srcCategory;
                        nameAndNumberMap.put(name, destCategory);
                    }

                    iteration++;
                }
            }

            seedAndItsCorrespondingNumbersMap.put(seedNumber, nameAndNumberMap);
        }

        return seedAndItsCorrespondingNumbersMap;
    }
}
