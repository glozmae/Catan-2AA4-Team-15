package Board;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import GameResources.ResourceType;
import GameResources.Road;
import GameResources.Settlement;
import Player.Player;

/**
 * manages the standard Catan setup phase:
 * Each player places on settlement and one road in forward order and a second
 * settlement and road in reverse order and receives starting resources from
 * tiles
 * Perfroms board placements
 * 
 * @author Elizabeth Glozman, 400559660, McMaster University
 */
public class SetupManager {

    /** board for setup */
    private final Board board;

    /** random number for placment */
    private final Random rng;

    /**
     * setup manager bound to specific board
     * 
     * @param board - board during setup
     * @param seed  - randomized placement sedd
     */
    public SetupManager(Board board, long seed) {
        this.board = board;
        this.rng = new Random(seed);
    }

    /**
     * Full standard setup phase
     * 
     * Forward order: 1 road + 1 settlement
     * Reverse Order: 1 road + 1 settlement
     * 
     * @param players - players in the game
     */
    public void run(List<Player> players) {
        // Forward placement
        for (Player p : players) {
            Node s1 = placeInitialSettlement(p);
            placeInitialRoad(p, s1);
        }

        // Reverse placement + starting resources
        for (int i = players.size() - 1; i >= 0; i--) {
            Player p = players.get(i);
            Node s2 = placeInitialSettlement(p);
            placeInitialRoad(p, s2);
            grantStartingResources(p, s2);
        }
    }

    /**
     * Places settlement during setup by selecting random legal node
     * settlement is plaed onto board and recorded on player
     * 
     * @param player - player placing settlement
     * @return - node where settlement is placed
     */
    private Node placeInitialSettlement(Player player) {
        Node node = pickRandomLegalSettlementNode();

        Settlement s = new Settlement();

        // Place on board
        node.setStructure(s);
        node.setPlayer(player);

        // Track on player
        player.addStructure(s);

        return node;
    }

    /**
     * Check node is legal for settlement placement
     * Follows catan distance rule
     * 
     * @param n - node to check
     * @return true if node is empty & no adjacent structures
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

    private Node pickRandomLegalSettlementNode() {
        List<Node> candidates = new ArrayList<>();
        for (Node n : board.getNodes()) {
            if (isLegalSettlementNode(n))
                candidates.add(n);
        }
        if (candidates.isEmpty()) {
            throw new IllegalStateException("No legal settlement nodes available.");
        }
        return candidates.get(rng.nextInt(candidates.size()));
    }

    /**
     * Build list of candidate nodes that satisfy settlement placement rules and
     * select one
     * 
     * @param player - Player placing road
     * @param from   - node road starts from
     * @throws IllegalStateException if no open edge exists
     */
    private void placeInitialRoad(Player player, Node from) {
        // Collect neighbors that appear open from 'from'
        List<Node> neighbors = new ArrayList<>();

        Node l = from.getLeft();
        if (l != null && from.getLeftRoad() == null)
            neighbors.add(l);

        Node r = from.getRight();

        if (r != null && from.getRightRoad() == null)
            neighbors.add(r);

        Node v = from.getVert();
        if (v != null && from.getVertRoad() == null)
            neighbors.add(v);

        if (neighbors.isEmpty()) {
            throw new IllegalStateException("No open edges available for road from node.");
        }

        // create a road and place
        Road road = new Road();

        // Shuffle candidate targets --> avoids bias
        List<Node> shuffled = new ArrayList<>(neighbors);
        for (int i = shuffled.size() - 1; i > 0; i--) {
            int j = rng.nextInt(i + 1);
            Node tmp = shuffled.get(i);
            shuffled.set(i, shuffled.get(j));
            shuffled.set(j, tmp);
        }

        for (Node to : shuffled) {
            if (tryPlaceRoadBothWays(from, to, road)) {
                player.addRoad(road);
                return;
            }
        }

        // all neighbors failed edge checks due to inconsistent adjacency
        throw new IllegalStateException("Could not place a road from the chosen settlement node (adjacency mismatch).");
    }

    /**
     * Attempt to place road reference on both ends of an edge
     * 
     * @param a    - first node end
     * @param b    - second node end
     * @param road - road ot place
     * @return - true of both sides place succesfully
     * @throws IllegalStateException if adjacency mismatch when placing
     */
    private boolean tryPlaceRoadBothWays(Node a, Node b, Road road) {
        if (!trySetRoadOnEdge(a, b, road))
            return false;

        if (!trySetRoadOnEdge(b, a, road)) {
            throw new IllegalStateException("Adjacency mismatch during road placement.");
        }

        return true;
    }

    /**
     * Attempt to set road reference on correct edge if target node is valid
     * 
     * @param from - node recieving road reference
     * @param to   - neighbouring node
     * @param road - road to set on edge
     * @return - true if placememnt is succesful
     */
    private boolean trySetRoadOnEdge(Node from, Node to, Road road) {
        if (to == from.getLeft()) {
            from.setLeftRoad(road);
            return true;
        } else if (to == from.getRight()) {
            from.setRightRoad(road);
            return true;
        } else if (to == from.getVert()) {
            from.setVertRoad(road);
            return true;
        }
        return false;
    }

    /**
     * Grant player one resource card per non-desert tile adjacent to second
     * settlement placement
     * 
     * @param player         - player receives starting resources
     * @param settlementNode - node containing player's second settlement
     */
    private void grantStartingResources(Player player, Node settlementNode) {
        for (Tile t : board.getTiles()) {
            for (Node n : t.getNodes()) {
                if (n == settlementNode) {
                    ResourceType rt = t.getType();
                    if (rt != null && rt != ResourceType.DESERT) {
                        player.addResource(rt);
                    }
                    break;
                }
            }
        }
    }
}
