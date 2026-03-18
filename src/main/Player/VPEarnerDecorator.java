package Player;

/**
 * A concrete decorator that applies the Victory Point evaluation rule.
 * <p>
 * In accordance with rule R3.2, this decorator adds a value of 1.0 to a move's
 * cumulative score if the action results in the player earning a Victory Point
 * (e.g., building a settlement or upgrading to a city).
 *
 * @author Taihan Mobasshir, 400578506, McMaster University
 * @version Winter, 2026
 */
public class VPEarnerDecorator extends MoveEvaluatorDecorator {

    /**
     * Constructs a new VP earner decorator wrapping the specified evaluator.
     *
     * @param wrappee The underlying {@link MoveEvaluator} to be decorated.
     */
    public VPEarnerDecorator(MoveEvaluator wrappee) {
        super(wrappee);
    }

    /**
     * Calculates the cumulative benefit value of the move, adding the VP bonus.
     *
     * @return The combined value of the wrappee's evaluation plus 1.0.
     */
    @Override
    public double evaluateValue() {
        return super.evaluateValue() + 1.0;
    }
}