import javax.swing.*;
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
        x = curr_x = (int)(imagePanel.getWidth() * 0.8);
        y = curr_y = (int)(imagePanel.getHeight() * .25);
        setBounds(x, y, width, height);
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
        JButton startSearchButton = new JButton("Start search");
        startSearchButton.setForeground(  new Color(0, 175, 0, 255)  );
        startSearchButton.addActionListener(
                event -> {
                    String state = startSearchButton.getText();
                    if (state.equals("Start search"))
                        runSearch();
                    else if (state.equals("Pause"))
                        pauseSearch();
                    else if (state.equals("Resume"))
                        resumeSearch();
                }
        );
        startSearchButton.setFont(new Font("plain", Font.BOLD, 13));
        startSearchButton.setForeground(defaultButtonTextColor);
        startSearchButton.setOpaque(false);

        return startSearchButton;
    }
    
    private JButton createMazeButton() {
        JButton createMazeButton = new JButton("Create Maze");
        createMazeButton.addActionListener(
                event -> {
                    String state = this.createMazeButton.getText();
                    if(state.equals("Create Maze"))
                        createMaze();
                    else if (state.equals("Pause"))
                        pauseMaze();
                    else if (state.equals("Resume"))
                        resumeMaze();
                }
        );
        createMazeButton.setFont(new Font("plain", Font.BOLD, 13));
        createMazeButton.setForeground(defaultButtonTextColor);
        createMazeButton.setOpaque(false);

        return createMazeButton;
    }
    
    private JPanel createAlgorithmPanel() {
        JPanel algoPanel = new JPanel(new GridLayout(0, 2));
        algoPanel.setOpaque(false);

        JButton selectBFS = new JButton("BFS");
        selectBFS.addActionListener(event -> selectAlgorithm(selectBFS));
        JButton selectDFS = new JButton("DFS");
        selectDFS.addActionListener(event -> selectAlgorithm(selectDFS));
        JButton selectDijkstra = new JButton("Dijkstra");
        selectDijkstra.addActionListener(event -> selectAlgorithm(selectDijkstra));
        JButton selectA_star = new JButton("A*");
        selectA_star.addActionListener(event -> selectAlgorithm(selectA_star));

        algorithmSelectButtons = new LinkedList<>();
        algorithmSelectButtons.add(selectBFS);
        algorithmSelectButtons.add(selectDFS);
        algorithmSelectButtons.add(selectDijkstra);
        algorithmSelectButtons.add(selectA_star);

        for(JButton button : algorithmSelectButtons) {
            button.setFont(new Font("plain", Font.BOLD, 13));
            button.setForeground(defaultButtonTextColor);
            button.setOpaque(false);
            algoPanel.add(button);
        }

        return algoPanel;
    }

    private void selectAlgorithm(JButton button) {
        this.selectedAlgorithm = button.getText();
        defaultTextColors();
        button.setForeground(  new Color(0, 175, 0, 255)  );
    }
    
    private JPanel createClearPanel() {
        JPanel clearPanel = new JPanel(new GridLayout(0, 2));
        JButton clearObstacles, clearPath;

        clearObstacles = new JButton("Clear walls");
        clearObstacles.addActionListener( event -> imagePanel.clearWalls() );

        clearPath = new JButton("Clear path");
        clearPath.addActionListener( event -> imagePanel.clearPath() );

        clearObstacles.setFont(new Font("plain", Font.BOLD, 13));
        clearPath.setFont(new Font("plain", Font.BOLD, 13));
        clearObstacles.setForeground(defaultButtonTextColor);
        clearPath.setForeground(defaultButtonTextColor);

        clearPanel.add(clearObstacles);
        clearPanel.add(clearPath);
        clearPanel.setOpaque(false);

        return clearPanel;
    }
    
    private JPanel createSpeedSliderPanel() {
        JPanel speedSlider = new JPanel();
        speedSlider.setLayout(new GridLayout(0, 1));

        JLabel label = new JLabel(" Speed");
        label.setFont(new Font("plain", Font.BOLD, 14));
        label.setForeground( new Color(0xffbbbbbb) );

        JSlider slider = new JSlider(0, 50, 25);
        slider.addChangeListener(  event -> imagePanel.setFrameDelay(slider.getValue()) );
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
        String numRows_str = String.valueOf(imagePanel.getHeight() / slider.getValue() + 1);
        String numCols_str = String.valueOf(imagePanel.getWidth() / slider.getValue() + 1);
        label.setText(" " + numRows_str + " rows , " + numCols_str  + " columns ");

        slider.addChangeListener(
                event -> {
                    int size = slider.getValue();
                    imagePanel.updateCellSize( size );
                    int numRows = imagePanel.getHeight() / size + 1;
                    int numCols = imagePanel.getWidth() / size + 1;
                    label.setText(" " + numRows + " rows , " + numCols  + " columns ");
                }
        );

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
        startSearchButton.setText( "Pause" );
        startSearchButton.setForeground(  Color.RED  );
        imagePanel.setSearchState(true);
        doSearch();
    }

    public void pauseSearch() {
        startSearchButton.setText( "Resume" );
        startSearchButton.setForeground(  new Color(0, 175, 0, 255)  );
        imagePanel.setSearchState(false);
    }

    public void resumeSearch() {
        startSearchButton.setText( "Pause" );
        startSearchButton.setForeground(  Color.RED  );
        imagePanel.setSearchState(true);
    }

    public void createMaze() {
        createMazeButton.setText( "Pause" );
        createMazeButton.setForeground(  Color.RED  );
        imagePanel.setSearchState(true);
        new Thread( () -> imagePanel.createMaze() ).start();
    }

    public void pauseMaze() {
        createMazeButton.setText( "Resume" );
        createMazeButton.setForeground(  new Color(0, 175, 0, 255)  );
        imagePanel.setSearchState(false);
    }

    public void resumeMaze() {
        createMazeButton.setText( "Pause" );
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

        new Thread( () -> {
                if      (selectedAlgorithm.equals("BFS"))        imagePanel.BFS();
                else if (selectedAlgorithm.equals("DFS"))        imagePanel.DFS();
                else if (selectedAlgorithm.equals("A*"))         imagePanel.A_Star();
                else if (selectedAlgorithm.equals("Dijkstra"))   imagePanel.Dijkstra();
                else    readySearch();
            }
        ).start();
    }

}