package TestGame;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import Game.Main;

/**
 * Unit test for Main.
 *
 * @author Parnia Yazdinia, 400567795, McMaster University
 */
public class TestMain {
    private final PrintStream originalOut = System.out;
    private static final int TIMEOUT = 5000;

    @BeforeEach
    public void suppressPrints() {
        System.setOut(new PrintStream(new ByteArrayOutputStream()));
    }

    @AfterEach
    public void restorePrints() {
        System.setOut(originalOut);
    }

    /**
     * Smoke test: main should run without throwing an exception.
     * Suppresses print statements from the game to prevent clutter in test output.
     */
    @Test()
    @Timeout(value = TIMEOUT)
    public void mainRuns() {
        Main.main(new String[0]);
        assertTrue(true);
    }
}