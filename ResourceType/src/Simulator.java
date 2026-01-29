import java.util.ArrayList;
import java.util.Random;

import player.Player;

public class Simulator{

    private List<player> players;
    private Dice dice;
    private Game game;

    public Simulator (List<Player> players){
        this.players = players;
        this.dice = new Dice();
        this.game = new Game();
    }

    public void run(int turns){

        for (int round = 1; round <= turns; round++){
            for (Player p: players){
                int roll = dice.roll();
                game.setLastRoll(roll);
            }
        }

        if(roll == 7) {
            System.out.println(round + " / " + p.getId() + ": rolled 7, produces no resources");
        }else{
            //To do later, distribute resources based on map/tokens
            System.out.println(round + " / " + p.getId() + ": rolled " + roll);
        }

        //Player does their turn
        p.takeTurn(game);

        String action = game.consumeLastAction(p);

        System.out.println(round + " / " + p.getId() + ": " + action);

        //should stop if 10 VPs reached
        if (p.calculateVictoryPoints() >= 10){
            System.out.println("Game over: Player" + p.getId() + "reached 10 VPs.");
            printVPs(round);
            return;
        }
    }
}

