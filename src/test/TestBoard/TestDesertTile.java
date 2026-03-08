package TestBoard;

import Board.DesertTile;
import Board.Node;
import Board.Tile;
import GameResources.ResourceType;
import Player.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the DesertTile class
 * 
 * @author Yojith Sai Biradavolu, McMaster University
 * @version Winter, 2026
 */
public class TestDesertTile {
    private static final int TIMEOUT = 2000;

    /**
     * DesertTile constructor should set resource type to DESERT.
     */
    @Test
    @Timeout(TIMEOUT)
    public void testConstructor() {
        Node[] nodes = {new Node(0), new Node(1), new Node(2), new Node(3), new Node(4), new Node(5)};
        Tile tile = new DesertTile(0, nodes);
        assertEquals(ResourceType.DESERT, tile.getType(), "Tile should have nodes");
    }
}