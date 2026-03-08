package test.Board;

import Board.DesertTile;
import Board.Node;
import Board.Tile;
import GameResources.ResourceType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestDesertTile {
    @Test
    public void testConstructor() {
        Node[] nodes = {new Node(0), new Node(1), new Node(2), new Node(3), new Node(4), new Node(5)};
        Tile tile = new DesertTile(0, nodes);
        assertEquals(ResourceType.DESERT, tile.getType(), "Tile should have nodes");
    }
}