import my.test.gui.jdbc.Utils;
import my.test.gui.jdbc.views.OverlayUI;

import javax.swing.*;
import java.awt.*;

public class Main {
    final static int MAIN_WIDTH = 785;
    final static int MAIN_HEIGHT = 510;

    public static void main(String[] args) {
        JFrame f = new JFrame();
        f.setTitle("Управление базой данных");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.getContentPane().add(new OverlayUI());
        f.setSize(MAIN_WIDTH, MAIN_HEIGHT);
        Utils.setFrameLocationToCenterOfScreen(f);
        f.setVisible(true);
    }
}
