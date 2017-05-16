import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ControlPanel extends JPanel {

    private ImagePanel imagePanel;
    private boolean BFS, DFS, Dijkstra, A_star;
    private JButton startBFS, startDFS, dijkstra, a_star;
    private Color textColor = new Color(0xff4d4d4d);

    public ControlPanel(ImagePanel imagePanel) {
        setLayout(new GridLayout(0, 1));
        this.imagePanel = imagePanel;
        setBounds(900, (int)(imagePanel.getHeight() * .25), 200, (int)(imagePanel.getHeight() * .5)  );

        JButton startSearch;
        JButton clearObstacles, clearPath;

        startSearch = new JButton(" Start search");
        startSearch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doSearch();
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
        algo_label.setFont(new Font("plain", Font.BOLD, 13));
        algo_label.setForeground( new Color(0xffFFF1A5) );

        startSearch.setFont(new Font("plain", Font.BOLD, 13));
        startDFS.setFont(new Font("plain", Font.BOLD, 13));
        startBFS.setFont(new Font("plain", Font.BOLD, 13));
        startSearch.setFont(new Font("plain", Font.BOLD, 13));
        dijkstra.setFont(new Font("plain", Font.BOLD, 13));
        a_star.setFont(new Font("plain", Font.BOLD, 13));
        clearObstacles.setFont(new Font("plain", Font.BOLD, 13));
        clearPath.setFont(new Font("plain", Font.BOLD, 13));

        startSearch.setForeground(textColor);
        clearObstacles.setForeground(textColor);
        clearPath.setForeground(textColor);
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

        setBackground(new Color(50, 50, 50, 200));
        // this.setBorder(BorderFactory.createLineBorder(new Color(0xffFFF1A5), 2));
        setVisible(true);
        setOpaque(true);
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

    private void doSearch() {
        imagePanel.clearPath();

        if(BFS)
            imagePanel.BFS();
        else if (DFS)
            imagePanel.DFS();
    }

}

