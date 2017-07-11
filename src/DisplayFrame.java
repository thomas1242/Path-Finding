import javax.swing.*;
import java.awt.*;

public class DisplayFrame extends JFrame {

    public DisplayFrame(int width, int height) {

        setSize( width, height );
        ImagePanel imagePanel = new ImagePanel(width, height);
        add(imagePanel, BorderLayout.CENTER);
    }
}
