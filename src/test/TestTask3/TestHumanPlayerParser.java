package TestTask3;

import Player.Player;
import Player.ComputerPlayer;
import Player.HumanPlayer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import Board.Board;
import Board.Tile;
import Game.Game;
import Game.Dice;
import GameResources.ResourceType;

import static org.junit.jupiter.api.Assertions.*;

class TestHumanPlayer {

    private final InputStream standardIn = System.in;

    /**
     * A simple fixed dice stub for testing games without randomness.
     */
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
        // Restore standard System.in after every test to prevent breaking the console
        System.setIn(standardIn);
    }

    /**
     * Helper method to simulate user typing into the console.
     * MUST be called BEFORE creating the HumanPlayer object so the Scanner grabs the fake input stream.
     */
    private HumanPlayer createPlayerWithInput(String data) {
        ByteArrayInputStream testIn = new ByteArrayInputStream(data.getBytes());
        System.setIn(testIn);
        return new HumanPlayer();
    }

    @Test
    void testTakeTurn_ListAndGo() {
        // Simulate user typing "list" to check inventory, then "go" to end turn
        HumanPlayer hp = createPlayerWithInput("list\ngo\n");
        ComputerPlayer cp = new ComputerPlayer();
        Game game = new Game(List.of(hp, cp), new FixedDice(6), new Board(), 10, 20);

        // If the method completes without getting stuck in an infinite loop, it passes
        assertDoesNotThrow(() -> hp.takeTurn(game));
    }

    @Test
    void testTakeTurn_RollCommandIntercept() {
        // Simulate user instinctively typing "roll", then "go"
        HumanPlayer hp = createPlayerWithInput("roll\ngo\n");
        ComputerPlayer cp = new ComputerPlayer();
        Game game = new Game(List.of(hp, cp), new FixedDice(8), new Board(), 10, 20);

        assertDoesNotThrow(() -> hp.takeTurn(game));
    }

    @Test
    void testSetup() {
        // Simulate placing Settlement at Node 0, Road to Node 1
        // Then Settlement at Node 3, Road to Node 4
        HumanPlayer hp = createPlayerWithInput("0\n1\n3\n4\n");
        ComputerPlayer cp = new ComputerPlayer();
        Game game = new Game(List.of(hp, cp), new FixedDice(6), new Board(), 10, 20);

        hp.setup(game);

        assertEquals(2, hp.getSettlements().size(), "Player should have placed exactly 2 setup settlements.");
        assertEquals(2, hp.getRoads().size(), "Player should have placed exactly 2 setup roads.");
    }

    @Test
    void testRobberDiscard() {
        // Simulate the user typing an invalid string first, then correctly typing "BRICK"
        HumanPlayer hp = createPlayerWithInput("INVALID_TEXT\nBRICK\n");

        // Give the player 8 Bricks (triggers the > 7 rule)
        for (int i = 0; i < 8; i++) {
            hp.addResource(ResourceType.BRICK);
        }

        assertEquals(8, hp.getHand().getCount(), "Hand should start with 8 cards.");

        hp.robberDiscard();

        // Hand should be successfully reduced to 7
        assertEquals(7, hp.getHand().getCount(), "Hand should be reduced to 7 cards after discarding.");
        assertEquals(7, hp.getResourceAmount(ResourceType.BRICK), "Remaining cards should all be BRICK.");
    }

    @Test
    void testSetRobber() {
        // Since setRobber is now fully random (Requirement R2.5), we don't need console input
        HumanPlayer hp = createPlayerWithInput("");
        Board board = new Board();
        List<Tile> tiles = board.getTiles();

        Tile chosenTile = hp.setRobber(tiles);

        assertNotNull(chosenTile, "The randomly selected tile should not be null.");
        assertTrue(tiles.contains(chosenTile), "The selected tile must exist on the board.");
    }

    // ==========================================
    // TASK 3.3: REGEX PARSER TESTS (10 Tests)
    // ==========================================

    @Test
    void testRegexRollCommand_Valid() {
        assertTrue(HumanPlayer.rollCommand("roll"));
        assertTrue(HumanPlayer.rollCommand("RoLl")); // Case insensitive
    }

    @Test
    void testRegexRollCommand_Invalid() {
        assertFalse(HumanPlayer.rollCommand("roll dice"));
        assertFalse(HumanPlayer.rollCommand(" roll")); // Leading spaces should be trimmed before parsing
    }

    @Test
    void testRegexGoCommand() {
        assertTrue(HumanPlayer.goCommand("go"));
        assertTrue(HumanPlayer.goCommand("GO"));
        assertFalse(HumanPlayer.goCommand("go next"));
    }

    @Test
    void testRegexListCommand() {
        assertTrue(HumanPlayer.listCommand("list"));
        assertFalse(HumanPlayer.listCommand("list hand"));
    }

    @Test
    void testRegexBuildSettlement_Valid() {
        assertEquals(15, HumanPlayer.parseSettlementNodeId("build settlement 15"));
        assertEquals(0, HumanPlayer.parseSettlementNodeId("BUILD settlement 0"));
    }

    @Test
    void testRegexBuildSettlement_Invalid() {
        assertNull(HumanPlayer.parseSettlementNodeId("build settlement")); // Missing ID
        assertNull(HumanPlayer.parseSettlementNodeId("build settlement fifteen")); // Not a number
    }

    @Test
    void testRegexBuildCity_Valid() {
        assertEquals(42, HumanPlayer.parseCityNodeId("build city 42"));
        assertEquals(5, HumanPlayer.parseCityNodeId("build CITY 5"));
    }

    @Test
    void testRegexBuildCity_Invalid() {
        assertNull(HumanPlayer.parseCityNodeId("build a city at 42"));
    }

    @Test
    void testRegexBuildRoad_StandardSyntax() {
        int[] nodes = HumanPlayer.parseRoadNodeIds("build road 12,13");
        assertNotNull(nodes);
        assertEquals(12, nodes[0]);
        assertEquals(13, nodes[1]);
    }

    @Test
    void testRegexBuildRoad_BracketSyntaxAndSpaces() {
        // Tests the optional brackets and messy spacing handled by the Regex
        int[] nodes = HumanPlayer.parseRoadNodeIds("build road [ 5 , 6 ]");
        assertNotNull(nodes);
        assertEquals(5, nodes[0]);
        assertEquals(6, nodes[1]);
    }

}