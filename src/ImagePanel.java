import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.Stack;


public class ImagePanel extends JLayeredPane {

    // Image
    private BufferedImage image = null;
    private Graphics2D g2d = null;

    // Colors
    private Color start_color      =  Color.GREEN;
    private Color end_color        =  Color.RED;
    private Color visited_color    =  Color.LIGHT_GRAY;
    private Color passable_color   =  Color.GRAY;
    private Color impassable_color =  Color.BLACK;
    private Color edge_color       =  new Color(0xffFFD700);
    private Color grid_line_color  =  new Color(0, 0, 0, 255);
    private Color path_line_color  =  new Color(0xff9bcc5a);
    private Color path_cell_color  =  new Color(255, 255, 240, 255);

    // Grid
    private Node[][] grid  = null;
    private Node startPoint, endPoint;
    private int cell_width  = 60;
    private int frame_delay = 32;
    private boolean draggingStart = false, draggingEnd  = false;
    private boolean drawingWalls  = false, erasingWalls = false;


    public ImagePanel(int width, int height) {
        setBounds(0, 0, width, height);
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        g2d = (Graphics2D)image.getGraphics();

        createGrid();
        setNeighbors();
        defaultStartEndLocs();
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
        return !( x >= grid.length || y >= grid[0].length || x < 0 || y < 0);
    }

    public void setStartPoint(int x, int y) {
        if( !isValidLoc(x, y) || !grid[x][y].isPassable)
            return;

        startPoint = grid[x][y];
    }

    public void setEndPoint(int x, int y) {
        if( !isValidLoc(x, y) || !grid[x][y].isPassable)
            return;

        endPoint = grid[x][y];
    }

    public void defaultStartEndLocs() {
        setStartPoint((int)(grid.length * 0.3), (int)(grid[0].length * 0.5));
        setEndPoint((int)(grid.length * 0.7), (int)(grid[0].length * 0.5));
    }

    public void addComponents() {
        add(new ControlPanel(this), new Integer(3));
    }

    public void createGrid() {

        grid = new Node[image.getWidth()/cell_width + 1][image.getHeight()/cell_width + 1];

        for (int i = 0; i < grid.length; i++)
            for (int j = 0; j < grid[i].length; j++)
                grid[i][j] = new Node(i, j);
    }

