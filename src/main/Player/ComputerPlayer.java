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

    private Random randomizer;

    public ComputerPlayer() {
        super();
        this.randomizer = new Random();
    }

    public ComputerPlayer(int seed) {
        super();
        this.randomizer = new Random(seed);
    }

    /**
     * Initiate the current player's turn.
     * Uses a while loop to execute multiple actions per turn if resources allow.
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

    private void placeSettlement(Node node, Settlement s) {
        node.setPlayer(this);
        node.setStructure(s);
        this.addStructure(s);
    }

    private void placeCity(Node node, City c) {
        if (node.getStructure() instanceof Settlement) {
            this.removeStructure(node.getStructure());
        }
        node.setStructure(c);
        this.addStructure(c);
    }

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

    private boolean isNeighborOccupied(Node n) {
        if (n.getLeft() != null && n.getLeft().getPlayer() != null) return true;
        if (n.getRight() != null && n.getRight().getPlayer() != null) return true;
        if (n.getVert() != null && n.getVert().getPlayer() != null) return true;
        return false;
    }

    private void payForRoad() {
        removeResource(ResourceType.BRICK);
        removeResource(ResourceType.LUMBER);
    }

    private void payForSettlement() {
        removeResource(ResourceType.BRICK);
        removeResource(ResourceType.LUMBER);
        removeResource(ResourceType.WOOL);
        removeResource(ResourceType.GRAIN);
    }

    private void payForCity() {
        removeResource(ResourceType.GRAIN);
        removeResource(ResourceType.GRAIN);
        removeResource(ResourceType.ORE);
        removeResource(ResourceType.ORE);
        removeResource(ResourceType.ORE);
    }

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

    @Override
    public Tile setRobber(List<Tile> tiles) {
        int randomIndex = randomizer.nextInt(tiles.size());
        return tiles.get(randomIndex);
    }
}