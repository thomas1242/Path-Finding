import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

import java.awt.image.BufferedImage;
import java.util.*;


public class ImagePanel extends JLayeredPane {

    // Image
    private BufferedImage image = null;
    private Graphics2D g2d = null;

    // Colors
    private Color start_color      =  Color.GREEN;
    private Color end_color        =  Color.RED;
    private Color visited_color    =  Color.LIGHT_GRAY;
    private Color passable_color   =  new Color(120,120,120, 255);
    private Color impassable_color =  Color.BLACK;
    private Color edge_color       =  new Color(0xffFFF1A5); // 0xffFFD700
    private Color grid_line_color  =  new Color(0, 0, 0, 255);
    private Color path_line_color  =  Color.BLACK;
    private Color path_cell_color  =  new Color(0xff9bcc5a);

    private Node[][] grid  = null;
    private Node startPoint, endPoint;
    private int cell_width  = 60;
    private int frame_delay = 25;
    private boolean draggingStart = false, draggingEnd  = false;
    private boolean drawingWalls  = false, erasingWalls = false;
    private Color[] cellColors;

    private ControlPanel controlPanel;
    private static boolean isRunning = false;

    public ImagePanel(int width, int height) {
        setBounds(0, 0, width, height);
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        g2d = (Graphics2D)image.getGraphics();
       
        createGrid();
        addComponents();
        addListeners();
        drawAll();
    }

    public void drawStartPoint() {
        g2d.setColor(start_color);
        g2d.fillRect(startPoint.x * cell_width + 1, startPoint.y * cell_width + 1, cell_width - 1, cell_width - 1);
    }

    public void drawEndPoint() {
        g2d.setColor(end_color);
        g2d.fillRect(endPoint.x * cell_width + 1, endPoint.y * cell_width + 1, cell_width - 1, cell_width - 1);
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

    public void defaultStartEndLocs() {
        setStartPoint((int)(grid.length * 0.3), (int)(grid[0].length * 0.5));
        setEndPoint((int)(grid.length * 0.65), (int)(grid[0].length * 0.5));
    }

    public void addComponents() {
        controlPanel = new ControlPanel(this);
        add(controlPanel, new Integer(3));
    }

    public void createGrid() {
        grid = new Node[image.getWidth()/cell_width + 1][image.getHeight()/cell_width + 1];

        for (int i = 0; i < grid.length; i++)
            for (int j = 0; j < grid[i].length; j++)
                grid[i][j] = new Node(i, j);

        generateCellColors();
        setNeighbors();
        defaultStartEndLocs();
    }

    public void setNeighbors() {    

         for (int i = 0; i < grid.length; i++) 
            for (int j = 0; j < grid[i].length; j++) 
                for (int n = -1; n <= 1; n++) 
                    for (int m = -1; m <= 1; m++) {
                        int x_step = i + n;
                        int y_step = j + m;
                        if(m == 0 || n == 0)
                            if (!(i == x_step && j == y_step) && x_step < grid.length && x_step >= 0 && y_step < grid[i].length && y_step >= 0)
                                grid[i][j].neighbors.add(grid[x_step][y_step]);
                    }
                
        // O( 9*nm where n = #rows, m = #cols)
        // for (int i = 0; i < grid.length; i++) {
        //     for (int j = 0; j < grid[i].length; j++) {
        //         for (int n = -1; n <= 1; n++) {
        //             for (int m = -1; m <= 1; m++) {
        //                 int x_step = i + n;
        //                 int y_step = j + m;
        //                 if (!(i == x_step && j == y_step) && x_step < grid.length && x_step >= 0 && y_step < grid[i].length && y_step >= 0)
        //                     grid[i][j].neighbors.add(grid[x_step][y_step]);
        //             }
        //         }
        //     }
        // }
    }

    public void drawGrid() {
        g2d.setColor(passable_color);
        g2d.fillRect(0, 0, image.getWidth(), image.getHeight());

        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if (!grid[i][j].isPassable) 
                    drawCell(i, j, impassable_color, 0);
                else if (grid[i][j].isVisited) 
                    drawCell(i, j, visited_color, 0);
                else if (grid[i][j].isQueued) 
                    drawCell(i, j, edge_color, 0);
            }
        }

