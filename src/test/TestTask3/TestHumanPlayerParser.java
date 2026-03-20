package TestTask3;

import org.junit.jupiter.api.Test;

import Player.HumanPlayer;

import static org.junit.jupiter.api.Assertions.*;

class TestHumanPlayerParser {

    @Test
    void testRegexRollCommand_Valid() {
        assertTrue(HumanPlayer.rollCommand("roll"));
        assertTrue(HumanPlayer.rollCommand("RoLl"));
    }

    @Test
    void testRegexRollCommand_Invalid() {
        assertFalse(HumanPlayer.rollCommand("roll dice"));
        assertFalse(HumanPlayer.rollCommand(" roll"));
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
    void testRegexUndoCommand() {
        assertTrue(HumanPlayer.undoCommand("undo"));
        assertTrue(HumanPlayer.undoCommand("UNDO"));
        assertFalse(HumanPlayer.undoCommand("undo last"));
    }

    @Test
    void testRegexRedoCommand() {
        assertTrue(HumanPlayer.redoCommand("redo"));
        assertTrue(HumanPlayer.redoCommand("REDO"));
        assertFalse(HumanPlayer.redoCommand("redo action"));
    }

    @Test
    void testRegexBuildSettlement_Valid() {
        assertEquals(15, HumanPlayer.parseSettlementNodeId("build settlement 15"));
        assertEquals(0, HumanPlayer.parseSettlementNodeId("BUILD settlement 0"));
    }

    @Test
    void testRegexBuildSettlement_Invalid() {
        assertNull(HumanPlayer.parseSettlementNodeId("build settlement"));
        assertNull(HumanPlayer.parseSettlementNodeId("build settlement fifteen"));
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
        int[] nodes = HumanPlayer.parseRoadNodeIds("build road [ 5 , 6 ]");
        assertNotNull(nodes);
        assertEquals(5, nodes[0]);
        assertEquals(6, nodes[1]);
    }
}