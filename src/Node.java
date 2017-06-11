import java.util.LinkedList;
import java.util.List;

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
        neighbors = new LinkedList<Node>();
        f = g = Double.POSITIVE_INFINITY;
    }

    public boolean equals(Node c) {
        return  (c.x == this.x && c.y == this.y);
    }

}