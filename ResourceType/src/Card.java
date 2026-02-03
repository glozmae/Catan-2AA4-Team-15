package player;

public abstract class Card {
    private final ResourceType type;

    protected Card(ResourceType type){
        this.type = type;
    }

    public ResourceType getType(){
        return type;
    }

    @Override
    public String toString(){
        return type.name();
    }
}

