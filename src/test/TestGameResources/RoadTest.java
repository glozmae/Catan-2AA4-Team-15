package TestGameResources;

import GameResources.Cost;
import GameResources.Road;
import Player.ComputerPlayer;
import Player.Player;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RoadTest {

    private Road road;

    @BeforeEach
    void setUp() {
        Player.resetNumPlayers();

        road = new Road();
    }

    @Test
    @DisplayName("Road should cost 1 Brick, 1 Lumber, 0 Grain, 0 Wool, and 0 Ore")
    void testGetCost() {
        Cost roadCost = road.getCost();

        assertEquals(1, roadCost.getBrick(), "Road should require 1 Brick");
        assertEquals(1, roadCost.getLumber(), "Road should require 1 Lumber");
        assertEquals(0, roadCost.getGrain(), "Road should require 0 Grain");
        assertEquals(0, roadCost.getWool(), "Road should require 0 Wool");
        assertEquals(0, roadCost.getOre(), "Road should require 0 Ore");
    }

    @Test
    @DisplayName("Individual road should provide exactly 0 victory points")
    void testGetVictoryPoints() {
        assertEquals(0, road.getVictoryPoints(), "A single road is worth 0 VP");
    }

    @Test
    @DisplayName("Maximum number of allowable roads should be 15")
    void testGetMax() {
        assertEquals(15, Road.getMax(), "The max road limit per player must be 15");
    }


    @Test
    @DisplayName("A newly instantiated Road should have no owner")
    void testInitialOwnerIsNull() {
        assertNull(road.getOwner(), "Owner should be null before assignment");
    }

    @Test
    @DisplayName("Setting a valid owner should assign the player to the road")
    void testSetOwnerValid() {
        Player testPlayer = new ComputerPlayer();
        road.setOwner(testPlayer);

        assertEquals(testPlayer, road.getOwner(), "The road's owner should match the assigned player");
    }

    @Test
    @DisplayName("Setting owner to null should throw IllegalArgumentException")
    void testSetOwnerNullThrowsException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            road.setOwner(null);
        });

        assertEquals("Owner cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Assigning an owner twice should throw IllegalStateException")
    void testSetOwnerTwiceThrowsException() {
        Player player1 = new ComputerPlayer();
        Player player2 = new ComputerPlayer();

        road.setOwner(player1);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            road.setOwner(player2);
        });

        assertEquals("Owner already assigned", exception.getMessage());
    }
}