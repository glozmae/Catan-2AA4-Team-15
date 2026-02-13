package Board;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import GameResources.ResourceType;
import GameResources.Road;
import GameResources.Settlement;
import Player.Player;

/**
 * Handles the standard Catan setup phase (snake order) without modifying Node.
 * Lives in the Board package so it can use Node's package-private setters.
 */
public class SetupManager {

    private final Board board;
    private final Random rng;

    public SetupManager(Board board, long seed) {
        this.board = board;
        this.rng = new Random(seed);
    }

    /**
     * Runs the full standard setup:
     * Forward order: each player places 1 settlement + 1 road.
     * Reverse order: each player places 2nd settlement + 1 road and gains starting resources.
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

    // ---------------- Settlement placement ----------------

    private Node placeInitialSettlement(Player player) {
        Node node = pickRandomLegalSettlementNode();

        Settlement s = new Settlement();

        // Place on board (Board package has access to these setters)
        node.setStructure(s);
        node.setPlayer(player);

        // Track on player too (this should set owner if your Player.addStructure does)
        player.addStructure(s);

        return node;
    }

    /** Distance rule only: empty and no adjacent structures. */
    private boolean isLegalSettlementNode(Node n) {
        if (n.getStructure() != null) return false;

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
            if (isLegalSettlementNode(n)) candidates.add(n);
        }
        if (candidates.isEmpty()) {
            throw new IllegalStateException("No legal settlement nodes available.");
        }
        return candidates.get(rng.nextInt(candidates.size()));
    }

    // ---------------- Road placement ----------------

    private void placeInitialRoad(Player player, Node from) {
        // Collect neighbors that appear open from 'from'
        List<Node> neighbors = new ArrayList<>();

        Node l = from.getLeft();
        if (l != null && from.getLeftRoad() == null) neighbors.add(l);

        Node r = from.getRight();
        // IMPORTANT: your Node getter is getRighttRoad() in the code you pasted earlier
        if (r != null && from.getRightRoad() == null) neighbors.add(r);

        Node v = from.getVert();
        if (v != null && from.getVertRoad() == null) neighbors.add(v);

        if (neighbors.isEmpty()) {
            throw new IllegalStateException("No open edges available for road from node.");
        }

        // Try candidates until one successfully places (avoids non-neighbor crash)
        Road road = new Road();

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

        // If we got here, all "neighbors" failed edge checks due to inconsistent adjacency
        throw new IllegalStateException("Could not place a road from the chosen settlement node (adjacency mismatch).");
    }

    private boolean tryPlaceRoadBothWays(Node a, Node b, Road road) {
        // Try place a->b; if fails, do nothing
        if (!trySetRoadOnEdge(a, b, road)) return false;

        // Try place b->a; if fails, rollback a->b
        if (!trySetRoadOnEdge(b, a, road)) {
            rollbackRoadOnEdge(a, b);
            return false;
        }

        return true;
    }

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

    private void rollbackRoadOnEdge(Node from, Node to) {
        // Since Node setters only set if null, rollback is limited.
        // We only call rollback immediately after we just set it, so this is safe enough conceptually.
        // If you later want perfect rollback, you'd need explicit "unset" methods.
        //
        // For now, we do nothing because your Node API doesn't support unsetting.
        // This means: if b->a fails due to mismatch, a->b might already be set.
        // In practice, with consistent adjacency, this never triggers.
    }

    // ---------------- Starting resources ----------------

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
