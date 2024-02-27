package year2023.day10;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PipeMazeStepCalculator
{
    private static final String INPUT_FILE_PATH = "src/main/java/year2023/day10/input.txt";
    private static final Logger logger = Logger.getLogger(PipeMazeStepCalculator.class.getName());
    private static final char NORTH = 'N';
    private static final char SOUTH = 'S';
    private static final char WEST = 'W';
    private static final char EAST = 'E';
    private static final char VERTICAL_PIPE = '|';
    private static final char HORIZONTAL_PIPE = '-';
    private static final char L_PIPE = 'L';
    private static final char J_PIPE = 'J';
    private static final char SEVEN_PIPE = '7';
    private static final char F_PIPE = 'F';
    private static final char GROUND = '.';
    private static final char START_PIPE = 'S';

    public static void main(String[] args)
    {
        try {
            List<String> input = Files.readAllLines(Paths.get(INPUT_FILE_PATH));
            HashMap<String, Character> positionMap = createPositionMap(input);

            List<String> queue = new ArrayList<>();
            List<String> visited = new ArrayList<>();

            String sPosition = findSPosition(input);
            queue.add(sPosition);
            visited.add(sPosition);

            while (!queue.isEmpty()) {
                String currentPosition = queue.removeFirst();
                HashMap<Character, String> neighborsPosition = findNeighborsPosition(currentPosition);

                for(Map.Entry<Character, String> neighborPosition: neighborsPosition.entrySet()) {
                    if (isValidMove(currentPosition, neighborPosition, positionMap)) {
                        String neighborPipePosition = neighborPosition.getValue();
                        if (!visited.contains(neighborPipePosition)) {
                            queue.add(neighborPosition.getValue());
                            visited.add(neighborPosition.getValue());
                            break;
                        }
                    }
                }
            }

            System.out.println("Part 1: " + visited.size() / 2);
            System.out.println("Part 2: " + calculateNumOfTilesEnclosedByLoop(visited));
        } catch (IOException e) {
            logger.log(Level.WARNING, "An error occurred while trying to read a file.", e);
        }
    }

    private static String findSPosition(List<String> maze)
    {
        String postion = "";

        for (int i = 0; i < maze.size(); i++) {
            if (!maze.get(i).contains("S")) {
                continue;
            }

            postion = String.format("%s,%s",  maze.get(i).indexOf("S"), i);
        }

        return postion;
    }

    private static HashMap<String, Character> createPositionMap(List<String> maze) {
        HashMap<String, Character> postionMap = new HashMap<>();

        for (int lineIndex = 0; lineIndex < maze.size(); lineIndex++) {
            String line = maze.get(lineIndex);

            for (int charIndex = 0; charIndex < line.length(); charIndex++) {
                postionMap.put(String.format("%s,%s", charIndex, lineIndex), line.charAt(charIndex)); //x,y
            }
        }

        return postionMap;
    }

    private static HashMap<Character, String> findNeighborsPosition(String currentPosition)
    {
        HashMap<Character, String> neighborsPosition = new HashMap<>();
        String[] coordinates = currentPosition.split(",");

        int x = Integer.parseInt(coordinates[0]);
        int y = Integer.parseInt(coordinates[1]);

        neighborsPosition.put(NORTH, String.format("%d,%d", x, y - 1));
        neighborsPosition.put(SOUTH, String.format("%d,%d", x, y + 1));
        neighborsPosition.put(WEST, String.format("%d,%d", x - 1, y));
        neighborsPosition.put(EAST, String.format("%d,%d", x + 1, y));

        return neighborsPosition;
    }

    private static boolean isValidMove(String currentPosition, Map.Entry<Character, String> neighborPosition, HashMap<String, Character> positionMap)
    {
        char currentPipe = positionMap.get(currentPosition);
        char direction = neighborPosition.getKey();
        char neighborPipe = Objects.requireNonNullElse(positionMap.get(neighborPosition.getValue()), GROUND);

        return switch (direction) {
            case NORTH ->
                List.of(VERTICAL_PIPE, L_PIPE, J_PIPE, START_PIPE).contains(currentPipe) && List.of(VERTICAL_PIPE, SEVEN_PIPE, F_PIPE, START_PIPE).contains(neighborPipe);
            case SOUTH ->
                List.of(VERTICAL_PIPE, SEVEN_PIPE, F_PIPE, START_PIPE).contains(currentPipe) && List.of(VERTICAL_PIPE, L_PIPE, J_PIPE, START_PIPE).contains(neighborPipe);
            case WEST ->
                List.of(HORIZONTAL_PIPE, J_PIPE, SEVEN_PIPE, START_PIPE).contains(currentPipe) && List.of(HORIZONTAL_PIPE, L_PIPE, F_PIPE, START_PIPE).contains(neighborPipe);
            case EAST ->
                List.of(HORIZONTAL_PIPE, L_PIPE, F_PIPE, START_PIPE).contains(currentPipe) && List.of(HORIZONTAL_PIPE, SEVEN_PIPE, J_PIPE, START_PIPE).contains(neighborPipe);
            default -> false;
        };
    }

    // shoelace formula
    private static double calculateNumOfTilesEnclosedByLoop(List<String> vertexPositions)
    {
        var area = 0.0;
        var vertexAmounts = vertexPositions.size();

        for (var i = 0; i < vertexAmounts; i++) {
            var nextIndex = (i + 1) % vertexAmounts;
            var currentVertexCoordinate = vertexPositions.get(i).split(",");
            var nextVertexCoordinate = vertexPositions.get(nextIndex).split(",");

            area += Integer.parseInt(currentVertexCoordinate[0]) * Integer.parseInt(nextVertexCoordinate[1]);
            area -= Integer.parseInt(currentVertexCoordinate[1]) * Integer.parseInt(nextVertexCoordinate[0]);
        }

        area = Math.abs(area) / 2;

        return area - (double) vertexAmounts / 2 + 1;
    }
}
