package TestTask3;


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

class TestHumanPlayerParser {

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