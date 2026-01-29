package Board;

import Entities.Player;
import Structures.Road;
import Structures.Structure;

public class Node {

    private int ID;

    private Node left;
    private Node right;
    private Node vert;

    private Player player;
    private Structure structure;

    private Road leftRoad;
    private Road rightRoad;
    private Road vertRoad;

    public Node(int ID) {
        this.ID = ID;
    }

    public int getID() {
        return ID;
    }

    public Node getLeft() {
        return left;
    }

    public Node getRight() {
        return right;
    }

    public Node getVert() {
        return vert;
    }

    public Player getPlayer() {
        return player;
    }

    public Structure getStructure() {
        return structure;
    }

    public Road getLeftRoad() {
        return leftRoad;
    }

    public Road getRightRoad() {
        return rightRoad;
    }

    public Road getVertRoad() {
        return vertRoad;
    }

    public void setLeft(Node left) {
        this.left = left;
    }

    public void setRight(Node right) {
        this.right = right;
    }

    public void setVert(Node vert) {
        this.vert = vert;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void setStructure(Structure structure) {
        this.structure = structure;
    }

    public void setLeftRoad(Road leftRoad) {
        this.leftRoad = leftRoad;
    }

    public void setRightRoad(Road rightRoad) {
        this.rightRoad = rightRoad;
    }

    public void setVertRoad(Road vertRoad) {
        this.vertRoad = vertRoad;
    }
}
