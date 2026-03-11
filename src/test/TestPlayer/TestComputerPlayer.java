package TestPlayer;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import Board.Board;
import Board.Node;
import Board.Tile;
import Game.Dice;
import Game.Game;
import GameResources.City;
import GameResources.ResourceType;
import GameResources.Settlement;
import Player.ComputerPlayer;
import Player.Player;

/**
 * JUnit 5 tests for ComputerPlayer
 * 
 * @author Elizabeth Glozman, McMaster University 
 * @version Winter, 2026
 * 
 */
public class TestComputerPlayer {

    /**
     * Simple fixed dice stub for testing
     */
    private static class FixedDice implements Dice {
        private final int value;

        /**
         * Constructs fixed dice object
         * 
         * @param value value that is always returned
         */
        FixedDice(int value) {
            this.value = value;
        }

         /**
         * Returns the fixed dice value
         *
         * @return the predetermined dice roll value
         */
        @Override
        public int roll() {
            return value;
        }
    }

    /**
     * Reset the static player counter before each test
     */
    @BeforeEach
    void resetPlayerCount() {
        Player.resetNumPlayers();
    }

    /**
     * Checks that default constructor creates non-null player and assigns initial ID
     */
    @Test
    void testDefaultConstructorCreatesComputerPlayer() {
        ComputerPlayer player = new ComputerPlayer();
        assertNotNull(player);
        assertEquals(0, player.getId());
    }

    /**
     * Checks that seeded constructor creates non-null player and assigns initial ID
     */
    @Test
    void testSeededConstructorCreatesComputerPlayer() {
        ComputerPlayer player = new ComputerPlayer(42);
        assertNotNull(player);
        assertEquals(0, player.getId());
    }

    /**
     * Checks that setup places are one settlement and one road
     */
    @Test
    void testSetupPlacesTwoSettlementsAndTwoRoads() {
        ComputerPlayer ai = new ComputerPlayer(7);
        ComputerPlayer other = new ComputerPlayer(99);

        Game game = new Game(
                java.util.List.of(ai, other),
                new FixedDice(6),
                new Board(),
                10,
                20
        );

        ai.setup(game);

        int ownedNodes = 0;

        for (Node node : game.getBoard().getNodes()) {
            if (node.getPlayer() == ai) {
                ownedNodes++;
            }
        }

        // Updated assertions from 1 to 2!
        assertEquals(2, ownedNodes, "Setup should place exactly two settlement nodes");
        assertEquals(2, ai.getSettlements().size(), "Player should have exactly two settlements after setup");
        assertEquals(2, ai.getRoads().size(), "Player should have exactly two roads after setup");
    }

    /**
     * Check that setup is deterministic if the same seed is used
     * 2 players with the same seed on the same board should have the same setup nodes
     */
    @Test
    void testSeededSetupIsDeterministic() {
        ComputerPlayer ai1 = new ComputerPlayer(123);
        ComputerPlayer other1 = new ComputerPlayer(999);

        Game game1 = new Game(
                java.util.List.of(ai1, other1),
                new FixedDice(6),
                new Board(),
                10,
                20
        );

        ai1.setup(game1);
        int firstChosenNode = findOwnedNodeId(game1, ai1);

        Player.resetNumPlayers();

        ComputerPlayer ai2 = new ComputerPlayer(123);
        ComputerPlayer other2 = new ComputerPlayer(555);

        Game game2 = new Game(
                java.util.List.of(ai2, other2),
                new FixedDice(6),
                new Board(),
                10,
                20
        );

        ai2.setup(game2);
        int secondChosenNode = findOwnedNodeId(game2, ai2);

        assertEquals(firstChosenNode, secondChosenNode,
                "Same seed on the same board should choose the same setup node");
    }

    /**
     * Checks that player does nothing on its turn when it has no resources
     */
    @Test
    void testTakeTurnDoesNothingWithoutResources() {
        ComputerPlayer ai = new ComputerPlayer(5);
        ComputerPlayer other = new ComputerPlayer(8);

        Game game = new Game(
                java.util.List.of(ai, other),
                new FixedDice(6),
                new Board(),
                10,
                20
        );

        int settlementsBefore = ai.getSettlements().size();
        int roadsBefore = ai.getRoads().size();
        int citiesBefore = ai.getCities().size();

        ai.takeTurn(game);

        assertEquals(settlementsBefore, ai.getSettlements().size(),
                "No settlement should be built without resources");
        assertEquals(roadsBefore, ai.getRoads().size(),
                "No road should be built without resources");
        assertEquals(citiesBefore, ai.getCities().size(),
                "No city should be built without resources");
    }

    /**
     * player can only build a road when it is affordable
     */
    @Test
    void testTakeTurnBuildsRoadWhenRoadIsOnlyAffordableMove() {
        ComputerPlayer ai = new ComputerPlayer(11);
        ComputerPlayer other = new ComputerPlayer(22);

        Game game = new Game(
                java.util.List.of(ai, other),
                new FixedDice(6),
                new Board(),
                10,
                20
        );

        ai.setup(game);

        int roadsBefore = ai.getRoads().size();

        ai.addResource(ResourceType.BRICK);
        ai.addResource(ResourceType.LUMBER);

        ai.takeTurn(game);

        assertEquals(roadsBefore + 1, ai.getRoads().size(),
                "AI should build one road when road is the only affordable move");
        assertEquals(0, ai.getResourceAmount(ResourceType.BRICK),
                "Brick should be spent after building a road");
        assertEquals(0, ai.getResourceAmount(ResourceType.LUMBER),
                "Lumber should be spent after building a road");
    }

