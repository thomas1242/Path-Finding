public class CellLoc {
    int x;
    int y;
    Node node;
    public CellLoc(int x, int y, Node node) {
        this.x = x;
        this.y = y;
        this.node = node;
    }

    public boolean equals(CellLoc c) {
        return  (c.x == this.x && c.y == this.y);
    }
}
