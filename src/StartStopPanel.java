
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StartStopPanel extends JPanel {

    ImagePanel imagePanel;
    JButton start;
    JButton clearObstacles;
    JButton clearPath;


    public StartStopPanel(ImagePanel imagePanel) {
        setLayout(new GridLayout(0, 1));
        this.imagePanel = imagePanel;
        setBounds(900, 500, 200, 100  );

        start = new JButton(" BFS");
        start.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                imagePanel.BFS();
            }
        });

        add(start);

        clearObstacles = new JButton(" Clear walls");
        clearObstacles.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                imagePanel.clearWalls();
            }
        });

        add(clearObstacles);


        clearPath = new JButton(" Clear path");
        clearPath.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                imagePanel.clearPath();
            }
        });

        add(clearPath);


        JSlider s = new JSlider(5, 256, 32);
        s.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                imagePanel.frame_delay = s.getValue();
            }
        });

        add(s);
        setBackground(Color.lightGray);
        setVisible(true);
    }

}

