package TestGameResources;

import GameResources.Cost;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CostTest {

    @Test
    @DisplayName("Constructor should correctly assign positive values")
    void testValidCostCreation() {
        Cost cost = new Cost(1, 2, 3, 4, 5);

        assertEquals(1, cost.getBrick(), "Brick cost should be 1");
        assertEquals(2, cost.getLumber(), "Lumber cost should be 2");
        assertEquals(3, cost.getGrain(), "Grain cost should be 3");
        assertEquals(4, cost.getWool(), "Wool cost should be 4");
        assertEquals(5, cost.getOre(), "Ore cost should be 5");
    }

    @Test
    @DisplayName("Constructor should accept zero as a valid amount")
    void testCostWithZeros() {
        Cost cost = new Cost(0, 0, 0, 0, 0);

        assertEquals(0, cost.getBrick(), "Brick cost should be 0");
        assertEquals(0, cost.getLumber(), "Lumber cost should be 0");
        assertEquals(0, cost.getGrain(), "Grain cost should be 0");
        assertEquals(0, cost.getWool(), "Wool cost should be 0");
        assertEquals(0, cost.getOre(), "Ore cost should be 0");
    }


    @Test
    @DisplayName("Negative brick amount should throw IllegalArgumentException")
    void testNegativeBrickThrowsException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new Cost(-1, 0, 0, 0, 0);
        });

        assertEquals("brick must be greater then or equal to 0", exception.getMessage());
    }

    @Test
    @DisplayName("Negative lumber amount should throw IllegalArgumentException")
    void testNegativeLumberThrowsException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new Cost(0, -5, 0, 0, 0);
        });

        assertEquals("lumber must be greater then or equal to 0", exception.getMessage());
    }

    @Test
    @DisplayName("Negative grain amount should throw IllegalArgumentException")
    void testNegativeGrainThrowsException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new Cost(0, 0, -2, 0, 0);
        });

        assertEquals("grain must be greater then or equal to 0", exception.getMessage());
    }

    @Test
    @DisplayName("Negative wool amount should throw IllegalArgumentException")
    void testNegativeWoolThrowsException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new Cost(0, 0, 0, -10, 0);
        });

        assertEquals("wool must be greater then or equal to 0", exception.getMessage());
    }

    @Test
    @DisplayName("Negative ore amount should throw IllegalArgumentException")
    void testNegativeOreThrowsException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new Cost(0, 0, 0, 0, -1);
        });

        assertEquals("ore must be greater then or equal to 0", exception.getMessage());
    }
}