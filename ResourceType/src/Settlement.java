package player;

/**
 * A settlement is a player-owned structure that is placed on a specific
 * node of the board.
 */
public class Settlement extends Structure{
    private final int nodeId;

    public Settlement(Player owner, int nodeId){
        super(owner);
        this.nodeId = nodeId;
    }

    public int getNodeId(){
        return nodeId;
    }

    @Override
    public String toString(){
        return "This settlement is located at node " + nodeId + ".";
    }

}
