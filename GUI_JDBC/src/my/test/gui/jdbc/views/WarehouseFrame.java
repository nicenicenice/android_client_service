package my.test.gui.jdbc.views;

import my.test.gui.jdbc.entities.Overlay;
import my.test.gui.jdbc.db.OverlayBean;
import my.test.gui.jdbc.resources.Strings.FormStrings;
import org.apache.commons.io.FilenameUtils;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.*;
import java.nio.file.Files;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import static javax.swing.JOptionPane.showMessageDialog;

public class WarehouseFrame extends JFrame {
    // controller for work with db
    private OverlayBean bean;
    private JLabel iconLabel;

    private static final int ADD_FORM = 1;
    private static final int EDIT_FORM = 2;

    // frame for add overlay form
    private JTextField idWarehouse = new JTextField(30);
    private JTextField nameWarehouse = new JTextField(30);
    private JTextField latLngBoundNEN = new JTextField(30);
    private JTextField latLngBoundNEE = new JTextField(30);
    private JTextField latLngBoundSWN = new JTextField(30);
    private JTextField latLngBoundSWE = new JTextField(30);
    private JButton chooseImageButton = new JButton("Выбрать изображение");
    private JButton addWarehouseButton = new JButton("Добавить");
    private JButton editWarehouseButton = new JButton("Изменить");

    // since we find a record by its name, we should save it before any changes in the form
    private int idWarehouseOfOverlayToEdit;
    private String decodedBytes;
    private File selectedImageFile = null;

    private final int FRAME_WIDTH = 500;
    private final int FRAME_HEIGHT = 550;
    private final int IMAGE_KB_LIMIT = 200;
    boolean isEditForm = false;

    OverlayUI parentComponent;
    JFrame that;

    public WarehouseFrame() {}

    public WarehouseFrame(JComponent component, Overlay overlay) {
        this(component);

        toFillFormByOverlay(overlay);
    }

    public WarehouseFrame(JComponent component) {
        super("Let's add an overlay to our DB");

        parentComponent = (OverlayUI)component;
        boolean isEditForm = parentComponent.areWeOpeningEditOverlayForm();

        that = this;
        iconLabel = new JLabel();

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(true);

        if (isEditForm) {
            JLabel lIDOfWarehouse = new JLabel("Id склада");
            panel.add(lIDOfWarehouse);
            panel.add(idWarehouse);
            idWarehouse.setEnabled(false);
            isEditForm = true;
        }

        JLabel lNameOfWarehouse = new JLabel("Название склада");
        panel.add(lNameOfWarehouse);
        panel.add(nameWarehouse);

        JLabel lLatLngBoundNEN = new JLabel("Координата северо-восточного угла (с.ш.)");
        panel.add(lLatLngBoundNEN);
        panel.add(latLngBoundNEN);

        JLabel lLatLngBoundNEE = new JLabel("Координата северо-восточного угла (в.д.)");
        panel.add(lLatLngBoundNEE);
        panel.add(latLngBoundNEE);

        JLabel lLatLngBoundSWN = new JLabel("Координата юго-западного угла (с.ш.)");
        panel.add(lLatLngBoundSWN);
        panel.add(latLngBoundSWN);

        JLabel lLatLngBoundSWE = new JLabel("Координата юго-западного угла (в.д.)");
        panel.add(lLatLngBoundSWE);
        panel.add(latLngBoundSWE);

        // image
        panel.add(iconLabel);

        // buttons
        panel.add(chooseImageButton);

        // handle if we should add a new record or edit an old one
        if (isEditForm) {
            panel.add(editWarehouseButton);
            parentComponent.setAreWeOpeningEditOverlayForm(false);
        } else {
            panel.add(addWarehouseButton);
        }

        initButtons();

        add(BorderLayout.WEST, panel);
        setLayout(new FlowLayout());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(FRAME_WIDTH,FRAME_HEIGHT);
        setVisible(true);
        setResizable(false);
    }

