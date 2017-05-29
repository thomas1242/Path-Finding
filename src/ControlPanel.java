import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ControlPanel extends JPanel {

    private ImagePanel imagePanel;
    private JButton startSearch;  
    private boolean BFS, DFS, Dijkstra, A_star;
    private JButton startBFS, startDFS, dijkstra, a_star;
    private Color textColor = new Color(0, 0, 0, 250);
    private int x, y, curr_x, curr_y, width, height; 

    public ControlPanel(ImagePanel imagePanel) {
        setLayout(new GridLayout(0, 1));
        this.imagePanel = imagePanel;
        x = curr_x = (int)(imagePanel.getWidth() * (1 - .2));
        y = curr_y = (int)(imagePanel.getHeight() * .25);
        width = (int)(imagePanel.getWidth() * .175);
        height = (int)(imagePanel.getHeight() * .5);
        setBounds(x, y, width, height  );

        JButton clearObstacles, clearPath;

        startSearch = new JButton(" Start search");
        startSearch.setForeground(  new Color(0, 175, 0, 255)  );
        startSearch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(startSearch.getText().equals(" Start search")) {
                    doSearch();
                    startSearch.setText( " Pause");
                    startSearch.setForeground(  Color.RED  );
                    imagePanel.setSearchState(true);
                }
                else if (startSearch.getText().equals( " Pause")) {
                    startSearch.setText( " Resume");
                    startSearch.setForeground(  new Color(0, 175, 0, 255)  );
                    imagePanel.setSearchState(false);
                }
                else if (startSearch.getText().equals( " Resume")) {
                    startSearch.setText( " Pause");
                    startSearch.setForeground(  Color.RED  );
                    imagePanel.setSearchState(true);
                }
            }
        });

        JButton createMaze = new JButton("Generate Maze");
        createMaze.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                imagePanel.createMaze();
            }
        });

        JPanel algoPanel = new JPanel(new GridLayout(0, 2));
        startBFS = new JButton(" BFS");
        startBFS.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearAll();
                BFS = true;
                startBFS.setForeground(  new Color(0, 175, 0, 255)  );
            }
        });

        startDFS = new JButton(" DFS");
        startDFS.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearAll();
                DFS = true;
                startDFS.setForeground(  new Color(0, 175, 0, 255)  );
            }
        });

        dijkstra = new JButton("Dijkstra");
        dijkstra.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearAll();
                Dijkstra = true;
                dijkstra.setForeground(  new Color(0, 175, 0, 255)  );
            }
        });

        a_star = new JButton("A*");
        a_star.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearAll();
                A_star = true;
                a_star.setForeground(  new Color(0, 175, 0, 255)  );
            }
        });

        algoPanel.add(startBFS);
        algoPanel.add(startDFS);
        algoPanel.add(dijkstra);
        algoPanel.add(a_star);

        JPanel clearPanel = new JPanel(new GridLayout(0, 2));
        clearObstacles = new JButton(" Clear walls");
        clearObstacles.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                imagePanel.clearWalls();
            }
        });

        clearPath = new JButton(" Clear path");
        clearPath.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                imagePanel.clearPath();
            }
        });

        clearPanel.add(clearObstacles);
        clearPanel.add(clearPath);

        JLabel algo_label = new JLabel(" Algorithms");
        algo_label.setFont(new Font("plain", Font.BOLD, 14));
        algo_label.setForeground( new Color(0xffcccccc) );

        startSearch.setFont(new Font("plain", Font.BOLD, 13));
        startDFS.setFont(new Font("plain", Font.BOLD, 13));
        startBFS.setFont(new Font("plain", Font.BOLD, 13));
        startSearch.setFont(new Font("plain", Font.BOLD, 13));
        dijkstra.setFont(new Font("plain", Font.BOLD, 13));
        a_star.setFont(new Font("plain", Font.BOLD, 13));
        clearObstacles.setFont(new Font("plain", Font.BOLD, 13));
        clearPath.setFont(new Font("plain", Font.BOLD, 13));
        createMaze.setFont(new Font("plain", Font.BOLD, 13));

        startSearch.setForeground(textColor);
        clearObstacles.setForeground(textColor);
        clearPath.setForeground(textColor);
        createMaze.setForeground(textColor);
        startDFS.setForeground(textColor);
        startBFS.setForeground(textColor);
        dijkstra.setForeground(textColor);
        a_star.setForeground(textColor);

        startDFS.setOpaque(false);
        startBFS.setOpaque(false);
        startSearch.setOpaque(false);
        dijkstra.setOpaque(false);
        a_star.setOpaque(false);
        clearPanel.setOpaque(false);
        algoPanel.setOpaque(false);

        add(startSearch);
        add(algo_label);
        add(algoPanel);
        add(new SpeedSlider(imagePanel));
        add(new SizeSlider(imagePanel));
        add(clearPanel);
        add(createMaze);

        setBackground(new Color(50, 50, 50, 200));
        setBackground(new Color(0xFF, 0xD7, 0x00, 120));
        setBackground(new Color(50, 50, 50, 200));
        this.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220,  32), 6));
        setVisible(true);
        setOpaque(true);

        addMouseListener( new MouseAdapter()
                             {
                public void mousePressed( MouseEvent event )
                {
                    if(event.getButton() == MouseEvent.BUTTON1)
                    {
                        x = (int)event.getPoint().getX();
                        y = (int)event.getPoint().getY();
                    }
                    else if(event.getButton() == MouseEvent.BUTTON2)
                    {
                    }
                    else if(event.getButton() == MouseEvent.BUTTON3)
                    {
                    }
                }
            } );

       addMouseMotionListener( new MouseMotionAdapter()
                                   {
                public void mouseDragged(MouseEvent event)
                {
                    Point p = event.getPoint();
                    curr_x += (p.getX() - x);
                    curr_y += (p.getY() - y);
                    setBounds(curr_x, curr_y, width, height);
                }
            } );
            addMouseListener( new MouseAdapter()
                             {
                public void mouseReleased(MouseEvent event)
                {
                }
            } );
    }

    private void clearAll() {
        BFS = false;
        DFS = false;
        Dijkstra = false;
        A_star = false;

        startBFS.setForeground(textColor);
        startDFS.setForeground(textColor);
        dijkstra.setForeground(textColor);
        a_star.setForeground(textColor);
    }

    public void setSearchText(String s) {
        startSearch.setText(s);
    }

    public void setSearchText(String s, Color c) {
        startSearch.setText(s);
        startSearch.setForeground(c);
    }

    private void doSearch() {
        imagePanel.clearPath();
        imagePanel.setSearchState(true);

        if(BFS)
            imagePanel.BFS();
        else if (DFS)
            imagePanel.DFS();
    }

}

