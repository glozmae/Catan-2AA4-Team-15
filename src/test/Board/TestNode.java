package test.Board;

import Board.Node;
import GameResources.Road;
import Player.Player;
import Player.ComputerPlayer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestNode {

    @Test
    public void canBuildSettlement() {
        Node node = new Node(0);
        Player.resetNumPlayers();
        Player dummyPlayer = new ComputerPlayer();
        Player dummyPlayer2 = new ComputerPlayer();
        Road dummyRoad = new Road();
        dummyRoad.setOwner(dummyPlayer);

        assertFalse(node.canBuildSettlement(dummyPlayer), "Node should not be buildable by player without connecting road");
        node.setLeftRoad(dummyRoad);
        assertTrue(node.canBuildSettlement(dummyPlayer), "Node should be buildable by player");
        node.setPlayer(dummyPlayer);
        assertFalse(node.canBuildSettlement(dummyPlayer2), "Occupied node should not be buildable by player");
    }

    @Test
    void canBuildSettlement2() {
        Node node = new Node(0);
        Node node2 = new Node(1);
        node.setLeft(node2);
        Player.resetNumPlayers();
        Player dummyPlayer = new ComputerPlayer();
        node2.setPlayer(dummyPlayer);

        assertFalse(node.canBuildSettlement(dummyPlayer), "Node should not be buildable if neighbor is occupied");
    }

    @Test
    public void getBuildableRoadNeighbors() {
        Node node = new Node(0);
        node.setLeft(new Node(1));
        node.setRight(new Node(2));
        node.setVert(new Node(3));
        Player.resetNumPlayers();
        Player dummyPlayer = new ComputerPlayer();

        assertEquals(0, node.getBuildableRoadNeighbors(dummyPlayer).size(), "Unoccupied node has no buildable roads");
        node.setPlayer(dummyPlayer);
        assertEquals(3, node.getBuildableRoadNeighbors(dummyPlayer).size(), "Occupied node has 3 buildable roads");
        node.setLeftRoad(new Road());
        assertEquals(2, node.getBuildableRoadNeighbors(dummyPlayer).size(), "Occupied node with left road has 2 buildable roads");
        node.setRightRoad(new Road());
        assertEquals(1, node.getBuildableRoadNeighbors(dummyPlayer).size(), "Occupied node with right road has 1 buildable road");
        node.setVertRoad(new Road());
        assertEquals(0, node.getBuildableRoadNeighbors(dummyPlayer).size(), "Occupied node with vert road has 0 buildable roads");
    }
}