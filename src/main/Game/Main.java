package Game;

import java.util.List;

import Player.ComputerPlayer;
import Player.Player;

/**
 * Entry point for launching and simulation Catan game
 * Initializes players, dice, and game parameters
 * Builds a game instance and runs a simulation
 * 
 * @author Elizabeth Glozman, 400559660, McMaster University
 */
public class Main {

    /**
     * Program entry point
     * Creates 4 computer players, initializes dice, builds the board,
     * runs the simulation, and prints the winner
     * 
     * @param args - command line arguments
     */
    public static void main(String[] args) {

        /** for deterministic player behaviour */
        int seed = 12345;

        /** victory point threshold */
        int winPoints = 10;

        /** Maximum possible runs before simulation stops */
        int maxRounds = 8192;

        Player.resetNumPlayers();

        // creaet four computer players based of seed (ensures no repeats)
        List<Player> players = List.of(
                new ComputerPlayer(seed + 1),
                new ComputerPlayer(seed + 2),
                new ComputerPlayer(seed + 3),
                new ComputerPlayer(seed + 4));

        // create dice
        Dice dice = new MultiDice();

        // construct and run game
        Game game = new Game(players, dice, winPoints, maxRounds);
        game.simulate();

        // print final winner
        System.out.println("\n\nWinner: " + game.getWinner());
    }
}
