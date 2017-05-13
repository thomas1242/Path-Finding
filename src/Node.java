import java.util.LinkedList;
import java.util.List;

class Node {

    Node parent;
    List<Node> neighbors;
    boolean isPassable, isVisited, isQueued;
    int x, y;

    public Node(int x, int y) {
        this.x = x;
        this.y = y;
        isPassable = true;
        neighbors = new LinkedList<Node>();
    }

    public boolean equals(Node c) {
        return  (c.x == this.x && c.y == this.y);
    }

}