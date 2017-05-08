import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

public class ImagePanel extends JLayeredPane {

    private BufferedImage image = null;
    private Graphics2D g2d = null;
    private Node[][] grid = null;
    private int cell_width = 60;

    CellLoc startPoint, endPoint;
    boolean draggingStart = false, draggingEnd = false;
    boolean drawingWalls = false, erasingWalls = false;

    private CellWidthSlider cellWidthSlider;

    public ImagePanel(int width, int height) {
        setBounds(0, 0, width, height);
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        g2d = (Graphics2D)image.getGraphics();

        createGrid();

        setNeighbors();

        addComponents();

        addListeners();

        defaultStartEndLocs();

        drawAll();

        repaint();
    }

    public boolean isValidLoc(int x, int y) {
        return !( x >= grid.length || y >= grid[0].length || x < 0 || y < 0);
    }

    public void setStartPoint(int x, int y) {

        if( !isValidLoc(x, y) )
            return;

        if(grid[x][y].isPassable)
            startPoint = new CellLoc(x, y);
    }

    public void drawStartPoint() {
        g2d.setColor(Color.green);
        g2d.fillRect(startPoint.x * cell_width + 1, startPoint.y * cell_width + 1, cell_width - 1, cell_width - 1);
    }

    public void setEndPoint(int x, int y) {

       if( !isValidLoc(x, y) )
            return;

        if(grid[x][y].isPassable)
            endPoint = new CellLoc(x, y);
    }

    public void drawEndPoint() {
        g2d.setColor(Color.red);
        g2d.fillRect(endPoint.x * cell_width + 1, endPoint.y * cell_width + 1, cell_width - 1, cell_width - 1);
    }

    public void defaultStartEndLocs() {
        setStartPoint((int)(grid.length * 0.3), (int)(grid[0].length * 0.5));
        setEndPoint((int)(grid.length * 0.7), (int)(grid[0].length * 0.5));
    }

    public void addComponents() {
        cellWidthSlider = new CellWidthSlider(this);
        add(cellWidthSlider, new Integer(3));
    }

    public void createGrid() {
        grid = new Node[image.getWidth()/cell_width][image.getHeight()/cell_width];
        for (int i = 0; i < grid.length; i++)
            for (int j = 0; j < grid[i].length; j++)
                grid[i][j] = new Node();
    }

    public void setNeighbors() {
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                for (int n = -1; n <= 1; n++) {
                    for (int m = -1; m <= 1; m++) {
                        int x_step = i + n;
                        int y_step = j + m;
                        if (!(i == x_step && j == y_step) &&x_step < grid.length && x_step >= 0 && y_step < grid[i].length && y_step >= 0) {
                            grid[i][j].neighbors.add(grid[x_step][y_step]);
                        }
                    }
                }
            }
        }
    }

    public void drawGrid() {
        g2d.setColor(Color.white);
        g2d.fillRect(0, 0, image.getWidth(), image.getHeight());


        g2d.setColor(Color.GRAY);
        // grid cells
        for (int i = 0; i < grid.length; i++)
            for (int j = 0; j < grid[i].length; j++)
                if( !grid[i][j].isPassable )
                    g2d.fillRect(i * cell_width + 1, j * cell_width + 1, cell_width - 1, cell_width - 1);




        // grid lines
        g2d.setColor(Color.black);
        for (int i = 0; i < image.getHeight(); i+=cell_width)
            g2d.drawLine(0, i, image.getWidth(), i );
        for (int i = 0; i < image.getWidth(); i+=cell_width)
            g2d.drawLine(i, 0, i, image.getHeight() );
    }

    public void updateCellSize(int size) {
        cell_width = size;

        createGrid();

        setNeighbors();

        defaultStartEndLocs();

        drawAll();

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
            public void mouseClicked(MouseEvent e) {

            }

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
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        } );

        addMouseMotionListener( new MouseMotionListener()
        {
            public void mouseDragged(MouseEvent event) {
                Point point = event.getPoint();

                int x = (int) point.getX() / cell_width;
                int y = (int) point.getY() / cell_width;

                if( !isValidLoc(x, y) )
                    return;

                CellLoc newLoc = new CellLoc(x, y);

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

            public void mouseMoved(MouseEvent event) {
                Point point = event.getPoint();



            }
        });

    }






    protected void paintComponent(Graphics g) {
        g.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
        super.paintComponent(g);
    }
}
