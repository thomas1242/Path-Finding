
import java.util.ArrayList;
import java.util.LinkedList;

class Node{

    LinkedList<Node> neighbors;
    boolean isPassable = true;
    boolean isVisited = false;
    boolean inQueue = false;

    public Node() {

        neighbors = new LinkedList<Node>();

    }
}