        drawGridLines();
    }

    private void drawGridLines() {
        g2d.setStroke( new BasicStroke( 1.0f,  BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND ) );
        g2d.setColor(grid_line_color);
        for (int i = 0; i < image.getHeight(); i += cell_width)
            g2d.drawLine(0, i, image.getWidth(), i );
        for (int i = 0; i < image.getWidth(); i += cell_width)
            g2d.drawLine(i, 0, i, image.getHeight() );
    }

    public void drawCell(int x, int y, Color color, int delay) {

        if(startPoint.equals(grid[x][y]) || endPoint.equals(grid[x][y]))
            return;

        x = x * cell_width + 1;
        y = y * cell_width + 1;
        g2d.setColor(color);
        g2d.fillRect(x, y, cell_width - 1, cell_width - 1);    
        repaint();

        try {
             Thread.sleep(delay);
        } catch (InterruptedException e) {
             e.printStackTrace();
        }
    
    }

    public void drawLineBetweenTwoCells(Node n1, Node n2) {
        int x1 = n1.x * cell_width + cell_width / 2 + 1;
        int x2 = n2.x * cell_width + cell_width / 2 + 1;
        int y1 = n1.y * cell_width + cell_width / 2 + 1;
        int y2 = n2.y * cell_width + cell_width / 2 + 1;

        g2d.setStroke( new BasicStroke( (float)(cell_width * .1),  BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND ) );
        g2d.setColor(path_line_color);
        g2d.drawLine(x1, y1, x2, y2);
        repaint();
    }

    public void updateCellSize(int size) {
        cell_width = size;
        createGrid();
        drawAll();
    }

    public void clearWalls() {
        for (int i = 0; i < grid.length; i++)
            for (int j = 0; j < grid[i].length; j++) 
                grid[i][j].isPassable = true;
        drawAll();
    }

    public void clearPath() {
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                grid[i][j].isVisited = false;
                grid[i][j].isQueued = false;
            }
        }
        drawAll();
    }

    private double[] getDeltas(int start, int end, int n)
    {
        double start_R, start_G, start_B,       
                 end_R,   end_G,   end_B,
               delta_R, delta_G, delta_B;
        
        end_R = (end >> 16) & 0xFF;             
        end_G = (end >> 8 ) & 0xFF;
        end_B = (end      ) & 0xFF;
        
        start_R = (start >> 16) & 0xFF;         
        start_G = (start >> 8 ) & 0xFF;
        start_B = (start      ) & 0xFF;
        
        delta_R = (end_R - start_R) / n;        // change per color channel
        delta_G = (end_G - start_G) / n;
        delta_B = (end_B - start_B) / n;
        
        double[] deltas = { delta_R, delta_G, delta_B };    
        return deltas;                                      
    }

    private Color[] getColors(int start, int end, int length) {

        Color[] colors = new Color[length];
        
        int intARGB;                            // integer to hold synthesized color values
        int value = start;                                            
        double value_R = (value >> 16) & 0xFF;
        double value_G = (value >> 8 ) & 0xFF;
        double value_B = (value      ) & 0xFF;
        
        double[] deltas = getDeltas( start, end, colors.length - 1 );  
        colors[0] = new Color(start);
        colors[colors.length - 1] = new Color(end);
        
        // fill with interpolated Colors
        for (int i = 1; i < colors.length - 1; i++) {         
            value_R += deltas[0];
            value_G += deltas[1];
            value_B += deltas[2];
             
            intARGB = (0xFF000000) | ((int)value_R << 16) | ((int)value_G << 8) | (int)value_B;
            colors[i] = new Color(intARGB);
        }

        return colors;
    }

    private void generateCellColors() {
        cellColors = getColors( 0xffcccccc, 0x0fFFD700, grid.length * grid[0].length);
    }
    
    public void drawPath(Node curr) {
        Color[] colors = getColors( 0xff00ff00, 0xffff0000, getPathLength(curr) );
        int n = colors.length;

        Node prev = curr;
        curr = curr.parent;
        while(curr.parent != null) {
            while(paused()) {}
            drawCell(curr.x, curr.y, colors[--n], frame_delay);
            drawLineBetweenTwoCells(prev, curr);
            try {
                Thread.sleep((int)(frame_delay * 2.5));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            prev = curr;
            curr = curr.parent;
        }
        drawLineBetweenTwoCells(prev, curr);
        controlPanel.readySearch();
        isRunning = false;
    }

    public int getPathLength(Node node) {
        int n = 0;
        while(node != null) {
            node = node.parent;
            n++;
        }
        return n;
    }

    public void drawAll() {
        drawGrid();
        drawEndPoint();
        drawStartPoint();
        repaint();
    }

    public void addListeners() {

        addMouseListener( new MouseListener()
        {

            @Override
            public void mousePressed(MouseEvent event )
            {
                Point point = event.getPoint();
                int x = (int)point.getX() / cell_width;
                int y = (int)point.getY() / cell_width;

                if( !isValidLoc(x, y) )
                    return;

                if(startPoint.x == x && startPoint.y == y)
                    draggingStart = true;
                else if(endPoint.x == x && endPoint.y == y)
                    draggingEnd = true;
                else if (grid[ x ][ y ].isPassable)
                    drawingWalls = true;
                else if (!grid[ x ][ y ].isPassable)
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

        addMouseMotionListener( new MouseMotionListener()
        {
            public void mouseDragged(MouseEvent event) {
                Point point = event.getPoint();

                int x = (int) point.getX() / cell_width;
                int y = (int) point.getY() / cell_width;

                if( !isValidLoc(x, y) )
                    return;

                Node newLoc = grid[x][y];
                if( endPoint.equals(newLoc) || startPoint.equals(newLoc))   // do nothing
                    return;

                if(draggingStart)
                    setStartPoint(x, y);
                else if (draggingEnd)
                    setEndPoint(x, y);
                else if (drawingWalls && grid[ x ][ y ].isPassable)
                    grid[ x ][ y ].isPassable = false;
                else if (erasingWalls && !grid[ x ][ y ].isPassable)
                    grid[ x ][ y ].isPassable = true;

                drawAll();
            }

            public void mouseMoved(MouseEvent event) {}
        });
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

    public int getStartNodeDistance(Node n) {
        return (int)Math.sqrt( Math.pow(startPoint.x - n.x, 2) + Math.pow(startPoint.y - n.y, 2) );
    }

    public void Dijkstra() {

        // TODO
        
    }

    public void A_Star() {

        new Thread(new Runnable() {
            @Override
            public void run() {

                    int d = 0;

                    LinkedList<Node> openSet = new LinkedList<Node>();
                    LinkedList<Node> closedSet = new LinkedList<Node>();

                    openSet.add( startPoint );

                    Node curr = null;

                    while( !openSet.isEmpty() ) {
                        
                        curr = openSet.get( getLowest(openSet) );
                        openSet.remove(curr);
                        closedSet.add(curr);
                        curr.g = distance_between(startPoint, curr);
                        curr.f = distance_between(curr, endPoint);

                        if(curr.equals(endPoint))
                            break;

                        drawCell(curr.x, curr.y, cellColors[d++ ], frame_delay);

                         for (Node n : curr.neighbors) {
                            if(!n.isPassable || closedSet.contains(n))
                                continue;

                            if(!openSet.contains(n))
                                openSet.add(n);

                            n.isQueued = true;
                            n.isVisited = true;
                            drawCell(n.x, n.y, edge_color, frame_delay);
                            
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
        }).start();
    }


    public double distance_between(Node a, Node b) {
        return Math.sqrt( Math.pow(a.x - b.x, 2) + Math.pow(a.y - b.y, 2) );
    }


    public int getLowest(LinkedList<Node> list) {

        double min = list.get(0).f;
        int n = 0;
        for(int i = 0; i < list.size(); i++) {
            if(list.get(i).f < min) {
                n = i;
                min = list.get(i).f;
            }
        }

        return n;
    }

    public void BFS() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                int d = 0;

                LinkedList<Node> q = new LinkedList<Node>();
                q.add( startPoint );

                Node curr = null;

                while( !q.isEmpty() ) {
                    while( paused() ) {}

                    curr = q.poll();
                    curr.isVisited = true;

                    if(curr.equals(endPoint))
                        break;

                    drawCell(curr.x, curr.y, cellColors[d++ ], frame_delay);//vis

                    for (Node n : curr.neighbors) {
                        if(!n.isVisited && n.isPassable) {
                            q.add(n);
                            n.isQueued = true;
                            n.isVisited = true;
                            n.parent = curr;
                           
                            drawCell(n.x, n.y, edge_color, frame_delay);
                        }
                    }
                }

                drawPath(curr);
            }
        }).start();
    }


    public void DFS() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                int d = 0;

                Stack<Node> stack = new Stack<Node>();
                stack.push( startPoint );

                Node curr = null;

                while( !stack.isEmpty() ) {
                    while( paused() ) {}

                    curr = stack.pop();
                    curr.isVisited = true;

                    drawCell(curr.x, curr.y, cellColors[d++], frame_delay);

                    if(curr.equals(endPoint))
                        break;

                    for (Node n : curr.neighbors) {
                        if(!n.isVisited && n.isPassable) {
                            stack.push(n);
                            n.isQueued = true;
                            n.isVisited = true;
                            n.parent = curr;

                            drawCell(n.x, n.y, edge_color, frame_delay);
                        }
                    }
                }

                drawPath(curr);
            }
        }).start();
    }

    public void createMaze() {

        new Thread(new Runnable() {
            @Override
            public void run() {

                for (int i = 0; i < grid.length; i++ ) {
                    for (int j = 0; j < grid[0].length; j++ ) {
                        if(grid[i][j].equals(startPoint) || grid[i][j].equals(endPoint))
                            continue;
                        grid[i][j].isPassable = false;
                        grid[i][j].isVisited = false;
                    }
                }
                g2d.setColor( impassable_color );
                g2d.fillRect(0, 0, image.getWidth(), image.getHeight());

                drawStartPoint();
                drawEndPoint();
                repaint();

                int d = 0;
                boolean[][] vis = new boolean[grid.length][grid[0].length];

                Stack<Node> stack = new Stack<Node>();
                Node center = grid[grid.length/2][grid[0].length/2];
                Node curr = center;

                Random rand = new Random();
                int rand_neighbor;

                stack.add(center);

                while( !stack.isEmpty() ) {
                    while(paused()) {}

                    boolean flag = false;
                    rand_neighbor = rand.nextInt(curr.neighbors.size());
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
                                d++;
                            }
                            curr.isPassable = true;
                            drawCell(prev.x, prev.y, cellColors[d], 0);
                            drawCell(curr.x, curr.y, passable_color, frame_delay);
                    }
                    if( !flag ) 
                        curr = stack.pop();
                }

                controlPanel.readyMaze();
            }
        }).start();
    }

    public void setSearchState(boolean isRunning) {
        this.isRunning = isRunning;
    }

    public void setFrameDelay(int delay) { 
        frame_delay = delay;
    }

    protected void paintComponent(Graphics g) {
        g.drawImage(image, 0, 0, null);
    }
}

