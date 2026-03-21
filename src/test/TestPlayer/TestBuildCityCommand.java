package TestPlayer;

import Board.Node;
import Board.Tile;
import Game.Game;
import GameResources.City;
import GameResources.ResourceType;
import GameResources.Settlement;
import Player.Player;
import Player.BuildCityCommand;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

/**
 * Tests for the BuildCityCommand class.
 * Ensures full coverage of execution, undo logic, and structure swapping.
 */
public class TestBuildCityCommand {

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
        Node node = new Node(10);
        Settlement originalSettlement = new Settlement();

        node.setStructure(originalSettlement);
        player.addStructure(originalSettlement);

        int initialResourceTotal = getTotalResources(player);

        BuildCityCommand command = new BuildCityCommand(player, node);

        command.execute();

        assertTrue(getTotalResources(player) < initialResourceTotal, "Resources should be deducted to pay for the city");
        assertTrue(node.getStructure() instanceof City, "Node should now hold a City");
        assertNotEquals(originalSettlement, node.getStructure(), "Node should no longer hold the original Settlement");
        assertEquals(0, player.getSettlements().size(), "Player should have 0 settlements");
        assertEquals(1, player.getCities().size(), "Player should have 1 city");
        assertEquals("Built city at node 10", command.getExecuteMessage());

        command.undo();

        assertEquals(initialResourceTotal, getTotalResources(player), "Resources should be fully refunded");
        assertEquals(originalSettlement, node.getStructure(), "Node should hold the original Settlement again");
        assertEquals(1, player.getSettlements().size(), "Player should have 1 settlement again");
        assertEquals(0, player.getCities().size(), "Player should have 0 cities");
        assertEquals("Removed city from node 10", command.getUndoMessage());
    }

    @Test
    void testExecutionAndUndoGuards() {
        Node node = new Node(5);
        Settlement originalSettlement = new Settlement();
        node.setStructure(originalSettlement);
        player.addStructure(originalSettlement);

        BuildCityCommand command = new BuildCityCommand(player, node);

        command.undo();
        assertEquals(1, player.getSettlements().size(), "Should safely ignore undo if not executed");
        assertEquals(0, player.getCities().size());

        command.execute();
        int resourcesAfterFirstExecute = getTotalResources(player);

        command.execute();
        assertEquals(resourcesAfterFirstExecute, getTotalResources(player), "Should safely ignore second execute (no double charging)");
        assertEquals(1, player.getCities().size(), "Should not add a second city");

        command.undo();
        int resourcesAfterFirstUndo = getTotalResources(player);

        command.undo();
        assertEquals(resourcesAfterFirstUndo, getTotalResources(player), "Should safely ignore second undo (no double refunding)");
        assertEquals(1, player.getSettlements().size(), "Should not add a second settlement");
    }
}