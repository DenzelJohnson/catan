package part1;

import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
public class ConfigLoader {

    public static int loadTurns(String filename) {
        try (Scanner scanner = new Scanner(new File(filename))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();

                if (line.startsWith("turns:")) {
                    int turns = Integer.parseInt(line.split(":")[1].trim());

                    if (turns < 1 || turns > 8192) {
                        throw new IllegalArgumentException("Turns must be between 1 and 8192");
                    }

                    return turns;
                }
            }

            throw new IllegalArgumentException("Missing turns field");

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
