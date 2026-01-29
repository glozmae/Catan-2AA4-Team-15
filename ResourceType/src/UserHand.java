import java.util.*;
public class UserHand {

    private Map<ResourceType, Integer> count;
    public UserHand(Map<ResourceType, Integer> count){
        this.count = new HashMap<>();
        for (ResourceType t: ResourceType.values()){
            count.put(t, 0);
        }

        if (count != null){
            for (ResourceType t: ResourceType.values()){
                int amount = count.get(t);
                if (amount < 0) throw new IllegalArgumentException("Amount of cards must be >= 0");
                this.count.put(t, amount);

            }
        }
    }

    public int getCount(ResourceType type){
        return this.count.get(type);
    }

    public int getTotalCard(){
        int sum = 0;
        for (int i : this.count.values()){
            sum += i;
        }
        return sum;
    }

    public void addCard(ResourceType type, int amount){
        if (amount < 0){
            throw new IllegalArgumentException("Amount of cards must be >= 0");
        }
        this.count.put(type, this.count.get(type) + amount);
    }

    public boolean has(ResourceType type, int amount){
        if (amount < 0){
            throw new IllegalArgumentException("Amount of cards must be >= 0");
        }
        if (this.count.get(type) >= amount){
            return true;
        }
        return false;
    }

    public void removeCard(ResourceType type, int amount){
        if (amount < 0){
            throw new IllegalArgumentException("Amount of cards must be >= 0");
        }
        if (this.count.get(type) < amount){
            throw new IllegalArgumentException("Error: Not enough" + type + ": have " + this.count.get(type) + ", need " + amount);
        }
        this.count.put(type, this.count.get(type) - amount);
    }

}