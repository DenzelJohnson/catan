package part1;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import part1.Player.TurnResult;
import static org.junit.jupiter.api.Assertions.*;

public class GameTerminationTest {

    /**
     * This player still counts as a ComputerPlayer (so setup works),
     * but it forces a win during the first normal turn.
     */
    static class WinnerComputerPlayer extends ComputerPlayer {
    public WinnerComputerPlayer(int playerId) {
        super(playerId);
    }

    @Override
    public TurnResult turn(Board board) {
        addVictoryPoints(10);
        return Player.TurnResult.pass("Forcing win for test");
    }
}

    @Test
    void stopsEarlyWhenSomeoneReaches10VP() {
        Board board = new Board();
        List<Player> players = new ArrayList<>();
        players.add(new WinnerComputerPlayer(1));
        players.add(new ComputerPlayer(2));
        players.add(new ComputerPlayer(3));
        players.add(new ComputerPlayer(4));

        
        Game game = new Game(board, players, 200);

        String output = runAndCaptureStdout(game);

        assertTrue(output.contains("Game Over! Player 1 reached"),
                "Expected game to end early when Player 1 hits 10 VP.\nOutput:\n" + output);
    }

    @Test
    void stopsAfterMaxRoundsIfNoOneWins() {
        Board board = new Board();
        List<Player> players = new ArrayList<>();
        players.add(new ComputerPlayer(1));
        players.add(new ComputerPlayer(2));
        players.add(new ComputerPlayer(3));
        players.add(new ComputerPlayer(4));

        // Small max rounds so test finishes quickly
        Game game = new Game(board, players, 3);

        String output = runAndCaptureStdout(game);

        assertTrue(output.contains("Game Over! Max round reached"),
                "Expected max-round termination message.\nOutput:\n" + output);

    }

    private String runAndCaptureStdout(Game game) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream old = System.out;
        System.setOut(new PrintStream(out));
        try {
            game.startGame();
        } finally {
            System.setOut(old);
        }
        return out.toString();
    }
}
