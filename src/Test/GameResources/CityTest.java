package Test.GameResources;

import GameResources.City;
import GameResources.Cost;
import Player.ComputerPlayer;
import Player.Player;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CityTest {

    private City city;

    @BeforeEach
    void setUp() {
        Player.resetNumPlayers();

        city = new City();
    }

    @Test
    @DisplayName("City should cost 0 Brick, 0 Lumber, 2 Grain, 0 Wool, and 3 Ore")
    void testGetCost() {
        Cost cityCost = city.getCost();

        assertEquals(0, cityCost.getBrick(), "City should require 0 Brick");
        assertEquals(0, cityCost.getLumber(), "City should require 0 Lumber");
        assertEquals(2, cityCost.getGrain(), "City should require 2 Grain");
        assertEquals(0, cityCost.getWool(), "City should require 0 Wool");
        assertEquals(3, cityCost.getOre(), "City should require 3 Ore");
    }

    @Test
    @DisplayName("City should provide exactly 2 victory points")
    void testGetVictoryPoints() {
        assertEquals(2, city.getVictoryPoints(), "A city must be worth 2 VP");
    }

    @Test
    @DisplayName("Maximum number of allowable cities should be 4")
    void testGetMax() {
        assertEquals(4, City.getMax(), "The max city limit per player must be 4");
    }

    @Test
    @DisplayName("A newly instantiated City should have no owner")
    void testInitialOwnerIsNull() {
        assertNull(city.getOwner(), "Owner should be null before assignment");
    }

    @Test
    @DisplayName("Setting a valid owner should assign the player to the city")
    void testSetOwnerValid() {
        Player testPlayer = new ComputerPlayer();
        city.setOwner(testPlayer);

        assertEquals(testPlayer, city.getOwner(), "The city's owner should match the assigned player");
    }

    @Test
    @DisplayName("Setting owner to null should throw IllegalArgumentException")
    void testSetOwnerNullThrowsException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            city.setOwner(null);
        });

        assertEquals("Owner cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Assigning an owner twice should throw IllegalStateException")
    void testSetOwnerTwiceThrowsException() {
        Player player1 = new ComputerPlayer();
        Player player2 = new ComputerPlayer();

        city.setOwner(player1);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            city.setOwner(player2);
        });

        assertEquals("Owner already assigned", exception.getMessage());
    }
}