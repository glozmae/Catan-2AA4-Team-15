public class Node {

    private int ID;

    private Node left;
    private Node right;
    private Node up;
    private Node down;

    public Node(int ID) {
        this.ID = ID;
    }

    public void setLeft(Node left) {
        this.left = left;
    }

    public void setRight(Node right) {
        this.right = right;
    }

    public void setUp(Node up) {
        this.up = up;
    }

    public void setDown(Node down) {
        this.down = down;
    }
}
