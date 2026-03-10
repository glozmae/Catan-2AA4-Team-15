package Player;

import java.util.*;
import Board.Node;
import Game.Game;
import GameResources.City;
import GameResources.Cost;
import GameResources.ResourceType;
import GameResources.Road;
import GameResources.Settlement;
import GameResources.Structure;

/**
 * This class implements the human player.
 *
 * @author Parnia Yazdinia, 400567795, McMaster University
 */
public class HumanPlayer extends Player{
    /** Reads commands from the console for this human player. */
    private final Scanner commandReader;

    /**
     * Constructor for a human player.
     */
    public HumanPlayer(){
        super();
    }

    /**
     * Runs the human player's turn.
     *
     * Note:
     * In the current Game class, the roll already happens before this method is
     * called. So for now, the "roll" command confirms that the player has seen
     * the roll result.
     *
     * @param game current game
     */
    @Override
    public void takeTurn(Game game) {
        boolean hasRolledThisTurn = false;
        boolean turnFinished = false;

        System.out.println();
        System.out.println("Human Player " + (getId() + 1) + " turn.");
        System.out.println("Commands: roll | list | build settlement <nodeId> | build city <nodeId> | build road <fromNodeId,toNodeId> | go");

        while (!turnFinished) {
            System.out.print("> ");
            String command = commandReader.nextLine().trim();

            if (command.isEmpty()) {
                System.out.println("Enter a command.");
                continue;
            }

            if (command.equalsIgnoreCase("roll")) {
                if (hasRolledThisTurn) {
                    System.out.println("You already rolled this turn.");
                } else {
                    hasRolledThisTurn = true;
                    System.out.println("Current roll: " + game.getLastRoll());
                }
                continue;
            }

            if (command.equalsIgnoreCase("list")) {
                printHand();
                continue;
            }

            if (command.equalsIgnoreCase("go")) {
                if (!hasRolledThisTurn) {
                    System.out.println("You must enter 'roll' before ending your turn.");
                } else {
                    turnFinished = true;
                    System.out.println("Turn ended.");
                }
                continue;
            }

            if (!hasRolledThisTurn) {
                System.out.println("You must enter 'roll' before building.");
                continue;
            }

            if (command.equalsIgnoreCase("go")) {
                if (!hasRolledThisTurn) {
                    System.out.println("You must enter 'roll' before ending your turn.");
                } else {
                    turnFinished = true;
                    System.out.println("Turn ended.");
                }
                continue;
            }

            if (!hasRolledThisTurn) {
                System.out.println("You must enter 'roll' before building.");
                continue;
            }

            if (command.startsWith("build city ")) {
                Integer nodeId = readSingleNodeId(command, "build city ");
                if (nodeId == null) {
                    System.out.println("Invalid command. Usage: build city <nodeId>");
                } else {
                    buildCity(game, nodeId);
                }
                continue;
            }

            if (command.startsWith("build road ")) {
                int[] roadEnds = readRoadNodeId(command.substring("build road ".length()).trim()); //shoudl make this simpler
                if (roadEnds == null) {
                    System.out.println("Invalid command. Usage: build road <fromNodeId,toNodeId>");
                } else {
                    buildRoad(game, roadEnds[0], roadEnds[1]);
                }
                continue;
            }
            System.out.println("Unknow command.");
        }
    }

    /**
     * Prints the current resource counts in the player's hand
     */
    private void printHand(){
        System.out.println("Hand: BRICK=" + getResourceAmount(ResourceType.BRICK)
                + " | LUMBER=" + getResourceAmount(ResourceType.LUMBER)
                + " | WOOL=" + getResourceAmount(ResourceType.WOOL)
                + " | GRAIN=" + getResourceAmount(ResourceType.GRAIN)
                + " | ORE=" + getResourceAmount(ResourceType.ORE));
    }

