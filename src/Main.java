import javax.swing.*;

public class Main {

    private static final int WIDTH = 1140;
    private static final int HEIGHT = 713;

    public static void main( String[] args ) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        } );
    }

    private static void createAndShowGUI()
    {
        JFrame frame = new DisplayFrame( WIDTH, HEIGHT);
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setVisible( true );
    }

}
