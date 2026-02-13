package Game;

import java.util.Random;

public class MultiDice implements Dice {

    private final Random rng;

    private int lastDie1 = 1;
    private int lastDie2 = 1;

    public MultiDice() {
        this.rng = new Random();
    }

    public MultiDice(int seed) {
        this.rng = new Random(seed);
    }

    @Override
    public int roll() {
        lastDie1 = rng.nextInt(6) + 1;
        lastDie2 = rng.nextInt(6) + 1;
        return lastDie1 + lastDie2;
    }

    public int getLastDie1() { return lastDie1; }
    public int getLastDie2() { return lastDie2; }
}
