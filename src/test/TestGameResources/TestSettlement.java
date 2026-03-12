package TestGameResources;

import GameResources.Cost;
import GameResources.Settlement;
import Player.ComputerPlayer;
import Player.Player;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TestSettlement {

    private Settlement settlement;

    @BeforeEach
    void setUp() {
        Player.resetNumPlayers();

        settlement = new Settlement();
    }


    @Test
    @DisplayName("Settlement should cost 1 Brick, 1 Lumber, 1 Grain, 1 Wool, and 0 Ore")
    void testGetCost() {
        Cost settlementCost = settlement.getCost();

        assertEquals(1, settlementCost.getBrick(), "Settlement should require 1 Brick");
        assertEquals(1, settlementCost.getLumber(), "Settlement should require 1 Lumber");
        assertEquals(1, settlementCost.getGrain(), "Settlement should require 1 Grain");
        assertEquals(1, settlementCost.getWool(), "Settlement should require 1 Wool");
        assertEquals(0, settlementCost.getOre(), "Settlement should require 0 Ore");
    }

    @Test
    @DisplayName("Settlement should provide exactly 1 victory point")
    void testGetVictoryPoints() {
        assertEquals(1, settlement.getVictoryPoints(), "A settlement must be worth 1 VP");
    }

    @Test
    @DisplayName("Maximum number of allowable settlements should be 5")
    void testGetMax() {
        assertEquals(5, Settlement.getMax(), "The max settlement limit per player must be 5");
    }


    @Test
    @DisplayName("A newly instantiated Settlement should have no owner")
    void testInitialOwnerIsNull() {
        assertNull(settlement.getOwner(), "Owner should be null before assignment");
    }

    @Test
    @DisplayName("Setting a valid owner should assign the player to the settlement")
    void testSetOwnerValid() {
        Player testPlayer = new ComputerPlayer();
        settlement.setOwner(testPlayer);

        assertEquals(testPlayer, settlement.getOwner(), "The settlement's owner should match the assigned player");
    }

    @Test
    @DisplayName("Setting owner to null should throw IllegalArgumentException")
    void testSetOwnerNullThrowsException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            settlement.setOwner(null);
        });

        assertEquals("Owner cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Assigning an owner twice should throw IllegalStateException")
    void testSetOwnerTwiceThrowsException() {
        Player player1 = new ComputerPlayer();
        Player player2 = new ComputerPlayer();

        settlement.setOwner(player1);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            settlement.setOwner(player2);
        });

        assertEquals("Owner already assigned", exception.getMessage());
    }
}