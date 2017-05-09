
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

public class CellWidthSlider extends JPanel {

    ImagePanel imagePanel;
    JSlider gridSlider;
    JLabel label;

    public CellWidthSlider(ImagePanel imagePanel) {
        setLayout(new BorderLayout());
        this.imagePanel = imagePanel;
        setBounds(900, 50, 170, 50  );
        label = new JLabel();

        gridSlider = new JSlider(2, 240, 60);
        label.setText(" Rows: " + String.valueOf(imagePanel.getHeight() / gridSlider.getValue() + 1) + " Columns: " + String.valueOf(imagePanel.getWidth() / gridSlider.getValue() + 1));
        gridSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int size = gridSlider.getValue();

                label.setText(" Rows: " + String.valueOf(imagePanel.getHeight() / size + 1) + " Columns: " + String.valueOf(imagePanel.getWidth() / size + 1));
                imagePanel.updateCellSize( size );
            }
        });;

        add(label, BorderLayout.CENTER);
        add(gridSlider, BorderLayout.SOUTH);

        setBackground(Color.lightGray);
        setVisible(true);
    }

}
