package TestGame;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 * Unit test for Main.
 *
 * @author Parnia Yazdinia, 400567795, McMaster University
 */
public class TestMain {

    private static final int TIMEOUT = 5000;

    /**
     * Smoke test: main should run without throwing an exception.
     */
    @Test(timeout = TIMEOUT)
    public void mainRuns() {
        Main.main(new String[0]);
        assertTrue(true);
    }
}