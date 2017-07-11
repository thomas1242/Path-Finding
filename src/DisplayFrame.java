import javax.swing.*;
import java.awt.*;

public class DisplayFrame extends JFrame {

    private ImagePanel imagePanel;

    public DisplayFrame(int width, int height) {

        this.setSize( width, height );
        imagePanel = new ImagePanel(width, height);
        add(imagePanel, BorderLayout.CENTER);
        repaint();
    }
}
