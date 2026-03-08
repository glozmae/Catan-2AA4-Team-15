package Test.GameResources;

import GameResources.Structure;
import Player.ComputerPlayer;
import Player.Player;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StructureTest {

    private Structure dummyStructure;

    @BeforeEach
    void setUp() {
        Player.resetNumPlayers();

        /**
         * DUMMY STRUCTURE CONCRETE CLASS
         */
        dummyStructure = new Structure() {
            @Override
            public int getVictoryPoints() {
                return 0;
            }
        };
    }

    @Test
    @DisplayName("A newly instantiated Structure should have a null owner")
    void testInitialOwnerIsNull() {
        assertNull(dummyStructure.getOwner(), "Owner should be null before assignment");
    }

    @Test
    @DisplayName("setOwner should successfully assign a valid Player")
    void testSetOwnerValid() {
        Player testPlayer = new ComputerPlayer();
        dummyStructure.setOwner(testPlayer);

        assertEquals(testPlayer, dummyStructure.getOwner(), "The structure's owner should match the assigned player");
    }

    @Test
    @DisplayName("setOwner should throw IllegalArgumentException when given null")
    void testSetOwnerNullThrowsException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            dummyStructure.setOwner(null);
        });

        assertEquals("Owner cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("setOwner should throw IllegalStateException if owner is already assigned")
    void testSetOwnerTwiceThrowsException() {
        Player player1 = new ComputerPlayer();
        Player player2 = new ComputerPlayer();

        dummyStructure.setOwner(player1);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            dummyStructure.setOwner(player2);
        });

        assertEquals("Owner already assigned", exception.getMessage());
    }
}