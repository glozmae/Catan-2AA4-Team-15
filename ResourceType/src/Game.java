package player;

import java.util.*;
public class Game {

    private int lastRoll;
    private final Map<Integer, String> lastActionByPlayerId = new HashMap<>();

    public void setLastRoll(int roll){
        this.lastRoll = roll;
    }

    public int gtLastRoll(){
        return lastRoll;
    }

    public void recordAction(Player player, String action){
        if (player == null){
            return;
        }
        if (action == null){
            action = "";
        }
        lastActionByPlayerId.put(player.getId(), action);
    }

    /**
     * Reutnrs the last action played by the player and removes it
     * to avoid being printed twice
     */
    public String deleteLastAction(Player player){
        if (player == null){
            return null;
        }
        return lastActionByPlayerId.remove(player.getId());

    }
}