    private void initButtons() {
        addWarehouseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { addWarehouse(); }
        });

        editWarehouseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { editWarehouse(); }
        });

        chooseImageButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { chooseImage(); }
        });
    }

    private void addWarehouse()
    {
        EventQueue.invokeLater(() -> {
            // check is a form complety filled
            if (!checkIfFormCompletyFilled()) {
                showMessageDialog(null, FormStrings.FILL_THE_FORM);
                return;
            }

            if (!checkCoordsRestrictions()) {
                showMessageDialog(null, "неправильные значения в координатах. (должно быть от -180.000000 до 180.000000)");
                return;
            }
            // forming data to insert to DB
            Overlay overlay = getAllDataFromForm();

            // insert data to DB
            boolean isSuccessInsertion = new OverlayBean().insertOverlayToDB(overlay);
            if (isSuccessInsertion) {
                clearForm();
                parentComponent.repaintOverlayTable();
            }
        });
    }

    private void editWarehouse()
    {
        EventQueue.invokeLater(() -> {
            // check is a form complety filled
            if (!checkIfFormCompletyFilled()) {
                showMessageDialog(null, FormStrings.FILL_THE_FORM);
                return;
            }

            if (!checkCoordsRestrictions()) {
                showMessageDialog(null, "неправильные значения в координатах. (должно быть от -180.000000 до 180.000000)");
                return;
            }
            // get all form's data to update db
            Overlay overlay = getAllDataFromForm();

            // update data in a row
            boolean isSuccessUpdating = new OverlayBean().updateOverlayIntoDb(overlay);

            if (isSuccessUpdating) {
                //OverlayUI panel = (OverlayUI)component;
                parentComponent.repaintOverlayTable();
                that.dispatchEvent(new WindowEvent(that, WindowEvent.WINDOW_CLOSING));
            }
        });
    }

    private void chooseImage()
    {
        EventQueue.invokeLater(() -> {
            JFileChooser file = new JFileChooser();
            file.setCurrentDirectory(new File(System.getProperty("user.home")));
            //filter the files
            FileNameExtensionFilter filter = new FileNameExtensionFilter(
                    "*.Images", "jpg", "png");
            file.addChoosableFileFilter(filter);
            int result = file.showSaveDialog(null);

            //if the user click on save in Jfilechooser
            if (result == JFileChooser.APPROVE_OPTION) {
                selectedImageFile = file.getSelectedFile();

                if (!checkUploadedImage(selectedImageFile))
                    return;

                // current uploaded image in a decodee string
                try {
                    byte[] imageBytes = Files.readAllBytes(selectedImageFile.toPath());
                    decodedBytes = new String(imageBytes, "ISO-8859-1");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                String path = selectedImageFile.getAbsolutePath();
                iconLabel.setSize(200, 200);
                ImageIcon icon = ResizeImage(path);
                iconLabel.setIcon(icon);
            } else if (result == JFileChooser.CANCEL_OPTION) {
                System.out.println("No File Select");
            }
        });
    }

    private void toFillFormByOverlay(Overlay overlay) {
        idWarehouse.setText(String.valueOf(overlay.getIdWarehouse()));
        //TODO:обработка ошибок
        idWarehouseOfOverlayToEdit = Integer.valueOf(idWarehouse.getText().trim());

        nameWarehouse.setText(overlay.getWarehouseName());
        latLngBoundNEN.setText(overlay.getLatLngBoundNEN());
        latLngBoundNEE.setText(overlay.getLatLngBoundNEE());
        latLngBoundSWN.setText(overlay.getLatLngBoundSWN());
        latLngBoundSWE.setText(overlay.getLatLngBoundSWE());

        String imageString = overlay.getDecodedOverlayPic();
        decodedBytes = imageString;
        byte[] pictureInBytes = getBytesFromDecodedString(imageString);

        iconLabel.setSize(200, 200);
        ImageIcon imageIcon = new ImageIcon(pictureInBytes);
        Image newImg = imageIcon.getImage().getScaledInstance(
        iconLabel.getWidth(), iconLabel.getHeight(), Image.SCALE_SMOOTH);
        ImageIcon image = new ImageIcon(newImg);
        iconLabel.setIcon(image);
        selectedImageFile = null;
    }

    private byte[] getBytesFromDecodedString(String decodedString) {
        try {
            // декодируем строку в массив битов
            return decodedString.getBytes("ISO-8859-1");
        } catch (Exception e) {
            return null;
        }
    }
    private void clearForm() {
        idWarehouse.setText("");
        nameWarehouse.setText("");
        latLngBoundNEN.setText("");
        latLngBoundNEE.setText("");
        latLngBoundSWN.setText("");
        latLngBoundSWE.setText("");
        iconLabel.setIcon(null);
        selectedImageFile = null;
    }

    private Overlay getAllDataFromForm() {
        Overlay overlay = new Overlay();

        if (!idWarehouse.getText().isEmpty())
            overlay.setIdWarehouse(Integer.valueOf(idWarehouse.getText().trim()));
        overlay.setWarehouseName(nameWarehouse.getText());
        overlay.setLatLngBoundNEN(latLngBoundNEN.getText());
        overlay.setLatLngBoundNEN(latLngBoundNEN.getText());
        overlay.setLatLngBoundNEE(latLngBoundNEE.getText());
        overlay.setLatLngBoundSWN(latLngBoundSWN.getText());
        overlay.setLatLngBoundSWE(latLngBoundSWE.getText());
        overlay.setDecodedOverlayPic(decodedBytes);
        return overlay;
    }

    private boolean checkIfFormCompletyFilled() {
        return
                !nameWarehouse.getText().isEmpty() &&
                !(idWarehouse.getText().isEmpty() & isEditForm) &&
                !latLngBoundNEN.getText().isEmpty() &&
                !latLngBoundNEE.getText().isEmpty() &&
                !latLngBoundSWN.getText().isEmpty() &&
                !latLngBoundSWE.getText().isEmpty() &&
                iconLabel.getIcon() != null;

    }

    private boolean checkCoordsRestrictions() {
        double lowerLimit = -180.000000;
        double upperLimit = 180.000000;

        double locLatLngBoundNEN = Double.parseDouble(latLngBoundNEN.getText().trim());
        double locLatLngBoundNEE = Double.parseDouble(latLngBoundNEE.getText().trim());
        double locLatLngBoundSWN = Double.parseDouble(latLngBoundSWN.getText().trim());
        double locLatLngBoundSWE = Double.parseDouble(latLngBoundSWE.getText().trim());
        double[] formValues = {
                locLatLngBoundNEN,
                locLatLngBoundNEE,
                locLatLngBoundSWN,
                locLatLngBoundSWE
        };

        for (double val : formValues) {
            if (lowerLimit - val > 0.0000001) {
                return false;
            }
            if (val - upperLimit > 0.0000001) {
                return false;
            }
        }
        return true;
    }

    private boolean checkUploadedImage(File image) {
        if (image == null)
            return false;

        String imageExtension = FilenameUtils.getExtension(image.getAbsolutePath());
        if (!imageExtension.equals("jpg") && !imageExtension.equals("png")) {
            showMessageDialog(null, "формат файла должен быть jpg или png");
            return false;
        }

        double bytes = image.length();
        if (bytes / 1024 > IMAGE_KB_LIMIT) {
            showMessageDialog(null, "размер картинки не должен превышать 200кб");
            return false;
        }

        return true;
    }

    // to resize imageIcon with the same size of a Jlabel
    public ImageIcon ResizeImage(String ImagePath)
    {
        ImageIcon MyImage = new ImageIcon(ImagePath);
        Image img = MyImage.getImage();
        Image newImg = img.getScaledInstance(iconLabel.getWidth(), iconLabel.getHeight(), Image.SCALE_SMOOTH);
        ImageIcon image = new ImageIcon(newImg);
        return image;
    }
}