package Player;

/**
 * A concrete decorator that applies the hand depletion evaluation rule.
 * <p>
 * In accordance with rule R3.2, this decorator conditionally adds a value of 0.5
 * to a move's cumulative score if the resource cost of the action causes the player's
 * remaining hand size to drop below 5 cards. This encourages the AI to avoid the
 * 7-card robber penalty.
 *
 * @author Taihan Mobasshir, 400578506, McMaster
 * @version Winter, 2026
 */
public class HandDepleterDecorator extends MoveEvaluatorDecorator {

    /** The player whose hand size is being evaluated. */
    private final Player player;

    /** The total number of resource cards required to execute the wrapped command. */
    private final int totalResourceCost;

    /**
     * Constructs a new hand depleter decorator.
     *
     * @param wrappee           The underlying {@link MoveEvaluator} to be decorated.
     * @param player            The {@link Player} executing the move.
     * @param totalResourceCost The total number of resources spent by this move.
     */
    public HandDepleterDecorator(MoveEvaluator wrappee, Player player, int totalResourceCost) {
        super(wrappee);
        this.player = player;
        this.totalResourceCost = totalResourceCost;
    }

    /**
     * Calculates the cumulative benefit value of the move, dynamically checking
     * if the hand depletion bonus should be applied.
     *
     * @return The combined value of the wrappee's evaluation, plus 0.5 if the
     * resulting hand size is less than 5; otherwise, returns the wrappee's base value.
     */
    @Override
    public double evaluateValue() {
        double currentTotal = super.evaluateValue();
        int futureHandSize = player.getHand().getCount() - totalResourceCost;

        if (futureHandSize < 5) {
            return currentTotal + 0.5;
        }
        return currentTotal;
    }
}