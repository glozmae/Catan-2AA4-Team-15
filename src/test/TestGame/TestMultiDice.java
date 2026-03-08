package TestGame;
import static org.junit.Assert.*;
import org.junit.*;
import java.util.Random;

/**
 * Unit tests for MultiDice
 *
 * Represent a pair of six-sided dice for this game version
 * This implementation generates two independent dice rolls
 * Stores individual die values
 *
 * @author Parnia Yazdinia, 400567795, McMaster University
 */
public class TestMultiDice {

    /** Default timeout for each test in ms */
    private static final int TIMEOUT = 2000;

    /**
     * Boundary test: after rolling many times, die 1 must stay within valid range.
     */
    @Test(timeout = TIMEOUT)
    public void die1Range() {
        MultiDice dice = new MultiDice(123);
        for (int i = 0; i < 200; i++) {
            dice.roll();
            int d1 = dice.getLastDie1();
            assertTrue("die 1 stays in valid range", d1 >= 1 && d1 <= 6);
        }
    }

    /**
     * Boundary test: after rolling many times, die 2 must stay within valid range.
     */
    @Test(timeout = TIMEOUT)
    public void die2Range() {
        MultiDice dice = new MultiDice(123);
        for (int i = 0; i < 200; i++) {
            dice.roll();
            int d2 = dice.getLastDie2();
            assertTrue("die 2 stays in valid range", d2 >= 1 && d2 <= 6);
        }
    }

    /**
     * Boundary test: the sum of two dice should always stay in [2, 12].
     */
    @Test(timeout = TIMEOUT)
    public void sumRange() {
        MultiDice dice = new MultiDice(123);

        for (int i = 0; i < 200; i++) {
            int sum = dice.roll();
            assertTrue("sum stays in valid range", sum >= 2 && sum <= 12);
        }
    }

    /**
     * Contract test: roll() should equal the sum of the stored dice values.
     */
    @Test(timeout = TIMEOUT)
    public void rollMatchesDice() {
        MultiDice dice = new MultiDice(456);

        for (int i = 0; i < 100; i++) {
            int sum = dice.roll();
            assertEquals("roll returns stored die sum", dice.getLastDie1() + dice.getLastDie2(), sum);
        }
    }

    /**
     * Determinism test: With a seed, MultiDice should match Java's Random sequence exactly.
     */
    @Test(timeout = TIMEOUT)
    public void seededFirstRoll() {
        int seed = 999;

        Random rand = new Random(seed);
        int expectedDie1 = rand.nextInt(6) + 1;
        int expectedDie2 = rand.nextInt(6) + 1;
        int expectedSum = expectedDie1 + expectedDie2;

        MultiDice dice = new MultiDice(seed);
        int actualSum = dice.roll();
        assertEquals("Seeded sum should match expected rand result", expectedSum, actualSum);
        assertEquals("Seeded die1 should match expected rand result", expectedDie1, dice.getLastDie1());
        assertEquals("Seeded die2 should match expected rand result", expectedDie2, dice.getLastDie2());
    }
}