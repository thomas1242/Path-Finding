import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

public class ImagePanel extends JLayeredPane {

    private BufferedImage image = null;
    private Graphics2D g2d = null;

    private Grid grid = null;
    private int cellWidth_pixels = 60;
    private Color[] cellColors = null;

    private ControlPanel controlPanel = null;
    private boolean draggingStart = false, draggingEnd  = false;
    private boolean drawingWalls  = false, erasingWalls = false;
    private int frameDelay_ms = 25;

    public ImagePanel(int width, int height) {
        setBounds(0, 0, width, height);
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        g2d = (Graphics2D)image.getGraphics();
       
        createGrid();
        generateCellColors();
        addComponents();
        addListeners();
        drawAll();
    }

    private void createGrid() {
        int numRows = image.getWidth() / cellWidth_pixels + 1;
        int numCols = image.getHeight() / cellWidth_pixels + 1;
        grid = new Grid( numRows, numCols );
    }

    private void addComponents() {
        controlPanel = new ControlPanel(this);
        add(controlPanel, new Integer(3));
    }

    protected void paintComponent(Graphics g) {
        g.drawImage(image, 0, 0, null);
    }

    private void drawStartPoint() {
        g2d.setColor(Colors.start_color);
        g2d.fillRect(grid.getStartPoint().x * cellWidth_pixels + 1, grid.getStartPoint().y * cellWidth_pixels + 1, cellWidth_pixels - 1, cellWidth_pixels - 1);
    }

    private void drawEndPoint() {
        g2d.setColor(Colors.end_color);
        g2d.fillRect(grid.getEndPoint().x * cellWidth_pixels + 1, grid.getEndPoint().y * cellWidth_pixels + 1, cellWidth_pixels - 1, cellWidth_pixels - 1);
    }

    private void generateCellColors() {
        cellColors = Interpolation.interpolateColors( 0xffcccccc, 0x0fFFD700, grid.getNumberOfNodes());
    }

    private void drawAll() {
        drawGrid();
        drawStartPoint();
        drawEndPoint();
        repaint();
    }

    private void drawGrid() {
        g2d.setColor(Colors.passable_color);
        g2d.fillRect(0, 0, image.getWidth(), image.getHeight());

        Node[][] _grid = grid.getGrid();
        for (int i = 0; i < _grid.length; i++) {
            for (int j = 0; j < _grid[i].length; j++) {
                Node cell = _grid[i][j];
                if (!cell.isPassable)
                    drawCell(cell, Colors.impassable_color, 0);
                else if (cell.isVisited)
                    drawCell(cell, Colors.visited_color, 0);
                else if (cell.isQueued)
                    drawCell(cell, Colors.edge_color, 0);
            }
        }

        drawGridLines();
    }

    private void drawGridLines() {
        g2d.setStroke( new BasicStroke( 1.0f,  BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND ) );
        g2d.setColor(Colors.grid_line_color);

        int imgWidth = image.getWidth();
        int imgHeight = image.getHeight();

        for (int i = 0; i < imgHeight; i += cellWidth_pixels)     g2d.drawLine(0, i, imgWidth, i);
        for (int i = 0; i < imgWidth;  i += cellWidth_pixels)     g2d.drawLine(i, 0, i, imgHeight);
    }

    private void drawCell(Node cell, Color color, int delay) {
        if(grid.isStartPoint(cell) || grid.isEndPoint(cell))
            return;

        int x = cell.x * cellWidth_pixels + 1;
        int y = cell.y * cellWidth_pixels + 1;
        g2d.setColor(color);
        g2d.fillRect(x, y, cellWidth_pixels - 1, cellWidth_pixels - 1);
        repaint();

        try { Thread.sleep(delay); }
        catch (Exception e) {}
    }

