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
     * Continually evaluates all currently available actions and chooses
     * the one with the highest immediate value.
     *
     * @param game The current game context.
     */
    @Override
    public void takeTurn(Game game) {
        boolean madeMove = true;

        while (madeMove) {
            madeMove = false;

            AIMove bestMove;
            if (mustSpendCards()) {
                bestMove = mandatorySpendMove(game);
            } else if (connectNearbyRoad(game)) {
                bestMove = connectRoadMove(game);
            } else if (protectLongestRoad(game)) {
                bestMove = connectedRoadMove(game);
            } else {
                bestMove = bestMove(game);
            }

            if (bestMove != null) {
                System.out.println(bestMove.message);
                bestMove.action.run();
                madeMove = true;
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
     * Chooses the highest-valued available move for the current game state.
     * If multiple moves have the same best value, one is chosen randomly.
     *
     * @param game The current game context.
     * @return The chosen move, or null if no valid move exists.
     */
    private AIMove bestMove(Game game) {
        List<AIMove> moves = new ArrayList<>();
        List<Node> allNodes = game.getBoard().getNodes();

        addCityMoves(moves, allNodes);
        addSettlementMoves(moves, allNodes);
        addRoadMoves(moves, allNodes);

        if (moves.isEmpty()) {
            return null;
        }

        double bestValue = -1.0;
        for (AIMove move : moves) {
            if (move.value > bestValue) {
                bestValue = move.value;
            }
        }

        List<AIMove> bestMoves = new ArrayList<>();
        for (AIMove move : moves) {
            if (move.value == bestValue) {
                bestMoves.add(move);
            }
        }

        return bestMoves.get(randomizer.nextInt(bestMoves.size()));
    }

    /**
     * Returns true if the AI currently has more than 7 resource cards.
     *
     * @return true if the AI must spend cards first, false otherwise
     */
    private boolean mustSpendCards() {
        return getHand().getCount() > 7;
    }

    /**
     * Chooses a move when the AI is forced to spend cards first.
     * This considers the same available build actions, since all of them
     * reduce the number of cards in hand. (This may need to be chaned later)
     *
     * @param game The current game context.
     * @return The chosen move, or null if no valid spending move exists.
     */
    private AIMove mandatorySpendMove(Game game) {
        List<AIMove> moves = new ArrayList<>();
        List<Node> allNodes = game.getBoard().getNodes();

        addCityMoves(moves, allNodes);
        addSettlementMoves(moves, allNodes);
        addRoadMoves(moves, allNodes);

        if (moves.isEmpty()) {
            return null;
        }

        double bestValue = -1.0;
        for (AIMove move : moves) {
            if (move.value > bestValue) {
                bestValue = move.value;
            }
        }

        List<AIMove> bestMoves = new ArrayList<>();
        for (AIMove move : moves) {
            if (move.value == bestValue) {
                bestMoves.add(move);
            }
        }

        return bestMoves.get(randomizer.nextInt(bestMoves.size()));
    }

    /**
     * Checks whether the AI should prioritize extending its road network
     * because another player is within one road of its longest road.
     *
     * @param game The current game context.
     * @return true if the AI should force a connected road move, false otherwise.
     */
    private boolean protectLongestRoad(Game game) {
        if (getLongestRoad() < 5) {
            return false;
        }

        if (getResourceAmount(ResourceType.BRICK) < 1
                || getResourceAmount(ResourceType.LUMBER) < 1
                || getRoads().size() >= Road.getMax()) {
            return false;
        }

        int myLongestRoad = getLongestRoad();

        for (Player player : game.getPlayers()) {
            if (player != this && player.getLongestRoad() >= myLongestRoad - 1) {
                return true;
            }
        }

        return false;
    }

    /**
     * Chooses a connected road move when the AI must protect its longest road lead.
     *
     * @param game The current game context.
     * @return A connected road move, or null if none exists.
     */
    private AIMove connectedRoadMove(Game game) {
        List<AIMove> roadMoves = new ArrayList<>();
        List<Node> allNodes = game.getBoard().getNodes();

        addRoadMoves(roadMoves, allNodes);

        if (roadMoves.isEmpty()) {
            return null;
        }

        return roadMoves.get(randomizer.nextInt(roadMoves.size()));
    }

    /**
     * Adds all currently valid city-upgrade moves.
     *
     * @param moves The list of candidate moves.
     * @param allNodes All nodes on the board.
     */
    private void addCityMoves(List<AIMove> moves, List<Node> allNodes) {
        if (getResourceAmount(ResourceType.GRAIN) < 2
                || getResourceAmount(ResourceType.ORE) < 3
                || getCities().size() >= City.getMax()) {
            return;
        }

        for (Node node : allNodes) {
            if (node.getPlayer() == this && node.getStructure() instanceof Settlement) {
                Node target = node;
                moves.add(new AIMove(
                        1.0,
                        this + " upgrading to City at Node " + target.getId(),
                        () -> {
                            payForCity();
                            placeCity(target, new City());
                        }
                ));
            }
        }
    }

    /**
     * Adds all currently valid settlement-building moves.
     *
     * @param moves The list of candidate moves.
     * @param allNodes All nodes on the board.
     */
    private void addSettlementMoves(List<AIMove> moves, List<Node> allNodes) {
        if (getResourceAmount(ResourceType.BRICK) < 1
                || getResourceAmount(ResourceType.LUMBER) < 1
                || getResourceAmount(ResourceType.WOOL) < 1
                || getResourceAmount(ResourceType.GRAIN) < 1
                || getSettlements().size() >= Settlement.getMax()) {
            return;
        }

        for (Node node : allNodes) {
            if (node.canBuildSettlement(this)) {
                Node target = node;
                moves.add(new AIMove(
                        1.0,
                        this + " building Settlement at Node " + target.getId(),
                        () -> {
                            payForSettlement();
                            placeSettlement(target, new Settlement());
                        }
                ));
            }
        }
    }

    /**
     * Adds all currently valid road-building moves.
     *
     * @param moves The list of candidate moves.
     * @param allNodes All nodes on the board.
     */
    private void addRoadMoves(List<AIMove> moves, List<Node> allNodes) {
        if (getResourceAmount(ResourceType.BRICK) < 1
                || getResourceAmount(ResourceType.LUMBER) < 1
                || getRoads().size() >= Road.getMax()) {
            return;
        }

        for (Node startNode : allNodes) {
            List<Node> validTargets = startNode.getBuildableRoadNeighbors(this);
            for (Node endNode : validTargets) {
                Node start = startNode;
                Node end = endNode;

                moves.add(new AIMove(
                        0.8,
                        this + " building Road from Node " + start.getId() + " to " + end.getId(),
                        () -> {
                            payForRoad();
                            placeRoad(start, end, new Road());
                        }
                ));
            }
        }
    }

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

    /**
     * Represents a possible AI move together with its immediate value.
     */
    private static class AIMove {
        private final double value;
        private final String message;
        private final Runnable action;

        private AIMove(double value, String message, Runnable action) {
            this.value = value;
            this.message = message;
            this.action = action;
        }
    }
}