class SpeedSlider extends JPanel {

    ImagePanel imagePanel;
    JSlider slider;
    JLabel label;

    public SpeedSlider(ImagePanel imagePanel) {
        setLayout(new GridLayout(0, 1));
        this.imagePanel = imagePanel;

        label = new JLabel(" Speed");
        label.setFont(new Font("plain", Font.BOLD, 14));
        label.setForeground( new Color(0xffbbbbbb) );

        slider = new JSlider(0, 50, 25);
        slider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                imagePanel.setFrameDelay(slider.getValue());
            }
        });;

        slider.setMinorTickSpacing(1);
        slider.setPaintTicks(true);
        slider.setSnapToTicks(true);

        add(label, BorderLayout.CENTER);
        add(slider, BorderLayout.SOUTH);

        setOpaque(false);
        setVisible(true);
    }
}

class SizeSlider extends JPanel {

    ImagePanel imagePanel;
    JSlider slider;
    JLabel label;

    public SizeSlider(ImagePanel imagePanel) {
        setLayout(new GridLayout(0, 1));
        this.imagePanel = imagePanel;

        label = new JLabel();
        label.setFont(new Font("plain", Font.BOLD, 14));
        label.setForeground( new Color(0xffbbbbbb) );


        slider = new JSlider(2, 120, 60);
        label.setText(" " + String.valueOf(imagePanel.getHeight() / slider.getValue() + 1) + " rows , " + String.valueOf(imagePanel.getWidth() / slider.getValue() + 1)  + " columns ");

        slider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int size = slider.getValue();
                label.setText(" " + String.valueOf(imagePanel.getHeight() / size + 1) + " rows , " + String.valueOf(imagePanel.getWidth() / size + 1)  + " columns ");
                imagePanel.updateCellSize( size );
            }
        });;

        slider.setMinorTickSpacing(3);
        slider.setPaintTicks(true);
        slider.setSnapToTicks(true);

      add(label, BorderLayout.CENTER);
      add(slider, BorderLayout.SOUTH);

      setOpaque(false );
      setVisible(true);
    }
}

