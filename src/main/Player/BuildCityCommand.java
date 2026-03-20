package Player;

import Board.Node;
import GameResources.City;
import GameResources.Cost;
import GameResources.Settlement;

/**
 * Command for upgrading a settlement into a city.
 */
public class BuildCityCommand implements PlayerCommand {
    private final Player player;
    private final Node node;
    private final Settlement previousSettlement;
    private final City city;
    private final Cost cost;
    private boolean executed;

    /**
     * Constructs a BuildCityCommand.
     * * @param player The player building the city.
     * @param node The node where the existing settlement is located.
     */
    public BuildCityCommand(Player player, Node node) {
        this.player = player;
        this.node = node;
        this.previousSettlement = (Settlement) node.getStructure();
        this.city = new City();
        this.cost = this.city.getCost();
        this.executed = false;
    }

    /**
     * Executes the command: removes the previous settlement, pays the cost,
     * places the city on the board, and adds it to the player's inventory.
     */
    @Override
    public void execute() {
        if (executed) return;

        player.removeStructure(previousSettlement);
        CommandCostHelper.payCost(player, cost);
        node.setStructure(city);
        player.addStructure(city);
        executed = true;
    }

    /**
     * Undoes the command: removes the city, restores the previous settlement,
     * and refunds the cost.
     */
    @Override
    public void undo() {
        if (!executed) return;

        player.removeStructure(city);
        node.setStructure(previousSettlement);
        player.addStructure(previousSettlement);
        CommandCostHelper.refundCost(player, cost);
        executed = false;
    }

    /**
     * Retrieves the message to display when the command is executed.
     * * @return The execution message.
     */
    @Override
    public String getExecuteMessage() {
        return "Built city at node " + node.getId();
    }

    /**
     * Retrieves the message to display when the command is undone.
     * * @return The undo message.
     */
    @Override
    public String getUndoMessage() {
        return "Removed city from node " + node.getId();
    }
}