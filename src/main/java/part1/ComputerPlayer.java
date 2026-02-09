package part1;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * ComputerPlayer
 * A Player that makes random choices.
 * It rolls the dicethen collects resources, then randomly builds a road/settlement/city
 * if it can and if there are valid spots.
 *
 * @author Team 28
 */

public class ComputerPlayer extends Player {

    private final Random r = new Random();

    public ComputerPlayer(int playerId){

        super(playerId);

    } 
    




/**
     * Runs one turn for the computer player.
     * Roll dice then collects resources, then try to build one thing (random).
     *
     * @param board game board
     * @return a text summary of what happened this turn
     */
    @Override
    public String turn(Board board){

    // 1) start turn with dice roll
        String action = "";
        int roll = diceRoll();


    // 2) check to add resources based on roll
        for (int tileId = 0; tileId < Board.TILE_COUNT; tileId++) {

            Board.TerrainTile tile = board.getTile(tileId);

            if (tile == null){
                continue;
            }
            if (tile.diceToken != roll) {
                continue;
            }
            if (tile.resourceType == null) { // desert
                continue;
            }
            
            for (int i = 0; i < tile.cornerNodeIdsClockwise.length; i++) {

                int nodeId = tile.cornerNodeIdsClockwise[i];

                Board.Building building = board.getBuilding(nodeId);
                if (building == null){
                    continue;
                }

                if (building.ownerPlayerId != getPlayerId()){
                    continue;
                }

                int amount;
                if (building.kind == BuildingKind.SETTLEMENT) {
                    amount = 1; 
                } else {
                    amount = 2;
                }

                addResource(tile.resourceType, amount);
                action += "Combined dice roll is " + roll + ". So, collected " + amount + " " + tile.resourceType + ". ";

            }

        }


        int totalCards = 0;
        for (int i = 0; i < ResourceType.values().length; i++) {
            ResourceType type = ResourceType.values()[i];
            totalCards += getResourceCount(type);
        }

        if (totalCards > 7) { // R1.8

        // 3) Find all valid moves between...
            // --> Build road
            // --> Build settlement
            // --> Build city
            // a) validate user has enough resources before checking valid moves
            boolean canBuildRoad = canAffordRoad();
            boolean canBuildSettlement = canAffordSettlement();
            boolean canBuildCity = canAffordCity();

            if (!canBuildRoad && !canBuildSettlement && !canBuildCity) {
                return "Cannot afford any roads or buildings.";
            }

            // b) Check valid placements
            List<Integer> validRoadEdges = new ArrayList<Integer>();
            List<Integer> validSettlementNodes = new ArrayList<Integer>();
            List<Integer> validCityNodes = new ArrayList<Integer>();

            if (canBuildRoad) {
                validRoadEdges = findValidRoadPlacements(board);
            }

            if (canBuildSettlement) {
                validSettlementNodes = findValidSettlementPlacements(board);
            }

            if (canBuildCity) {
                validCityNodes = findValidCityPlacements(board);
            }


        // 4) Randomize move between valid options
            int totalOptions = validRoadEdges.size() + validSettlementNodes.size() + validCityNodes.size();
            if (totalOptions == 0) {
                action += "No valid moves to be made.";
                return action;
            }
            int choice = r.nextInt(totalOptions);

            // buildRoad
            if (choice < validRoadEdges.size()) {
                int edgeIndex = validRoadEdges.get(choice).intValue();
                buildRoad(board, edgeIndex);
                action += "Placed road at edge " + edgeIndex;
                return action;

            }

            // buildSettlement
            choice -= validRoadEdges.size();
            if (choice < validSettlementNodes.size()) {
                int nodeId = validSettlementNodes.get(choice).intValue();
                buildSettlement(board, nodeId);
                action += "Placed settlement at node " + nodeId;
                return action;

            }

            // buildCity
            choice -= validSettlementNodes.size();
            int nodeId = validCityNodes.get(choice).intValue();
            buildCity(board, nodeId);
            action += "Placed city at node " + nodeId;
            return action;
        }

        else { // under 8 cards
            action += "Under 8 cards, so no move.";
        }

        return action;

    }









    // Helpers
    /**
     * Places a settlement at a random valid node.
     * It tries many random nodes until it finds one that works.
     *
     * @param board game board
     * @param isSecondSettlement true if this is the second starting settlement (not used here)
     * @return the node id where it placed the settlement
     */
    public int placeRandomValidSettlement(Board board, boolean isSecondSettlement) {

        int maxAttempts = 10000;

        for (int attempt = 0; attempt < maxAttempts; attempt++) {

            int nodeId = r.nextInt(Board.NODE_COUNT);

            if (!board.isNodeEmpty(nodeId)){
                continue;
            }
            if (board.violatesDistanceRule(nodeId)) {
                continue;
            }

            board.placeSettlement(nodeId, this);
            return nodeId;

        }

        throw new IllegalStateException("Nowhere to place a valid settlement");

    }
/**
     * Places a road next to a given settlement node.
     * Picks a random adjacent edge that is empty.
     *
     * @param board game board
     * @param settlementNodeId node id of the settlement
     * @return the edge index where it placed the road
     */
    public int placeRandomValidRoad(Board board, int settlementNodeId) {

        List<Integer> adjacentEdgeIndices = board.getAdjacentEdgeIndicesForNode(settlementNodeId);

        if (adjacentEdgeIndices.isEmpty()) {
            throw new IllegalStateException("No edges adjacent to node " + settlementNodeId + ". Board edges arent initialized yet.");
        }

        List<Integer> freeAdjacentEdges = new ArrayList<>();
        for (int i = 0; i < adjacentEdgeIndices.size(); i++){

            int edgeIndex = adjacentEdgeIndices.get(i).intValue();
            if (board.isRoadEmpty(edgeIndex)){
                freeAdjacentEdges.add(Integer.valueOf(edgeIndex));
            }

        }

        if (freeAdjacentEdges.isEmpty()) {
            throw new IllegalStateException("No free adjacent edges for the road the node: " + settlementNodeId);
        }

        int chosenEdgeIndex = freeAdjacentEdges.get(r.nextInt(freeAdjacentEdges.size()));
        board.placeRoad(chosenEdgeIndex, getPlayerId());
        return chosenEdgeIndex;

    }
}