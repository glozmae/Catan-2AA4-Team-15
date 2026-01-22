import java.util.ArrayList;

public class Tile {

    private int ID;
    private Node[] nodes;

    private ResourceType resourceType;

    // FULLY MANUAL CONSTRUCTION
    public Tile(int ID, Node node0, Node node1, Node node2, Node node3, Node node4, Node node5, ResourceType resourceType) {
        this.ID = ID;
        nodes = new Node[6];
        this.nodes[0] = node0;
        this.nodes[1] = node1;
        this.nodes[2] = node2;
        this.nodes[3] = node3;
        this.nodes[4] = node4;
        this.nodes[5] = node5;
        nodeConnector();
        this.resourceType = resourceType;
    }

    private void nodeConnector () {
        nodes[0].setDown(nodes[1]);
        nodes[1].setLeft(nodes[2]);
        nodes[2].setLeft(nodes[3]);
        nodes[3].setUp(nodes[4]);
        nodes[4].setRight(nodes[5]);
        nodes[5].setRight(nodes[0]);
    }

}
