package part1;

public class Game {

    private static final int TARGET_VICTORY_POINTS = 10;
    private static final int MAX_ROUNDS = 8192;

    private final Board board;
    private final List<Player> players;

    private int startingPlayerIndex;
    private int currentPlayerIndex;
    private int rounds;

    private boolean isWinner;
    private Player winner;

    private final Random r = new Random();


    public Game(Board board, List<Player> players) {

        this.board = board;
        this.players = new ArrayList<>(players);
        this.startingPlayerIndex = 0;
        this.currentPlayerIndex = 0;
        this.rounds = 0;
        this.isWinner = false;
        this.winner = null;

    }






    public void startGame() {

        determineStartingPlayer();
        playerFirstTwoRoundsSetup();
        playMainGame();

    }

    private void determineStartingPlayer() {


    }

    private void playFirstTwoRoundsSetup() {


    }

    private void playMainGame() {


    }











    private void checkWinner() {


    }

    private int clockwiseIndex() {


    }

    private int counterClockwiseIndex() {


    }

    private int placeInitialSettlementAndRoad() {


    }

    private void addStartingResourcesFromSecondSettlement() {


    }







    
}