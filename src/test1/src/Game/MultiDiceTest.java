package Game;
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
 * @author Elizabeth Glozman, 400559660, McMaster University
 */
public class MultiDiceTest{

    /**
     * Boundry test: after rolling many times, values must stay within valid ranges
     */
    @Test
    public void test_rollValuesAlwaysInValidRange(){
        MultiDice dice = new MultiDice(123);
        for (int i = 0; i < 500; i++){
            int sum = dice.roll();
            int d1 = dice.getLastDie1();
            int d2 = dice.getLastDie2();

            //Each die must be between 1 and 6
            assertTrue("Die1 must be in [1, 6], got " + d1, d1 >= 1 && d1 <= 6);
            assertTrue("Die2 must be in [1, 6], got " + d2, d2 >= 1 && d2 <= 6);

            //Sum must be between 2 and 12
            assertTrue("Sum must be in [2, 12], got " + sum, sum >= 2 && sum <= 12);
        }
    }

    /**
     * Contract test: roll() should return lastDie1 and lastDie2 every time
     */
    @Test
    public void test_rollReturnEqualsStoredDiceValue(){
        MultiDice dice = new MultiDice(456);
        for (int i = 0; i < 100; i++){
            int sum = dice.roll();
            assertEquals("roll() must equal lastDie1 + lastDie2", dice.getLastDie1() + dice.getLastDie2(), sum);
        }
    }

    /**
     * Determinism test: With a seed, MultiDice should match Java's Random sequence exactly.
     */
    @Test
    public void test_seededRollMatchesJavaRandom(){
        int seed = 999;

        //Expected values from Java
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
