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
import GameResources.Road;
import GameResources.Settlement;
import Player.ComputerPlayer;
import Player.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

/**
 * JUnit 5 tests for ComputerPlayer
 * Ensures full coverage of AI decision branches, setup logic, and edge cases.
 */
public class TestComputerPlayer {

    /** Simple fixed dice stub for testing */
    private static class FixedDice implements Dice {
        private final int value;
        FixedDice(int value) { this.value = value; }
        @Override public int roll() { return value; }
    }

    @BeforeEach
    void resetPlayerCount() {
        Player.resetNumPlayers();
    }

    // ==========================================
    // EXISTING TESTS
    // ==========================================

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
    void testSetupPlacesTwoSettlementsAndTwoRoads() {
        ComputerPlayer ai = new ComputerPlayer(7);
        ComputerPlayer other = new ComputerPlayer(99);
        Game game = new Game(List.of(ai, other), new FixedDice(6), new Board(), 10, 20);

        ai.setup(game);

        int ownedNodes = 0;
        for (Node node : game.getBoard().getNodes()) {
            if (node.getPlayer() == ai) ownedNodes++;
        }

        assertEquals(2, ownedNodes, "Setup should place exactly two settlement nodes");
        assertEquals(2, ai.getSettlements().size(), "Player should have exactly two settlements after setup");
        assertEquals(2, ai.getRoads().size(), "Player should have exactly two roads after setup");
    }

    @Test
    void testSeededSetupIsDeterministic() {
        ComputerPlayer ai1 = new ComputerPlayer(123);
        Game game1 = new Game(List.of(ai1, new ComputerPlayer(999)), new FixedDice(6), new Board(), 10, 20);
        ai1.setup(game1);
        int firstChosenNode = findOwnedNodeId(game1, ai1);

        Player.resetNumPlayers();
        ComputerPlayer ai2 = new ComputerPlayer(123);
        Game game2 = new Game(List.of(ai2, new ComputerPlayer(555)), new FixedDice(6), new Board(), 10, 20);
        ai2.setup(game2);
        int secondChosenNode = findOwnedNodeId(game2, ai2);

        assertEquals(firstChosenNode, secondChosenNode, "Same seed on the same board should choose the same setup node");
    }

    @Test
    void testTakeTurnDoesNothingWithoutResources() {
        ComputerPlayer ai = new ComputerPlayer(5);
        Game game = new Game(List.of(ai, new ComputerPlayer(8)), new FixedDice(6), new Board(), 10, 20);

        int settlementsBefore = ai.getSettlements().size();
        ai.takeTurn(game);

        assertEquals(settlementsBefore, ai.getSettlements().size(), "No settlement should be built without resources");
    }

    @Test
    void testTakeTurnBuildsRoadWhenRoadIsOnlyAffordableMove() {
        ComputerPlayer ai = new ComputerPlayer(11);
        Game game = new Game(List.of(ai, new ComputerPlayer(22)), new FixedDice(6), new Board(), 10, 20);
        ai.setup(game);

        int roadsBefore = ai.getRoads().size();
        ai.addResource(ResourceType.BRICK);
        ai.addResource(ResourceType.LUMBER);

        ai.takeTurn(game);

        assertEquals(roadsBefore + 1, ai.getRoads().size(), "AI should build one road when it is the only affordable move");
    }

