package Player;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

/**
 * Test cases for Human Player parser.
 * @author Parnia Yazdinia, 400567795, McMaster University
 */
public class TestHumanPlayerParser {

    //Test cases that parser accepts the commands
    @Test
    void testRoll() {
        assertTrue(HumanPlayer.rollCommand("roll"));
    }

    @Test
    void testGo() {
        assertTrue(HumanPlayer.goCommand("go"));
    }

    @Test
    void testList() {
        assertTrue(HumanPlayer.listCommand("list"));
    }

    @Test
    void testSettlement() {
        assertEquals(12, HumanPlayer.parseSettlementNodeId("build settlement 12"));
    }

    @Test
    void testCity() {
        assertEquals(5, HumanPlayer.parseCityNodeId("build city 5"));
    }

    /**
     * Test that a simple road command returns the correct two node ids.
     */
    @Test
    void testRoad() {
        int[] result = HumanPlayer.parseRoadNodeIds("build road 3,4");
        assertNotNull(result);
        assertEquals(3, result[0]);
        assertEquals(4, result[1]);
    }

    /**
     * Test that a road command with brackets is also accepted.
     */
    @Test
    void testRoadBrackets() {
        int[] result = HumanPlayer.parseRoadNodeIds("build road [8,9]");
        assertNotNull(result);
        assertEquals(8, result[0]);
        assertEquals(9, result[1]);
    }

    //Test cases for invalid settlement, city and road command
    @Test
    void testBadSettlement() {
        assertNull(HumanPlayer.parseSettlementNodeId("build settlement x"));
    }

    @Test
    void testBadCity() {
        assertNull(HumanPlayer.parseCityNodeId("build city"));
    }

    @Test
    void testBadRoad() {
        assertNull(HumanPlayer.parseRoadNodeIds("build road 7"));
    }
}


