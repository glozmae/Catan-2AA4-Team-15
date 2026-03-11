package TestBoard;

import Board.Node;
import Board.Robber;
import Board.Tile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import GameResources.ResourceType;

/**
 * Test class for the Robber entity.
 * Verifies initialization and movement logic.
 *
 * @author Taihan Mobasshir, 400578506, McMaster University
 */
public class TestRobber {

    private Tile desertTile;
    private Tile forestTile;
    private Robber robber;

    /**
     * Set up dummy tiles and initialize the robber before each test.
     */
    @BeforeEach
    public void setUp() {
        // 1. Create a valid array of 6 actual Node objects for the Desert Tile
        Node[] desertNodes = new Node[6];
        for (int i = 0; i < 6; i++) {
            desertNodes[i] = new Node(i);
        }

        // 2. Create a separate valid array of 6 Node objects for the Forest Tile
        Node[] forestNodes = new Node[6];
        for (int i = 0; i < 6; i++) {
            forestNodes[i] = new Node(i + 6);
        }

        // 3. Initialize the tiles with valid nodes
        desertTile = new Tile(0, desertNodes, ResourceType.DESERT);
        forestTile = new Tile(1, forestNodes, ResourceType.LUMBER);

        // 4. Place robber on the desert initially
        robber = new Robber(desertTile);
    }

    /**
     * Tests that the constructor correctly assigns the initial tile.
     */
    @Test
    public void testRobberInitialization() {
        // Robber should be on the desert tile
        assertTrue(robber.hasRobber(desertTile), "Robber should be located on the initial desert tile.");

        // Robber should NOT be on the forest tile
        assertFalse(robber.hasRobber(forestTile), "Robber should not be on a tile it wasn't placed on.");
    }

    /**
     * Tests that the robber correctly updates its location when moved.
     */
    @Test
    public void testNewRobberMovement() {
        // Move the robber to the forest tile
        robber.newRobber(forestTile);

        // Verify the robber is now on the forest tile
        assertTrue(robber.hasRobber(forestTile), "Robber should be on the forest tile after moving.");

        // Verify the robber is no longer on the desert tile
        assertFalse(robber.hasRobber(desertTile), "Robber should no longer be on the desert tile after moving.");
    }
}