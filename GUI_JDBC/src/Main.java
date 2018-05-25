import my.test.gui.jdbc.views.OverlayUI;

import javax.swing.*;
import java.awt.*;

public class Main {
    final static int MAIN_WIDTH = 1250;
    final static int MAIN_HEIGHT = 520;

    public static void main(String[] args) {
        JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.getContentPane().setLayout(new FlowLayout(FlowLayout.LEFT));
        f.getContentPane().add(new OverlayUI());
        f.setSize(MAIN_WIDTH, MAIN_HEIGHT);
        f.setVisible(true);
    }
}
