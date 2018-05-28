package my.test.gui.jdbc;

import my.test.gui.jdbc.resources.Strings;

import javax.swing.*;
import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static javax.swing.JOptionPane.showMessageDialog;

public class Utils {

    public static Object[][] getFilledDataForTableFromRowsList(List<Object[]> rows, String[] colNames) {
        Object[][] data;

        int numRows = rows.size();
        if (numRows <= 0) {
            data = new Object[1][colNames.length];
        } else {
            data = new Object[numRows][];
            for (int i = 0; i < numRows; ++i) {
                Object[] row = rows.get(i);
                int rowSize = row.length;
                data[i] = new Object[rowSize];
                for (int j = 0; j < rowSize; ++j) {
                    data[i][j] = row[j];
                }
            }
        }
        return data;
    }

    public static void setFrameLocationToCenterOfScreen(JFrame frame) {
        frame.setLocationRelativeTo(null);
//        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
//        frame.setLocation(dim.width/2-frame.getSize().width/2, dim.height/2-frame.getSize().height/2);
    }

    public static String getDecodedStringFromBlob(InputStream input) throws IOException {
        byte[] bytes = null;
        ByteArrayOutputStream baos = null;
        String decodedBytes = null;
        try {
            baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            while (input.read(buffer) > 0) {
                baos.write(buffer);
            }
            bytes = baos.toByteArray();
            decodedBytes = new String(bytes, "ISO-8859-1");
            return decodedBytes;
        } catch (Exception e) {
            return null;
        } finally {
            if (baos != null)
                baos.close();
            if (input != null)
                input.close();
        }
    }

    public static byte[] getBytesFromDecodedString(String decodedString) {
        try {
            // декодируем строку в массив битов
            return decodedString.getBytes("ISO-8859-1");
        } catch (Exception e) {
            return null;
        }
    }

    public static int okcancel(String theMessage) {
        int result = JOptionPane.showConfirmDialog((Component) null, theMessage,
                "alert", JOptionPane.OK_CANCEL_OPTION);
        return result;
    }
}
