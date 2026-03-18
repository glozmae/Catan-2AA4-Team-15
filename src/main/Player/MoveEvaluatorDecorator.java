package Player;

/**
 * The abstract base class for all move evaluator decorators.
 * <p>
 * This class forms the structural core of the Decorator design pattern for move evaluation.
 * It implements the {@link MoveEvaluator} interface and maintains a reference to a
 * "wrappee" (another evaluator). By default, it delegates all method calls to this wrappee.
 * Concrete subclasses will extend this class to add specific rule-based scoring modifications
 * (such as adding points for building or earning Victory Points) to the base move.
 *
 * @author Taihan Mobasshir, 400578506
 * @version Winter, 2026
 */
public abstract class MoveEvaluatorDecorator implements MoveEvaluator {

    /** The encapsulated evaluator that is being decorated with additional scoring rules. */
    protected final MoveEvaluator wrappee;

    /**
     * Constructs a new decorator wrapping the specified evaluator.
     *
     * @param wrappee The underlying {@link MoveEvaluator} to be decorated.
     */
    public MoveEvaluatorDecorator(MoveEvaluator wrappee) {
        this.wrappee = wrappee;
    }

    /**
     * Calculates the cumulative benefit value by delegating to the wrappee.
     * Concrete subclasses should override this method to add their specific scoring values.
     *
     * @return The base value calculated by the wrapped evaluator.
     */
    @Override
    public double evaluateValue() {
        return wrappee.evaluateValue(); // By default, just pass the value up the chain
    }

    /**
     * Retrieves the encapsulated command by delegating to the wrappee.
     *
     * @return The underlying {@link PlayerCommand} associated with this move.
     */
    @Override
    public PlayerCommand getCommand() {
        return wrappee.getCommand();
    }
}