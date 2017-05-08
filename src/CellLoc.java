
public class CellLoc {
    int x;
    int y;
    public CellLoc(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public boolean equals(CellLoc c) {
        return  (c.x == this.x && c.y == this.y);
    }
}
