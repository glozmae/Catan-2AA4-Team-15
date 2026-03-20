package TestPlayer;

import Board.Node;
import Board.Tile;
import Game.Game;
import GameResources.ResourceType;
import Player.Player;
import Player.BuildSettlementCommand;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

/**
 * Tests for the BuildSettlementCommand class.
 * Ensures full coverage of execution, undo logic, and structure assignments.
 */
public class TestBuildSettlementCommand {

    /**
     * A simple concrete subclass of Player to isolate testing of the command.
     */
    private static class DummyPlayer extends Player {
        @Override public void takeTurn(Game game) {}
        @Override public void setup(Game game) {}
        @Override public void robberDiscard() {}
        @Override public Tile setRobber(List<Tile> tiles) { return null; }
    }

    private DummyPlayer player;

    @BeforeEach
    void setUp() {
        Player.resetNumPlayers();
        player = new DummyPlayer();

        // Give the player enough resources to pay for a settlement
        for (int i = 0; i < 5; i++) {
            player.addResource(ResourceType.BRICK);
            player.addResource(ResourceType.LUMBER);
            player.addResource(ResourceType.WOOL);
            player.addResource(ResourceType.GRAIN);
            player.addResource(ResourceType.ORE);
        }
    }

    /**
     * Helper method to calculate the total number of resources in the player's hand.
     */
    private int getTotalResources(Player p) {
        return p.getResourceAmount(ResourceType.BRICK) +
                p.getResourceAmount(ResourceType.LUMBER) +
                p.getResourceAmount(ResourceType.WOOL) +
                p.getResourceAmount(ResourceType.GRAIN) +
                p.getResourceAmount(ResourceType.ORE);
    }

    /**
     * Tests standard execution and undo: placing a settlement on an empty node and adjusting costs.
     */
    @Test
    void testExecuteAndUndo() {
        Node node = new Node(15);
        int initialResourceTotal = getTotalResources(player);

        BuildSettlementCommand command = new BuildSettlementCommand(player, node);

        // --- 1. Test Execute ---
        command.execute();

        // Verify resources were deducted
        assertTrue(getTotalResources(player) < initialResourceTotal, "Resources should be deducted to pay for the settlement");

        // Verify structure placement on the Node
        assertNotNull(node.getStructure(), "Node should now hold a structure");
        assertEquals(player, node.getStructure().getOwner(), "The new settlement should be owned by the player");

        // Verify structure addition to Player's inventory
        assertEquals(1, player.getSettlements().size(), "Player should have 1 settlement");

        // Verify Message
        assertEquals("Built settlement at node 15", command.getExecuteMessage());

        // --- 2. Test Undo ---
        command.undo();

        // Verify resources were refunded
        assertEquals(initialResourceTotal, getTotalResources(player), "Resources should be fully refunded");

        // Verify structure removal from the Node
        assertNull(node.getStructure(), "Node should be empty again");

        // Verify structure removal from Player's inventory
        assertEquals(0, player.getSettlements().size(), "Player should have 0 settlements");

        // Verify Message
        assertEquals("Removed settlement from node 15", command.getUndoMessage());
    }

    /**
     * Tests the guard clauses that prevent double-execution or double-undoing.
     */
    @Test
    void testExecutionAndUndoGuards() {
        Node node = new Node(7);
        BuildSettlementCommand command = new BuildSettlementCommand(player, node);

        // Test Undo before Execute (Should safely do nothing)
        command.undo();
        assertNull(node.getStructure(), "Should safely ignore undo if not executed (Node stays empty)");
        assertEquals(0, player.getSettlements().size());

        // Test Double Execute
        command.execute();
        int resourcesAfterFirstExecute = getTotalResources(player);

        command.execute(); // Second call
        assertEquals(resourcesAfterFirstExecute, getTotalResources(player), "Should safely ignore second execute (no double charging)");
        assertEquals(1, player.getSettlements().size(), "Should not add a second settlement to inventory");

        // Test Double Undo
        command.undo();
        int resourcesAfterFirstUndo = getTotalResources(player);

        command.undo(); // Second call
        assertEquals(resourcesAfterFirstUndo, getTotalResources(player), "Should safely ignore second undo (no double refunding)");
        assertEquals(0, player.getSettlements().size(), "Should safely handle redundant undo");
    }
}