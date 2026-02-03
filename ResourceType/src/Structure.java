package player;

/**
 * Base class for buildable structures owned by player.
 * This class stores which Player owns the structure; Game enforces
 * placement rules and creates structures.
 */
public class Structure {
    private final Player owner;

    public Structure(Player owner){
        this.owner = owner;
    }

    public Player getOwner(){
        return owner;
    }
}
