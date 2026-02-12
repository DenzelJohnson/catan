package part1;
import java.util.List;
import java.util.ArrayList;

public class Demonstrator {
    public static void main(String[] args) {

        int turns = ConfigLoader.loadTurns("config.txt");

        Board board = new Board();

        List<Player> players = new ArrayList<>();
        players.add(new ComputerPlayer(1));
        players.add(new ComputerPlayer(2));
        players.add(new ComputerPlayer(3));
        players.add(new ComputerPlayer(4));

        Game game = new Game(board, players, turns);
        game.startGame();
    }
}