    /**
     * Checks that player can upgrade to a city
     */
    @Test
    void testTakeTurnCanUpgradeSettlementToCity() {
    ComputerPlayer ai = new ComputerPlayer(17);
    ComputerPlayer other = new ComputerPlayer(18);

    Game game = new Game(
            java.util.List.of(ai, other),
            new FixedDice(6),
            new Board(),
            10,
            20
    );

    ai.setup(game);

    Node ownedNode = findOwnedNode(game, ai);
    assertNotNull(ownedNode);
    assertTrue(ownedNode.getStructure() instanceof Settlement);

    ai.addResource(ResourceType.GRAIN);
    ai.addResource(ResourceType.GRAIN);
    ai.addResource(ResourceType.ORE);
    ai.addResource(ResourceType.ORE);
    ai.addResource(ResourceType.ORE);

    boolean upgraded = false;

    for (int i = 0; i < 20; i++) {
        ai.takeTurn(game);
        if (ownedNode.getStructure() instanceof City) {
            upgraded = true;
            break;
        }
    }

    assertTrue(upgraded, "AI should eventually upgrade settlement to city");
}
    /**
     * Finds the node owned by the given player
     *
     * @param game current game
     * @param player player whose node is being searched for
     * @return owned node, or null if none exists
     */
    private Node findOwnedNode(Game game, Player player) {
        for (Node node : game.getBoard().getNodes()) {
            if (node.getPlayer() == player) {
                return node;
            }
        }
        return null;
    }

    /**
     * Finds the integer node id of the node owned by the given player
     *
     * @param game current game
     * @param player player whose owned node id is needed
     * @return node id, or -1 if no owned node exists
     */
    private int findOwnedNodeId(Game game, Player player) {
        Node owned = findOwnedNode(game, player);
        if (owned == null) {
            return -1;
        }
        return Integer.parseInt(owned.toString());
    }

    @Test
    void testTakeTurnBuildsSettlement() {
        ComputerPlayer ai = new ComputerPlayer(100);
        ComputerPlayer other = new ComputerPlayer(101);

        Game game = new Game(
                java.util.List.of(ai, other),
                new FixedDice(6),
                new Board(),
                10,
                20
        );

        // Run setup so the AI has a starting Settlement and Road on the board
        ai.setup(game);
        int initialSettlements = ai.getSettlements().size();

        // Give the AI a massive stockpile of resources
        // It needs to build at least 1 more road before a settlement spot becomes mathematically legal
        for (int i = 0; i < 10; i++) {
            ai.addResource(ResourceType.BRICK);
            ai.addResource(ResourceType.LUMBER);
            ai.addResource(ResourceType.WOOL);
            ai.addResource(ResourceType.GRAIN);
        }

        boolean settlementBuilt = false;

        // Give the AI plenty of turns to randomly choose road building
        // until a legal spot opens, and then randomly choose settlement building.
        for (int i = 0; i < 30; i++) {
            ai.takeTurn(game);

            if (ai.getSettlements().size() > initialSettlements) {
                settlementBuilt = true;
                break;
            }
        }

        assertTrue(settlementBuilt, "AI should eventually build a settlement after expanding its roads.");
    }

    @Test
    void testSetRobberSelectsValidTile() {
        // FORCE the player count to reset so we don't accidentally hit the 4-player limit
        Player.resetNumPlayers();

        ComputerPlayer ai = new ComputerPlayer(99);
        Board board = new Board(); // Generate a standard board

        // Ask the AI to pick a tile for the robber
        Tile chosenTile = ai.setRobber(board.getTiles());

        // Verify the choice is valid
        assertNotNull(chosenTile, "AI should return a valid tile, not null");
        assertTrue(board.getTiles().contains(chosenTile), "The chosen tile must be from the provided list");
    }

    @Test
    void testRobberDiscardReducesHandToSeven() {
        // Create a player (using a seed for consistent testing)
        ComputerPlayer ai = new ComputerPlayer(50);

        // 1. Give the player exactly 8 total cards to force the hand size over 7.
        // We spread them out across different resource types so that the
        // randomizer inside robberDiscard() easily finds cards to discard.
        ai.addResource(ResourceType.LUMBER);
        ai.addResource(ResourceType.LUMBER);
        ai.addResource(ResourceType.BRICK);
        ai.addResource(ResourceType.BRICK);
        ai.addResource(ResourceType.WOOL);
        ai.addResource(ResourceType.WOOL);
        ai.addResource(ResourceType.GRAIN);
        ai.addResource(ResourceType.GRAIN);

        // 2. Pre-condition check: Ensure the hand actually holds 8 cards.
        // Because you fixed PlayerHand.getCount(), this will now pass perfectly!
        assertEquals(8, ai.getHand().getCount(), "Pre-condition: Hand must hold exactly 8 cards before discard.");

        // 3. Trigger the method we actually want to test
        ai.robberDiscard();

        // 4. Post-condition check: The while loop should have stopped exactly at 7.
        assertEquals(7, ai.getHand().getCount(), "Post-condition: Hand should be reduced to exactly 7 cards.");
    }
}