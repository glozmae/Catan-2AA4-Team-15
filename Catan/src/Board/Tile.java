package Board;

public class Tile {

    private int ID;
    private Node[] nodes;
    private ResourceType type;

    // FULLY MANUAL CONSTRUCTION
    public Tile(int ID, Node[] nodes, ResourceType type) {
        this.ID = ID;
        nodes = new Node[6];
        this.nodes = nodes;
        nodeConnector();
        this.type = type;
    }

    private void nodeConnector () {
        nodes[0].setVert(nodes[1]);
        nodes[1].setLeft(nodes[2]);
        nodes[2].setLeft(nodes[3]);
        nodes[3].setVert(nodes[4]);
        nodes[4].setRight(nodes[5]);
        nodes[5].setRight(nodes[0]);
    }

}
