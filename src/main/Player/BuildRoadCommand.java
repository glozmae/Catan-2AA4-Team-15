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

    public BuildRoadCommand(Player player, Node start, Node end) {
        this.player = player;
        this.start = start;
        this.end = end;
        this.road = new Road();
        this.cost = this.road.getCost();
        this.executed = false;
    }

    @Override
    public void execute() {
        if (executed) {
            return;
        }

        payCost();
        placeRoad();
        player.addRoad(road);
        executed = true;
    }

    @Override
    public void undo() {
        if (!executed) {
            return;
        }

        player.removeRoad(road);
        clearRoad();
        refundCost();
        executed = false;
    }

    @Override
    public String getExecuteMessage() {
        return "Built road [" + start.getId() + "," + end.getId() + "]";
    }

    @Override
    public String getUndoMessage() {
        return "Removed road [" + start.getId() + "," + end.getId() + "]";
    }

    private void placeRoad() {
        if (start.getLeft() == end) {
            start.setLeftRoad(road);
            end.setRightRoad(road);
        } else if (start.getRight() == end) {
            start.setRightRoad(road);
            end.setLeftRoad(road);
        } else if (start.getVert() == end) {
            start.setVertRoad(road);
            end.setVertRoad(road);
        }
    }

    private void clearRoad() {
        if (start.getLeft() == end) {
            start.clearLeftRoad();
            end.clearRightRoad();
        } else if (start.getRight() == end) {
            start.clearRightRoad();
            end.clearLeftRoad();
        } else if (start.getVert() == end) {
            start.clearVertRoad();
            end.clearVertRoad();
        }
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