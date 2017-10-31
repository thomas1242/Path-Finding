import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.*;

public class ImagePanel extends JLayeredPane {

    // Image
    private BufferedImage image = null;
    private Graphics2D g2d = null;

    // Grid
    private Grid grid;
    private int cellPixelWidth = 60;

    // Colors
    private Color start_color      =  Color.GREEN;
    private Color end_color        =  Color.RED;
    private Color visited_color    =  Color.LIGHT_GRAY;
    private Color passable_color   =  new Color(120,120,120, 255);
    private Color impassable_color =  Color.BLACK;
    private Color edge_color       =  new Color(0xffFFF1A5);
    private Color grid_line_color  =  new Color(0, 0, 0, 255);
    private Color path_line_color  =  Color.BLACK;
    private Color[] cellColors;

    private ControlPanel controlPanel;
    private boolean draggingStart = false, draggingEnd  = false;
    private boolean drawingWalls  = false, erasingWalls = false;
    private static boolean isRunning = false;
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
        int numRows = image.getWidth() / cellPixelWidth + 1;
        int numCols = image.getHeight() / cellPixelWidth + 1;
        grid = new Grid( numRows, numCols );
        grid.defaultStartEndLocs();
    }

    private void addComponents() {
        controlPanel = new ControlPanel(this);
        add(controlPanel, new Integer(3));
    }

    private void drawStartPoint() {
        g2d.setColor(start_color);
        g2d.fillRect(grid.getStartPoint().x * cellPixelWidth + 1, grid.getStartPoint().y * cellPixelWidth + 1, cellPixelWidth - 1, cellPixelWidth - 1);
    }

    private void drawEndPoint() {
        g2d.setColor(end_color);
        g2d.fillRect(grid.getEndPoint().x * cellPixelWidth + 1, grid.getEndPoint().y * cellPixelWidth + 1, cellPixelWidth - 1, cellPixelWidth - 1);
    }

    private void generateCellColors() {
        cellColors = Interpolation.getColors( 0xffcccccc, 0x0fFFD700, grid.getNumberOfNodes());
    }

    private void drawAll() {
        drawGrid();
        drawStartPoint();
        drawEndPoint();
        repaint();
    }

    private void drawGrid() {
        g2d.setColor(passable_color);
        g2d.fillRect(0, 0, image.getWidth(), image.getHeight());

        Node[][] _grid = grid.getGrid();

        for (int i = 0; i < _grid.length; i++) {
            for (int j = 0; j < _grid[i].length; j++) {
                Node cell = _grid[i][j];
                if (!cell.isPassable)
                    drawCell(cell, impassable_color, 0);
                else if (cell.isVisited)
                    drawCell(cell, visited_color, 0);
                else if (cell.isQueued)
                    drawCell(cell, edge_color, 0);
            }
        }

        drawGridLines();
    }

    private void drawGridLines() {
        g2d.setStroke( new BasicStroke( 1.0f,  BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND ) );
        g2d.setColor(grid_line_color);
        for (int i = 0; i < image.getHeight(); i += cellPixelWidth)
            g2d.drawLine(0, i, image.getWidth(), i );
        for (int i = 0; i < image.getWidth(); i += cellPixelWidth)
            g2d.drawLine(i, 0, i, image.getHeight() );
    }

    private void drawCell(Node cell, Color color, int delay) {

        if(grid.isStartPoint(cell) || grid.isEndPoint(cell))
            return;

        int x = cell.x * cellPixelWidth + 1;
        int y = cell.y * cellPixelWidth + 1;
        g2d.setColor(color);
        g2d.fillRect(x, y, cellPixelWidth - 1, cellPixelWidth - 1);
        repaint();

        try {
             Thread.sleep(delay);
        } catch (InterruptedException e) {
             e.printStackTrace();
        }
    
    }

    private void drawPath(Node curr) {
        Color[] colors = Interpolation.getColors( 0xff00ff00, 0xffff0000, getPathLength(curr) );
        int n = colors.length;

        Node prev = curr;
        curr = curr.parent;
        while(curr.parent != null) {
            while(paused()) {}
            drawCell(curr, colors[--n], frameDelay_ms);
//            drawPathLineBetweenTwoCells(prev, curr);
            try {
                Thread.sleep((int)(frameDelay_ms * 2.5));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            prev = curr;
            curr = curr.parent;
        }

//        drawPathLineBetweenTwoCells(prev, curr);
        controlPanel.readySearch();
        isRunning = false;
    }

//        private void drawPathLineBetweenTwoCells(Node n1, Node n2) {
//        int x1 = n1.x * cellPixelWidth + cellPixelWidth / 2 + 1;
//        int y1 = n1.y * cellPixelWidth + cellPixelWidth / 2 + 1;
//        int x2 = n2.x * cellPixelWidth + cellPixelWidth / 2 + 1;
//        int y2 = n2.y * cellPixelWidth + cellPixelWidth / 2 + 1;
//
//        g2d.setStroke( new BasicStroke( (float)(cellPixelWidth * .1),  BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND ) );
//        g2d.setColor(path_line_color);
//        g2d.drawLine(x1, y1, x2, y2);
//        repaint();
//    }

    private int getPathLength(Node node) {
        int n = 0;
        while(node != null) {
            node = node.parent;
            n++;
        }
        return n;
    }

    private void addListeners() {
        addMouseListener( new MouseListener() {
            @Override
            public void mousePressed(MouseEvent event ) {
                Point point = event.getPoint();
                int x = (int)point.getX() / cellPixelWidth;
                int y = (int)point.getY() / cellPixelWidth;

                if( !grid.isValidLoc(x, y) )
                    return;

                if( grid.isStartPoint(x, y) )
                    draggingStart = true;
                else if( grid.isEndPoint(x, y) )
                    draggingEnd = true;
                else if ( grid.isPassable(x, y) )
                    drawingWalls = true;
                else if ( !grid.isPassable(x, y) )
                    erasingWalls = true;
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                if(draggingStart)
                    draggingStart = false;
                else if(draggingEnd)
                    draggingEnd = false;
                else if (drawingWalls)
                    drawingWalls = false;
                else if (erasingWalls)
                    erasingWalls = false;
            }
            @Override
            public void mouseClicked(MouseEvent e) {}
            @Override
            public void mouseEntered(MouseEvent e) {}
            @Override
            public void mouseExited(MouseEvent e) {}
        } );

        addMouseMotionListener( new MouseMotionListener() {
            public void mouseDragged(MouseEvent event) {
                Point point = event.getPoint();

                int x = (int) point.getX() / cellPixelWidth;
                int y = (int) point.getY() / cellPixelWidth;

                if( !grid.isValidLoc(x, y) )
                    return;

                Node newLoc = grid.nodeAt(x, y);
                if( grid.isEndPoint(newLoc) || grid.isStartPoint(newLoc) )   // do nothing
                    return;

                if(draggingStart)
                    grid.setStartPoint(x, y);
                else if (draggingEnd)
                    grid.setEndPoint(x, y);
                else if (drawingWalls && grid.isPassable(x, y))
                    grid.makeNodeUnpassable(x, y);
                else if (erasingWalls && !grid.isPassable(x, y))
                    grid.makeNodePassable(x, y);

                drawAll();
            }
            public void mouseMoved(MouseEvent event) {}
        });
    }


    // public API

    public void updateCellSize(int size) {
        cellPixelWidth = size;
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
        if( isRunning )
            return false;
        try {
             Thread.sleep(50);
        }
        catch(InterruptedException e) {}
        return isRunning == false;
    }

    public void Dijkstra() {
        int distanceFromStart = 0;

        Queue<Node> q = new LinkedList<Node>();
        Node startPoint = grid.getStartPoint();
        Node endPoint = grid.getEndPoint();
        q.add( startPoint );
        Node curr = null;

        while( !q.isEmpty() ) {
            while( paused() ) {}

            curr = q.poll();
            curr.isVisited = true;
            if(curr.equals(endPoint))
                break;

            drawCell(curr, cellColors[distanceFromStart++ ], frameDelay_ms);//vis

            for (Node n : curr.neighbors) {
                if(!n.isVisited && n.isPassable) {
                    n.isQueued = true;
                    n.isVisited = true;
                    n.parent = curr;
                    q.add(n);
                    drawCell(n, edge_color, frameDelay_ms);
                }
            }
        }

        drawPath(curr);
    }

    public void A_Star() {
        grid.AstarReset();

        int distanceFromStart = 0;
        LinkedList<Node> openSet = new LinkedList<Node>();
        LinkedList<Node> closedSet = new LinkedList<Node>();

        Node startPoint = grid.getStartPoint();
        Node endPoint = grid.getEndPoint();
        openSet.add( startPoint );
        startPoint.f = distance_between(startPoint, endPoint);

        Node curr = null;
        while( !openSet.isEmpty() ) {
            
            curr = openSet.get( getLowest(openSet) );
            openSet.remove(curr);
            closedSet.add(curr);
            curr.g = distance_between(startPoint, curr);
            curr.f = distance_between(curr, endPoint);

            if(curr.equals(endPoint))
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
                drawCell(n, edge_color, frameDelay_ms);
                
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
        Node endPoint = grid.getEndPoint();
        Queue<Node> q = new LinkedList<Node>();
        q.add( startPoint );

        Node curr = null;
        while( !q.isEmpty() ) {
            while( paused() ) {}

            curr = q.poll();
            curr.isVisited = true;

            if(curr.equals(endPoint))
                break;
            drawCell(curr, cellColors[distanceFromStart++ ], frameDelay_ms);

            for (Node n : curr.neighbors) {
                if(!n.isVisited && n.isPassable) {
                    n.isQueued = true;
                    n.isVisited = true;
                    n.parent = curr;
                    q.add(n);
                    drawCell(n, edge_color, frameDelay_ms);
                }
            }
        }

        drawPath(curr);
    }


    public void DFS() {
        int distanceFromStart = 0;

        Node startPoint = grid.getStartPoint();
        Node endPoint = grid.getEndPoint();
        Stack<Node> stack = new Stack<Node>();
        stack.push( startPoint );

        Node curr = null;
        while( !stack.isEmpty() ) {
            while( paused() ) {}

            curr = stack.pop();
            curr.isVisited = true;
            drawCell(curr, cellColors[distanceFromStart++], frameDelay_ms);

            if(curr.equals(endPoint))
                break;

            for (Node n : curr.neighbors) {
                if(!n.isVisited && n.isPassable) {
                    n.isQueued = true;
                    n.isVisited = true;
                    n.parent = curr;
                    stack.push(n);
                    drawCell(n, edge_color, frameDelay_ms);
                }
            }
        }

        drawPath(curr);
    }

    public void createMaze() {
        grid.MazeReset();
        g2d.setColor( impassable_color );
        g2d.fillRect(0, 0, image.getWidth(), image.getHeight());

        drawStartPoint();
        drawEndPoint();
        repaint();

        int distanceFromStart = 0;
        boolean[][] vis = new boolean[grid.numRows()][grid.numCols()];

        Stack<Node> stack = new Stack<Node>();
        Node center = grid.getCenterNode();
        stack.add(center);
        Node curr = center;

        Random rand = new Random();

        while( !stack.isEmpty() ) {
            while(paused()) {}

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
                    drawCell(curr, passable_color, frameDelay_ms);
            }
            if( !flag ) 
                curr = stack.pop();
        }

        controlPanel.readyMaze();
    }

    public void setSearchState(boolean isRunning) {
        this.isRunning = isRunning;
    }

    public void setFrameDelay(int delay) { 
        frameDelay_ms = delay;
    }

    protected void paintComponent(Graphics g) {
        g.drawImage(image, 0, 0, null);
    }
}