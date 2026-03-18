package Player;

/**
 * A concrete decorator that applies the non-VP building evaluation rule.
 * <p>
 * In accordance with rule R3.2, this decorator adds a value of 0.8 to a move's
 * cumulative score if the action involves building a structure or buying an item
 * that does not directly yield a Victory Point (e.g., building a road or buying
 * a development card).
 *
 * @author Taihan Mobasshir, 400578506, McMaster University
 * @version Winter, 2026
 */
public class BuilderDecorator extends MoveEvaluatorDecorator {

    /**
     * Constructs a new builder decorator wrapping the specified evaluator.
     *
     * @param wrappee The underlying {@link MoveEvaluator} to be decorated.
     */
    public BuilderDecorator(MoveEvaluator wrappee) {
        super(wrappee);
    }

    /**
     * Calculates the cumulative benefit value of the move, adding the building bonus.
     *
     * @return The combined value of the wrappee's evaluation plus 0.8.
     */
    @Override
    public double evaluateValue() {
        return super.evaluateValue() + 0.8;
    }
}