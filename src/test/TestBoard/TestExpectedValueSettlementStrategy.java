package TestBoard;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.Test;

import Board.Board;
import Board.ExpectedValueSettlementStrategy;
import Board.Node;

/** Tests the probability-based settlement placement strategy. */
public class TestExpectedValueSettlementStrategy {

    private static final double TOLERANCE = 0.000001;

    @Test
    void usesExactTwoDiceProbabilities() {
        assertEquals(5.0 / 36.0, ExpectedValueSettlementStrategy.probabilityOf(6), TOLERANCE);
        assertEquals(5.0 / 36.0, ExpectedValueSettlementStrategy.probabilityOf(8), TOLERANCE);
        assertEquals(1.0 / 36.0, ExpectedValueSettlementStrategy.probabilityOf(2), TOLERANCE);
        assertEquals(1.0 / 36.0, ExpectedValueSettlementStrategy.probabilityOf(12), TOLERANCE);
        assertEquals(0.0, ExpectedValueSettlementStrategy.probabilityOf(7), TOLERANCE);
        assertEquals(0.0, ExpectedValueSettlementStrategy.probabilityOf(null), TOLERANCE);
    }

    @Test
    void choosesTheLocationWithHigherExpectedProduction() {
        Board board = new Board();
        Node highValueNode = board.getNodes().get(0);
        Node lowValueNode = board.getNodes().get(30);
        ExpectedValueSettlementStrategy strategy = new ExpectedValueSettlementStrategy();

        Node chosen = strategy.choose(
                board,
                List.of(lowValueNode, highValueNode),
                new Random(42));

        assertEquals(highValueNode, chosen);
        assertEquals(8.0 / 36.0, strategy.score(board, highValueNode), TOLERANCE);
        assertEquals(2.0 / 36.0, strategy.score(board, lowValueNode), TOLERANCE);
    }

    @Test
    void rejectsAnEmptyCandidateList() {
        ExpectedValueSettlementStrategy strategy = new ExpectedValueSettlementStrategy();

        assertThrows(
                IllegalArgumentException.class,
                () -> strategy.choose(new Board(), List.of(), new Random(42)));
    }
}
