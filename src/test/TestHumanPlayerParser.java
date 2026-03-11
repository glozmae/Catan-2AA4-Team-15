package Player;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class TestHumanPlayerParser {

    //Test commands like Roll and go
    @Test
    void testRoll() {
        assertTrue(HumanPlayer.rollCommand("roll"));
    }

    @Test
    void testGo() {
        assertTrue(HumanPlayer.goCommand("go"));
    }

    @Test
    void testRoad() {
        int[] result = HumanPlayer.parseRoadNodeIds("build road 3,4");
        assertNotNull(result);
        assertEquals(3, result[0]);
        assertEquals(4, result[1]);
    }

    //test Roads
    @Test
    void testRoadBrackets() {
        int[] result = HumanPlayer.parseRoadNodeIds("build road [8,9]");
        assertNotNull(result);
        assertEquals(8, result[0]);
        assertEquals(9, result[1]);
    }
}
