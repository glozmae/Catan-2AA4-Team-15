package TestPlayer;

import static org.junit.jupiter.api.Assertions.*;

import Board.Tile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import Board.Node;
import Game.Game;
import GameResources.City;
import GameResources.ResourceType;
import GameResources.Road;
import GameResources.Settlement;
import Player.Player;

import java.util.List;

/**
 * JUnit tests for the Player abstract class, using a simple concrete test subclass.
* 
* @author Elizabeth Glozman, McMaster University 
* @version Winter 2026
*/
public class TestPlayer {

    /**
     * Simple concrete subclass used only for testing Player
     */
    private static class DummyPlayer extends Player {
        @Override
        public void takeTurn(Game game) {
        }

        @Override
        public void setup(Game game) {
        }

        @Override
        public void robberDiscard() {
            // no action needed
        }

        @Override
        public Tile setRobber(List<Tile> tiles) {
            return null;
        }
    }

    @BeforeEach
    void resetPlayerCount() {
        Player.resetNumPlayers();
    }

    /**
     * Check that IDs are assigned in increasing order
     */
    @Test
    void testPlayerConstructorAssignsId() {
        DummyPlayer p1 = new DummyPlayer();
        DummyPlayer p2 = new DummyPlayer();

        assertEquals(0, p1.getId());
        assertEquals(1, p2.getId());
    }

    /**
     * Check that every player gets a different, non-null colour
     */
    @Test
    void testPlayerConstructorAssignsColor() {
        DummyPlayer p1 = new DummyPlayer();
        DummyPlayer p2 = new DummyPlayer();

        assertNotNull(p1.getColor());
        assertNotNull(p2.getColor());
        assertNotEquals(p1.getColor(), p2.getColor());
    }

    /**
     * Chek that resources can be added and removed correctly from hand
     */
    @Test
    void testAddAndRemoveResource() {
        DummyPlayer p = new DummyPlayer();

        assertEquals(0, p.getResourceAmount(ResourceType.BRICK));

        p.addResource(ResourceType.BRICK);
        p.addResource(ResourceType.BRICK);

        assertEquals(2, p.getResourceAmount(ResourceType.BRICK));

        p.removeResource(ResourceType.BRICK);

        assertEquals(1, p.getResourceAmount(ResourceType.BRICK));
    }

    /**
     * Check that adding a settlement stores it in the list and assigns to correct owner
     */
    @Test
    void testAddSettlement() {
        DummyPlayer p = new DummyPlayer();
        Settlement settlement = new Settlement();

        p.addStructure(settlement);

        assertEquals(1, p.getSettlements().size());
        assertEquals(0, p.getCities().size());
        assertEquals(p, settlement.getOwner());
    }

    /**
     * Checks that adding a city stores it in the list and assigns to correct owner
     */
    @Test
    void testAddCity() {
        DummyPlayer p = new DummyPlayer();
        City city = new City();

        p.addStructure(city);

        assertEquals(1, p.getCities().size());
        assertEquals(0, p.getSettlements().size());
        assertEquals(p, city.getOwner());
    }

    /**
     * check that removing a structure deletes it from appropriate collection
     */
    @Test
    void testRemoveStructure() {
        DummyPlayer p = new DummyPlayer();
        Settlement settlement = new Settlement();

        p.addStructure(settlement);
        assertEquals(1, p.getSettlements().size());

        p.removeStructure(settlement);
        assertEquals(0, p.getSettlements().size());
    }

    /**
     * Checks that adding a road stores it in the list and assigns to correct owner
     */
    @Test
    void testAddRoad() {
        DummyPlayer p = new DummyPlayer();
        Road road = new Road();

        p.addRoad(road);

        assertEquals(1, p.getRoads().size());
        assertEquals(p, road.getOwner());
    }

    /**
     * Checks that hand is initialized and not null
     */
    @Test
    void testGetHandIsNotNull() {
        DummyPlayer p = new DummyPlayer();
        assertNotNull(p.getHand());
    }

    /**
     * Check that string representation of player has player number and colour
     */
    @Test
    void testToStringContainsPlayerNumber() {
        DummyPlayer p = new DummyPlayer();

        String text = p.toString();

        assertTrue(text.contains("Player 1"));
        assertTrue(text.contains("color"));
    }

    /**
     * Checks that creating more than maximum allowed players throws an exception
     */
    @Test
    void testMaxPlayersExceededThrowsException() {
        new DummyPlayer();
        new DummyPlayer();
        new DummyPlayer();
        new DummyPlayer();

        assertThrows(IllegalStateException.class, DummyPlayer::new);
    }

    /**
     * Checks that resetting static player counter restarts ID count from 0
     */
    @Test
    void testResetNumPlayersAllowsFreshCreation() {
        new DummyPlayer();
        new DummyPlayer();

        Player.resetNumPlayers();

        DummyPlayer fresh = new DummyPlayer();
        assertEquals(0, fresh.getId());
    }

    /**
     * Checks that longest road starts at road 0
     */
    @Test
    void testLongestRoadStartsAtZero() {
        DummyPlayer p = new DummyPlayer();
        assertEquals(0, p.getLongestRoad());
    }
}