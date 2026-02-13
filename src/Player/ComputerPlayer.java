package Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import Board.Node;
import Game.Game;
import GameResources.City;
import GameResources.ResourceType;
import GameResources.Road;
import GameResources.Settlement;

/************************************************************/
/**
 * Represents a computer simulation of a player (AI Agent).
 * <p>
 * This AI uses a "Random Valid Move" strategy:
 * 1. It calculates all moves it can legally make based on resources and board state.
 * 2. It collects these moves into a list.
 * 3. It executes one of them at random.
 * * @author Yojith Sai Biradavolu, McMaster University
 * @version Winter, 2026
 */
public class ComputerPlayer extends Player {

    /** Randomizer used to select moves non-deterministically **/
    private Random randomizer;

    /**
     * Constructor for a computer player
     */
    public ComputerPlayer() {
        super();
        this.randomizer = new Random();
    }

    /**
     * Constructor for a computer player with a seed for the randomizer
     * (Useful for testing to ensure reproducible behavior)
     *
     * @param seed Seed for the randomizer
     */
    public ComputerPlayer(int seed) {
        super();
        this.randomizer = new Random(seed);
    }

    /**
     * Initiate the current player's turn.
     * <p>
     * The strategy is:
     * 1. Check current resources to see what types of structures are affordable.
     * 2. Scan the entire board to find every legal spot to build those structures.
     * 3. Add every legal move (as a Runnable action) to a list.
     * 4. Randomly execute one move from the list.
     *
     * @param game The current game instance
     */
    @Override
    public void takeTurn(Game game) {
        //
        // We store actions as Runnable objects so we can execute them later
        List<Runnable> potentialMoves = new ArrayList<>();
        List<Node> allNodes = game.getBoard().getNodes();

        // --- STEP 1: RESOURCE CHECKS ---
        // Determine what the AI can afford this turn
        boolean canAffordRoad = getNumResource(ResourceType.BRICK) >= 1 && getNumResource(ResourceType.LUMBER) >= 1;
        boolean canAffordSettlement = getNumResource(ResourceType.BRICK) >= 1 && getNumResource(ResourceType.LUMBER) >= 1
                && getNumResource(ResourceType.WOOL) >= 1 && getNumResource(ResourceType.GRAIN) >= 1;
        boolean canAffordCity = getNumResource(ResourceType.GRAIN) >= 2 && getNumResource(ResourceType.ORE) >= 3;

        // --- STEP 2: SCAN FOR VALID MOVES ---

        // A. FIND VALID ROADS
        if (canAffordRoad) {
            for (Node startNode : allNodes) {
                // getBuildableRoadNeighbors checks:
                // 1. Do we have a connection to startNode?
                // 2. Is the path blocked by an enemy?
                // 3. Is the edge empty?
                List<Node> validTargets = startNode.getBuildableRoadNeighbors(this);

                for (Node endNode : validTargets) {
                    // Create a "Move" action and store it
                    potentialMoves.add(() -> {
                        System.out.println(this + " building Road from Node " + startNode + " to " + endNode);
                        payForRoad();
                        placeRoad(startNode, endNode, new Road());
                    });
                }
            }
        }

        // B. FIND VALID SETTLEMENTS
        if (canAffordSettlement) {
            for (Node node : allNodes) {
                // canBuildSettlement checks:
                // 1. Is node empty?
                // 2. Distance Rule (no neighbors have buildings)
                // 3. Connection Rule (we have a road leading here)
                if (node.canBuildSettlement(this)) {
                    potentialMoves.add(() -> {
                        System.out.println(this + " building Settlement at Node " + node);
                        payForSettlement();
                        placeSettlement(node, new Settlement());
                    });
                }
            }
        }

        // C. FIND VALID CITIES
        if (canAffordCity) {
            for (Node node : allNodes) {
                // Rule: Can only build a City by upgrading an existing Settlement we own
                if (node.getPlayer() == this && node.getStructure() instanceof Settlement) {
                    potentialMoves.add(() -> {
                        System.out.println(this + " upgrading to City at Node " + node);
                        payForCity();
                        placeCity(node, new City());
                    });
                }
            }
        }

        // --- STEP 3: EXECUTE RANDOM MOVE ---
        if (!potentialMoves.isEmpty()) {
            // Pick a random index from the list of legal moves
            Runnable move = potentialMoves.get(randomizer.nextInt(potentialMoves.size()));
            move.run(); // Execute the logic inside the lambda above
        } else {
            System.out.println(this + " does nothing this turn.");
        }
    }

