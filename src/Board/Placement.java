package Board;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import GameResources.City;
import GameResources.Road;
import GameResources.Settlement;
import GameResources.Structure;
import Player.Player;

/**
 * Handles legal placement of structures/roads on the board.
 * Update the structure and roads on the map allowing for initial setup and building.
 *
 * @author Elizabeth Glozman, 400559660, McMaster University
 */
public class Placement {

    /** Game board for placement */
    private final Board board;

    /** Random Number for random placement */
    private final Random rng;

    /**
     * Constructs placement manager for specific board
     *
     * @param board - Game board where structures are placed
     * @param seed  - Random placement seed
     */
    public Placement(Board board, long seed) {
        this.board = board;
        this.rng = new Random(seed);
    }

    /**
     * Places initial settlement to follow distance rule
     * Settlement is assigned to player
     *
     * @param player - player receiving the settlement
     * @return - node where settlement is placed
     */
    public Node placeInitialSettlement(Player player) {
        Node node = pickRandomLegalSettlementNode();
        Settlement s = new Settlement();

        placeStructure(node, s, player);
        player.addStructure(s);

        return node;
    }

    /**
     * Place initial road extending from given node
     * Randomly selected neighbouring node with an open edge
     *
     * @param player - Player receiving road
     * @param from   - Starting node of the road
     */
    public void placeInitialRoad(Player player, Node from) {
        Node to = pickRandomNeighborWithOpenEdge(from);
        Road r = new Road();

        placeRoad(from, to, r, player);
        player.addRoad(r);
    }

    /**
     * Attempt to build a road during gameplay.
     * Picks a random start node that has at least one buildable neighbor,
     * then places a road to a random valid target.
     *
     * @param player - Player building the road
     * @return true if a road was built, false otherwise
     */
    public boolean tryBuildRoad(Player player) {
        List<Node> starts = new ArrayList<>();

        for (Node n : board.getNodes()) {
            if (!n.getBuildableRoadNeighbors(player).isEmpty()) {
                starts.add(n);
            }
        }

        if (starts.isEmpty())
            return false;

        Node from = starts.get(rng.nextInt(starts.size()));
        List<Node> targets = from.getBuildableRoadNeighbors(player);
        Node to = targets.get(rng.nextInt(targets.size()));

        Road r = new Road();
        placeRoad(from, to, r, player);
        player.addRoad(r);
        return true;
    }

    /**
     * Attempts to build a new Settlement for the given player during gameplay.
     *
     * @param player Player attempting to build
     * @return true if a settlement was built, otherwise false
     */
    public boolean tryBuildSettlement(Player player) {
        List<Node> candidates = new ArrayList<>();
        for (Node n : board.getNodes()) {
            if (n.canBuildSettlement(player)) {
                candidates.add(n);
            }
        }
        if (candidates.isEmpty())
            return false;

        Node node = candidates.get(rng.nextInt(candidates.size()));
        Settlement s = new Settlement();
        placeStructure(node, s, player);
        player.addStructure(s);
        return true;
    }

    /**
     * Attempts to upgrade one of the player's settlements into a city.
     *
     * @param player Player attempting to upgrade
     * @return true if a city upgrade happened, otherwise false
     */
    public boolean tryBuildCity(Player player) {
        List<Node> candidates = new ArrayList<>();
        for (Node n : board.getNodes()) {
            if (n.canUpgradeToCity(player)) {
                candidates.add(n);
            }
        }
        if (candidates.isEmpty())
            return false;

        Node node = candidates.get(rng.nextInt(candidates.size()));
        Structure old = node.getStructure();
        City city = new City();
        node.upgradeToCity(player, city);
        player.removeStructure(old);
        player.addStructure(city);

        return true;
    }

    /**
     * Select random node that is permitted in placement rules
     *
     * @return - legal node for placement
     * @throws IllegalStateException - no legal nodes available
     */
    private Node pickRandomLegalSettlementNode() {
        List<Node> candidates = new ArrayList<>();
        for (Node n : board.getNodes()) {
            if (isLegalSettlementNode(n))
                candidates.add(n);
        }
        if (candidates.isEmpty()) {
            throw new IllegalStateException("No legal nodes available for settlement placement.");
        }
        return candidates.get(rng.nextInt(candidates.size()));
    }

    /**
     * Checks for node to satisfy settlement distance rule
     * Legal node must be empty and have no adjacent structures
     *
     * @param n - node to check
     * @return - true if node is legal
     */
    private boolean isLegalSettlementNode(Node n) {
        if (n.getStructure() != null)
            return false;

        Node l = n.getLeft();
        Node r = n.getRight();
        Node v = n.getVert();

        return (l == null || l.getStructure() == null)
                && (r == null || r.getStructure() == null)
                && (v == null || v.getStructure() == null);
    }

    /**
     * Select random neighbouring node connected by an open edge
     *
     * @param from - starting node
     * @return - neighbouring node with available edge
     * @throws IllegalStateException if no open edges exist
     */
    private Node pickRandomNeighborWithOpenEdge(Node from) {
        List<Node> options = new ArrayList<>();

        if (from.getLeft() != null && from.getLeftRoad() == null)
            options.add(from.getLeft());
        if (from.getRight() != null && from.getRightRoad() == null)
            options.add(from.getRight());
        if (from.getVert() != null && from.getVertRoad() == null)
            options.add(from.getVert());

        if (options.isEmpty()) {
            throw new IllegalStateException("No open edges for road placement from node " + from);
        }
        return options.get(rng.nextInt(options.size()));
    }

    /**
     * Select random neighbour node connected by an open edge.
     * Returns null if none exist (useful during gameplay to avoid crashing).
     *
     * @param from - starting node
     * @return - neighbouring node or null if none
     */
    private Node pickNeighborWithOpenEdgeOrNull(Node from) {
        List<Node> options = new ArrayList<>();

        if (from.getLeft() != null && from.getLeftRoad() == null)
            options.add(from.getLeft());
        if (from.getRight() != null && from.getRightRoad() == null)
            options.add(from.getRight());
        if (from.getVert() != null && from.getVertRoad() == null)
            options.add(from.getVert());

        if (options.isEmpty())
            return null;
        return options.get(rng.nextInt(options.size()));
    }

    /**
     * Places structure on a node and assigns ownership
     *
     * @param node      - node where structure is placed
     * @param structure - structure being placed
     * @param owner     - owning player
     */
    private void placeStructure(Node node, Structure structure, Player owner) {
        structure.setOwner(owner);
        node.setStructure(structure);
        node.setPlayer(owner);
    }

    /**
     * Place road between two neighbouring nodes
     * road is mirrored on both ends of the edge
     *
     * @param a     - First node
     * @param b     - Second node
     * @param road  - road being placed
     * @param owner - owning player
     */
    private void placeRoad(Node a, Node b, Road road, Player owner) {
        road.setOwner(owner);

        // set road on BOTH ends (mirror)
        setRoadOnEdge(a, b, road);
        setRoadOnEdge(b, a, road);
    }

    /**
     * Assign road to the correct edge of a node
     *
     * @param from - node receiving the road reference
     * @param to   - neighbouring node
     * @param road - road to assign
     * @throws IllegalStateException if nodes are not neighbours
     */
    private void setRoadOnEdge(Node from, Node to, Road road) {
        if (to == from.getLeft()) {
            from.setLeftRoad(road);
        } else if (to == from.getRight()) {
            from.setRightRoad(road);
        } else if (to == from.getVert()) {
            from.setVertRoad(road);
        } else {
            throw new IllegalStateException("Tried to place road between non-neighbors.");
        }
    }
}