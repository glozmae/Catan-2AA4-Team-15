package Board;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.util.List;

class TestBoard {

    @Test
    void getTilesForRoll() {
        Board board = new Board();
        for (int i = 2; i < 6; i++) {
            DiceNum diceNum = board.getTilesForRoll(i);
            assertNotNull(diceNum, "DiceNum should not be null for " + i);
            assertEquals(i, diceNum.getNumber(), "Value of the diceNums in the Board");
        }
        for (int i = 8; i <= 12; i++) {
            DiceNum diceNum = board.getTilesForRoll(i);
            assertNotNull(diceNum, "DiceNum should not be null for " + i);
            assertEquals(i, diceNum.getNumber(), "Value of the diceNums in the Board");
        }
    }

    @Test
    void getTiles() {
        Board board = new Board();
        List<Tile> tiles = board.getTiles();
        assertNotNull(tiles, "Tiles list should not be null");
        assertEquals(19, tiles.size(), "There should be 19 tiles on the board");

    }

    @Test
    void getNodes() {

    }
}
