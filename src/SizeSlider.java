import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

public class SizeSlider extends JPanel {

    ImagePanel imagePanel;
    JSlider slider;
    JLabel label;

    public SizeSlider(ImagePanel imagePanel) {
        setLayout(new GridLayout(0, 1));
        this.imagePanel = imagePanel;

        label = new JLabel();
        slider = new JSlider(2, 240, 60);
        label.setText(" Rows: " + String.valueOf(imagePanel.getHeight() / slider.getValue() + 1) + " Columns: " + String.valueOf(imagePanel.getWidth() / slider.getValue() + 1));

        slider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int size = slider.getValue();
                label.setText(" Rows: " + String.valueOf(imagePanel.getHeight() / size + 1) + " Columns: " + String.valueOf(imagePanel.getWidth() / size + 1));
                imagePanel.updateCellSize( size );
            }
        });;

        add(label, BorderLayout.CENTER);
        add(slider, BorderLayout.SOUTH);

        setOpaque(false );
        setVisible(true);
    }
}
