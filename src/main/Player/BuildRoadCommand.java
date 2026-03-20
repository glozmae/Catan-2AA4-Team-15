package Player;

import Board.Node;
import GameResources.Cost;
import GameResources.ResourceType;
import GameResources.Road;

/**
 * Command for building a road between two adjacent nodes.
 */
public class BuildRoadCommand implements PlayerCommand {
    private final Player player;
    private final Node start;
    private final Node end;
    private final Road road;
    private final Cost cost;
    private boolean executed;

    /**
     * Constructs a BuildRoadCommand.
     * * @param player The player building the road.
     * @param start The starting node for the road.
     * @param end The ending node for the road.
     */
    public BuildRoadCommand(Player player, Node start, Node end) {
        this.player = player;
        this.start = start;
        this.end = end;
        this.road = new Road();
        this.cost = this.road.getCost();
        this.executed = false;
    }

    /**
     * Executes the command: pays the cost, places the road on the board,
     * and adds it to the player's inventory.
     */
    @Override
    public void execute() {
        if (executed) return;

        CommandCostHelper.payCost(player, cost);
        placeRoad();
        player.addRoad(road);
        executed = true;
    }

    /**
     * Undoes the command: removes the road from the board and player inventory,
     * and refunds the cost.
     */
    @Override
    public void undo() {
        if (!executed) return;

        player.removeRoad(road);
        clearRoad();
        CommandCostHelper.refundCost(player, cost);
        executed = false;
    }

    /**
     * Retrieves the message to display when the command is executed.
     * * @return The execution message.
     */
    @Override
    public String getExecuteMessage() {
        return "Built road [" + start.getId() + "," + end.getId() + "]";
    }

    /**
     * Retrieves the message to display when the command is undone.
     * * @return The undo message.
     */
    @Override
    public String getUndoMessage() {
        return "Removed road [" + start.getId() + "," + end.getId() + "]";
    }

    /**
     * Helper method to physically link the road object to the adjacent nodes.
     */
    private void placeRoad() {
        if (start.getLeft() == end) {
            start.setLeftRoad(road); end.setRightRoad(road);
        } else if (start.getRight() == end) {
            start.setRightRoad(road); end.setLeftRoad(road);
        } else if (start.getVert() == end) {
            start.setVertRoad(road); end.setVertRoad(road);
        }
    }

    /**
     * Helper method to physically remove the road link between the adjacent nodes.
     */
    private void clearRoad() {
        if (start.getLeft() == end) {
            start.clearLeftRoad(); end.clearRightRoad();
        } else if (start.getRight() == end) {
            start.clearRightRoad(); end.clearLeftRoad();
        } else if (start.getVert() == end) {
            start.clearVertRoad(); end.clearVertRoad();
        }
    }
}

/**
 * Helper class to centralize cost processing for build commands.
 * By keeping it package-private, all commands in the Player package can use it,
 * eliminating duplicate code across the command classes.
 */
class CommandCostHelper {

    /**
     * Deducts the specified cost from the player's resource hand.
     * * @param player The player paying the cost.
     * @param cost The cost to deduct.
     */
    static void payCost(Player player, Cost cost) {
        adjust(player, cost, false);
    }

    /**
     * Refunds the specified cost back to the player's resource hand.
     * * @param player The player receiving the refund.
     * @param cost The cost to refund.
     */
    static void refundCost(Player player, Cost cost) {
        adjust(player, cost, true);
    }

    /**
     * Internal logic to iterate through all resource types and either add or remove them.
     * * @param p The player whose inventory is being adjusted.
     * @param c The cost matrix to apply.
     * @param refund True to add resources, false to remove them.
     */
    private static void adjust(Player p, Cost c, boolean refund) {
        for (int i = 0; i < c.getBrick(); i++) if (refund) p.addResource(ResourceType.BRICK); else p.removeResource(ResourceType.BRICK);
        for (int i = 0; i < c.getLumber(); i++) if (refund) p.addResource(ResourceType.LUMBER); else p.removeResource(ResourceType.LUMBER);
        for (int i = 0; i < c.getWool(); i++) if (refund) p.addResource(ResourceType.WOOL); else p.removeResource(ResourceType.WOOL);
        for (int i = 0; i < c.getGrain(); i++) if (refund) p.addResource(ResourceType.GRAIN); else p.removeResource(ResourceType.GRAIN);
        for (int i = 0; i < c.getOre(); i++) if (refund) p.addResource(ResourceType.ORE); else p.removeResource(ResourceType.ORE);
    }
}