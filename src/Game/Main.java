package Game;

import java.util.List;

import Player.ComputerPlayer;
import Player.Player;

public class Main {

    public static void main(String[] args) {

        int seed = 12345;
        int winPoints = 10;
        int maxRounds = 8192;

        Player.resetNumPlayers();

        List<Player> players = List.of(
            new ComputerPlayer(seed + 1),
            new ComputerPlayer(seed + 2),
            new ComputerPlayer(seed + 3),
            new ComputerPlayer(seed + 4)
        );

        Dice dice = new MultiDice();
        
        Game game = new Game(players, dice, winPoints, maxRounds);
        game.simulate();

        System.out.println("\n\nWinner: " + game.getWinner());
    }
}
