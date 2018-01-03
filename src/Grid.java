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

        connectAdjacentNodes();
    }

    private void connectAdjacentNodes() {
        for (int i = 0; i < grid.length; i++)
            for (int j = 0; j < grid[i].length; j++)
                for (int n = -1; n <= 1; n++)
                    for (int m = -1; m <= 1; m++) 
                        if ((m & n) == 0 && (m | n) != 0 && isValidLoc(i + n, j + m))
                            grid[i][j].neighbors.add(grid[i + n][j + m]);
    }

    public boolean isValidLoc(int x, int y) {
        return x >= 0 && x < grid.length && y >= 0 && y < grid[0].length;
    }

    public void setStartPoint(int x, int y) {
        if(isValidLoc(x, y) && grid[x][y].isPassable)
            startPoint = grid[x][y];
    }
    public void setEndPoint(int x, int y) {
        if(isValidLoc(x, y) && grid[x][y].isPassable)
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

    public int size() {
        return grid.length * grid[0].length;
    }

    public Node[][] getGrid() {
        return grid;
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
        for (int i = 0; i < grid.length; i++) 
            for (int j = 0; j < grid[i].length; j++) 
                grid[i][j].isVisited = grid[i][j].isQueued = false;
    }

    public int getNumRows() {
        return grid.length;
    }

    public int getNumCols() {
        return grid[0].length;
    }
    
    public Node getCenterNode() {
        return grid[ grid.length / 2 ][ grid[0].length / 2 ];
    }

    public void reset(String s) {
        for (int i = 0; i < grid.length; i++ )
            for (int j = 0; j < grid[0].length; j++ )
                if(!isStartPoint(i, j) && !isEndPoint(i, j)) {
                    if(s.equals("Maze")) 
                        grid[i][j].isPassable = grid[i][j].isVisited = false;
                    else if (s.equals("A*")) 
                        grid[i][j].g = grid[i][j].f = Double.MAX_VALUE;
                }
    }

    public boolean isStartPoint(int x, int y) {
        return startPoint.x == x && startPoint.y == y;
    }
    public boolean isStartPoint(Node node) { 
        return startPoint.x == node.x && startPoint.y == node.y; 
    }

    public boolean isEndPoint(int x, int y) {
        return endPoint.x == x && endPoint.y == y;
    }
    public boolean isEndPoint(Node node) {
        return endPoint.x == node.x && endPoint.y == node.y;
    }
}