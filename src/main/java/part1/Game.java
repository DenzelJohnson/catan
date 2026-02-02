package part1;
import java.util.ArrayList;
import java.util.List;

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

    private Player longestRoadHolder;


    public Game(Board board, List<Player> players) {

        this.board = board;
        this.players = new ArrayList<>(players);

        this.startingPlayerIndex = 0;
        this.currentPlayerIndex = 0;
        this.rounds = 0;

        this.isWinner = false;
        this.winner = null;

        this.longestRoadHolder = null;

    }






    public void startGame() {

        determineStartingPlayer();

        playFirstTwoRoundsSetup();
        playMainGame();

    }

    private void determineStartingPlayer() {

        int bestRoll = -1;
        int bestIndex = 0;

        while (true) {

            bestRoll = -1;
            bestIndex = 0;
            boolean tie = false;

            for (int i = 0; i < players.size(); i++) {

                int roll = players.get(i).diceRoll();
                if (roll > bestRoll) {

                    bestRoll = roll;
                    bestIndex = i;
                    tie = false;
                }
                else if (roll == bestRoll) {

                    tie = true;
                }
            }

            if (!tie) {
                break;
            }

        }
        startingPlayerIndex = bestIndex;
        currentPlayerIndex = startingPlayerIndex;

    }

    private void playFirstTwoRoundsSetup() {

        // Round 1
        System.out.println("\nGame has started!\n");

        for (int step = 0; step < players.size(); step++){
            int index = getPlayerIndexClockwise(startingPlayerIndex, step);
            Player p = players.get(index);

            placeInitialSettlementAndRoad(p, 1);
            currentPlayerIndex = index;
        }
        rounds = 1;
        displayRoundSummary();

        // Round 2
        for (int step = 0; step < players.size(); step++){
            int index = getPlayerIndexCounterClockwise(startingPlayerIndex, step);
            Player p = players.get(index);

            int secondSettlementNodeId = placeInitialSettlementAndRoad(p, 2);
            addStartingResourcesFromSecondSettlement(p, secondSettlementNodeId);
            
            currentPlayerIndex = index;
        }
        
        rounds = 2;
        displayRoundSummary();
        currentPlayerIndex = startingPlayerIndex;
        

    }

    private void playMainGame() {

        while (!isWinner && rounds < MAX_ROUNDS) {

            for (int step = 0; step < players.size(); step++){
                currentPlayerIndex = getPlayerIndexClockwise(startingPlayerIndex, step);
                Player p = players.get(currentPlayerIndex);

                String action = p.turn(board);
                displayTurnSummary(rounds, p.getPlayerId(), action);
                checkWinner();

                if (isWinner) {
                    break;
                }

            }

            rounds++;
            displayRoundSummary();

        }

        if (!isWinner) {
            System.out.println("\n\nGame Over! Max round reached.\n");
        }
    }

    private void checkWinner() {

        for (int i = 0; i < players.size(); i++) {
            Player p = players.get(i);

            if (p.getVictoryPoints() >= TARGET_VICTORY_POINTS) {
                isWinner = true;
                winner = p;

                System.out.println("\n\nGame Over! Player " + p.getPlayerId() + " reached " + p.getVictoryPoints() + " victory points.\n");

                return;
            }
        }

    }












    private int getPlayerIndexClockwise(int startIndex, int stepsForward) {

        int playerCount = players.size();
        int indexAfterMovingForward = startIndex + stepsForward;
        int indexAfterRotating = indexAfterMovingForward % playerCount;
        return indexAfterRotating;

    }

    private int getPlayerIndexCounterClockwise(int startIndex, int stepsBackward) {

        int playerCount = players.size();
        int indexAfterMovingBackwards = startIndex - stepsBackward;

        while (indexAfterMovingBackwards < 0) {
            indexAfterMovingBackwards = indexAfterMovingBackwards + playerCount;
        }

        int indexAfterRotating = indexAfterMovingBackwards % playerCount;
        return indexAfterRotating;

    }

    /**
     * Instead of using instanceof,
     * Use abstract methods in player class to handle human and computer
     */
    private int placeInitialSettlementAndRoad(Player p, int roundNumber) {

        if (p instanceof ComputerPlayer) {

            ComputerPlayer cp = (ComputerPlayer) p;

            int settlementNodeId = cp.placeRandomValidSettlement(board, (roundNumber == 2));
            displayTurnSummary(roundNumber, p.getPlayerId(), "Placed settlement at node " + settlementNodeId);

            int edgeIndex = cp.placeRandomValidRoad(board, settlementNodeId);
            displayTurnSummary(roundNumber, p.getPlayerId(), "Placed road at edge " + edgeIndex + " (adjacent to node " + settlementNodeId + ")");

            return settlementNodeId;

        }

        return -1;

    }

    private void addStartingResourcesFromSecondSettlement(Player p, int settlementNodeId) {

        Board.Node node = board.getNode(settlementNodeId);

        Integer[] adjacentTileIds = node.adjacentTileIds.toArray(new Integer[0]);

        for (int i = 0; i < adjacentTileIds.length; i++) {

            int tileId = adjacentTileIds[i].intValue();

            Board.TerrainTile tile = board.getTile(tileId);
            if (tile == null) {
                continue;
            }
            if (tile.resourceType == null) { // desert
                continue;
            }

            p.addResource(tile.resourceType, 1);
        }

        displayTurnSummary(2, p.getPlayerId(), "Gained starting resources from 2nd settlement at node " + settlementNodeId);

    }

    private void displayTurnSummary(int roundNumber, int playerId, String action) {

        System.out.println("[" + roundNumber + "] / [" + playerId + "]: " + action);

        
        try { 
            Thread.sleep(500);
        } 

        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

    }

    private void displayRoundSummary() {

        StringBuilder summary = new StringBuilder();
        summary.append("Round " + rounds + " Summary: \n\n");

        for (int i = 0; i < players.size(); i++) {
            Player p = players.get(i);

            summary.append("Player ").append(p.getPlayerId()).append(": ");

            for (int r = 0; r < ResourceType.values().length; r++) {
                ResourceType type = ResourceType.values()[r];

                summary.append(type.name()).append("=").append(p.getResourceCount(type));

                if (r < ResourceType.values().length - 1) {
                    summary.append(", ");
                }
            }

            summary.append(" | longestRoadStreak=").append(p.getLongestRoadStreak()).append(" | victoryPoints=").append(p.getVictoryPoints());
            summary.append("\n\n");
                    
        }

        summary.append("---------------------------------------------------------------------------------\n\n");
        System.out.print(summary.toString());


        try { 
            Thread.sleep(500);
        } 

        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

    }


    
}