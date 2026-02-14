package Game;

import java.util.Random;

/**
 * Represent a pair of six-sided dice for this game version
 * This implementation generates two independent dice rolls
 * Stores individual die values
 * 
 * @author Elizabeth Glozman, 400559660, McMaster University
 */
public class MultiDice implements Dice {

    /**
     * Random Number generator for rolls
     */
    private final Random rng;

    private int lastDie1 = 1;
    private int lastDie2 = 1;

    /**
     * Construct MultiDice instance with random seed
     */
    public MultiDice() {
        this.rng = new Random();
    }

    /**
     * Construct MultiDice instance with fixed seed for repretable dice
     * Used for controlability and testing
     * 
     * @param seed - random number generator seed
     */
    public MultiDice(int seed) {
        this.rng = new Random(seed);
    }

    /**
     * Rolls both dice and returns total and stores individual dice
     * 
     * @return sum of two dice
     */
    @Override
    public int roll() {
        lastDie1 = rng.nextInt(6) + 1;
        lastDie2 = rng.nextInt(6) + 1;
        return lastDie1 + lastDie2;
    }

    /**
     * Reutrn value of first dice
     * 
     * @return - last value of dice 1
     */
    public int getLastDie1() {
        return lastDie1;
    }

    /**
     * Return value of second dice
     * 
     * @return last value of dice 2
     */
    public int getLastDie2() {
        return lastDie2;
    }
}
