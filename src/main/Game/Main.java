package Game;

import java.util.List;

import Player.ComputerPlayer;
import Player.HumanPlayer;
import Player.Player;

/**
 * Entry point for launching and simulating the Catan game.
 *
 * @author Elizabeth Glozman, 400559660, McMaster University
 */
public class Main {

    /**
     * Program entry point.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        int seed = 12345;
        int winPoints = 10;
        int maxRounds = 8192;

        Player.resetNumPlayers();

        List<Player> players = List.of(
                new HumanPlayer(),
                new ComputerPlayer(seed + 2),
                new ComputerPlayer(seed + 3),
                new ComputerPlayer(seed + 4));

        Dice dice = new MultiDice();

        Game game = new Game(players, dice, winPoints, maxRounds);
        game.simulate();

        System.out.println("\n\nWinner: " + game.getWinner());
    }
}