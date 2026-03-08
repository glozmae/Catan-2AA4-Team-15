package TestBoard;

import Board.Board;
import Board.SetupManager;
import GameResources.ResourceType;
import Player.Player;
import Player.ComputerPlayer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the SetupManager class
 * 
 * @author Yojith Sai Biradavolu, McMaster University
 * @version Winter, 2026
 */
public class TestSetupManager {
    private static final int TIMEOUT = 2000;

    @BeforeEach
    public void resetPlayers() {
        Player.resetNumPlayers();
    }
    /**
     * Running setup should yield two settlements, two roads, and some resources for the players
     */
    @Test
    @Timeout(value = TIMEOUT)
    public void run() {
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