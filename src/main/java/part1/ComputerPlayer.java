package part1;

public class ComputerPlayer extends Player {




    public ComputerPlayer(int playerId){

        super(playerId);

    } 
    
    @Override
    public void turn(Board board){

        // 1) start turn with dice roll
        int roll = diceRoll();

        // 2) check to add resources based on roll
        for (int i = 0; tileId < Board.TILE_COUNT; tileId++) {

            Board.TerrainTile tile = board.getTile(tileId);

            if (tile == null){
                continue
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

                int amount = (building.kind == BuildingKind.SETTLEMENT) ? 1 : 2;
                addResource(title.resourceType, amount);
            }

        }

        // 3) First 2 turns 


        // 4) regular turn add 
        
    }
}