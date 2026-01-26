import java.util.HashMap;
import java.util.Map;

public class Player {

    private UserHand handOfPlayer;
    private int id;

    public Player(UserHand handOfPlayer, int id){
        this.handOfPlayer = new UserHand();
        this.id = id;
    }

    public int getId(){
        return id;
    }

    public UserHand getUserHand(){
        return handOfPlayer;
    }

    public boolean enoughCards(ResourceType type, int amount){
        return handOfPlayer.has(type, amount);
    }

    public void gainCards(ResourceType type, int amount){
        handOfPlayer.addCard(type, amount);
    }

    public void spend(ResourceType type, int amount){
        handOfPlayer.removeCard(type, amount);
    }

    public int totalCards(){
        return handOfPlayer.getTotalCard();
    }
}