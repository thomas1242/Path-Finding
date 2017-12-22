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
    private boolean isRunning = false;

    public ControlPanel(ImagePanel imagePanel) {
        this.imagePanel = imagePanel;
        setPositionAndSize();
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

    private void setPositionAndSize() {
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
                    startSearch();
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
                String state = createMazeButton.getText();
                if(state.equals("Create Maze"))
                    startMazeCreation();
                else if (state.equals("Pause"))
                    pauseMazeCreation();
                else if (state.equals("Resume"))
                    resumeMazeCreation();
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

        algorithmSelectButtons = new LinkedList<>();
        algorithmSelectButtons.add(new JButton("BFS"));
        algorithmSelectButtons.add(new JButton("DFS"));
        algorithmSelectButtons.add(new JButton("Dijkstra"));
        algorithmSelectButtons.add(new JButton("A*"));

        for(JButton button : algorithmSelectButtons) {
        	button.addActionListener(event -> selectAlgorithm(button));
            button.setFont(new Font("plain", Font.BOLD, 13));
            button.setForeground(defaultButtonTextColor);
            button.setOpaque(false);
            algoPanel.add(button);
        }

        return algoPanel;
    }

    private void selectAlgorithm(JButton button) {
        this.selectedAlgorithm = button.getText();
        useDefaultTextColors();
        button.setForeground( new Color(0, 175, 0, 255) );
    }

    private JPanel createClearPanel() {
        JButton clearObstacles = new JButton("Clear walls");
        JButton clearPath = new JButton("Clear path");

        clearObstacles.addActionListener( event -> imagePanel.clearWalls() );
        clearPath.addActionListener( event -> imagePanel.clearPath() );

        clearObstacles.setFont(new Font("plain", Font.BOLD, 13));
        clearPath.setFont(new Font("plain", Font.BOLD, 13));
        clearObstacles.setForeground(defaultButtonTextColor);
        clearPath.setForeground(defaultButtonTextColor);

        JPanel clearPanel = new JPanel(new GridLayout(0, 2));
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
        slider.addChangeListener(event -> imagePanel.setFrameDelay(slider.getMaximum() - slider.getValue()));
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
        JLabel label = new JLabel();
        label.setFont(new Font("plain", Font.BOLD, 14));
        label.setForeground( new Color(0xffbbbbbb) );

        JSlider slider = new JSlider(2, 120, 60);
        slider.setMinorTickSpacing(3);
        slider.setPaintTicks(true);
        slider.setSnapToTicks(true);
        slider.addChangeListener(
            event -> {
                int cellWidth =  Math.max(2, slider.getMaximum() - slider.getValue() + 1);
                int numRows = imagePanel.getHeight() / cellWidth + 1;
                int numCols = imagePanel.getWidth() / cellWidth + 1;
                imagePanel.updateCellSize( cellWidth );
                label.setText(" " + numRows + " rows, " + numCols  + " columns ");
            }
        );

        JPanel sizeSliderPanel = new JPanel();
        sizeSliderPanel.setLayout(new GridLayout(0, 1));
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

    private void useDefaultTextColors() {
        for(JButton button : algorithmSelectButtons)
            button.setForeground(defaultButtonTextColor);
    }

    private void setButtonTextAndColor(JButton btn, String text, Color color) {
        btn.setText(text);
        btn.setForeground(color);
    }

    public void startSearch() {
        imagePanel.clearPath();
        new Thread( () -> {
            setButtonTextAndColor(startSearchButton, "Pause", Color.RED);
            if (selectedAlgorithm.equals("BFS"))
                imagePanel.BFS();
            else if (selectedAlgorithm.equals("DFS"))
                imagePanel.DFS();
            else if (selectedAlgorithm.equals("A*"))
                imagePanel.A_Star();
            else if (selectedAlgorithm.equals("Dijkstra"))
                imagePanel.Dijkstra();
            setButtonTextAndColor(startSearchButton, "Start search", defaultButtonTextColor);
        }).start();
        isRunning = true;
    }

    public void pauseSearch() {
        setButtonTextAndColor(startSearchButton, "Resume", new Color(0, 175, 0, 255));
        isRunning = false;
    }

    public void resumeSearch() {
        setButtonTextAndColor(startSearchButton, "Pause", Color.RED);
        isRunning = true;
    }

    public void startMazeCreation() {
        new Thread( () -> {
            setButtonTextAndColor(createMazeButton, "Pause", Color.RED);
            imagePanel.createMaze();
            setButtonTextAndColor(createMazeButton, "Create Maze", defaultButtonTextColor);
        }).start();
        isRunning = true;
    }

    public void pauseMazeCreation() {
        setButtonTextAndColor(createMazeButton, "Resume", new Color(0, 175, 0, 255));
        isRunning = false;
    }

    public void resumeMazeCreation() {
        setButtonTextAndColor(createMazeButton, "Pause", Color.RED);
        isRunning = true;
    }

    public boolean isRunning() {
        if(isRunning) return true;
        try { Thread.sleep(32); }
        catch(InterruptedException e) {}
        return false;
    }
}