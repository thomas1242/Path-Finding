import javax.swing.*;
import java.awt.*;

public class DisplayFrame extends JFrame {

    public DisplayFrame(int width, int height) {
        setSize(width, height);
        add(new ImagePanel(width, height), BorderLayout.CENTER);
    }

}
