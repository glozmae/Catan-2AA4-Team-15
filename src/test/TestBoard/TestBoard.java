package TestBoard;

import Board.Board;
import Board.DiceNum;
import Board.Tile;
import Board.Node;
import Player.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

/**
 * Tests the Board class
 *
 * @author Yojith Sai Biradavolu, McMaster University
 * @version Winter, 2026
 */
public class TestBoard {
    private static final int TIMEOUT = 2000;

    @BeforeEach
    public void resetPlayers() {
        Player.resetNumPlayers();
    }

    /**
     * Check getTilesForRoll returns a matching DiceNum for each valid roll.
     */
    @Test()
    @Timeout(value = TIMEOUT)
    public void getTilesForRoll() {
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

    /**
     * Ensure getTiles returns a non-null list of size 19.
     */
    @Test
    @Timeout(value = TIMEOUT)
    public void getTiles() {
        Board board = new Board();
        List<Tile> tiles = board.getTiles();
        assertNotNull(tiles, "Tiles list should not be null");
        assertEquals(19, tiles.size(), "There should be 19 tiles on the board");

    }

    /**
     * Ensure getNodes returns a non-null list of size 54.
     */
    @Test
    @Timeout(value = TIMEOUT)
    public void getNodes() {
        Board board = new Board();
        List<Node> nodes = board.getNodes();
        assertNotNull(nodes, "Nodes list should not be null");
        assertEquals(54, nodes.size(), "There should be 54 nodes on the board");
    }
}