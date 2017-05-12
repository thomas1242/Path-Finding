
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

class Node{

    List<Node> neighbors;
    boolean isPassable;
    boolean isVisited;
    boolean inQueue;

    public Node() {
        isPassable = true;
        neighbors = new LinkedList<Node>();
    }
}