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
 * JUnit 5 tests for ComputerPlayer.
 */
public class TestComputerPlayer {

    /**
     * Simple fixed dice stub for testing.
     */
    private static class FixedDice implements Dice {
        private final int value;

        FixedDice(int value) {
            this.value = value;
        }

        @Override
        public int roll() {
            return value;
        }
    }

    @BeforeEach
    void resetPlayerCount() {
        Player.resetNumPlayers();
    }

    @Test
    void testDefaultConstructorCreatesComputerPlayer() {
        ComputerPlayer player = new ComputerPlayer();
        assertNotNull(player);
        assertEquals(0, player.getId());
    }

    @Test
    void testSeededConstructorCreatesComputerPlayer() {
        ComputerPlayer player = new ComputerPlayer(42);
        assertNotNull(player);
        assertEquals(0, player.getId());
    }

    @Test
    void testSetupPlacesOneSettlementAndOneRoad() {
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
        Node ownedNode = null;

        for (Node node : game.getBoard().getNodes()) {
            if (node.getPlayer() == ai) {
                ownedNodes++;
                ownedNode = node;
            }
        }

        assertEquals(1, ownedNodes, "Setup should place exactly one settlement node");
        assertNotNull(ownedNode, "Computer player should own one node after setup");
        assertTrue(ownedNode.getStructure() instanceof Settlement,
                "Owned node should contain a Settlement after setup");

        assertEquals(1, ai.getSettlements().size(),
                "Player should have exactly one settlement after setup");
        assertEquals(1, ai.getRoads().size(),
                "Player should have exactly one road after setup");
    }

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
     * Finds the node owned by the given player.
     *
     * @param game the current game
     * @param player the player whose node is being searched for
     * @return the owned node, or null if none exists
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
     * Finds the integer node id of the node owned by the given player.
     *
     * Since Node does not expose getId(), we use toString(), which returns the node id as a string.
     *
     * @param game the current game
     * @param player the player whose owned node id is needed
     * @return the node id, or -1 if no owned node exists
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

        // Give the AI exactly enough resources to build ONE settlement
        ai.addResource(ResourceType.BRICK);
        ai.addResource(ResourceType.LUMBER);
        ai.addResource(ResourceType.WOOL);
        ai.addResource(ResourceType.GRAIN);

        boolean settlementBuilt = false;

        // Give the AI a few turns to execute the move.
        // Since it also has resources for a road, it might randomly pick road first,
        // so we replenish resources if it makes the "wrong" random choice.
        for (int i = 0; i < 20; i++) {
            ai.takeTurn(game);

            if (ai.getSettlements().size() > initialSettlements) {
                settlementBuilt = true;
                break;
            }

            // If it spent resources on a road instead, give them back for the next attempt
            if (ai.getResourceAmount(ResourceType.GRAIN) < 1) {
                ai.addResource(ResourceType.BRICK);
                ai.addResource(ResourceType.LUMBER);
                ai.addResource(ResourceType.WOOL);
                ai.addResource(ResourceType.GRAIN);
            }
        }

        assertTrue(settlementBuilt, "AI should eventually build a settlement when it has resources and a valid spot at the end of its road.");
    }

    @Test
    void testRobberDiscardReducesHandToSeven() {
        ComputerPlayer ai = new ComputerPlayer(50);

        // Give the player 10 identical resources (simulating a hand > 7)
        for (int i = 0; i < 10; i++) {
            ai.addResource(ResourceType.LUMBER);
        }

        // Verify initial hand size
        assertEquals(10, ai.getHand().getCount(), "Hand should start with 10 cards");

        // Trigger the discard logic
        ai.robberDiscard();

        // Verify the hand was correctly reduced
        assertEquals(7, ai.getHand().getCount(), "Hand should be reduced to exactly 7 cards");
    }

    @Test
    void testSetRobberSelectsValidTile() {
        ComputerPlayer ai = new ComputerPlayer(99);
        Board board = new Board(); // Generate a standard board

        // Ask the AI to pick a tile for the robber
        Tile chosenTile = ai.setRobber(board.getTiles());

        // Verify the choice is valid
        assertNotNull(chosenTile, "AI should return a valid tile, not null");
        assertTrue(board.getTiles().contains(chosenTile), "The chosen tile must be from the provided list");
    }
}