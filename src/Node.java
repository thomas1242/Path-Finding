import java.util.List;
import java.util.LinkedList;

class Node {
    
    Node parent;
    List<Node> neighbors;
    boolean isPassable, isVisited, isQueued;
    int x, y;
    double f, g;

    public Node(int x, int y) {
        this.x = x;
        this.y = y;
        isPassable = true;
        neighbors = new LinkedList<>();
        f = g = Double.POSITIVE_INFINITY;
    }

}