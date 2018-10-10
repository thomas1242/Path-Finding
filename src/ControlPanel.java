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
    private int x, y, curr_x, curr_y, width, height;
    private boolean isRunning = false;

    public ControlPanel(ImagePanel imagePanel) {
        this.imagePanel = imagePanel;
        drawBackground();
        setPositionAndSize();
        addComponents();
        addMouseListeners();
        setVisible(true);
        setOpaque(true);
    }

    private void drawBackground() {
        setBackground(new Color(50, 50, 50, 200));
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
        startSearchButton = createStartSearchButton();
        createMazeButton = createMazeButton();

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
        startSearchButton.addActionListener(event -> {
            String selection = startSearchButton.getText();
            if (selection.equals("Start search"))
                startSearch();
            else if (selection.equals("Pause"))
                pauseSearch();
            else if (selection.equals("Resume"))
                resumeSearch();
        });
        setButtonFont(startSearchButton, Color.BLACK, 13);
        return startSearchButton;
    }

    private JButton createMazeButton() {
        JButton createMazeButton = new JButton("Create Maze");
        createMazeButton.addActionListener(event -> {
            String selection = createMazeButton.getText();
            if (selection.equals("Create Maze"))
                startMazeCreation();
            else if (selection.equals("Pause"))
                pauseMazeCreation();
            else if (selection.equals("Resume"))
                resumeMazeCreation();
        });
        setButtonFont(createMazeButton, Color.BLACK, 13);
        return createMazeButton;
    }

    private JPanel createAlgorithmPanel() {
        JPanel algoPanel = new JPanel(new GridLayout(0, 2));
        algorithmSelectButtons = new LinkedList<>();

        for (String s : new String[]{"BFS", "DFS", "Dijkstra", "A*"}) {
            JButton button = new JButton(s);
            button.addActionListener(event -> selectAlgorithm(button));
            setButtonFont(button, Color.BLACK, 13);
            algorithmSelectButtons.add(button);
            algoPanel.add(button);
        }
        selectAlgorithm(algorithmSelectButtons.get(0));

        algoPanel.setOpaque(false);
        return algoPanel;
    }

    private void selectAlgorithm(JButton button) {
        this.selectedAlgorithm = button.getText();
        useDefaultTextColors();
        button.setForeground(new Color(0, 175, 0, 255));
    }

    private void setButtonFont(JButton button, Color color, int fontSize) {
        button.setFont(new Font("plain", Font.BOLD, fontSize));
        button.setForeground(color);
        button.setOpaque(false);
    }

    private void setButtonText(JButton btn, String text, Color color) {
        btn.setText(text);
        btn.setForeground(color);
    }

    private void useDefaultTextColors() {
        for (JButton button : algorithmSelectButtons)
            button.setForeground(Color.BLACK);
    }

    private JPanel createClearPanel() {
        JButton clearPath = new JButton("Clear path");
        clearPath.addActionListener(event -> imagePanel.clearPath());
        setButtonFont(clearPath, Color.BLACK, 13);

        JButton clearObstacles = new JButton("Clear walls");
        clearObstacles.addActionListener(event -> imagePanel.clearWalls());
        setButtonFont(clearObstacles, Color.BLACK, 13);

        JPanel clearPanel = new JPanel(new GridLayout(0, 2));
        clearPanel.add(clearObstacles);
        clearPanel.add(clearPath);
        clearPanel.setOpaque(false);
        return clearPanel;
    }

    private JPanel createSpeedSliderPanel() {
        JSlider slider = createSlider(0, 50, 25);
        slider.addChangeListener(event -> imagePanel.setFrameDelay(slider.getMaximum() - slider.getValue()));
        return createSliderPanel(createLabel(" Speed", new Color(0xffbbbbbb)), slider);
    }

    private JPanel createSizeSliderPanel() {
        JLabel label = createLabel("", new Color(0xffbbbbbb));
        JSlider slider = createSlider(2, 120, 60);
        slider.addChangeListener(event -> {
            int cellWidth =  Math.max(2, slider.getMaximum() - slider.getValue() + 1);
            int numRows = imagePanel.getHeight() / cellWidth + 1;
            int numCols = imagePanel.getWidth() / cellWidth + 1;
            imagePanel.updateCellSize(cellWidth);
            label.setText(" " + numRows + " rows, " + numCols  + " columns ");
        });
        slider.setValue(60);
        return createSliderPanel(label, slider);
    }

    private JSlider createSlider(int low, int high, int value) {
        JSlider slider = new JSlider();
        slider.setMinorTickSpacing(1);
        slider.setPaintTicks(true);
        slider.setSnapToTicks(true);
        return slider;
    }

    private JPanel createSliderPanel(JLabel label, JSlider slider) {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, 1));
        panel.add(label, BorderLayout.CENTER);
        panel.add(slider, BorderLayout.SOUTH);
        panel.setOpaque(false);
        panel.setVisible(true);
        return panel;
    }

    private JLabel createAlgorithmLabel() {
        return createLabel(" Algorithms", new Color(0xffbbbbbb));
    }

    private JLabel createLabel(String s, Color color) {
        JLabel label = new JLabel(s);
        label.setFont(new Font("plain", Font.BOLD, 14));
        label.setForeground(color);
        return label;
    }

    private void addMouseListeners() {
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent event) {
                if (event.getButton() == MouseEvent.BUTTON1) {
                    x = (int)event.getPoint().getX();
                    y = (int)event.getPoint().getY();
                }
            }
        });
        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent event) {
                Point p = event.getPoint();
                curr_x += (p.getX() - x);
                curr_y += (p.getY() - y);
                setBounds(curr_x, curr_y, width, height);
            }
        });
    }

    public void startSearch() {
        imagePanel.clearPath();
        new Thread( () -> {
            setButtonText(startSearchButton, "Pause", Color.RED);
            if (selectedAlgorithm.equals("BFS"))
                imagePanel.BFS();
            else if (selectedAlgorithm.equals("DFS"))
                imagePanel.DFS();
            else if (selectedAlgorithm.equals("A*"))
                imagePanel.A_Star();
            else if (selectedAlgorithm.equals("Dijkstra"))
                imagePanel.Dijkstra();
            setButtonText(startSearchButton, "Start search", Color.BLACK);
        }).start();
        isRunning = true;
    }

    public void pauseSearch() {
        setButtonText(startSearchButton, "Resume", new Color(0, 175, 0, 255));
        isRunning = false;
    }

    public void resumeSearch() {
        setButtonText(startSearchButton, "Pause", Color.RED);
        isRunning = true;
    }

    public void startMazeCreation() {
        new Thread( () -> {
            setButtonText(createMazeButton, "Pause", Color.RED);
            imagePanel.createMaze();
            setButtonText(createMazeButton, "Create Maze", Color.BLACK);
        }).start();
        isRunning = true;
    }

    public void pauseMazeCreation() {
        setButtonText(createMazeButton, "Resume", new Color(0, 175, 0, 255));
        isRunning = false;
    }

    public void resumeMazeCreation() {
        setButtonText(createMazeButton, "Pause", Color.RED);
        isRunning = true;
    }

    public boolean isRunning() {
        if (isRunning) return true;
        try { Thread.sleep(32); }
        catch(InterruptedException e) {}
        return false;
    }
}
