package Player;

import java.util.*;
import Board.Node;
import Game.Game;
import GameResources.City;
import GameResources.Cost;
import GameResources.ResourceType;
import GameResources.Road;
import GameResources.Settlement;
import GameResources.Structure;

/**
 * This class implements the human player.
 *
 * @author Parnia Yazdinia, 400567795, McMaster University
 */
public class HumanPlayer extends Player{
    /** Shared scanner for console input */
    private static final Scanner INPUT = new Scanner(System.in);

    /**
     * Constructor for a human player.
     */
    public HumanPlayer(){
        super();
    }

    @Override
    public void takeTurn(Game game){
        boolean roll = false;
        boolean finishedTurn = false;
        System.out.println();
        System.out.println("Human Player " + (getId() + 1) + " turn.");
        System.out.println("Commands: roll | list | build settlement <nodeId> | build city <nodeId> | build road <from,to> | go");

        while(!finishedTurn){
            System.out.println("> ");
            String line = INPUT.nextLine();

            if(line == null){
                continue;
            }

            line = line.trim();
            if (line.isEmpty()) {
                System.out.println("Enter a command.");
                continue;
            }

            String lower = line.toLowerCase(Locale.ROOT);

            if(lower.equals("roll")){
                if(roll){
                    System.out.println("You already rolled this turn.");
                }else{
                    roll = true;
                    System.out.println("Roll accepted. Current roll is " + game.getLastRoll() + ".");
                }
                continue;
            }
            if (lower.equals("go")){
                if(!roll){
                    System.out.println("You must either 'roll' before ending your turn.");
                }else{
                    finishedTurn = true;
                    System.out.println("Turn ended.");
                }
                continue;
            }

            if(!roll){
                System.out.println("You must either 'roll' before building.");
                continue;
            }

            if(lower.startsWith("build settlement ")){
                Integer nodeId = parseSingNodeId(line, "build settlement ").
            }

            /**
             *
             */
            public void setup(Game game){
                //
            }

            /**
             * Prints the human player's current hand.
             */
            private void printHand(){
                StringBuilder sb = new StringBuilder("Hand: ");
                boolean first = true;
                if(ResourceType type : ResourceType.values()){
                    if(type== ResourceType.DESERT){
                        continue;
                    }

                    if(!first){
                        sb.append(" | ");
                    }
                    sb.append(type).append("=").append(getResourceAmount(type));
                    first = false;
                }
                System.out.println(sb.toString());
            }
        }
    }
}