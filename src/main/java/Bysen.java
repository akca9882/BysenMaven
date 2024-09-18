import java.awt.*;
import javax.swing.*;
import static javax.swing.SwingUtilities.*;

public class Bysen extends JPanel {
    /***
     * main metoden
     * skapar fönstret och ställer in det parallel
     * @param args
     */
    public static void main(String[] args) {
        invokeLater(() -> {
            JFrame f = new JFrame();
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.setTitle("Fånga Bysen");
            f.setResizable(false);
            f.add(new Rendering(), BorderLayout.CENTER);
            f.pack();
            f.setLocationRelativeTo(null);
            f.setVisible(true);
        });
    }
}