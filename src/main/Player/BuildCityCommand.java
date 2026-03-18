package Player;

import Board.Node;
import GameResources.City;
import GameResources.Cost;
import GameResources.ResourceType;
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

    public BuildCityCommand(Player player, Node node) {
        this.player = player;
        this.node = node;
        this.previousSettlement = (Settlement) node.getStructure();
        this.city = new City();
        this.cost = this.city.getCost();
        this.executed = false;
    }

    @Override
    public void execute() {
        if (executed) {
            return;
        }

        player.removeStructure(previousSettlement);
        payCost();
        node.setStructure(city);
        player.addStructure(city);
        executed = true;
    }

    @Override
    public void undo() {
        if (!executed) {
            return;
        }

        player.removeStructure(city);
        node.setStructure(previousSettlement);
        player.addStructure(previousSettlement);
        refundCost();
        executed = false;
    }

    @Override
    public String getExecuteMessage() {
        return "Built city at node " + node.getId();
    }

    @Override
    public String getUndoMessage() {
        return "Removed city from node " + node.getId();
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