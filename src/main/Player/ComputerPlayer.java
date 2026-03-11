package Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import Board.Node;
import Board.Tile;
import Game.Game;
import GameResources.City;
import GameResources.ResourceType;
import GameResources.Road;
import GameResources.Settlement;

/************************************************************/
/**
 * Represents a computer simulation of a player (AI Agent).
 * <p>
 * This AI uses a "Priority Action Loop" strategy:
 * 1. It attempts to build the most valuable structure it can afford (City > Settlement > Road).
 * 2. It executes that move immediately.
 * 3. It loops and repeats this process until it runs out of resources or valid moves.
 * * @author Yojith Sai Biradavolu, McMaster University
 * @version Winter, 2026
 */
public class ComputerPlayer extends Player {

    /** Random number generator for decision-making and move selection. */
    private Random randomizer;

    /**
     * Default constructor. Initializes the computer player with a default Random instance.
     */
    public ComputerPlayer() {
        super();
        this.randomizer = new Random();
    }

    /**
     * Seeded constructor for deterministic behavior during testing.
     * @param seed The seed for the random number generator.
     */
    public ComputerPlayer(int seed) {
        super();
        this.randomizer = new Random(seed);
    }

    /**
     * Logic for the computer player's turn.
     * Continually evaluates and executes building priorities as long as moves are possible.
     * @param game The current game context.
     */
    @Override
    public void takeTurn(Game game) {
        boolean madeMove = true;

        // Loop allows the AI to make multiple purchases per turn (preventing Robber hoarding)
        while (madeMove) {
            madeMove = false;
            List<Node> allNodes = game.getBoard().getNodes();

            // --- PRIORITY 1: CITIES (Highest VP, Best Resource Gen) ---
            if (getResourceAmount(ResourceType.GRAIN) >= 2 && getResourceAmount(ResourceType.ORE) >= 3 && getCities().size() < City.getMax()) {
                List<Node> validCityNodes = new ArrayList<>();
                for (Node node : allNodes) {
                    if (node.getPlayer() == this && node.getStructure() instanceof Settlement) {
                        validCityNodes.add(node);
                    }
                }
                if (!validCityNodes.isEmpty()) {
                    Node target = validCityNodes.get(randomizer.nextInt(validCityNodes.size()));
                    System.out.println(this + " upgrading to City at Node " + target.getId());
                    payForCity();
                    placeCity(target, new City());
                    madeMove = true;
                    continue; // Loop again from the top!
                }
            }

            // --- PRIORITY 2: SETTLEMENTS (VP + New Resource Gen) ---
            if (getResourceAmount(ResourceType.BRICK) >= 1 && getResourceAmount(ResourceType.LUMBER) >= 1
                    && getResourceAmount(ResourceType.WOOL) >= 1 && getResourceAmount(ResourceType.GRAIN) >= 1
                    && getSettlements().size() < Settlement.getMax()) {

                List<Node> validSettlementNodes = new ArrayList<>();
                for (Node node : allNodes) {
                    if (node.canBuildSettlement(this)) {
                        validSettlementNodes.add(node);
                    }
                }
                if (!validSettlementNodes.isEmpty()) {
                    Node target = validSettlementNodes.get(randomizer.nextInt(validSettlementNodes.size()));
                    System.out.println(this + " building Settlement at Node " + target.getId());
                    payForSettlement();
                    placeSettlement(target, new Settlement());
                    madeMove = true;
                    continue;
                }
            }

            // --- PRIORITY 3: ROADS (Expansion) ---
            if (getResourceAmount(ResourceType.BRICK) >= 1 && getResourceAmount(ResourceType.LUMBER) >= 1 && getRoads().size() < Road.getMax()) {
                List<Runnable> validRoadMoves = new ArrayList<>();

                for (Node startNode : allNodes) {
                    List<Node> validTargets = startNode.getBuildableRoadNeighbors(this);
                    for (Node endNode : validTargets) {
                        validRoadMoves.add(() -> {
                            System.out.println(this + " building Road from Node " + startNode.getId() + " to " + endNode.getId());
                            payForRoad();
                            placeRoad(startNode, endNode, new Road());
                        });
                    }
                }
                if (!validRoadMoves.isEmpty()) {
                    Runnable move = validRoadMoves.get(randomizer.nextInt(validRoadMoves.size()));
                    move.run();
                    madeMove = true;
                }
            }
        }
        System.out.println(this + " ends their turn.");
    }