    private void addListeners() {
        addMouseListener( new MouseListener() {
            public void mousePressed(MouseEvent event) {
                Point point = event.getPoint();
                int x = (int)point.getX() / cellWidth_pixels;
                int y = (int)point.getY() / cellWidth_pixels;

                if( !grid.isValidLoc(x, y) ) return;

                if      (grid.isStartPoint(x, y))  draggingStart = true;
                else if (grid.isEndPoint(x, y))    draggingEnd = true;
                else if (grid.isPassable(x, y))    drawingWalls = true;
                else if (!grid.isPassable(x, y))   erasingWalls = true;
            }
            public void mouseReleased(MouseEvent e) {
                if (draggingStart)       draggingStart = false;
                else if(draggingEnd)     draggingEnd = false;
                else if (drawingWalls)   drawingWalls = false;
                else if (erasingWalls)   erasingWalls = false;
            }
            public void mouseClicked(MouseEvent e) {}
            public void mouseEntered(MouseEvent e) {}
            public void mouseExited(MouseEvent e) {}
        } );

        addMouseMotionListener( new MouseMotionListener() {
            public void mouseDragged(MouseEvent event) {
                Point point = event.getPoint();
                int x = (int) point.getX() / cellWidth_pixels;
                int y = (int) point.getY() / cellWidth_pixels;

                if(!grid.isValidLoc(x, y) || grid.isStartPoint(x, y) || grid.isEndPoint(x, y))
                    return;

                if(draggingStart)                                   grid.setStartPoint(x, y);
                else if (draggingEnd)                               grid.setEndPoint(x, y);
                else if (drawingWalls && grid.isPassable(x, y))     grid.makeNodeUnpassable(x, y);
                else if (erasingWalls && !grid.isPassable(x, y))    grid.makeNodePassable(x, y);
                drawAll();
            }
            public void mouseMoved(MouseEvent event) {}
        });
    }

    public void updateCellSize(int size) {
        cellWidth_pixels = size;
        createGrid();
        generateCellColors();
        drawAll();
    }

    public void clearWalls() {
        grid.clearWalls();
        drawAll();
    }

    public void clearPath() {
        grid.clearPath();
        drawAll();
    }

    public boolean paused() {
        return !controlPanel.isRunning();
    }

    public void Dijkstra() {
        int distanceFromStart = 0;

        Queue<Node> q = new LinkedList<>();
        Node startPoint = grid.getStartPoint();
        q.add( startPoint );
        Node curr = null;

        while( !q.isEmpty() ) {
            while( paused() );

            curr = q.poll();
            curr.isVisited = true;
            if(grid.isEndPoint(curr))
                break;

            drawCell(curr, cellColors[distanceFromStart++ ], frameDelay_ms);

            for (Node n : curr.neighbors) {
                if(!n.isVisited && n.isPassable) {
                    n.isQueued = true;
                    n.isVisited = true;
                    n.parent = curr;
                    q.add(n);
                    drawCell(n, Colors.edge_color, frameDelay_ms);
                }
            }
        }

        drawPath(curr);
    }

    public void A_Star() {
        grid.AstarReset();

        int distanceFromStart = 0;
        LinkedList<Node> openSet = new LinkedList<>();
        LinkedList<Node> closedSet = new LinkedList<>();

        Node startPoint = grid.getStartPoint();
        Node endPoint = grid.getEndPoint();
        openSet.add( startPoint );
        startPoint.f = distance_between(startPoint, endPoint);

        Node curr = null;
        while( !openSet.isEmpty() ) {
            while (paused());
            
            curr = openSet.get( getLowest(openSet) );
            openSet.remove(curr);
            closedSet.add(curr);
            curr.g = distance_between(startPoint, curr);
            curr.f = distance_between(curr, endPoint);

            if(grid.isEndPoint(curr))
                break;

            drawCell(curr, cellColors[distanceFromStart++ ], frameDelay_ms);

             for (Node n : curr.neighbors) {
                if(!n.isPassable)
                    closedSet.add(n);
                if(closedSet.contains(n))
                    continue;
                if(!openSet.contains(n))
                    openSet.add(n);

                n.isQueued = true;
                n.isVisited = true;
                drawCell(n, Colors.edge_color, frameDelay_ms);
                
                double temp = curr.g + distance_between(curr, n);
                if(temp >= n.g) // not a better path
                    continue;

                 n.parent = curr;
                 n.g = temp;
                 n.f = temp + distance_between(n , endPoint);
            }
        }

        drawPath(curr);
    }

    private double distance_between(Node a, Node b) {
        return Math.sqrt( Math.pow(a.x - b.x, 2) + Math.pow(a.y - b.y, 2) );
    }

