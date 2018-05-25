package my.test.gui.jdbc.views;

import my.test.gui.jdbc.db.OverlayBean;
import my.test.gui.jdbc.entities.Product;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import javax.swing.*;

import static javax.swing.JOptionPane.showMessageDialog;
import static my.test.gui.jdbc.resources.Strings.FormStrings;

public class AddEditProductFrame extends JFrame {
    // controller for work with db
    private OverlayBean bean = new OverlayBean();;

    // frame for add overlay form
    private JTextField productId = new JTextField(30);
    private JTextField productName = new JTextField(30);

    private JButton addProductButton = new JButton("Добавить");
    private JButton editProductButton = new JButton("Изменить");

    private final int FRAME_WIDTH = 450;
    private final int FRAME_HEIGHT = 170;
    boolean isFormEdit = false;

    JFrame that;
    ProductFrame parentFrame;

    AddEditProductFrame(JFrame frame, Product product) {
        this(frame);
        toFillFormByProduct(product);
    }

    AddEditProductFrame(JFrame frame) {
        super("Добавление продукта");

        that = this;
        parentFrame = (ProductFrame)frame;
        isFormEdit = parentFrame.isFormEdit();

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setOpaque(true);

        if (parentFrame.isFormEdit()) {
            JLabel lIdOfProduct = new JLabel("Id склада");
            mainPanel.add(lIdOfProduct);
            mainPanel.add(productId);
            productId.setEnabled(false);
        }

        JLabel lNameOfProduct = new JLabel("Название продукта");
        mainPanel.add(lNameOfProduct);
        mainPanel.add(productName);

        // handle if we should add a new record or edit an old one
        if (parentFrame.isFormEdit()) {
            mainPanel.add(editProductButton);
        } else {
            mainPanel.add(addProductButton);
        }

        initButtons();

        add(BorderLayout.WEST, mainPanel);
        setLayout(new FlowLayout());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(FRAME_WIDTH,FRAME_HEIGHT);
        setVisible(true);
        setResizable(false);
    }

    private void initButtons() {
        addProductButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { addProd(); }
        });

        editProductButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { editProd(); }
        });
    }

    private void addProd()
    {
        EventQueue.invokeLater(() -> {
            // check if a form complety filled
            if (!checkIfFormCompletyFilled()) {
                showMessageDialog(null, FormStrings.FILL_THE_FORM);
                return;
            }
            // forming data to insert to DB
            Product product = getAllDataFromForm();

            // insert data to DB
            if (new OverlayBean().insertProductToDB(product)) {
                clearForm();
                parentFrame.repaintProdTable();
            }
        });
    }

    private void editProd()
    {
        EventQueue.invokeLater(() -> {
            // check is a form complety filled
            if (!checkIfFormCompletyFilled()) {
                showMessageDialog(null, FormStrings.FILL_THE_FORM);
                return;
            }

            // get all form's data to update db
            Product product = getAllDataFromForm();

            // update data in a row
            if (new OverlayBean().updateProductByIdIntoDb(product)) {
                parentFrame.repaintProdTable();
                that.dispatchEvent(new WindowEvent(that, WindowEvent.WINDOW_CLOSING));
            }
        });
    }

    private void toFillFormByProduct(Product product) {
        productId.setText(String.valueOf(product.getId()));
        productName.setText(product.getName());
    }

    private void clearForm() {
        productId.setText("");
        productName.setText("");
    }

    private Product getAllDataFromForm() {
        Product product = new Product();

        if (!productId.getText().isEmpty() && isFormEdit)
            product.setId(Integer.valueOf(productId.getText().trim()));
        product.setName(productName.getText());
        return product;
    }

    private boolean checkIfFormCompletyFilled() {
        return !productName.getText().isEmpty() &&
                !(productId.getText().isEmpty() & isFormEdit);

    }
}