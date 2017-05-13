


import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

public class SpeedSlider extends JPanel {

    ImagePanel imagePanel;
    JSlider slider;
    JLabel label;

    public SpeedSlider(ImagePanel imagePanel) {
        setLayout(new GridLayout(0, 1));
        this.imagePanel = imagePanel;

        label = new JLabel(" Speed");
        label.setFont(new Font("plain", Font.BOLD, 13));

        slider = new JSlider(16, 256, 64);
        slider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                imagePanel.frame_delay = slider.getValue();
            }
        });;

        add(label, BorderLayout.CENTER);
        add(slider, BorderLayout.SOUTH);

        setOpaque(false);
        setVisible(true);
    }
}