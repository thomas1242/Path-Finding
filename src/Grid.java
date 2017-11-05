public class Grid  {

    private Node[][] grid;
    private Node startPoint, endPoint;

    public Grid(int numRows, int numCols) {
        createGrid(numRows, numCols);
        defaultStartEndLocs();
    }

    public void createGrid(int numRows, int numCols) {
        grid = new Node[numRows][numCols];

        for (int i = 0; i < grid.length; i++)
            for (int j = 0; j < grid[i].length; j++)
                grid[i][j] = new Node(i, j);

        connectNeighbors();
    }

    private void connectNeighbors() {
        for (int i = 0; i < grid.length; i++)
            for (int j = 0; j < grid[i].length; j++)
                for (int n = -1; n <= 1; n++)
                    for (int m = -1; m <= 1; m++) {
                        int x_step = i + n;
                        int y_step = j + m;
                        if (m == 0 || n == 0)
                            if ( !(i == x_step && j == y_step) && isValidLoc(x_step, y_step) )
                                grid[i][j].neighbors.add(grid[x_step][y_step]);
                    }
    }

    public boolean isValidLoc(int x, int y) {
        return !( x >= grid.length || y >= grid[0].length || x < 0 || y < 0 );
    }

    public void setStartPoint(int x, int y) {
        if( isValidLoc(x, y) && grid[x][y].isPassable )
            startPoint = grid[x][y];
    }
    public void setEndPoint(int x, int y) {
        if( isValidLoc(x, y) && grid[x][y].isPassable )
            endPoint = grid[x][y];
    }

    public Node getStartPoint() {
        return startPoint;
    }

    public Node getEndPoint() {
        return endPoint;
    }

    public void defaultStartEndLocs() {
        setStartPoint((int)(grid.length * 0.3), (int)(grid[0].length * 0.5));
        setEndPoint((int)(grid.length * 0.65), (int)(grid[0].length * 0.5));
    }

    public int getNumberOfNodes() {
        return grid.length * grid[0].length;
    }

    public Node[][] getGrid() {
        return grid;
    }

    public Node nodeAt(int x, int y) {
        return grid[x][y];
    }

    public void makeNodePassable(int x, int y) {
        grid[x][y].isPassable = true;
    }

    public void makeNodeUnpassable(int x, int y) {
        grid[x][y].isPassable = false;
    }

    public boolean isPassable(int x, int y) {
        return grid[x][y].isPassable;
    }

    public void clearWalls() {
        for (int i = 0; i < grid.length; i++)
            for (int j = 0; j < grid[i].length; j++)
                grid[i][j].isPassable = true;
    }

    public void clearPath() {
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                grid[i][j].isVisited = false;
                grid[i][j].isQueued = false;
            }
        }
    }

    public int numRows() {
        return grid.length;
    }

    public int numCols() {
        return grid[0].length;
    }

    public Node getCenterNode() {
        return grid[ grid.length / 2 ][ grid[0].length / 2 ];
    }

    public void AstarReset() {
        for (int i = 0; i < grid.length; i++ ) {
            for (int j = 0; j < grid[0].length; j++ ) {
                if( !(grid[i][j].equals(startPoint) || grid[i][j].equals(endPoint)) ) {
                    grid[i][j].g = Double.MAX_VALUE;
                    grid[i][j].f = Double.MAX_VALUE;
                }
            }
        }
    }

    public void MazeReset() {
        for (int i = 0; i < grid.length; i++ ) {
            for (int j = 0; j < grid[0].length; j++ ) {
                if( !(grid[i][j].equals(startPoint) || grid[i][j].equals(endPoint)) ) {
                    grid[i][j].isPassable = false;
                    grid[i][j].isVisited = false;
                }
            }
        }
    }

    public boolean isStartPoint(int x, int y) {
        return startPoint.x == x && startPoint.y == y;
    }
    public boolean isStartPoint(Node node) { return startPoint.x == node.x && startPoint.y == node.y; }

    public boolean isEndPoint(int x, int y) {
        return endPoint.x == x && endPoint.y == y;
    }
    public boolean isEndPoint(Node node) {
        return endPoint.x == node.x && endPoint.y == node.y;
    }

}