package Board;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import GameResources.Road;
import GameResources.Settlement;
import GameResources.Structure;
import Player.Player;

/**
 * Handles legal placement of structures/roads on the board WITHOUT changing Node.
 * Because it lives in the Board package, it can call Node's package-private setters.
 */
public class Placement {

    private final Board board;
    private final Random rng;

    public Placement(Board board, long seed) {
        this.board = board;
        this.rng = new Random(seed);
    }

    // ----------- Setup placements (free) -----------

    /** Places a settlement respecting the distance rule. Returns the chosen node. */
    public Node placeInitialSettlement(Player player) {
        Node node = pickRandomLegalSettlementNode();
        Settlement s = new Settlement();

        placeStructure(node, s, player);
        player.addStructure(s);

        return node;
    }

    /** Places a road from 'from' to a random open neighbor edge. */
    public void placeInitialRoad(Player player, Node from) {
        Node to = pickRandomNeighborWithOpenEdge(from);
        Road r = new Road();

        placeRoad(from, to, r, player);
        player.addRoad(r);
    }

    // ----------- Turn builds (optional for later) -----------

    public boolean tryBuildRoad(Player player) {
        // naive: find one owned node and build an open edge from it
        for (Node n : board.getNodes()) {
            Structure s = n.getStructure();
            if (s != null && s.getOwner() == player) {
                Node to = pickNeighborWithOpenEdgeOrNull(n);
                if (to != null) {
                    Road r = new Road();
                    placeRoad(n, to, r, player);
                    player.addRoad(r);
                    return true;
                }
            }
        }
        return false;
    }

    // ----------- Internal helpers -----------

    private Node pickRandomLegalSettlementNode() {
        List<Node> candidates = new ArrayList<>();
        for (Node n : board.getNodes()) {
            if (isLegalSettlementNode(n)) candidates.add(n);
        }
        if (candidates.isEmpty()) {
            throw new IllegalStateException("No legal nodes available for settlement placement.");
        }
        return candidates.get(rng.nextInt(candidates.size()));
    }

    /** Distance rule only (for now): empty and no adjacent structures. */
    private boolean isLegalSettlementNode(Node n) {
        if (n.getStructure() != null) return false;

        Node l = n.getLeft();
        Node r = n.getRight();
        Node v = n.getVert();

        return (l == null || l.getStructure() == null)
            && (r == null || r.getStructure() == null)
            && (v == null || v.getStructure() == null);
    }

    private Node pickRandomNeighborWithOpenEdge(Node from) {
        List<Node> options = new ArrayList<>();

        if (from.getLeft() != null && from.getLeftRoad() == null) options.add(from.getLeft());
        if (from.getRight() != null && from.getRightRoad() == null) options.add(from.getRight());
        if (from.getVert() != null && from.getVertRoad() == null) options.add(from.getVert());

        if (options.isEmpty()) {
            throw new IllegalStateException("No open edges for road placement from node " + from);
        }
        return options.get(rng.nextInt(options.size()));
    }

    private Node pickNeighborWithOpenEdgeOrNull(Node from) {
        List<Node> options = new ArrayList<>();

        if (from.getLeft() != null && from.getLeftRoad() == null) options.add(from.getLeft());
        if (from.getRight() != null && from.getRightRoad() == null) options.add(from.getRight());
        if (from.getVert() != null && from.getVertRoad() == null) options.add(from.getVert());

        if (options.isEmpty()) return null;
        return options.get(rng.nextInt(options.size()));
    }

    private void placeStructure(Node node, Structure structure, Player owner) {
        // set owner at the structure level
        structure.setOwner(owner);

        // Board-package access to Node internals:
        node.setStructure(structure);
        node.setPlayer(owner);
    }

    private void placeRoad(Node a, Node b, Road road, Player owner) {
        road.setOwner(owner);

        // set road on BOTH ends (mirror)
        setRoadOnEdge(a, b, road);
        setRoadOnEdge(b, a, road);
    }

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
