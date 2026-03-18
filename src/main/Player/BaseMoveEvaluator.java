package Player;

/**
 * The foundational component in the move evaluation system.
 *
 * In the context of the Decorator design pattern, this class serves as the Concrete Component.
 * It encapsulates a raw {@link PlayerCommand} and provides a baseline evaluation score
 * of 0.0. Specific scoring rules (such as R3.2 criteria) are meant to be dynamically
 * wrapped around this base class using various decorators to calculate the final cumulative score.
 *
 * @author Taihan Mobasshir, 400578506, McMaster University
 * @version Winter, 2026
 */
public class BaseMoveEvaluator implements MoveEvaluator {

    /** The underlying action to be executed if this move is chosen. */
    private final PlayerCommand command;

    /**
     * Constructs a new baseline evaluator for the specified command.
     *
     * @param command The {@link PlayerCommand} (e.g., building a road or settlement)
     * that this evaluator is wrapping.
     */
    public BaseMoveEvaluator(PlayerCommand command) {
        this.command = command;
    }

    /**
     * Returns the baseline value for any raw move before specific game rules are applied.
     *
     * @return 0.0, acting as the starting mathematical baseline for the decorators to add onto.
     */
    @Override
    public double evaluateValue() {
        return 0.0; // Base value before any rules are applied
    }

    /**
     * Retrieves the encapsulated command associated with this baseline move.
     *
     * @return The {@link PlayerCommand} passed during construction.
     */
    @Override
    public PlayerCommand getCommand() {
        return command;
    }
}