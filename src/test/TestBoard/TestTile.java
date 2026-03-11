package TestBoard;

import Board.Node;
import Board.Tile;
import GameResources.ResourceType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the Tile class
 * 
 * @author Yojith Sai Biradavolu, McMaster University
 * @version Winter, 2026
 */
public class TestTile {
    private static final int TIMEOUT = 2000;

    /**
     * Checks that constructor throws IllegalArgumentException with null nodes.
     */
    @Test
    @Timeout(TIMEOUT)
    public void testConstructor() {
        try {
            new Tile(0, null, ResourceType.ORE);
            fail("Tile should not be created with null nodes");
        } catch (IllegalArgumentException e) {
            // Do nothing, expected the exception
        }
    }

    /**
     * Checks that getNodes returns nodes for a properly constructed tile.
     */
    @Test
    @Timeout(TIMEOUT)
    public void getNodes() {
        Node node0 = new Node(0);
        Node node1 = new Node(1);
        Node node2 = new Node(2);
        Node node3 = new Node(3);
        Node node4 = new Node(4);
        Node node5 = new Node(5);
        Node[] nodes = {node0, node1, node2, node3, node4, node5};
        Tile tile = new Tile(0, nodes, ResourceType.DESERT);
        assertEquals(6, tile.getNodes().length, "Tile should have 6 nodes");
    }
}