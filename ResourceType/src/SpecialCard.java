package player;

/**
 * This is a placeholder class for special cards.
 * Buying/using development cards is ignored in Assignment 1.
 */
public abstract class SpecialCard {
    private final String name;

    protected SpecialCard(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }

    @Override
    public String toString(){
        return name;
    }
}
