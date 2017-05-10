import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class DisplayFrame extends JFrame {

    ImagePanel imagePanel;

    public DisplayFrame(int width, int height) {

//      this.setTitle( "Path Finder" );
        this.setSize( width, height );
        imagePanel = new ImagePanel(width, height);
        add(imagePanel, BorderLayout.CENTER);

        repaint();
    }
}