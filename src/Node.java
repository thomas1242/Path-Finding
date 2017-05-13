
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

class Node{

    List<Node> neighbors;
    boolean isPassable;
    boolean isVisited;
    boolean inQueue;
    Node parent;
    int x, y;

    public Node(int x, int y) {
        isPassable = true;
        neighbors = new LinkedList<Node>();
        parent = null;
        this.x = x;
        this.y = y;
    }
}