    public void setNeighbors() {    // O( 9*nm where n = #rows, m = #cols)
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


         for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                for (int n = -1; n <= 1; n++) {
                    for (int m = -1; m <= 1; m++) {
                        int x_step = i + n;
                        int y_step = j + m;
                        if(m == 0 || n == 0)
                            if (!(i == x_step && j == y_step) && x_step < grid.length && x_step >= 0 && y_step < grid[i].length && y_step >= 0)
                                grid[i][j].neighbors.add(grid[x_step][y_step]);
                    }
                }
            }
        }
    }

    public void drawGrid() {
        g2d.setColor(passable_color);
        g2d.fillRect(0, 0, image.getWidth(), image.getHeight());

        int x, y;

        // grid cells
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {

                x = i * cell_width + 1;
                y = j * cell_width + 1;

                if (!grid[i][j].isPassable) {
                    g2d.setColor(impassable_color);
                    g2d.fillRect(x, y, cell_width - 1, cell_width - 1);
                }
                else if (grid[i][j].isQueued) {
                    g2d.setColor(edge_color);
                    g2d.fillRect(x, y, cell_width - 1, cell_width - 1);
                }
                else if (grid[i][j].isVisited) {
                    g2d.setColor(visited_color);
                    g2d.fillRect(x, y, cell_width - 1, cell_width - 1);
                }
            }
        }

        drawGridLines();
    }

    private void drawGridLines() {
        // grid lines
        g2d.setStroke( new BasicStroke( 1.0f,  BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND ) );
        g2d.setColor(grid_line_color);
        for (int i = 0; i < image.getHeight(); i+=cell_width)
            g2d.drawLine(0, i, image.getWidth(), i );
        for (int i = 0; i < image.getWidth(); i+=cell_width)
            g2d.drawLine(i, 0, i, image.getHeight() );
    }

    public void drawCell(int x, int y, Color color) {
        x = x * cell_width + 1;
        y = y * cell_width + 1;
        g2d.setColor(color);
        g2d.fillRect(x, y, cell_width - 1, cell_width - 1);
    }

    public void drawLineBetweenTwoCells(Node n1, Node n2) {
        int x1 = n1.x * cell_width + cell_width / 2 + 1;
        int y1 = n1.y * cell_width + cell_width / 2 + 1;
        int x2 = n2.x * cell_width + cell_width / 2 + 1;
        int y2 = n2.y * cell_width + cell_width / 2 + 1;
        g2d.setStroke( new BasicStroke( 4.0f,  BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND ) );
        g2d.setColor(path_line_color);
        g2d.drawLine(x1, y1, x2, y2);
    }


    public void updateCellSize(int size) {
        cell_width = size;
        createGrid();
        setNeighbors();
        defaultStartEndLocs();
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
    
    public void drawPath(Node curr) {
        Node prev = curr;
        curr = curr.parent;
        while(curr.parent != null) {
            drawCell(curr.x, curr.y, path_cell_color);
            if(prev != null)
                drawLineBetweenTwoCells(prev, curr);
            repaint();
            try {
                Thread.sleep(frame_delay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            prev = curr;
            curr = curr.parent;
        }
        drawLineBetweenTwoCells(prev, curr);
        // drawGridLines();
        repaint();

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
            public void mouseClicked(MouseEvent e) {}

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


    public void BFS() {

        new Thread(new Runnable() {
            @Override
            public void run() {

                LinkedList<Node> q = new LinkedList<Node>();
                q.add( startPoint );

                Node curr = null;

                while( !q.isEmpty() ) {

                    curr = q.poll();
                    curr.isVisited = true;
                    curr.isQueued = false;

                    drawAll();

                    if(curr.equals(endPoint))
                        break;

                    try {
                        Thread.sleep(frame_delay);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    for (Node n : curr.neighbors) {
                        if(!n.isVisited && n.isPassable) {
                            q.add(n);
                            n.isQueued = true;
                            n.isVisited = true;
                            n.parent = curr;
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

                Stack<Node> stack = new Stack<Node>();
                stack.push( startPoint );

                Node curr = null;

                while( !stack.isEmpty() ) {

                    curr = stack.pop();
                    curr.isVisited = true;
                    curr.isQueued = false;

                    drawAll();

                    if(curr.equals(endPoint))
                        break;

                    try {
                        Thread.sleep(frame_delay);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    for (Node n : curr.neighbors) {
                        if(!n.isVisited && n.isPassable) {
                            stack.push(n);
                            n.isQueued = true;
                            n.isVisited = true;
                            n.parent = curr;
                        }
                    }

                }

                drawPath(curr);

            }
        }).start();

    }

    public void setFrameDelay(int delay) { // (1000 / delay) = nodes visited per second
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
        label.setFont(new Font("plain", Font.BOLD, 13));
        label.setForeground( new Color(0xffFFF1A5) );

        slider = new JSlider(16, 256, 64);
        slider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                imagePanel.setFrameDelay(slider.getValue());
            }
        });;

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
        label.setFont(new Font("plain", Font.BOLD, 13));
        label.setForeground( new Color(0xffFFF1A5) );


        slider = new JSlider(2, 240, 60);
        label.setText(" Rows: " + String.valueOf(imagePanel.getHeight() / slider.getValue() + 1) + " Columns: " + String.valueOf(imagePanel.getWidth() / slider.getValue() + 1));

        slider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int size = slider.getValue();
                label.setText(" Rows: " + String.valueOf(imagePanel.getHeight() / size + 1) + " Columns: " + String.valueOf(imagePanel.getWidth() / size + 1));
                imagePanel.updateCellSize( size );
            }
        });;

        add(label, BorderLayout.CENTER);
        add(slider, BorderLayout.SOUTH);

        setOpaque(false );
        setVisible(true);
    }
}

