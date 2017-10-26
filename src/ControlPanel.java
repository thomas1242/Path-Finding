import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.LinkedList;

public class ControlPanel extends JPanel {

    private ImagePanel imagePanel;
    private JButton startSearchButton, createMazeButton;
    private List<JButton> algorithmSelectButtons;
    private String selectedAlgorithm = "BFS";
    private Color defaultButtonTextColor = new Color(0, 0, 0, 250);
    private int x, y, curr_x, curr_y, width, height;

    public ControlPanel(ImagePanel imagePanel) {
        this.imagePanel = imagePanel;
        setInitialPosition();
        addComponents();
        addMouseListeners();
        drawBackground(new Color(50, 50, 50, 200));
        setVisible(true);
        setOpaque(true);
    }

    private void drawBackground(Color color) {
        setBackground( color );
        setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220,  32), 6));
    }

    private void setInitialPosition() {
        width = (int)(imagePanel.getWidth() * .175);
        height = (int)(imagePanel.getHeight() * .5);
        x = curr_x = (int)(imagePanel.getWidth() * (1 - .2));
        y = curr_y = (int)(imagePanel.getHeight() * .25);
        setBounds(x, y, width, height  );
    }

    private void addComponents() {
        this.startSearchButton = createStartSearchButton();
        this.createMazeButton = createMazeButton();

        JLabel algoLabel = createAlgorithmLabel();
        JPanel algoButtonPanel = createAlgorithmPanel();
        JPanel clearPanel = createClearPanel();
        JPanel speedSliderPanel = createSpeedSliderPanel();
        JPanel sizeSliderPanel = createSizeSliderPanel();

        setLayout(new GridLayout(0, 1));
        add(startSearchButton);
        add(algoLabel);
        add(algoButtonPanel);
        add(speedSliderPanel);
        add(sizeSliderPanel);
        add(clearPanel);
        add(createMazeButton);
    }
    
    private JButton createStartSearchButton() {
        JButton startSearch = new JButton("Start search");
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
        startSearch.setFont(new Font("plain", Font.BOLD, 13));
        startSearch.setForeground(defaultButtonTextColor);
        startSearch.setOpaque(false);

        return startSearch;
    }
    
    private JButton createMazeButton() {
        JButton createMaze = new JButton("Create Maze");
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
        createMaze.setFont(new Font("plain", Font.BOLD, 13));
        createMaze.setForeground(defaultButtonTextColor);
        createMaze.setOpaque(false);
        
        return createMaze;
    }
    
    private JPanel createAlgorithmPanel() {
        JPanel algoPanel = new JPanel(new GridLayout(0, 2));
        algoPanel.setOpaque(false);

        JButton selectBFS = new JButton("BFS");
        selectBFS.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                defaultTextColors();
                selectedAlgorithm = "BFS";
                selectBFS.setForeground(  new Color(0, 175, 0, 255)  );
            }
        });
        JButton selectDFS = new JButton("DFS");
        selectDFS.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                defaultTextColors();
                selectedAlgorithm = "DFS";
                selectDFS.setForeground(  new Color(0, 175, 0, 255)  );
            }
        });
        JButton selectDijkstra = new JButton("Dijkstra");
        selectDijkstra.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                defaultTextColors();
                selectedAlgorithm = "Dijkstra";
                selectDijkstra.setForeground(  new Color(0, 175, 0, 255)  );
            }
        });
        JButton selectA_star = new JButton("A*");
        selectA_star.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                defaultTextColors();
                selectedAlgorithm = "A*";
                selectA_star.setForeground(  new Color(0, 175, 0, 255)  );
            }
        });

        selectDFS.setFont(new Font("plain", Font.BOLD, 13));
        selectBFS.setFont(new Font("plain", Font.BOLD, 13));
        selectDijkstra.setFont(new Font("plain", Font.BOLD, 13));
        selectA_star.setFont(new Font("plain", Font.BOLD, 13));
        
        selectDFS.setForeground(defaultButtonTextColor);
        selectBFS.setForeground(defaultButtonTextColor);
        selectDijkstra.setForeground(defaultButtonTextColor);
        selectA_star.setForeground(defaultButtonTextColor);
        
        selectDFS.setOpaque(false);
        selectBFS.setOpaque(false);
        selectDijkstra.setOpaque(false);
        selectA_star.setOpaque(false);

        algorithmSelectButtons = new LinkedList<>();
        algorithmSelectButtons.add(selectBFS);
        algorithmSelectButtons.add(selectDFS);
        algorithmSelectButtons.add(selectDijkstra);
        algorithmSelectButtons.add(selectA_star);

        for(JButton button : algorithmSelectButtons)
            algoPanel.add(button);

        return algoPanel;
    }
    
    private JPanel createClearPanel() {
        JPanel clearPanel = new JPanel(new GridLayout(0, 2));
        JButton clearObstacles, clearPath;

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

        clearObstacles.setFont(new Font("plain", Font.BOLD, 13));
        clearPath.setFont(new Font("plain", Font.BOLD, 13));
        clearObstacles.setForeground(defaultButtonTextColor);
        clearPath.setForeground(defaultButtonTextColor);
        clearPanel.setOpaque(false);

        clearPanel.add(clearObstacles);
        clearPanel.add(clearPath);

        return clearPanel;
    }
    
    private JPanel createSpeedSliderPanel() {
        JPanel speedSlider = new JPanel();
        speedSlider.setLayout(new GridLayout(0, 1));

        JLabel label = new JLabel(" Speed");
        label.setFont(new Font("plain", Font.BOLD, 14));
        label.setForeground( new Color(0xffbbbbbb) );

        JSlider slider = new JSlider(0, 50, 25);
        slider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                imagePanel.setFrameDelay(slider.getValue());
            }
        });;
        slider.setMinorTickSpacing(1);
        slider.setPaintTicks(true);
        slider.setSnapToTicks(true);
        speedSlider.add(label, BorderLayout.CENTER);
        speedSlider.add(slider, BorderLayout.SOUTH);
        speedSlider.setOpaque(false);
        speedSlider.setVisible(true);
        
        return speedSlider;
    }

    private JPanel createSizeSliderPanel() {
        JPanel sizeSliderPanel = new JPanel();
        sizeSliderPanel.setLayout(new GridLayout(0, 1));

        JLabel label = new JLabel();
        label.setFont(new Font("plain", Font.BOLD, 14));
        label.setForeground( new Color(0xffbbbbbb) );

        JSlider slider = new JSlider(2, 120, 60);
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

        sizeSliderPanel.add(label, BorderLayout.CENTER);
        sizeSliderPanel.add(slider, BorderLayout.SOUTH);

        sizeSliderPanel.setOpaque(false );
        sizeSliderPanel.setVisible(true);

        return sizeSliderPanel;
    }

    private JLabel createAlgorithmLabel() {
        JLabel algo_label = new JLabel(" Algorithms");
        algo_label.setFont(new Font("plain", Font.BOLD, 14));
        algo_label.setForeground( new Color(0xffbbbbbb) );
        return algo_label;
    }

    private void addMouseListeners() {
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

    private void defaultTextColors() {
        for(JButton button : algorithmSelectButtons)
            button.setForeground(defaultButtonTextColor);
    }

    public void setSearchText(String s, Color c) {
        startSearchButton.setText(s);
        startSearchButton.setForeground(c);
    }

    public void setMazeText(String s, Color c) {
        createMazeButton.setText(s);
        createMazeButton.setForeground(c);
    }

    public void runSearch() {
        startSearchButton.setText( "Pause");
        startSearchButton.setForeground(  Color.RED  );
        imagePanel.setSearchState(true);
        doSearch();
    }

    public void pauseSearch() {
        startSearchButton.setText( "Resume");
        startSearchButton.setForeground(  new Color(0, 175, 0, 255)  );
        imagePanel.setSearchState(false);
    }

    public void resumeSearch() {
        startSearchButton.setText( "Pause");
        startSearchButton.setForeground(  Color.RED  );
        imagePanel.setSearchState(true);
    }

    public void createMaze() {
        createMazeButton.setText( "Pause");
        createMazeButton.setForeground(  Color.RED  );
        imagePanel.setSearchState(true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                 imagePanel.createMaze();
            }
        }).start();
    }

    public void pauseMaze() {
        createMazeButton.setText( "Resume");
        createMazeButton.setForeground(  new Color(0, 175, 0, 255)  );
        imagePanel.setSearchState(false);
    }

    public void resumeMaze() {
        createMazeButton.setText( "Pause");
        createMazeButton.setForeground(  Color.RED  );
        imagePanel.setSearchState(true);
    }

    public void readyMaze() {
        setMazeText("Create Maze", defaultButtonTextColor);
    }

    public void readySearch() {
        setSearchText("Start search", defaultButtonTextColor);
    }

    private void doSearch() {
        imagePanel.clearPath();
        imagePanel.setSearchState(true);

        new Thread(new Runnable() {
            @Override
            public void run() {
                if(selectedAlgorithm.equals("BFS"))
                    imagePanel.BFS();
                else if (selectedAlgorithm.equals("DFS"))
                    imagePanel.DFS();
                else if (selectedAlgorithm.equals("A*"))
                    imagePanel.A_Star();
                else if (selectedAlgorithm.equals("Dijkstra"))
                    imagePanel.Dijkstra();
                else
                    readySearch();
            }
        }).start();
    }

}