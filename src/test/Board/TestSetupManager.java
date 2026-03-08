package test.Board;

import Board.Board;
import Board.SetupManager;
import GameResources.ResourceType;
import Player.Player;
import Player.ComputerPlayer;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class TestSetupManager {

    @Test
    public void run() {
        Player.resetNumPlayers();
        Player dummyPlayer = new ComputerPlayer();
        ArrayList<Player> players = new ArrayList<>();
        players.add(dummyPlayer);
        SetupManager setupManager = new SetupManager(new Board(), 0);

        setupManager.run(players);
        int totalResources = 0;
        for (ResourceType type : ResourceType.values()) {
            totalResources += dummyPlayer.getResourceAmount(type);
        }
        assertEquals(2, dummyPlayer.getSettlements().size(), "Player should have 2 settlements after setup");
        assertEquals(2, dummyPlayer.getRoads().size(), "Player should have 2 roads after setup");
        assertTrue(totalResources >= 1 && totalResources <= 3, "Player should have 1-3 total resources after setup");
    }
}