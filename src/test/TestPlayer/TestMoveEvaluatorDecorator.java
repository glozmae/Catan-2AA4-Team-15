package TestPlayer;

import Player.MoveEvaluator;
import Player.MoveEvaluatorDecorator;
import Player.PlayerCommand;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the MoveEvaluatorDecorator abstract class.
 * Ensures that the base decorator correctly delegates method calls to its wrappee.
 */
public class TestMoveEvaluatorDecorator {

    /**
     * A simple dummy command to be returned by our mock evaluator.
     */
    private static class DummyCommand implements PlayerCommand {
        @Override public void execute() {}
        @Override public void undo() {}
        @Override public String getExecuteMessage() { return "Dummy Execute"; }
        @Override public String getUndoMessage() { return "Dummy Undo"; }
    }

    /**
     * A dummy evaluator that acts as the 'wrappee' at the core of the decorator chain.
     */
    private static class DummyEvaluator implements MoveEvaluator {
        private final PlayerCommand command = new DummyCommand();

        @Override
        public double evaluateValue() {
            return 42.5; // Arbitrary specific value to test delegation
        }

        @Override
        public PlayerCommand getCommand() {
            return command;
        }
    }

    /**
     * A minimal concrete implementation of the abstract MoveEvaluatorDecorator.
     * We don't override the methods here because we specifically want to test
     * the default delegation behavior in the abstract base class.
     */
    private static class ConcreteDecorator extends MoveEvaluatorDecorator {
        public ConcreteDecorator(MoveEvaluator wrappee) {
            super(wrappee);
        }
    }

    /**
     * Tests that evaluateValue() correctly asks the wrappee for its value.
     */
    @Test
    void testEvaluateValueDelegation() {
        MoveEvaluator wrappee = new DummyEvaluator();
        MoveEvaluatorDecorator decorator = new ConcreteDecorator(wrappee);

        assertEquals(42.5, decorator.evaluateValue(), "The decorator must return the exact value provided by its wrappee");
    }

    /**
     * Tests that getCommand() correctly retrieves the command from the wrappee.
     */
    @Test
    void testGetCommandDelegation() {
        MoveEvaluator wrappee = new DummyEvaluator();
        MoveEvaluatorDecorator decorator = new ConcreteDecorator(wrappee);

        assertSame(wrappee.getCommand(), decorator.getCommand(), "The decorator must return the exact same command instance as its wrappee");
    }
}