    private int getLowest(LinkedList<Node> list) {
        double min = list.get(0).f;
        int n = 0;
        for(int i = 1; i < list.size(); i++) {
            if(list.get(i).f < min) {
                n = i;
                min = list.get(i).f;
            }
        }
        return n;
    }

    public void BFS() {
        int distanceFromStart = 0;

        Node startPoint = grid.getStartPoint();
        Queue<Node> q = new LinkedList<>();
        q.add( startPoint );

        Node curr = null;
        while( !q.isEmpty() ) {
            while( paused() );

            curr = q.poll();
            curr.isVisited = true;

            if(grid.isEndPoint(curr))
                break;
            drawCell(curr, cellColors[distanceFromStart++ ], frameDelay_ms);

            for (Node n : curr.neighbors) {
                if(!n.isVisited && n.isPassable) {
                    n.isQueued = true;
                    n.isVisited = true;
                    n.parent = curr;
                    q.add(n);
                    drawCell(n, Colors.edge_color, frameDelay_ms);
                }
            }
        }

        drawPath(curr);
    }

    public void DFS() {
        int distanceFromStart = 0;

        Node startPoint = grid.getStartPoint();
        Stack<Node> stack = new Stack<>();
        stack.push( startPoint );

        Node curr = null;
        while( !stack.isEmpty() ) {
            while( paused() );

            curr = stack.pop();
            curr.isVisited = true;
            drawCell(curr, cellColors[distanceFromStart++], frameDelay_ms);

            if(grid.isEndPoint(curr))
                break;

            for (Node n : curr.neighbors) {
                if(!n.isVisited && n.isPassable) {
                    n.isQueued = true;
                    n.isVisited = true;
                    n.parent = curr;
                    stack.push(n);
                    drawCell(n, Colors.edge_color, frameDelay_ms);
                }
            }
        }

        drawPath(curr);
    }

    public void createMaze() {
        grid.MazeReset();
        g2d.setColor( Colors.impassable_color );
        g2d.fillRect(0, 0, image.getWidth(), image.getHeight());

        drawStartPoint();
        drawEndPoint();
        repaint();

        int distanceFromStart = 0;
        boolean[][] vis = new boolean[grid.getNumRows()][grid.getNumCols()];

        Stack<Node> stack = new Stack<>();
        Node center = grid.getCenterNode();
        stack.add(center);
        Node curr = center;

        Random rand = new Random();

        while( !stack.isEmpty() ) {
            while(paused());

            boolean flag = false;
            int rand_neighbor = rand.nextInt(curr.neighbors.size());
            Node prev = curr;
        
            for(int z = 0; z < curr.neighbors.size(); z++) {
                int v = (z + rand_neighbor) % curr.neighbors.size();
                Node n = curr.neighbors.get(v);
                if(flag)
                    vis[n.x][n.y] = true;
                else if(!vis[n.x][n.y] ) {
                    stack.push(curr);
                    vis[curr.x][curr.y] = true;
                    prev = curr;
                    curr = n;
                    vis[curr.x][curr.y] = true;
                    flag = true;
                    distanceFromStart++;
                }
                curr.isPassable = true;
                drawCell(prev, cellColors[distanceFromStart], 0);
                drawCell(curr, Colors.passable_color, frameDelay_ms);
            }
            if( !flag ) 
                curr = stack.pop();
        }
    }

    private void drawPath(Node node) {
        Color[] pathColors = Interpolation.interpolateColors( Color.GREEN, Color.RED, getPathLength(node) );
        int index = pathColors.length;

        while(node.parent != null) {
            while(paused());
            node = node.parent;
            drawCell(node, pathColors[--index], frameDelay_ms * 5);
        }
    }

    private int getPathLength(Node node) {
        int pathLength = 0;
        while(node != null) {
            node = node.parent;
            pathLength++;
        }
        return pathLength;
    }

    public void setFrameDelay(int delay) { 
        frameDelay_ms = delay;
    }

    private static class Colors {
        private static Color start_color      =  Color.GREEN;
        private static Color end_color        =  Color.RED;
        private static Color visited_color    =  Color.LIGHT_GRAY;
        private static Color passable_color   =  new Color(120,120,120, 255);
        private static Color impassable_color =  Color.BLACK;
        private static Color edge_color       =  new Color(0xffFFF1A5);
        private static Color grid_line_color  =  new Color(0, 0, 0, 255);
    }
}