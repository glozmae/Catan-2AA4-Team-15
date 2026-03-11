package TestPlayer;

import static org.junit.jupiter.api.Assertions.*;

import Board.Tile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import Game.Game;
import GameResources.City;
import GameResources.Settlement;
import Player.Player;
import Player.VictoryPointCalculator;

import java.util.List;

/**
 * JUnit tests for VictoryPointCalculator.
 */
public class TestVictoryPointCalculator {

    /**
     * Dummy subclass because Player is abstract.
     */
    private static class DummyPlayer extends Player {

        @Override
        public void takeTurn(Game game) {
            // not required
        }

        @Override
        public void setup(Game game) {
            // not required
        }

        @Override
        public void robberDiscard() {
            // not required
        }

        @Override
        public Tile setRobber(List<Tile> tiles) {
            return null;
        }
    }

    @BeforeEach
    void resetPlayers() {
        Player.resetNumPlayers();
    }

    @Test
    void testNoStructuresGivesZeroPoints() {
        DummyPlayer player = new DummyPlayer();

        int points = VictoryPointCalculator.calculate(player);

        assertEquals(0, points);
    }

    @Test
    void testSettlementGivesOnePoint() {
        DummyPlayer player = new DummyPlayer();

        player.addStructure(new Settlement());

        int points = VictoryPointCalculator.calculate(player);

        assertEquals(1, points);
    }

    @Test
    void testCityGivesTwoPoints() {
        DummyPlayer player = new DummyPlayer();

        player.addStructure(new City());

        int points = VictoryPointCalculator.calculate(player);

        assertEquals(2, points);
    }

    @Test
    void testMixedStructures() {
        DummyPlayer player = new DummyPlayer();

        player.addStructure(new Settlement());
        player.addStructure(new Settlement());
        player.addStructure(new City());

        int points = VictoryPointCalculator.calculate(player);

        assertEquals(4, points); // 1 + 1 + 2
    }

    @Test
    void testMultipleCities() {
        DummyPlayer player = new DummyPlayer();

        player.addStructure(new City());
        player.addStructure(new City());

        int points = VictoryPointCalculator.calculate(player);

        assertEquals(4, points);
    }
}