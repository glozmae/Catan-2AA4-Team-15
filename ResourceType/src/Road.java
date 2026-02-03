package player;

/**
 * A road belongs to a player and connects two nodes.
 * Node placement is checked by the Game/Board layer.
 */
public class Road extends Structure {

    private final int nodeA;
    private final int nodeB;

    public Road(Player owner, int nodeA, int nodeB) {
        super(owner);
        this.nodeA = nodeA;
        this.nodeB = nodeB;
    }

    public int getNodeA(){
        return nodeA;
    }
    public int getNodeB(){
        return nodeB;
    }
    @Override
    public String toString(){
        return "Road between nodes " + nodeA + " and " + nodeB;
    }
}
