import java.util.*;

import player.Player;
public class Simulator{

    private final List<Player> players;
    private final Dice dice;
    private final Game game;

    public Simulator (List<Player> players, Dice dice, Game game){
        this.players = players;
        this.dice = dice();
        this.game = game();
    }

    public void run(int turns){

        if (turns < 1){
            turns = 1;
        }
        if (turns > 8192){
            turns = 8192;
        }

        for (int round = 1; round <= turns; round++){
            for (Player p: players) {
                int roll = dice.roll();
                game.setLastRoll(roll);

                if (roll == 7) {
                    System.out.println(round + " / " + p.getId() + ": rolled 7, produces no resources");
                } else {
                    //To do later, distribute resources based on map/tokens
                    System.out.println(round + " / " + p.getId() + ": rolled " + roll);
                }
                p.takeTurn(game);

                String action = game.consumeLastAction(p);
                if (action == null || action.isBlank()){
                    action = "does nothing";
                }

                System.out.println(round + " / " + p.getId() + ": " + action);

                //should stop if 10 VPs reached
                if (p.calculateVictoryPoints() >= 10) {
                    System.out.println("Game over: Player " + p.getId() + " reached 10 VPs.");
                    printVPs(round);
                    return;
                }
            }
            printVPs(round);
        }
    }

    private void printVPs(int round){
        StringBuffer sb = new StringBuffer();
        sb.append(round);
        sb.append(" / VP: ");

        for (int i = 0; i < players.size(); i++){
            Player p = players.get(i);
            sb.append("P");
            sb.append(p.getId());
            sb.append("=");
            sb.append(p.calculateVictoryPoints());
            if (i < players.size() -1){
                sb.append(", ");
            }
        }
        System.out.println(sb.toString());
    }
}