    /**
     * Build a settelment on the given node.
     * @param game game being played
     * @param nodeID target node id
     */
    private void buildSettlement(Game game, int nodeID){
        Node node = findNode(game, nodeId);

        if (node == null){
            System.out.println("Invalid node id entered.");
            return;
        }
        if(getSettlements().size() >= Settlement.getMax()){
            System.out.println("You have already built the maximum number of settlements.");
            return;
        }
        if (!node.canBuildSettlement(this)){
            System.out.println("You cannot build a settlement there.");
        }

        Settlement settlement = new Settlement();
        Cost cost = settlement.getCost();

        if(!affordable(cost)){
            System.out.println("Not enough resources to build a settlement.");
            return;
        }

        payCost(cost);
        node.setPlayer(this);
        node.setStructure(settlement);
        addStructure(settlement);
        System.out.println("Build settlement at node " + nodeId + ".");
    }

    /**
     * Upgrade one of the player's settlements to a city.
     *
     * @param game game being played
     * @param nodeId target node id
     */
    private void buildCity(Game game, int nodeId) {
        Node node = findNode(game, nodeId);

        if (node == null){
            System.out.println("Invalid node id entered.");
            return;
        }
        if(getCities().size() >= City.getMax()){
            System.out.println("You have already built the maximum number of cities.");
            return;
        }
        Structure currrentStructure = node.getStructure();
        if (!node.getPlayer() != this || !(currentStructure instanceof Settlement)){
            System.out.println("You can only upgrade one of your own settlements.");
        }

        City city = new City();
        Cost cost = city.getCost();

        if(!affordable(cost)){
            System.out.println("Not enough resources to build a city.");
            return;
        }
        System.out.println("City upgrade will be completed after the next Node/Game update.");
    }

    /**
     * Builds a road between two nodes.
     *
     * @param game game being played
     * @param fromNodeId start node id
     * @param toNodeId end node id
     */
    private void buildRoad(Game game, int fromNodeId, int toNodeId) {
        if (fromNodeId == toNodeId){
            System.out.println("A road must have two different node ids.");
            return;
        }

        Node beginNode = findNode(game, fromNodeId);
        Node endNode = findNode(game toNodeId);

        if(beginNode == null || endNode == null){
            System.out.println("Invalid node id.");
            return;
        }
        if (getRoads().size >= Road.getMax()){
            System.out.println("You have already built the maximum number of roads.");
            return;
        }

        //mak it easier too long
       boolean canBuild = beginNode.getBuildableNeighbors(this).contains(endNode) || endNode.getBuildableRoadNeighbors(this).contains(beginNode);

        if(!canBuild){
            System.out.println("You cannot build a road on that edge.");
            return;
        }

        Road road = new Road();
        Cost cost = road.getCost();

        if(!affordable(cost)){
            System.out.println("Not enough resources to build a road");
        }

        payCost(cost);

        if(beginNode.getLeft() == endNode){
            beginNode.setLeftRoad(road);
            endNode.setRightRoad(road);
        }else if (beginNode.getRight() == endNode){
            beginNode.setRightRoad(road);
            endNode.setLeftRoad(road);
        }else if (beginNode.getVert() == endNode){
            beginNode.setVertRoad(road);
            endNode.setVertRoad(road);
        }
        addRoad(road);
        System.out.println("Built road from node " + fromNodeId + " to node " + toNodeId + ".");
    }

    /**
     * This method checks if the player can afford a cost.
     * @param cost required cost
     * @return true if the player has enough resources, otherwise false
     */
    private boolean affordable(Cost cost){
        return getResourceAmount(ResourceType.BRICK) >= cost.getBrick()
                && getResourceAmount(ResourceType.LUMBER) >= cost.getLumber()
                && getResourceAmount(ResourceType.WOOL) >= cost.getWool()
                && getResourceAmount(ResourceType.GRAIN) >= cost.getGrain()
                && getResourceAmount(ResourceType.ORE) >= cost.getOre();
    }


}