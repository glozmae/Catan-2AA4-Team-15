package TestBoard;

import Board.Node;
import GameResources.Road;
import GameResources.Settlement;
import GameResources.Structure;
import Player.Player;
import Player.ComputerPlayer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the Node class
 * * @author Yojith Sai Biradavolu, McMaster University
 * @version Winter, 2026
 */
public class TestNode {
    private static final int TIMEOUT = 2000;

    @BeforeEach
    public void resetPlayers() {
        Player.resetNumPlayers();
    }

    // --- EXISTING TESTS (Slightly modified for completeness) ---

    @Test
    @Timeout(TIMEOUT)
    public void canBuildSettlement() {
        Node node = new Node(0);
        Node node2 = new Node(1);
        Node node3 = new Node(2);
        Player dummyPlayer = new ComputerPlayer();
        Road dummyRoad = new Road();
        dummyRoad.setOwner(dummyPlayer);

        assertFalse(node.canBuildSettlement(dummyPlayer), "Node should not be buildable by player without connecting road");
        node.setLeftRoad(dummyRoad);
        assertTrue(node.canBuildSettlement(dummyPlayer), "Node should be buildable by player");
        node2.setRightRoad(dummyRoad);
        assertTrue(node2.canBuildSettlement(dummyPlayer), "Node should be buildable by player");
        node3.setVertRoad(dummyRoad);
        assertTrue(node3.canBuildSettlement(dummyPlayer), "Node should be buildable by player");
    }

    @Test
    @Timeout(TIMEOUT)
    public void getBuildableRoadNeighbors() {
        Node node = new Node(0);
        node.setLeft(new Node(1));
        node.setRight(new Node(2));
        node.setVert(new Node(3));
        Player dummyPlayer = new ComputerPlayer();
        Structure dummySettlement = new Settlement();
        dummySettlement.setOwner(dummyPlayer);

        assertEquals(0, node.getBuildableRoadNeighbors(dummyPlayer).size(), "Unoccupied node has no buildable roads");
        node.setStructure(dummySettlement);
        assertEquals(3, node.getBuildableRoadNeighbors(dummyPlayer).size(), "Occupied node has 3 buildable roads");
        node.setLeftRoad(new Road());
        assertEquals(2, node.getBuildableRoadNeighbors(dummyPlayer).size(), "Occupied node with left road has 2 buildable roads");
        node.setRightRoad(new Road());
        assertEquals(1, node.getBuildableRoadNeighbors(dummyPlayer).size(), "Occupied node with right road has 1 buildable road");
        node.setVertRoad(new Road());
        assertEquals(0, node.getBuildableRoadNeighbors(dummyPlayer).size(), "Occupied node with vert road has 0 buildable roads");
    }

    // --- NEW TESTS FOR 100% COVERAGE ---

    /**
     * Tests basic ID, toString, and empty player retrieval.
     */
    @Test
    @Timeout(TIMEOUT)
    public void testIdentityAndEmptyPlayer() {
        Node node = new Node(42);
        assertEquals(42, node.getId());
        assertEquals("42", node.toString());
        assertNull(node.getPlayer(), "Player should be null if no structure exists");
        assertNull(node.getStructure());
    }

    /**
     * Tests the bidirectional linking of neighbors and null handling.
     */
    @Test
    @Timeout(TIMEOUT)
    public void testNeighborLinks() {
        Node center = new Node(0);
        Node left = new Node(1);
        Node right = new Node(2);
        Node vert = new Node(3);

        // Test Left linking
        center.setLeft(left);
        assertEquals(left, center.getLeft());
        assertEquals(center, left.getRight()); // Bidirectional check

        // Test Right linking
        center.setRight(right);
        assertEquals(right, center.getRight());
        assertEquals(center, right.getLeft());

        // Test Vertical linking
        center.setVert(vert);
        assertEquals(vert, center.getVert());
        assertEquals(center, vert.getVert());

        // Test Null Guards (coverage for 'if (node == null) return;')
        center.setLeft(null);
        center.setRight(null);
        center.setVert(null);

        // Ensure values weren't overwritten by nulls
        assertNotNull(center.getLeft());
        assertNotNull(center.getRight());
        assertNotNull(center.getVert());
    }

    /**
     * Tests Road placement, clearing, and the "slot empty" logic.
     */
    @Test
    @Timeout(TIMEOUT)
    public void testRoadManagement() {
        Node node = new Node(0);
        Road r1 = new Road();
        Road r2 = new Road(); // Decoy road to test overwrite prevention

        // Left Road
        node.setLeftRoad(r1);
        assertEquals(r1, node.getLeftRoad());
        node.setLeftRoad(r2); // Should NOT set because it's not null
        assertEquals(r1, node.getLeftRoad());
        node.clearLeftRoad();
        assertNull(node.getLeftRoad());

        // Right Road
        node.setRightRoad(r1);
        assertEquals(r1, node.getRightRoad());
        node.setRightRoad(r2);
        assertEquals(r1, node.getRightRoad());
        node.clearRightRoad();
        assertNull(node.getRightRoad());

        // Vert Road
        node.setVertRoad(r1);
        assertEquals(r1, node.getVertRoad());
        node.setVertRoad(r2);
        assertEquals(r1, node.getVertRoad());
        node.clearVertRoad();
        assertNull(node.getVertRoad());
    }

    /**
     * Tests distance rule specifically for the Right and Vertical directions.
     * (Left direction was covered by the original test).
     */
    @Test
    @Timeout(TIMEOUT)
    public void testDistanceRuleRightAndVert() {
        Player p1 = new ComputerPlayer();
        Road r1 = new Road(); r1.setOwner(p1);
        Structure s1 = new Settlement(); s1.setOwner(p1);

        Node center = new Node(0);
        center.setLeftRoad(r1); // Give P1 connectivity

        Node right = new Node(1);
        Node vert = new Node(2);
        center.setRight(right);
        center.setVert(vert);

        // Check Right neighbor occupied
        right.setStructure(s1);
        assertFalse(center.canBuildSettlement(p1), "Should be blocked by right neighbor");
        right.setStructure(null);

        // Check Vert neighbor occupied
        vert.setStructure(s1);
        assertFalse(center.canBuildSettlement(p1), "Should be blocked by vertical neighbor");
    }

    /**
     * Tests the "Road Interruption" rule:
     * You cannot build past an enemy settlement, but you can build through an empty node.
     */
    @Test
    @Timeout(TIMEOUT)
    public void testRoadInterruptionRule() {
        Player p1 = new ComputerPlayer();
        Player p2 = new ComputerPlayer();

        Node node = new Node(0);
        node.setLeft(new Node(1)); // Provide an empty edge to extend to

        // P1 has a road leading to this node
        Road r1 = new Road();
        r1.setOwner(p1);
        node.setVertRoad(r1);

        // Case 1: Node is empty - P1 can extend their road past it
        assertFalse(node.getBuildableRoadNeighbors(p1).isEmpty(), "P1 should extend through empty node");

        // Case 2: Node has P2's settlement - P1 is BLOCKED
        Structure enemySettlement = new Settlement();
        enemySettlement.setOwner(p2);
        node.setStructure(enemySettlement);

        List<Node> validTargets = node.getBuildableRoadNeighbors(p1);
        assertTrue(validTargets.isEmpty(), "P1 should be blocked by P2's settlement");
    }
}