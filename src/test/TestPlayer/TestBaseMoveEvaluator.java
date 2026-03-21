package TestPlayer;

import Player.BaseMoveEvaluator;
import Player.PlayerCommand;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the BaseMoveEvaluator class.
 * Ensures that the baseline component of the Decorator pattern properly stores commands
 * and returns the correct initial evaluation score of 0.0.
 */
public class TestBaseMoveEvaluator {

    /**
     * A simple dummy command to inject into the evaluator.
     */
    private static class DummyCommand implements PlayerCommand {
        @Override public void execute() {}
        @Override public void undo() {}
        @Override public String getExecuteMessage() { return "Execute dummy"; }
        @Override public String getUndoMessage() { return "Undo dummy"; }
    }

    /**
     * Tests that the base evaluation value is exactly 0.0.
     */
    @Test
    void testEvaluateValue() {
        PlayerCommand dummyCommand = new DummyCommand();
        BaseMoveEvaluator evaluator = new BaseMoveEvaluator(dummyCommand);

        assertEquals(0.0, evaluator.evaluateValue(), "The baseline evaluation score must be exactly 0.0");
    }

    /**
     * Tests that the command passed into the constructor is correctly stored and retrieved.
     */
    @Test
    void testGetCommand() {
        PlayerCommand dummyCommand = new DummyCommand();
        BaseMoveEvaluator evaluator = new BaseMoveEvaluator(dummyCommand);

        assertSame(dummyCommand, evaluator.getCommand(), "The retrieved command should be the exact same instance passed to the constructor");
    }
}