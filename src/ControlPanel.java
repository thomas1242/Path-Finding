import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ControlPanel extends JPanel {

    private ImagePanel imagePanel;
    private JButton startSearch, createMaze;
    private JButton selectBFS, selectDFS, selectDijkstra, selectA_star;
    private Color textColor = new Color(0, 0, 0, 250);
    private boolean BFS, DFS, Dijkstra, A_star;
    private int x, y, curr_x, curr_y, width, height; 

    public ControlPanel(ImagePanel imagePanel) {
        
        setLayout(new GridLayout(0, 1));
        this.imagePanel = imagePanel;
        width = (int)(imagePanel.getWidth() * .175);
        height = (int)(imagePanel.getHeight() * .5);
        x = curr_x = (int)(imagePanel.getWidth() * (1 - .2));
        y = curr_y = (int)(imagePanel.getHeight() * .25);
       
        setBounds(x, y, width, height  );

        JButton clearObstacles, clearPath;

        startSearch = new JButton("Start search");
        startSearch.setForeground(  new Color(0, 175, 0, 255)  );
        startSearch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(startSearch.getText().equals("Start search"))
                    runSearch();
                else if (startSearch.getText().equals( "Pause"))
                    pauseSearch();
                else if (startSearch.getText().equals( "Resume"))
                    resumeSearch();
            }
        });

        createMaze = new JButton("Create Maze");
        createMaze.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(createMaze.getText().equals("Create Maze")) 
                    createMaze();
                else if (createMaze.getText().equals( "Pause"))  
                    pauseMaze();
                else if (createMaze.getText().equals( "Resume")) 
                   resumeMaze();
            }
        });

        JPanel algoPanel = new JPanel(new GridLayout(0, 2));
        selectBFS = new JButton("BFS");
        selectBFS.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectBFS();
            }
        });

        selectDFS = new JButton("DFS");
        selectDFS.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectDFS();
            }
        });

        selectDijkstra = new JButton("Dijkstra");
        selectDijkstra.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectDijkstra();
            }
        });

        selectA_star = new JButton("A*");
        selectA_star.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectAstar();
            }
        });

        algoPanel.add(selectBFS);
        algoPanel.add(selectDFS);
        algoPanel.add(selectDijkstra);
        algoPanel.add(selectA_star);

        JPanel clearPanel = new JPanel(new GridLayout(0, 2));
        clearObstacles = new JButton("Clear walls");
        clearObstacles.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                imagePanel.clearWalls();
            }
        });

        clearPath = new JButton("Clear path");
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
        startSearch.setFont(new Font("plain", Font.BOLD, 13));
        selectDFS.setFont(new Font("plain", Font.BOLD, 13));
        selectBFS.setFont(new Font("plain", Font.BOLD, 13));
        startSearch.setFont(new Font("plain", Font.BOLD, 13));
        selectDijkstra.setFont(new Font("plain", Font.BOLD, 13));
        selectA_star.setFont(new Font("plain", Font.BOLD, 13));
        clearObstacles.setFont(new Font("plain", Font.BOLD, 13));
        clearPath.setFont(new Font("plain", Font.BOLD, 13));
        createMaze.setFont(new Font("plain", Font.BOLD, 13));

        algo_label.setForeground( new Color(0xffbbbbbb) );
        startSearch.setForeground(textColor);
        clearObstacles.setForeground(textColor);
        clearPath.setForeground(textColor);
        createMaze.setForeground(textColor);
        selectDFS.setForeground(textColor);
        selectBFS.setForeground(textColor);
        selectDijkstra.setForeground(textColor);
        selectA_star.setForeground(textColor);

        selectDFS.setOpaque(false);
        selectBFS.setOpaque(false);
        startSearch.setOpaque(false);
        selectDijkstra.setOpaque(false);
        selectA_star.setOpaque(false);
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
        this.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220,  32), 6));
        setVisible(true);
        setOpaque(true);

        addMouseListener( new MouseAdapter() {
                public void mousePressed( MouseEvent event ) {
                    if(event.getButton() == MouseEvent.BUTTON1) {
                        x = (int)event.getPoint().getX();
                        y = (int)event.getPoint().getY();
                    }
                }
            } );

       addMouseMotionListener( new MouseMotionAdapter() {
                public void mouseDragged(MouseEvent event) {
                    Point p = event.getPoint();
                    curr_x += (p.getX() - x);
                    curr_y += (p.getY() - y);
                    setBounds(curr_x, curr_y, width, height);
                }
            } );
    }

    private void clearAll() {
        BFS = false;
        DFS = false;
        Dijkstra = false;
        A_star = false;

        selectBFS.setForeground(textColor);
        selectDFS.setForeground(textColor);
        selectDijkstra.setForeground(textColor);
        selectA_star.setForeground(textColor);
    }

    public void setSearchText(String s, Color c) {
        startSearch.setText(s);
        startSearch.setForeground(c);
    }

    public void setMazeText(String s, Color c) {
        createMaze.setText(s);
        createMaze.setForeground(c);
    }

    public void runSearch() {
        startSearch.setText( "Pause");
        startSearch.setForeground(  Color.RED  );
        imagePanel.setSearchState(true);
        doSearch();
    }

    public void pauseSearch() {
        startSearch.setText( "Resume");
        startSearch.setForeground(  new Color(0, 175, 0, 255)  );
        imagePanel.setSearchState(false);
    }

    public void resumeSearch() {
        startSearch.setText( "Pause");
        startSearch.setForeground(  Color.RED  );
        imagePanel.setSearchState(true);
    }

    public void createMaze() {
        createMaze.setText( "Pause");
        createMaze.setForeground(  Color.RED  );
        imagePanel.setSearchState(true);
        imagePanel.createMaze();
    }

    public void pauseMaze() {
        createMaze.setText( "Resume");
        createMaze.setForeground(  new Color(0, 175, 0, 255)  );
        imagePanel.setSearchState(false);
    }

    public void resumeMaze() {
        createMaze.setText( "Pause");
        createMaze.setForeground(  Color.RED  );
        imagePanel.setSearchState(true);
    }

    public void readyMaze() {
        setMazeText("Create Maze", textColor);
    }

    public void readySearch() {
        setSearchText("Start search", textColor);
    }

    private void doSearch() {
        imagePanel.clearPath();
        imagePanel.setSearchState(true);

        if(BFS)
            imagePanel.BFS();
        else if (DFS)
            imagePanel.DFS();
        else if (A_star)
            imagePanel.A_Star();
        else if (Dijkstra)
            imagePanel.Dijkstra();
        else
            readySearch();
    }

    private void selectBFS() {
        clearAll();
        BFS = true;
        selectBFS.setForeground(  new Color(0, 175, 0, 255)  );
    }

    private void selectDFS() {
        clearAll();
        DFS = true;
        selectDFS.setForeground(  new Color(0, 175, 0, 255)  );
    }

    private void selectAstar() {
        clearAll();
        A_star = true;
        selectA_star.setForeground(  new Color(0, 175, 0, 255)  );
    }

    private void selectDijkstra() {
        clearAll();
        Dijkstra = true;
        selectDijkstra.setForeground(  new Color(0, 175, 0, 255)  );
    }

}

