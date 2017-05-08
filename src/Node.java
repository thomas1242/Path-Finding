import java.util.ArrayList;

class Node{

    ArrayList<Node> neighbors;
    boolean isPassable = true;

    public Node() {

        neighbors = new ArrayList<Node>();

    }
}