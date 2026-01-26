import java.util.*;
public class Bank {

    private HashMap<ResourceType, Integer> count;

    public Bank(UserHand handOfPlayer, int id){
        this.count = new HashMap<>();

        for (ResourceType t: ResourceType.values()){
            this.count.put(t, 0);
        }
        this.count.put(ResourceType.BRICK, 19);
        this.count.put(ResourceType.WOOD, 19);
        this.count.put(ResourceType.SHEEP, 19);
        this.count.put(ResourceType.WHEAT, 19);
        this.count.put(ResourceType.ORE, 19);
        this.count.put(ResourceType.DESERT, 0); //DESERT is not a resource Card
    }

    public int getCount(ResourceType type){
        return this.count.get(type);
    }

    public boolean has(ResourceType type, int amount){
        if (amount < 0){
            throw new IllegalArgumentException("Error: Amount of cards must be >= 0");
        }
        if (this.count.get(type) >= amount){
            return true;
        }else{
            return false;
        }
    }

    public void giveCard(ResourceType type, int amount, Player player){
        if (amount < 0){
            throw new IllegalArgumentException("Error: Amount of cards must be >= 0");
        }

        if (type == ResourceType.DESERT){
            return;
        }
        if (count.get(type) < amount){
            throw new IllegalArgumentException("Error: Bank does not have enough" + type);

        }
        this.count.put(type, this.count.get(type) - amount);
        player.gainCards(type,amount); //e.g, player will gain # wood

    }

    public void takeCard(ResourceType type, int amount, Player player){
        if (amount < 0){
            throw new IllegalArgumentException("Error: Amount of cards must be >= 0");
        }

        if (type == ResourceType.DESERT){
            return;
        }
        player.spend(type, amount);
        this.count.put(type, this.count.get(type) + amount);
    }
}