    /**
     * Setup of the player's structures at the beginning of the game.
     * <p>
     * Logic:
     * 1. Randomly pick nodes until we find one that satisfies the Distance Rule.
     * 2. Place a Settlement there.
     * 3. Pick a random valid neighbor and place a Road.
     *
     * @param game The current game
     */
    @Override
    public void setup(Game game) {
        List<Node> allNodes = game.getBoard().getNodes();
        Node chosenNode = null;
        int attempts = 0;

        // 1. Find a valid settlement spot
        // Note: We cannot use node.canBuildSettlement() here because that method checks
        // for road connections, which don't exist yet in the setup phase.
        // We strictly check the Distance Rule manually.
        while (chosenNode == null && attempts < 500) {
            Node candidate = allNodes.get(randomizer.nextInt(allNodes.size()));

            // Check if node is empty AND passes distance rule (no occupied neighbors)
            if (candidate.getPlayer() == null && !isNeighborOccupied(candidate)) {
                chosenNode = candidate;
            }
            attempts++;
        }

        if (chosenNode == null) {
            System.err.println("Computer could not find a valid setup spot!");
            return;
        }

        // 2. Place Settlement
        placeSettlement(chosenNode, new Settlement());

        // 3. Place Road connecting to it
        // Check which directions actually exist on the grid (handle board edges)
        List<Node> neighbors = new ArrayList<>();
        if (chosenNode.getLeft() != null) neighbors.add(chosenNode.getLeft());
        if (chosenNode.getRight() != null) neighbors.add(chosenNode.getRight());
        if (chosenNode.getVert() != null) neighbors.add(chosenNode.getVert());

        // Randomly pick one available direction for the initial road
        if (!neighbors.isEmpty()) {
            Node roadTarget = neighbors.get(randomizer.nextInt(neighbors.size()));
            placeRoad(chosenNode, roadTarget, new Road());
        }
    }

    // =========================================================================
    // HELPER METHODS
    // =========================================================================

    /**
     * Helper to update the Board State and Player Inventory when building a Settlement.
     */
    private void placeSettlement(Node node, Settlement s) {
        node.setPlayer(this);       // Mark board node as owned
        node.setStructure(s);       // Place structure on board
        this.addStructure(s);       // Add to player inventory
        try {
            this.addNode(node);     // Add to player's list of owned nodes
        } catch (NullPointerException e) {
            // Safety catch: Your Player class might not have initialized 'nodes' list
            System.err.println("Warning: Player nodes list not initialized.");
        }
    }

    /**
     * Helper to update the Board State and Player Inventory when upgrading to a City.
     */
    private void placeCity(Node node, City c) {
        node.setStructure(c);
        this.addStructure(c);
    }

    /**
     * Helper to place a road on the edge between two nodes.
     * * IMPORTANT: This method attempts to set the road on both the 'start' node
     * and the 'end' node to ensure the connection is bidirectional.
     * *
     */
    private void placeRoad(Node start, Node end, Road r) {

        // We must set the road on BOTH nodes involved in the edge
        if (start.getLeft() == end) {
            start.setLeftRoad(r);
            end.setRightRoad(r); // Left's inverse is Right
        } else if (start.getRight() == end) {
            start.setRightRoad(r);
            end.setLeftRoad(r); // Right's inverse is Left
        } else if (start.getVert() == end) {
            start.setVertRoad(r);
            end.setVertRoad(r); // Vert's inverse is Vert
        }

        this.addRoad(r);
        this.addNode(end);
    }

    /**
     * Manual Distance Rule check for Setup Phase.
     * Returns true if any adjacent node has a player on it.
     */
    private boolean isNeighborOccupied(Node n) {
        if (n.getLeft() != null && n.getLeft().getPlayer() != null) return true;
        if (n.getRight() != null && n.getRight().getPlayer() != null) return true;
        if (n.getVert() != null && n.getVert().getPlayer() != null) return true;
        return false;
    }

    // --- Payment Helpers ---
    // These remove the required resources from the player's hand.

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
}