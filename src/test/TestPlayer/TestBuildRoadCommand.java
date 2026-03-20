package TestPlayer;

import Board.Node;
import Game.Game;
import Board.Tile;
import GameResources.ResourceType;
import Player.Player;
import Player.BuildRoadCommand;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

/**
 * Tests for the BuildRoadCommand class.
 * Ensures full coverage of execution, undo logic, and all directional placements.
 */
public class TestBuildRoadCommand {

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

        // Give the player plenty of resources so payCost() doesn't throw underflow exceptions
        for (int i = 0; i < 5; i++) {
            player.addResource(ResourceType.BRICK);
            player.addResource(ResourceType.LUMBER);
            player.addResource(ResourceType.WOOL);
            player.addResource(ResourceType.GRAIN);
            player.addResource(ResourceType.ORE);
        }
    }

    /**
     * Tests standard execution and undo across a LEFT/RIGHT connection.
     */
    @Test
    void testExecuteAndUndo_LeftConnection() {
        Node start = new Node(1);
        Node end = new Node(2);
        start.setLeft(end); // This automatically sets end.right = start based on your Node logic

        int initialBrick = player.getResourceAmount(ResourceType.BRICK);
        int initialLumber = player.getResourceAmount(ResourceType.LUMBER);

        BuildRoadCommand command = new BuildRoadCommand(player, start, end);

        // 1. Test Execute
        command.execute();

        assertEquals(initialBrick - 1, player.getResourceAmount(ResourceType.BRICK), "Brick should be deducted");
        assertEquals(initialLumber - 1, player.getResourceAmount(ResourceType.LUMBER), "Lumber should be deducted");
        assertEquals(1, player.getRoads().size(), "Player should have 1 road");
        assertNotNull(start.getLeftRoad(), "Road should be placed on start's left edge");
        assertNotNull(end.getRightRoad(), "Road should be placed on end's right edge");
        assertEquals("Built road [1,2]", command.getExecuteMessage());

        // 2. Test Undo
        command.undo();

        assertEquals(initialBrick, player.getResourceAmount(ResourceType.BRICK), "Brick should be refunded");
        assertEquals(initialLumber, player.getResourceAmount(ResourceType.LUMBER), "Lumber should be refunded");
        assertEquals(0, player.getRoads().size(), "Player should have 0 roads");
        assertNull(start.getLeftRoad(), "Road should be removed from start");
        assertNull(end.getRightRoad(), "Road should be removed from end");
        assertEquals("Removed road [1,2]", command.getUndoMessage());
    }

    /**
     * Tests standard execution and undo across a RIGHT/LEFT connection to hit the first 'else if'.
     */
    @Test
    void testExecuteAndUndo_RightConnection() {
        Node start = new Node(3);
        Node end = new Node(4);
        start.setRight(end);

        BuildRoadCommand command = new BuildRoadCommand(player, start, end);
        command.execute();

        assertNotNull(start.getRightRoad(), "Road should be on start's right edge");
        assertNotNull(end.getLeftRoad(), "Road should be on end's left edge");

        command.undo();

        assertNull(start.getRightRoad());
        assertNull(end.getLeftRoad());
    }

    /**
     * Tests standard execution and undo across a VERTICAL connection to hit the final 'else if'.
     */
    @Test
    void testExecuteAndUndo_VerticalConnection() {
        Node start = new Node(5);
        Node end = new Node(6);
        start.setVert(end);

        BuildRoadCommand command = new BuildRoadCommand(player, start, end);
        command.execute();

        assertNotNull(start.getVertRoad(), "Road should be on start's vertical edge");
        assertNotNull(end.getVertRoad(), "Road should be on end's vertical edge");

        command.undo();

        assertNull(start.getVertRoad());
        assertNull(end.getVertRoad());
    }

    /**
     * Tests the guard clauses that prevent double-execution or double-undoing.
     */
    @Test
    void testExecutionAndUndoGuards() {
        Node start = new Node(7);
        Node end = new Node(8);
        start.setLeft(end);

        BuildRoadCommand command = new BuildRoadCommand(player, start, end);

        // Test Undo before Execute (Should safely do nothing)
        command.undo();
        assertEquals(0, player.getRoads().size(), "Should safely ignore undo if not executed");

        command.execute();
        int roadsAfterFirstExecute = player.getRoads().size();
        assertEquals(1, roadsAfterFirstExecute);

        // Test Double Execute (Should safely do nothing)
        command.execute();
        assertEquals(roadsAfterFirstExecute, player.getRoads().size(), "Should safely ignore second execute");

        command.undo();
        int roadsAfterFirstUndo = player.getRoads().size();
        assertEquals(0, roadsAfterFirstUndo);

        // Test Double Undo (Should safely do nothing)
        command.undo();
        assertEquals(roadsAfterFirstUndo, player.getRoads().size(), "Should safely ignore second undo");
    }
}