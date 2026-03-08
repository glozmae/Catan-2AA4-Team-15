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
        //
    }

    /**
     * Upgrade one of the player's settlements to a city.
     *
     * @param game game being played
     * @param nodeId target node id
     */
    private void buildCity(Game game, int nodeId) {
        //
    }

    /**
     * Builds a road between two nodes.
     *
     * @param game game being played
     * @param fromNodeId start node id
     * @param toNodeId end node id
     */
    private void buildRoad(Game game, int fromNodeId, int toNodeId) {
        //
    }
}