package Player;

/**
 * Defines the contract for evaluating the benefit of a potential AI move.
 * <p>
 * This interface serves as the base component in a Decorator design pattern.
 * It allows the AI to dynamically calculate the cumulative value of an action
 * based on overlapping game rules (specifically R3.2) by wrapping base
 * moves with various scoring decorators.
 * * @author Taihan Mobasshir, 400578506, McMaster University
 * @version Winter, 2026
 */
public interface MoveEvaluator {

    /** * Calculates and returns the cumulative benefit value of this move.
     * <p>
     * The value is determined by the specific rules applied to the move
     * (e.g., 1.0 for earning a VP, 0.8 for building a structure, or 0.5
     * for dropping the hand size below 5).
     * * @return The calculated value representing the immediate benefit of the move.
     */
    double evaluateValue();

    /** * Retrieves the encapsulated command associated with this evaluated move.
     * <p>
     * This command contains the actual execution and undo logic for the action
     * (such as building a road or a settlement) independent of its calculated value.
     * * @return The {@link PlayerCommand} to be executed if this move is selected.
     */
    PlayerCommand getCommand();
}