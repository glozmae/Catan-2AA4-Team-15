package TestPlayer;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import Player.PlayerHand;
import GameResources.ResourceType;

/**
 * JUnit tests for PlayerHand
 * 
 * @author Elizabeth Glozman, McMaster university
 * @version Winter 2026
 */
public class TestPlayerHand {

    /**
     * Checks that newly created PlayerHand has zero ards for all reousrces
     */
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

    /**
     * Checks that adding cards increases the count of the resource
     */
    @Test
    void testAddCardIncreasesCount() {
        PlayerHand hand = new PlayerHand();

        hand.addCard(ResourceType.BRICK, 2);

        assertEquals(2, hand.getCount(ResourceType.BRICK));
    }

    /**
     * Checks that adding same resource severa times properly adds the amount
     */
    @Test
    void testAddMultipleCards() {
        PlayerHand hand = new PlayerHand();

        hand.addCard(ResourceType.GRAIN, 1);
        hand.addCard(ResourceType.GRAIN, 3);

        assertEquals(4, hand.getCount(ResourceType.GRAIN));
    }

    /**
     * Check that removing cards decreases resource count
     */
    @Test
    void testRemoveCardReducesCount() {
        PlayerHand hand = new PlayerHand();

        hand.addCard(ResourceType.WOOL, 3);
        hand.removeCard(ResourceType.WOOL, 1);

        assertEquals(2, hand.getCount(ResourceType.WOOL));
    }

    /**
     * Check that removing number of cards results in 0
     */
    @Test
    void testRemoveExactAmount() {
        PlayerHand hand = new PlayerHand();

        hand.addCard(ResourceType.LUMBER, 2);
        hand.removeCard(ResourceType.LUMBER, 2);

        assertEquals(0, hand.getCount(ResourceType.LUMBER));
    }

    /**
     * Check that removing more cards that player has throws execption
     */
    @Test
    void testRemoveTooManyThrowsException() {
        PlayerHand hand = new PlayerHand();

        hand.addCard(ResourceType.ORE, 1);

        assertThrows(IllegalArgumentException.class, () -> {
            hand.removeCard(ResourceType.ORE, 2);
        });
    }

    /**
     * Check that removing resource not in players card throws an execption
     */
    @Test
    void testRemoveResourceNotInHandThrowsException() {
        PlayerHand hand = new PlayerHand();

        assertThrows(IllegalArgumentException.class, () -> {
            hand.removeCard(ResourceType.BRICK, 1);
        });
    }

    /**
     * Checks that requesting count of resource that has not been aded is 0
     */
    @Test
    void testGetCountForUnknownResourceReturnsZero() {
        PlayerHand hand = new PlayerHand();

        assertEquals(0, hand.getCount(ResourceType.BRICK));
    }
}