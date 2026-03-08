package Player;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import Board.Node;
import Game.Game;
import GameResources.City;
import GameResources.ResourceType;
import GameResources.Road;
import GameResources.Settlement;

/**
 * JUnit tests for the Player abstract class,
 * using a simple concrete test subclass.
 */
public class TestPlayer {

    /**
     * Simple concrete subclass used only for testing Player.
     */
    private static class DummyPlayer extends Player {
        @Override
        public void takeTurn(Game game) {
            // no-op for testing
        }

        @Override
        public void setup(Game game) {
            // no-op for testing
        }
    }

    @BeforeEach
    void resetPlayerCount() {
        Player.resetNumPlayers();
    }

    @Test
    void testPlayerConstructorAssignsId() {
        DummyPlayer p1 = new DummyPlayer();
        DummyPlayer p2 = new DummyPlayer();

        assertEquals(0, p1.getId());
        assertEquals(1, p2.getId());
    }

    @Test
    void testPlayerConstructorAssignsColor() {
        DummyPlayer p1 = new DummyPlayer();
        DummyPlayer p2 = new DummyPlayer();

        assertNotNull(p1.getColor());
        assertNotNull(p2.getColor());
        assertNotEquals(p1.getColor(), p2.getColor());
    }

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

    @Test
    void testAddSettlement() {
        DummyPlayer p = new DummyPlayer();
        Settlement settlement = new Settlement();

        p.addStructure(settlement);

        assertEquals(1, p.getSettlements().size());
        assertEquals(0, p.getCities().size());
        assertEquals(p, settlement.getOwner());
    }

    @Test
    void testAddCity() {
        DummyPlayer p = new DummyPlayer();
        City city = new City();

        p.addStructure(city);

        assertEquals(1, p.getCities().size());
        assertEquals(0, p.getSettlements().size());
        assertEquals(p, city.getOwner());
    }

    @Test
    void testRemoveStructure() {
        DummyPlayer p = new DummyPlayer();
        Settlement settlement = new Settlement();

        p.addStructure(settlement);
        assertEquals(1, p.getSettlements().size());

        p.removeStructure(settlement);
        assertEquals(0, p.getSettlements().size());
    }

    @Test
    void testAddRoad() {
        DummyPlayer p = new DummyPlayer();
        Road road = new Road();

        p.addRoad(road);

        assertEquals(1, p.getRoads().size());
        assertEquals(p, road.getOwner());
    }

    @Test
    void testGetHandIsNotNull() {
        DummyPlayer p = new DummyPlayer();
        assertNotNull(p.getHand());
    }

    @Test
    void testToStringContainsPlayerNumber() {
        DummyPlayer p = new DummyPlayer();

        String text = p.toString();

        assertTrue(text.contains("Player 1"));
        assertTrue(text.contains("color"));
    }

    @Test
    void testMaxPlayersExceededThrowsException() {
        new DummyPlayer();
        new DummyPlayer();
        new DummyPlayer();
        new DummyPlayer();

        assertThrows(IllegalStateException.class, DummyPlayer::new);
    }

    @Test
    void testResetNumPlayersAllowsFreshCreation() {
        new DummyPlayer();
        new DummyPlayer();

        Player.resetNumPlayers();

        DummyPlayer fresh = new DummyPlayer();
        assertEquals(0, fresh.getId());
    }

    @Test
    void testAddNodeDoesNotThrow() {
        DummyPlayer p = new DummyPlayer();
        Node node = new Node(5);

        assertDoesNotThrow(() -> p.addNode(node));
    }

    @Test
    void testLongestRoadStartsAtZero() {
        DummyPlayer p = new DummyPlayer();
        assertEquals(0, p.getLongestRoad());
    }
}