    @Test
    void testTakeTurnCanUpgradeSettlementToCity() {
        ComputerPlayer ai = new ComputerPlayer(17);
        Game game = new Game(List.of(ai, new ComputerPlayer(18)), new FixedDice(6), new Board(), 10, 20);
        ai.setup(game);

        Node ownedNode = findOwnedNode(game, ai);
        for (int i = 0; i < 5; i++) ai.addResource(ResourceType.ORE);
        for (int i = 0; i < 5; i++) ai.addResource(ResourceType.GRAIN);

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

    @Test
    void testSetRobberSelectsValidTile() {
        ComputerPlayer ai = new ComputerPlayer(99);
        Board board = new Board();
        Tile chosenTile = ai.setRobber(board.getTiles());
        assertNotNull(chosenTile);
        assertTrue(board.getTiles().contains(chosenTile));
    }

    @Test
    void testRobberDiscardReducesHandToSeven() {
        ComputerPlayer ai = new ComputerPlayer(50);
        for (int i = 0; i < 4; i++) {
            ai.addResource(ResourceType.LUMBER);
            ai.addResource(ResourceType.BRICK);
        }
        assertEquals(8, ai.getHand().getCount());
        ai.robberDiscard();
        assertEquals(7, ai.getHand().getCount());
    }

    // ==========================================
    // NEW TESTS FOR 100% COVERAGE
    // ==========================================

    @Test
    void testSetupFailsWhenBoardFull() {
        ComputerPlayer ai = new ComputerPlayer(1);
        ComputerPlayer opponent = new ComputerPlayer(2); // Create a second player

        // Pass both players to satisfy the 2-4 player rule
        Game game = new Game(List.of(ai, opponent), new FixedDice(6), new Board(), 10, 20);

        // Fill the entire board with opponent settlements
        for (Node n : game.getBoard().getNodes()) {
            Settlement s = new Settlement();
            s.setOwner(opponent);
            n.setStructure(s);
        }

        ai.setup(game); // Should trigger the "could not find a valid setup spot" warning
        assertEquals(0, ai.getSettlements().size(), "AI should fail to place settlements when board is completely full");
    }

    @Test
    void testMandatorySpendMoveReturnsNullWhenNoValidMoves() {
        ComputerPlayer ai = new ComputerPlayer(1);

        // Added new ComputerPlayer(2) to the list
        Game game = new Game(List.of(ai, new ComputerPlayer(2)), new FixedDice(6), new Board(), 10, 20);

        // Give 8 Wool (Triggers mustSpendCards, but no moves are valid to spend just Wool)
        for(int i = 0; i < 8; i++) ai.addResource(ResourceType.WOOL);

        // takeTurn will call mandatorySpendMove, which will return null, safely ending the loop
        assertDoesNotThrow(() -> ai.takeTurn(game));
    }

    @Test
    void testProtectLongestRoadLogic() throws Exception {
        ComputerPlayer ai = new ComputerPlayer(1);
        ComputerPlayer opponent = new ComputerPlayer(2);
        Game game = new Game(List.of(ai, opponent), new FixedDice(6), new Board(), 10, 20);

        ai.setup(game); // Ensure AI has a road to extend

        // Use reflection to set longest road scores directly to trigger the protection logic
        Field lrField = Player.class.getDeclaredField("longestRoad");
        lrField.setAccessible(true);
        lrField.set(ai, 5);
        lrField.set(opponent, 4);

        int initialRoads = ai.getRoads().size();
        ai.addResource(ResourceType.BRICK);
        ai.addResource(ResourceType.LUMBER);

        ai.takeTurn(game);
        assertTrue(ai.getRoads().size() > initialRoads, "AI should build a road specifically to protect its longest road lead");
    }

    @Test
    void testConnectNearbyRoad_TwoEdgeGap() {
        ComputerPlayer ai = new ComputerPlayer(1);
        Board board = new Board();

        // Added new ComputerPlayer(2) to the list
        Game game = new Game(List.of(ai, new ComputerPlayer(2)), new FixedDice(6), board, 10, 20);

        // Manually link 5 nodes in a line
        Node n0 = board.getNodes().get(0); Node n1 = board.getNodes().get(1);
        Node n2 = board.getNodes().get(2); Node n3 = board.getNodes().get(3);
        Node n4 = board.getNodes().get(4);
        n0.setRight(n1); n1.setRight(n2); n2.setRight(n3); n3.setRight(n4);

        // Build Road 1 on (0,1)
        Road r1 = new Road(); r1.setOwner(ai);
        n0.setRightRoad(r1); n1.setLeftRoad(r1);
        ai.addRoad(r1);

        // Build Road 2 on (3,4) -- creating a 2-edge gap at (1,2) and (2,3)
        Road r2 = new Road(); r2.setOwner(ai);
        n3.setRightRoad(r2); n4.setLeftRoad(r2);
        ai.addRoad(r2);

        ai.addResource(ResourceType.BRICK);
        ai.addResource(ResourceType.LUMBER);

        ai.takeTurn(game);

        // AI should detect the gap and build a road to start closing it
        assertEquals(3, ai.getRoads().size(), "AI should build a 3rd road to connect its nearby networks");
    }

    @Test
    void testMaxEntitiesReachedBranches() {
        ComputerPlayer ai = new ComputerPlayer(1);

        // Added new ComputerPlayer(2) to the list
        Game game = new Game(List.of(ai, new ComputerPlayer(2)), new FixedDice(6), new Board(), 10, 20);

        // Max out the AI's structures
        for(int i = 0; i < Settlement.getMax(); i++) ai.addStructure(new Settlement());
        for(int i = 0; i < City.getMax(); i++) ai.addStructure(new City());
        for(int i = 0; i < Road.getMax(); i++) ai.addRoad(new Road());

        // Give enough resources to build everything
        ai.addResource(ResourceType.BRICK); ai.addResource(ResourceType.LUMBER);
        ai.addResource(ResourceType.WOOL); ai.addResource(ResourceType.GRAIN);
        for(int i = 0; i < 3; i++) ai.addResource(ResourceType.ORE);

        ai.takeTurn(game);

        // Assert nothing was built because max limits caught the attempt
        assertEquals(Settlement.getMax(), ai.getSettlements().size());
        assertEquals(City.getMax(), ai.getCities().size());
        assertEquals(Road.getMax(), ai.getRoads().size());
    }

    @Test
    void testDeadCodePayMethodsViaReflection() throws Exception {
        // These methods exist in ComputerPlayer but are technically obsolete because
        // PlayerCommand handles the payments. This reflection test gets them 100% coverage.
        ComputerPlayer ai = new ComputerPlayer(1);
        ai.addResource(ResourceType.BRICK); ai.addResource(ResourceType.LUMBER);

        Method payRoad = ComputerPlayer.class.getDeclaredMethod("payForRoad");
        payRoad.setAccessible(true);
        payRoad.invoke(ai);
        assertEquals(0, ai.getResourceAmount(ResourceType.BRICK));

        ai.addResource(ResourceType.BRICK); ai.addResource(ResourceType.LUMBER);
        ai.addResource(ResourceType.WOOL); ai.addResource(ResourceType.GRAIN);

        Method paySettlement = ComputerPlayer.class.getDeclaredMethod("payForSettlement");
        paySettlement.setAccessible(true);
        paySettlement.invoke(ai);
        assertEquals(0, ai.getResourceAmount(ResourceType.WOOL));

        for(int i=0; i<3; i++) ai.addResource(ResourceType.ORE);
        for(int i=0; i<2; i++) ai.addResource(ResourceType.GRAIN);

        Method payCity = ComputerPlayer.class.getDeclaredMethod("payForCity");
        payCity.setAccessible(true);
        payCity.invoke(ai);
        assertEquals(0, ai.getResourceAmount(ResourceType.ORE));
    }

    // ==========================================
    // PRIVATE HELPERS
    // ==========================================

    private Node findOwnedNode(Game game, Player player) {
        for (Node node : game.getBoard().getNodes()) {
            if (node.getPlayer() == player) return node;
        }
        return null;
    }

    private int findOwnedNodeId(Game game, Player player) {
        Node owned = findOwnedNode(game, player);
        return owned == null ? -1 : Integer.parseInt(owned.toString());
    }
}