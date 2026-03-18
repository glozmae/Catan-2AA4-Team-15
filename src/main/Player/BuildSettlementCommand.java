package Player;

import Board.Node;
import GameResources.Cost;
import GameResources.ResourceType;
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

    public BuildSettlementCommand(Player player, Node node) {
        this.player = player;
        this.node = node;
        this.settlement = new Settlement();
        this.cost = this.settlement.getCost();
        this.executed = false;
    }

    @Override
    public void execute() {
        if (executed) {
            return;
        }

        payCost();
        node.setStructure(settlement);
        player.addStructure(settlement);
        executed = true;
    }

    @Override
    public void undo() {
        if (!executed) {
            return;
        }

        player.removeStructure(settlement);
        node.setStructure(null);
        refundCost();
        executed = false;
    }

    @Override
    public String getExecuteMessage() {
        return "Built settlement at node " + node.getId();
    }

    @Override
    public String getUndoMessage() {
        return "Undid settlement at node " + node.getId();
    }

    private void payCost() {
        for (int i = 0; i < cost.getBrick(); i++) player.removeResource(ResourceType.BRICK);
        for (int i = 0; i < cost.getLumber(); i++) player.removeResource(ResourceType.LUMBER);
        for (int i = 0; i < cost.getWool(); i++) player.removeResource(ResourceType.WOOL);
        for (int i = 0; i < cost.getGrain(); i++) player.removeResource(ResourceType.GRAIN);
        for (int i = 0; i < cost.getOre(); i++) player.removeResource(ResourceType.ORE);
    }

    private void refundCost() {
        for (int i = 0; i < cost.getBrick(); i++) player.addResource(ResourceType.BRICK);
        for (int i = 0; i < cost.getLumber(); i++) player.addResource(ResourceType.LUMBER);
        for (int i = 0; i < cost.getWool(); i++) player.addResource(ResourceType.WOOL);
        for (int i = 0; i < cost.getGrain(); i++) player.addResource(ResourceType.GRAIN);
        for (int i = 0; i < cost.getOre(); i++) player.addResource(ResourceType.ORE);
    }
}