package TestTask3;

import GameResources.City;
import GameResources.Road;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import GameResources.Settlement;
import Player.Player;
import Player.ComputerPlayer;
import Player.HumanPlayer;
import Board.Board;
import Board.Node;
import Board.Tile;
import Game.Game;
import Game.Dice;
import GameResources.ResourceType;

import static org.junit.jupiter.api.Assertions.*;

public class TestHumanPlayer {

    private final InputStream standardIn = System.in;

    private static class FixedDice implements Dice {
        private final int fixedRoll;
        public FixedDice(int fixedRoll) { this.fixedRoll = fixedRoll; }
        @Override
        public int roll() { return fixedRoll; }
    }

    @BeforeEach
    void setUp() {
        Player.resetNumPlayers();
    }

    @AfterEach
    void tearDown() {
        System.setIn(standardIn);
    }

    private HumanPlayer createPlayerWithInput(String data) {
        ByteArrayInputStream testIn = new ByteArrayInputStream(data.getBytes());
        System.setIn(testIn);
        return new HumanPlayer();
    }

    // --- EXISTING TESTS ---

    @Test
    void testTakeTurn_ListAndGo() {
        HumanPlayer hp = createPlayerWithInput("list\ngo\n");
        ComputerPlayer cp = new ComputerPlayer();
        Game game = new Game(List.of(hp, cp), new TestHumanPlayer.FixedDice(6), new Board(), 10, 20);
        assertDoesNotThrow(() -> hp.takeTurn(game));
    }

    @Test
    void testTakeTurn_RollCommandIntercept() {
        HumanPlayer hp = createPlayerWithInput("roll\ngo\n");
        ComputerPlayer cp = new ComputerPlayer();
        Game game = new Game(List.of(hp, cp), new TestHumanPlayer.FixedDice(8), new Board(), 10, 20);
        assertDoesNotThrow(() -> hp.takeTurn(game));
    }

    @Test
    void testSetup() {
        HumanPlayer hp = createPlayerWithInput("0\n1\n3\n4\n");
        ComputerPlayer cp = new ComputerPlayer();
        Game game = new Game(List.of(hp, cp), new TestHumanPlayer.FixedDice(6), new Board(), 10, 20);
        hp.setup(game);

        assertEquals(2, hp.getSettlements().size());
        assertEquals(2, hp.getRoads().size());
    }

    @Test
    void testRobberDiscard() {
        HumanPlayer hp = createPlayerWithInput("INVALID_TEXT\nBRICK\n");
        for (int i = 0; i < 8; i++) hp.addResource(ResourceType.BRICK);

        assertEquals(8, hp.getHand().getCount());
        hp.robberDiscard();

        assertEquals(7, hp.getHand().getCount());
        assertEquals(7, hp.getResourceAmount(ResourceType.BRICK));
    }

    @Test
    void testSetRobber() {
        HumanPlayer hp = createPlayerWithInput("");
        Board board = new Board();
        List<Tile> tiles = board.getTiles();
        Tile chosenTile = hp.setRobber(tiles);

        assertNotNull(chosenTile);
        assertTrue(tiles.contains(chosenTile));
    }

    @Test
    void testBuildSettlement_NotAffordable() {
        HumanPlayer hp = createPlayerWithInput("build settlement 0\ngo\n");
        ComputerPlayer cp = new ComputerPlayer();
        Game game = new Game(List.of(hp, cp), new TestHumanPlayer.FixedDice(6), new Board(), 10, 20);

        hp.takeTurn(game);
        assertEquals(0, hp.getSettlements().size());
    }

    @Test
    void testBuildCity_NoSettlementAtNode() {
        HumanPlayer hp = createPlayerWithInput("build city 0\ngo\n");
        hp.addResource(ResourceType.GRAIN); hp.addResource(ResourceType.GRAIN);
        hp.addResource(ResourceType.ORE); hp.addResource(ResourceType.ORE); hp.addResource(ResourceType.ORE);
        ComputerPlayer cp = new ComputerPlayer();
        Game game = new Game(List.of(hp, cp), new TestHumanPlayer.FixedDice(6), new Board(), 10, 20);

        hp.takeTurn(game);
        assertEquals(0, hp.getCities().size());
    }

    @Test
    void testBuildRoad_InvalidNodeId() {
        HumanPlayer hp = createPlayerWithInput("build road 0,9999\ngo\n");
        hp.addResource(ResourceType.BRICK); hp.addResource(ResourceType.LUMBER);
        ComputerPlayer cp = new ComputerPlayer();
        Game game = new Game(List.of(hp, cp), new TestHumanPlayer.FixedDice(6), new Board(), 10, 20);

        hp.takeTurn(game);
        assertEquals(0, hp.getRoads().size());
    }

    @Test
    void testBuildSettlement_Valid() {
        Board board = new Board();
        Node node0 = board.getNodes().get(0);
        Node node1 = board.getNodes().get(1);
        Road r = new Road();
        node0.setLeftRoad(r);
        node1.setRightRoad(r);

        HumanPlayer hp = createPlayerWithInput("build settlement 1\ngo\n");
        hp.addResource(ResourceType.BRICK); hp.addResource(ResourceType.LUMBER);
        hp.addResource(ResourceType.WOOL); hp.addResource(ResourceType.GRAIN);
        r.setOwner(hp);

        ComputerPlayer cp = new ComputerPlayer();
        Game game = new Game(List.of(hp, cp), new FixedDice(6), board, 10, 1);

        hp.takeTurn(game);
        assertEquals(1, hp.getSettlements().size());
    }

