package TestPlayer;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import GameResources.ResourceType;

/**
 * JUnit tests for PlayerHand.
 */
public class TestPlayerHand {

    @Test
    void testInitialCountsAreZero() {
        PlayerHand hand = new PlayerHand();

        for (ResourceType type : ResourceType.values()) {
            if (type != ResourceType.DESERT) {
                assertEquals(0, hand.getCount(type),
                        "Initial resource count should be zero");
            }
        }
    }

    @Test
    void testAddCardIncreasesCount() {
        PlayerHand hand = new PlayerHand();

        hand.addCard(ResourceType.BRICK, 2);

        assertEquals(2, hand.getCount(ResourceType.BRICK));
    }

    @Test
    void testAddMultipleCards() {
        PlayerHand hand = new PlayerHand();

        hand.addCard(ResourceType.GRAIN, 1);
        hand.addCard(ResourceType.GRAIN, 3);

        assertEquals(4, hand.getCount(ResourceType.GRAIN));
    }

    @Test
    void testRemoveCardReducesCount() {
        PlayerHand hand = new PlayerHand();

        hand.addCard(ResourceType.WOOL, 3);
        hand.removeCard(ResourceType.WOOL, 1);

        assertEquals(2, hand.getCount(ResourceType.WOOL));
    }

    @Test
    void testRemoveExactAmount() {
        PlayerHand hand = new PlayerHand();

        hand.addCard(ResourceType.LUMBER, 2);
        hand.removeCard(ResourceType.LUMBER, 2);

        assertEquals(0, hand.getCount(ResourceType.LUMBER));
    }

    @Test
    void testRemoveTooManyThrowsException() {
        PlayerHand hand = new PlayerHand();

        hand.addCard(ResourceType.ORE, 1);

        assertThrows(IllegalArgumentException.class, () -> {
            hand.removeCard(ResourceType.ORE, 2);
        });
    }

    @Test
    void testRemoveResourceNotInHandThrowsException() {
        PlayerHand hand = new PlayerHand();

        assertThrows(IllegalArgumentException.class, () -> {
            hand.removeCard(ResourceType.BRICK, 1);
        });
    }

    @Test
    void testGetCountForUnknownResourceReturnsZero() {
        PlayerHand hand = new PlayerHand();

        assertEquals(0, hand.getCount(ResourceType.BRICK));
    }
}