    /**
     * Handles initial board setup for the AI.
     * Places two settlements and two roads according to standard game rules.
     * @param game The current game instance.
     */
    @Override
    public void setup(Game game) {
        List<Node> allNodes = game.getBoard().getNodes();

        for (int i = 0; i < 2; i++) {
            Node chosenNode = null;
            int attempts = 0;

            while (chosenNode == null && attempts < 500) {
                Node candidate = allNodes.get(randomizer.nextInt(allNodes.size()));

                if (candidate.getPlayer() == null && !isNeighborOccupied(candidate)) {
                    chosenNode = candidate;
                }
                attempts++;
            }

            if (chosenNode == null) {
                System.err.println(this + " could not find a valid setup spot!");
                continue;
            }

            placeSettlement(chosenNode, new Settlement());

            List<Node> neighbors = new ArrayList<>();
            if (chosenNode.getLeft() != null) neighbors.add(chosenNode.getLeft());
            if (chosenNode.getRight() != null) neighbors.add(chosenNode.getRight());
            if (chosenNode.getVert() != null) neighbors.add(chosenNode.getVert());

            if (!neighbors.isEmpty()) {
                Node roadTarget = neighbors.get(randomizer.nextInt(neighbors.size()));
                placeRoad(chosenNode, roadTarget, new Road());
            }
        }
    }

    // =========================================================================
    // HELPER METHODS
    // =========================================================================

    /**
     * Places a settlement on a node and updates player and node states.
     * @param node The node to build on.
     * @param s The settlement object to place.
     */
    private void placeSettlement(Node node, Settlement s) {
        node.setStructure(s);       // Place structure on board
        this.addStructure(s);       // Add to player inventory
    }

    /**
     * Upgrades a settlement to a city on the board and in the player's inventory.
     * @param node The node where the city is being built.
     * @param c The city object to place.
     */
    private void placeCity(Node node, City c) {
        if (node.getStructure() instanceof Settlement) {
            this.removeStructure(node.getStructure());
        }
        node.setStructure(c);
        this.addStructure(c);
    }

    /**
     * Establishes a road between two nodes.
     * Handles bidirectional edge assignment.
     * @param start The starting node of the road.
     * @param end The ending node of the road.
     * @param r The road object.
     */
    private void placeRoad(Node start, Node end, Road r) {
        if (start.getLeft() == end) {
            start.setLeftRoad(r);
            end.setRightRoad(r);
        } else if (start.getRight() == end) {
            start.setRightRoad(r);
            end.setLeftRoad(r);
        } else if (start.getVert() == end) {
            start.setVertRoad(r);
            end.setVertRoad(r);
        }
        this.addRoad(r);
    }

    /**
     * Checks if any neighbors of a node are occupied by a player.
     * Helper for distance rule validation.
     * @param n The node to check.
     * @return True if a neighbor is occupied, false otherwise.
     */
    private boolean isNeighborOccupied(Node n) {
        if (n.getLeft() != null && n.getLeft().getPlayer() != null) return true;
        if (n.getRight() != null && n.getRight().getPlayer() != null) return true;
        if (n.getVert() != null && n.getVert().getPlayer() != null) return true;
        return false;
    }

    /** Deducts resources for a road construction. */
    private void payForRoad() {
        removeResource(ResourceType.BRICK);
        removeResource(ResourceType.LUMBER);
    }

    /** Deducts resources for a settlement construction. */
    private void payForSettlement() {
        removeResource(ResourceType.BRICK);
        removeResource(ResourceType.LUMBER);
        removeResource(ResourceType.WOOL);
        removeResource(ResourceType.GRAIN);
    }

    /** Deducts resources for a city upgrade. */
    private void payForCity() {
        removeResource(ResourceType.GRAIN);
        removeResource(ResourceType.GRAIN);
        removeResource(ResourceType.ORE);
        removeResource(ResourceType.ORE);
        removeResource(ResourceType.ORE);
    }

    /**
     * Implementation of the robber discard rule.
     * Randomly removes cards until the player's hand count is 7 or fewer.
     */
    @Override
    public void robberDiscard() {
        while (getHand().getCount() > 7) {
            ResourceType[] types = ResourceType.values();
            int randomIndex = randomizer.nextInt(types.length);
            if (types[randomIndex] != ResourceType.DESERT && getHand().getCount(types[randomIndex]) > 0 ) {
                getHand().removeCard(types[randomIndex], 1);
            }
        }
    }

    /**
     * Chooses a random tile to place the robber on.
     * @param tiles List of available tiles on the board.
     * @return The tile selected by the AI.
     */
    @Override
    public Tile setRobber(List<Tile> tiles) {
        int randomIndex = randomizer.nextInt(tiles.size());
        return tiles.get(randomIndex);
    }
}