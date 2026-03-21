package Player;

import Board.Node;
import GameResources.Cost;
import GameResources.Settlement;

/**
 * Command for building a settlement on a specific node.
 */
public class BuildSettlementCommand implements PlayerCommand {
    private final Player player;
    private final Node node;
    private final Settlement settlement;
    private final Cost cost;
    private boolean executed;

    /**
     * Constructs a BuildSettlementCommand.
     * * @param player The player building the settlement.
     * @param node The node where the settlement will be built.
     */
    public BuildSettlementCommand(Player player, Node node) {
        this.player = player;
        this.node = node;
        this.settlement = new Settlement();
        this.cost = this.settlement.getCost();
        this.executed = false;
    }

    /**
     * Executes the command: pays the cost, places the settlement on the board,
     * and adds it to the player's inventory.
     */
    @Override
    public void execute() {
        if (executed) return;

        CommandCostHelper.payCost(player, cost);
        node.setStructure(settlement);
        player.addStructure(settlement);
        executed = true;
    }

    /**
     * Undoes the command: removes the settlement from the board and player inventory,
     * and refunds the cost.
     */
    @Override
    public void undo() {
        if (!executed) return;

        player.removeStructure(settlement);
        node.setStructure(null);
        CommandCostHelper.refundCost(player, cost);
        executed = false;
    }

    /**
     * Retrieves the message to display when the command is executed.
     * * @return The execution message.
     */
    @Override
    public String getExecuteMessage() {
        return "Built settlement at node " + node.getId();
    }

    /**
     * Retrieves the message to display when the command is undone.
     * * @return The undo message.
     */
    @Override
    public String getUndoMessage() {
        return "Removed settlement from node " + node.getId();
    }
}