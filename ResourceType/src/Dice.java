import java.util.*;

public class Dice {

    private final Random random;

    public Dice(){
        this.random = new Random();
    }

    public Dice(Random random){
        this.random = new Random();
    }

    public int roll(){
        return (random.nextInt(6) + 1) + (random.nextInt(6) + 1);
    }

}