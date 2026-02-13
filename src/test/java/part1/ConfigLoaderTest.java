package part1;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class ConfigLoaderTest {

    @TempDir
    Path tempDir;

    @Test
    void loadsValidTurns() throws Exception {
        Path config = tempDir.resolve("config.txt");
        Files.writeString(config, "turns: 50");

        int turns = ConfigLoader.loadTurns(config.toString());
        assertEquals(50, turns);
    }

    @Test
    void rejectsInvalidTurns() throws Exception {
        Path config = tempDir.resolve("config.txt");
        Files.writeString(config, "turns: 9000");

        assertThrows(IllegalArgumentException.class,
                () -> ConfigLoader.loadTurns(config.toString()));
    }
}
