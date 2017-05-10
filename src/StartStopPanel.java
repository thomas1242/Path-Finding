import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StartStopPanel extends JPanel {

    ImagePanel imagePanel;
    JButton startSearch;
    JButton startBFS;
    JButton startDFS;
    JButton clearObstacles;
    JButton clearPath;

    public StartStopPanel(ImagePanel imagePanel) {
        setLayout(new GridLayout(0, 1));
        this.imagePanel = imagePanel;
        setBounds(900, (int)(imagePanel.getHeight() * .3), 200, (int)(imagePanel.getHeight() * .4)  );

        startSearch = new JButton(" Start search");
        startSearch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        JPanel algoPanel = new JPanel(new GridLayout(0, 2));

        startBFS = new JButton(" BFS");

        startBFS.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                imagePanel.clearPath();
                imagePanel.BFS();
            }
        });

        startDFS = new JButton(" DFS");
        startDFS.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                imagePanel.clearPath();
                imagePanel.DFS();
//                startDFS.setForeground(  new Color(0, 175, 0, 255)  );
            }
        });

        algoPanel.add(startBFS);
        algoPanel.add(startDFS);
        algoPanel.add(new JButton("A*"));
        algoPanel.add(new JButton("Dijkstra"));

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


        add(startSearch);
        add(new JLabel(" Algorithms"));
        add(algoPanel);
        add(clearPanel);
        add(new SpeedSlider(imagePanel));
        add( new SizeSlider(imagePanel) );

        clearPanel.setBackground(new Color(0xffCABD80));
        algoPanel.setBackground(new Color(0xffCABD80));

        setBackground(new Color(0xffCABD80));
        setVisible(true);
        setOpaque(true);
    }

}

