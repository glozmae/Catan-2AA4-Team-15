package player;

public class City extends Structure{
    private final int nodeId;

    public City(Player owner, int nodeId){
        super(owner);
        this.nodeId = nodeId;
    }

    public int getNodeId(){
        return nodeId;
    }
    @Override
    public String toString(){
        return "This city is located at node " + nodeId + ".";
    }

}
