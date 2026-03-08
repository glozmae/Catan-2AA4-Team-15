package test.Board;

import Board.DiceNum;
import Board.Node;
import Board.Tile;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestDiceNum {

    @Test
    public void testConstructor() {
        Node[] dummyNodes = {new Node(0), new Node(1), new Node(2), new Node(3), new Node(4), new Node(5)};
        Tile tile = new Tile(0, dummyNodes, null);
        DiceNum diceNum = new DiceNum(1);
        assertEquals(1, diceNum.getNumber(), "DiceNum should have number");
        assertEquals(0, diceNum.getTiles().size(), "DiceNum should have 0 tiles");
        diceNum.addTile(tile);
        assertEquals(1, diceNum.getTiles().size(), "DiceNum should have 0 tiles");
        assertEquals(tile, diceNum.getTiles().getFirst(), "DiceNum should have 0 tiles");
    }
}