    @Test
    void testBuildCity_Valid() {
        Board board = new Board();
        Node node0 = board.getNodes().get(0);
        HumanPlayer hp = createPlayerWithInput("build city 0\ngo\n");
        Settlement s = new Settlement();
        node0.setStructure(s); hp.addStructure(s);
        hp.addResource(ResourceType.GRAIN); hp.addResource(ResourceType.GRAIN);
        hp.addResource(ResourceType.ORE); hp.addResource(ResourceType.ORE); hp.addResource(ResourceType.ORE);

        ComputerPlayer cp = new ComputerPlayer();
        Game game = new Game(List.of(hp, cp), new TestHumanPlayer.FixedDice(6), board, 10, 20);

        hp.takeTurn(game);
        assertEquals(1, hp.getCities().size());
    }

    @Test
    void testBuildRoad_Valid() {
        Board board = new Board();
        Node node0 = board.getNodes().get(0);
        HumanPlayer hp = createPlayerWithInput("build road 0,1\ngo\n");
        Settlement s = new Settlement();
        node0.setStructure(s); hp.addStructure(s);
        hp.addResource(ResourceType.BRICK); hp.addResource(ResourceType.LUMBER);

        ComputerPlayer cp = new ComputerPlayer();
        Game game = new Game(List.of(hp, cp), new TestHumanPlayer.FixedDice(6), board, 10, 20);
        hp.takeTurn(game);
        assertEquals(1, hp.getRoads().size());
    }

    // --- NEW TESTS FOR 100% COVERAGE ---

    @Test
    void testEmptyAndUnknownCommands() {
        // Tests hitting Enter (empty) and typing gibberish
        HumanPlayer hp = createPlayerWithInput("\ngibberish\ngo\n");
        ComputerPlayer cp = new ComputerPlayer();
        Game game = new Game(List.of(hp, cp), new FixedDice(6), new Board(), 10, 20);
        assertDoesNotThrow(() -> hp.takeTurn(game));
    }

    @Test
    void testSetup_InvalidInputsAndExceptions() {
        // 1. Text ("abc" -> NumberFormatException)
        // 2. Invalid Node ID ("999")
        // 3. Valid Settlement Node ("0")
        // 4. Text for Road ("abc")
        // 5. Invalid target for Road ("999")
        // 6. Valid target for Road ("1")
        // 7,8. Valid 2nd Setup ("3", "4")
        HumanPlayer hp = createPlayerWithInput("abc\n999\n0\nabc\n999\n1\n3\n4\n");
        ComputerPlayer cp = new ComputerPlayer();
        Game game = new Game(List.of(hp, cp), new FixedDice(6), new Board(), 10, 20);

        hp.setup(game);
        assertEquals(2, hp.getSettlements().size(), "Should eventually place 2 settlements despite invalid inputs");
    }

    @Test
    void testRobberDiscard_UnderLimitAndZeroResource() {
        // Case 1: Less than 7 cards, should immediately return
        HumanPlayer hp1 = createPlayerWithInput("");
        for (int i = 0; i < 5; i++) hp1.addResource(ResourceType.LUMBER);
        hp1.robberDiscard();
        assertEquals(5, hp1.getHand().getCount(), "Should not discard if under limit");

        // Case 2: Greater than 7 cards, user tries to discard a valid type they don't own (BRICK), then corrects to LUMBER
        HumanPlayer hp2 = createPlayerWithInput("BRICK\nLUMBER\n");
        for (int i = 0; i < 8; i++) hp2.addResource(ResourceType.LUMBER);
        hp2.robberDiscard();
        assertEquals(7, hp2.getHand().getCount());
    }

    @Test
    void testMaxEntityLimitsReached() {
        HumanPlayer hp = createPlayerWithInput("build settlement 0\nbuild city 0\nbuild road 0,1\ngo\n");
        ComputerPlayer cp = new ComputerPlayer();
        Game game = new Game(List.of(hp, cp), new FixedDice(6), new Board(), 10, 20);

        // Pre-fill max limits
        for (int i = 0; i < Settlement.getMax(); i++) hp.addStructure(new Settlement());
        for (int i = 0; i < City.getMax(); i++) hp.addStructure(new City());
        for (int i = 0; i < Road.getMax(); i++) hp.addRoad(new Road());

        hp.takeTurn(game);

        // Assert sizes did not increase past the maximum
        assertEquals(Settlement.getMax(), hp.getSettlements().size());
        assertEquals(City.getMax(), hp.getCities().size());
        assertEquals(Road.getMax(), hp.getRoads().size());
    }

    @Test
    void testUndoRedoEmptyStacks() {
        // Tests the empty pop fallback
        HumanPlayer hp = createPlayerWithInput("undo\nredo\ngo\n");
        ComputerPlayer cp = new ComputerPlayer();
        Game game = new Game(List.of(hp, cp), new FixedDice(6), new Board(), 10, 20);
        assertDoesNotThrow(() -> hp.takeTurn(game));
    }

    @Test
    void testUndoRedoValidAction() {
        Board board = new Board();
        Node node0 = board.getNodes().get(0);
        Node node1 = board.getNodes().get(1);
        Road r = new Road();
        node0.setLeftRoad(r);
        node1.setRightRoad(r);

        // Build a settlement, undo it, redo it, then go.
        HumanPlayer hp = createPlayerWithInput("build settlement 1\nundo\nredo\ngo\n");
        hp.addResource(ResourceType.BRICK); hp.addResource(ResourceType.LUMBER);
        hp.addResource(ResourceType.WOOL); hp.addResource(ResourceType.GRAIN);
        r.setOwner(hp);

        ComputerPlayer cp = new ComputerPlayer();
        Game game = new Game(List.of(hp, cp), new FixedDice(6), board, 10, 1);

        hp.takeTurn(game);
        // If undo and redo worked, the settlement should still exist at the end
        assertEquals(1, hp.getSettlements().size());
    }
}