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
 * Represents a computer-controlled player that uses rule-based machine intelligence.
 * <p>
 * This AI evaluates the actions currently available to it, assigns each one an
 * immediate value, and chooses the highest-valued move, using randomness to break ties.
 * It also spends cards when its hand is too large and prioritizes road-building situations
 * that protect or improve its road network.
 *
 * @author Parnia Yazdinia, McMaster University
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

            List<Node> neighbors = getNeighbors(chosenNode);
            if (!neighbors.isEmpty()) {
                Node roadTarget = neighbors.get(randomizer.nextInt(neighbors.size()));
                placeRoad(chosenNode, roadTarget, new Road());
            }
        }
    }

    // =========================================================================
    // AI MOVE SELECTION
    // =========================================================================

    /**
     * Helper to extract the highest valued move from a list of candidate moves,
     * breaking any ties randomly to ensure dynamic AI behavior.
     * * @param moves The list of evaluated AIMoves.
     * @return The chosen optimal AIMove, or null if the list is empty.
     */
    private AIMove pickBestMove(List<AIMove> moves) {
        if (moves.isEmpty()) return null;

        double bestValue = -1.0;
        for (AIMove move : moves) {
            if (move.value > bestValue) bestValue = move.value;
        }

        List<AIMove> bestMoves = new ArrayList<>();
        for (AIMove move : moves) {
            if (move.value == bestValue) bestMoves.add(move);
        }

        return bestMoves.get(randomizer.nextInt(bestMoves.size()));
    }

    /**
     * Chooses the highest-valued available move for the current game state.
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

        return pickBestMove(moves);
    }

    /**
     * Chooses a move when the AI is forced to spend cards first.
     *
     * @param game The current game context.
     * @return The chosen move, or null if no valid spending move exists.
     */
    private AIMove mandatorySpendMove(Game game) {
        return bestMove(game);
    }

    /**
     * Chooses a connected road move when the AI must protect its longest road lead.
     *
     * @param game The current game context.
     * @return A connected road move, or null if none exists.
     */
    private AIMove connectedRoadMove(Game game) {
        List<AIMove> roadMoves = new ArrayList<>();
        addRoadMoves(roadMoves, game.getBoard().getNodes());
        return pickBestMove(roadMoves);
    }

    /**
     * Chooses a road move that helps connect two nearby road segments.
     *
     * @param game The current game context.
     * @return A road-connecting move, or null if none exists.
     */
    private AIMove connectRoadMove(Game game) {
        List<AIMove> connectingMoves = new ArrayList<>();
        List<Node> allNodes = game.getBoard().getNodes();

        for (Node startNode : allNodes) {
            List<Node> validTargets = startNode.getBuildableRoadNeighbors(this);
            for (Node endNode : validTargets) {
                if (connectRoadCandidate(startNode, endNode)) {

                    PlayerCommand buildRoadCommand = new BuildRoadCommand(this, startNode, endNode);
                    MoveEvaluator evaluator = new BaseMoveEvaluator(buildRoadCommand);
                    evaluator = new BuilderDecorator(evaluator);
                    evaluator = new HandDepleterDecorator(evaluator, this, 2);

                    connectingMoves.add(new AIMove(
                            evaluator.evaluateValue(),
                            buildRoadCommand.getExecuteMessage() + " (Connecting)",
                            buildRoadCommand::execute
                    ));
                }
            }
        }

        return pickBestMove(connectingMoves);
    }

    // =========================================================================
    // HELPER METHODS
    // =========================================================================

    /**
     * Returns true if the AI currently has more than 7 resource cards.
     *
     * @return true if the AI must spend cards first, false otherwise.
     */
    private boolean mustSpendCards() {
        return getHand().getCount() > 7;
    }

    /**
     * Checks whether the AI should prioritize extending its road network
     * because another player is within one road of its longest road.
     *
     * @param game The current game context.
     * @return true if the AI should force a connected road move, false otherwise.
     */
    private boolean protectLongestRoad(Game game) {
        if (getLongestRoad() < 5) return false;
        if (getResourceAmount(ResourceType.BRICK) < 1 || getResourceAmount(ResourceType.LUMBER) < 1 || getRoads().size() >= Road.getMax()) return false;

        int myLongestRoad = getLongestRoad();
        for (Player player : game.getPlayers()) {
            if (player != this && player.getLongestRoad() >= myLongestRoad - 1) return true;
        }
        return false;
    }

    /**
     * Checks whether there is a road move that helps connect two of this player's
     * road segments that are at most two units apart.
     *
     * @param game The current game context.
     * @return true if such a move exists, false otherwise.
     */
    private boolean connectNearbyRoad(Game game) {
        if (getResourceAmount(ResourceType.BRICK) < 1 || getResourceAmount(ResourceType.LUMBER) < 1 || getRoads().size() >= Road.getMax()) return false;

        for (Node startNode : game.getBoard().getNodes()) {
            for (Node endNode : startNode.getBuildableRoadNeighbors(this)) {
                if (connectRoadCandidate(startNode, endNode)) return true;
            }
        }
        return false;
    }

    /**
     * Returns true if building a road from start to end helps connect this player's
     * road network to another owned road segment within at most two units.
     *
     * @param start Starting node of the candidate road.
     * @param end Ending node of the candidate road.
     * @return true if this road is a good connecting move.
     */
    private boolean connectRoadCandidate(Node start, Node end) {
        if (hasOtherOwnedRoad(end, start)) return true;
        for (Node next : getNeighbors(end)) {
            if (next == start) continue;
            if (getRoadBetween(end, next) == null && hasOtherOwnedRoad(next, end)) return true;
        }
        return false;
    }

    /**
     * Returns true if the given node touches one of this player's roads,
     * excluding the edge that leads to the excluded neighbor.
     *
     * @param node The node being checked.
     * @param excludedNeighbor The neighbor edge to ignore.
     * @return true if another owned road is present.
     */
    private boolean hasOtherOwnedRoad(Node node, Node excludedNeighbor) {
        for (Node neighbor : getNeighbors(node)) {
            if (neighbor == excludedNeighbor) continue;
            Road road = getRoadBetween(node, neighbor);
            if (road != null && road.getOwner() == this) return true;
        }
        return false;
    }

    /**
     * Returns all non-null neighboring nodes of the given node.
     *
     * @param node The node whose neighbors are needed.
     * @return List of neighboring nodes.
     */
    private List<Node> getNeighbors(Node node) {
        List<Node> neighbors = new ArrayList<>();
        if (node.getLeft() != null) neighbors.add(node.getLeft());
        if (node.getRight() != null) neighbors.add(node.getRight());
        if (node.getVert() != null) neighbors.add(node.getVert());
        return neighbors;
    }

    /**
     * Returns the road on the edge between two adjacent nodes, if one exists.
     *
     * @param a First node.
     * @param b Second node.
     * @return The road on that edge, or null if none exists.
     */
    private Road getRoadBetween(Node a, Node b) {
        if (a.getLeft() == b) return a.getLeftRoad();
        if (a.getRight() == b) return a.getRightRoad();
        if (a.getVert() == b) return a.getVertRoad();
        return null;
    }

    /**
     * Adds all currently valid city-upgrade moves dynamically calculating R3.2 values.
     *
     * @param moves The list of candidate moves.
     * @param allNodes All nodes on the board.
     */
    private void addCityMoves(List<AIMove> moves, List<Node> allNodes) {
        if (getResourceAmount(ResourceType.GRAIN) < 2 || getResourceAmount(ResourceType.ORE) < 3 || getCities().size() >= City.getMax()) return;

        for (Node node : allNodes) {
            if (node.getPlayer() == this && node.getStructure() instanceof Settlement) {
                PlayerCommand buildCityCommand = new BuildCityCommand(this, node);
                MoveEvaluator evaluator = new BaseMoveEvaluator(buildCityCommand);
                evaluator = new VPEarnerDecorator(evaluator);
                evaluator = new HandDepleterDecorator(evaluator, this, 5);
                moves.add(new AIMove(evaluator.evaluateValue(), buildCityCommand.getExecuteMessage(), buildCityCommand::execute));
            }
        }
    }

    /**
     * Adds all currently valid settlement-building moves dynamically calculating R3.2 values.
     *
     * @param moves The list of candidate moves.
     * @param allNodes All nodes on the board.
     */
    private void addSettlementMoves(List<AIMove> moves, List<Node> allNodes) {
        if (getResourceAmount(ResourceType.BRICK) < 1 || getResourceAmount(ResourceType.LUMBER) < 1
                || getResourceAmount(ResourceType.WOOL) < 1 || getResourceAmount(ResourceType.GRAIN) < 1
                || getSettlements().size() >= Settlement.getMax()) return;

        for (Node node : allNodes) {
            if (node.canBuildSettlement(this)) {
                PlayerCommand buildSettlementCommand = new BuildSettlementCommand(this, node);
                MoveEvaluator evaluator = new BaseMoveEvaluator(buildSettlementCommand);
                evaluator = new VPEarnerDecorator(evaluator);
                evaluator = new HandDepleterDecorator(evaluator, this, 4);
                moves.add(new AIMove(evaluator.evaluateValue(), buildSettlementCommand.getExecuteMessage(), buildSettlementCommand::execute));
            }
        }
    }

    /**
     * Adds all currently valid road-building moves dynamically calculating R3.2 values.
     *
     * @param moves The list of candidate moves.
     * @param allNodes All nodes on the board.
     */
    private void addRoadMoves(List<AIMove> moves, List<Node> allNodes) {
        if (getResourceAmount(ResourceType.BRICK) < 1 || getResourceAmount(ResourceType.LUMBER) < 1 || getRoads().size() >= Road.getMax()) return;

        for (Node startNode : allNodes) {
            for (Node endNode : startNode.getBuildableRoadNeighbors(this)) {
                PlayerCommand buildRoadCommand = new BuildRoadCommand(this, startNode, endNode);
                MoveEvaluator evaluator = new BaseMoveEvaluator(buildRoadCommand);
                evaluator = new BuilderDecorator(evaluator);
                evaluator = new HandDepleterDecorator(evaluator, this, 2);
                moves.add(new AIMove(evaluator.evaluateValue(), buildRoadCommand.getExecuteMessage(), buildRoadCommand::execute));
            }
        }
    }

    /**
     * Places a settlement on a node and updates player and node states.
     * @param node The node to build on.
     * @param s The settlement object to place.
     */
    private void placeSettlement(Node node, Settlement s) {
        node.setStructure(s);
        this.addStructure(s);
    }

    /**
     * Upgrades a settlement to a city on the board and in the player's inventory.
     * @param node The node where the city is being built.
     * @param c The city object to place.
     */
    private void placeCity(Node node, City c) {
        if (node.getStructure() instanceof Settlement) this.removeStructure(node.getStructure());
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
            start.setLeftRoad(r); end.setRightRoad(r);
        } else if (start.getRight() == end) {
            start.setRightRoad(r); end.setLeftRoad(r);
        } else if (start.getVert() == end) {
            start.setVertRoad(r); end.setVertRoad(r);
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
        return n.getVert() != null && n.getVert().getPlayer() != null;
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
            if (types[randomIndex] != ResourceType.DESERT && getHand().getCount(types[randomIndex]) > 0) {
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
        return tiles.get(randomizer.nextInt(tiles.size()));
    }

    /**
     * Represents a possible AI move together with its immediate value.
     */
    private static class AIMove {
        private final double value;
        private final String message;
        private final Runnable action;

        /**
         * Constructs a new AIMove.
         * * @param value The calculated value/priority of this move.
         * @param message The message to print if this move is executed.
         * @param action The logic to run if this move is chosen.
         */
        private AIMove(double value, String message, Runnable action) {
            this.value = value;
            this.message = message;
            this.action = action;
        }
    }
}