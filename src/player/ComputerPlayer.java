package player;

import java.util.Random;

/**
 * Represents an computer simulated player.
 *
 * @author Yojith Sai Biradavolu, McMaster University
 * @version Winter, 2026
 */
public class ComputerPlayer extends Player {
    /** Randomizer for moves **/
    private Random randomizer;

    /**
     * Constructor for a computer player
     */
    public ComputerPlayer() {
        super();
        this.randomizer = new Random();
    }

    /**
     * Constructor for a computer player with a seed for the randomizer
     *
     * @param seed Seed for the randomizer
     */
    public ComputerPlayer(int seed) {
        super();
        this.randomizer = new Random(seed);
    }

    /**
     * Initiate the current player's turn
     *
     * @param game The current game
     */
    @Override
    public void takeTurn(Game game) {
        return;

    }

    /**
     * Setup of the player's structures at the beginning of the game
     *
     * @param game The current game
     */
    @Override
    public void setup(Game game) {
        return;
    }
}
