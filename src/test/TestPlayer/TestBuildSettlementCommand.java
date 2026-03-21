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

        for (int i = 0; i < 5; i++) {
            player.addResource(ResourceType.BRICK);
            player.addResource(ResourceType.LUMBER);
            player.addResource(ResourceType.WOOL);
            player.addResource(ResourceType.GRAIN);
            player.addResource(ResourceType.ORE);
        }
    }

    private int getTotalResources(Player p) {
        return p.getResourceAmount(ResourceType.BRICK) +
                p.getResourceAmount(ResourceType.LUMBER) +
                p.getResourceAmount(ResourceType.WOOL) +
                p.getResourceAmount(ResourceType.GRAIN) +
                p.getResourceAmount(ResourceType.ORE);
    }

    @Test
    void testExecuteAndUndo() {
        Node node = new Node(15);
        int initialResourceTotal = getTotalResources(player);

        BuildSettlementCommand command = new BuildSettlementCommand(player, node);

        command.execute();

        assertTrue(getTotalResources(player) < initialResourceTotal, "Resources should be deducted to pay for the settlement");
        assertNotNull(node.getStructure(), "Node should now hold a structure");
        assertEquals(player, node.getStructure().getOwner(), "The new settlement should be owned by the player");
        assertEquals(1, player.getSettlements().size(), "Player should have 1 settlement");
        assertEquals("Built settlement at node 15", command.getExecuteMessage());

        command.undo();

        assertEquals(initialResourceTotal, getTotalResources(player), "Resources should be fully refunded");
        assertNull(node.getStructure(), "Node should be empty again");
        assertEquals(0, player.getSettlements().size(), "Player should have 0 settlements");
        assertEquals("Removed settlement from node 15", command.getUndoMessage());
    }

    @Test
    void testExecutionAndUndoGuards() {
        Node node = new Node(7);
        BuildSettlementCommand command = new BuildSettlementCommand(player, node);

        command.undo();
        assertNull(node.getStructure(), "Should safely ignore undo if not executed (Node stays empty)");
        assertEquals(0, player.getSettlements().size());

        command.execute();
        int resourcesAfterFirstExecute = getTotalResources(player);

        command.execute();
        assertEquals(resourcesAfterFirstExecute, getTotalResources(player), "Should safely ignore second execute (no double charging)");
        assertEquals(1, player.getSettlements().size(), "Should not add a second settlement to inventory");

        command.undo();
        int resourcesAfterFirstUndo = getTotalResources(player);

        command.undo();
        assertEquals(resourcesAfterFirstUndo, getTotalResources(player), "Should safely ignore second undo (no double refunding)");
        assertEquals(0, player.getSettlements().size(), "Should safely handle redundant undo");
    }
}