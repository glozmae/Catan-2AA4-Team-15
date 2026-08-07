package Board;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import GameResources.ResourceType;

/**
 * Chooses settlement locations using the expected value of two six-sided dice.
 * Locations beside frequently rolled numbers, such as 6 and 8, receive a
 * higher score than locations beside rare numbers, such as 2 and 12.
 */
public final class ExpectedValueSettlementStrategy {

    private static final double DICE_OUTCOMES = 36.0;

    /**
     * Chooses the legal node with the highest expected resource production.
     * Randomness is used only to break ties between equally valuable nodes.
     *
     * @param board current game board
     * @param legalNodes nodes that already satisfy the settlement rules
     * @param random random source used for tie breaking
     * @return the highest-scoring legal node
     */
    public Node choose(Board board, List<Node> legalNodes, Random random) {
        Objects.requireNonNull(board, "board");
        Objects.requireNonNull(legalNodes, "legalNodes");
        Objects.requireNonNull(random, "random");

        if (legalNodes.isEmpty()) {
            throw new IllegalArgumentException("At least one legal settlement node is required.");
        }

        double bestScore = Double.NEGATIVE_INFINITY;
        List<Node> bestNodes = new ArrayList<>();

        for (Node node : legalNodes) {
            double score = score(board, node);
            int comparison = Double.compare(score, bestScore);
            if (comparison > 0) {
                bestScore = score;
                bestNodes.clear();
                bestNodes.add(node);
            } else if (comparison == 0) {
                bestNodes.add(node);
            }
        }

        return bestNodes.get(random.nextInt(bestNodes.size()));
    }

    /**
     * Calculates the expected number of resources produced by a settlement on
     * the supplied node for one dice roll.
     *
     * @param board current game board
     * @param node settlement location to evaluate
     * @return expected resources produced per roll
     */
    public double score(Board board, Node node) {
        Objects.requireNonNull(board, "board");
        Objects.requireNonNull(node, "node");

        double expectedResources = 0.0;
        for (Tile tile : board.getTiles()) {
            if (tile.getType() == ResourceType.DESERT || !touches(tile, node)) {
                continue;
            }
            expectedResources += probabilityOf(tile.getProductionNumber());
        }
        return expectedResources;
    }

    /**
     * Returns the exact probability of rolling a total with two six-sided dice.
     *
     * @param total dice total
     * @return probability from 0.0 to 1.0
     */
    public static double probabilityOf(Integer total) {
        if (total == null || total < 2 || total > 12 || total == 7) {
            return 0.0;
        }
        int combinations = 6 - Math.abs(7 - total);
        return combinations / DICE_OUTCOMES;
    }

    private boolean touches(Tile tile, Node node) {
        for (Node tileNode : tile.getNodes()) {
            if (tileNode == node) {
                return true;
            }
        }
        return false;
    }
}
