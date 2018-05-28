package my.test.gui.jdbc.views;

import my.test.gui.jdbc.Utils;
import my.test.gui.jdbc.contracts.ProductContract.Products;
import my.test.gui.jdbc.db.OverlayBean;
import my.test.gui.jdbc.entities.Product;
import my.test.gui.jdbc.resources.Strings.FormStrings;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

import static javax.swing.JOptionPane.showMessageDialog;
import static my.test.gui.jdbc.Utils.okcancel;

public class ProductFrame extends JFrame {
    // controller for work with db
    private OverlayBean bean = new OverlayBean();;

    private JButton addProductButton = new JButton("Добавить новый продукт");
    private JButton editProductButton = new JButton("Изменить выбранный продукт");
    private JButton deleteProductButton = new JButton("Удалить выбранный продукт");

    private final int FRAME_WIDTH = 500;
    private final int FRAME_HEIGHT = 560;

    public boolean isFormEdit() {
        return isFormEdit;
    }
    private boolean isFormEdit = false;

    private JTable productTable = new JTable();
    private JScrollPane productScrollPane = new JScrollPane();

    JFrame that;

    ProductFrame() {}

    ProductFrame(JComponent component) {
        super("Форма работы с продуктами");

        that = this;
        OverlayUI parentComponent = (OverlayUI)component;

        productScrollPane = getProductScrollPaneWithTable();
        add(productScrollPane);
        add(editProductButton);
        add(addProductButton);
        add(deleteProductButton);

        initButtons();

        setLayout(new FlowLayout(FlowLayout.CENTER, 0, 15));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(FRAME_WIDTH,FRAME_HEIGHT);

        Utils.setFrameLocationToCenterOfScreen(this);
        setVisible(true);
        setResizable(false);
    }

    private void initButtons() {
        addProductButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { showAddProdFrame(); }
        });

        editProductButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { showEditProdFrame(); }
        });

        deleteProductButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { deleteSelectedProduct(); }
        });
    }

    private void deleteSelectedProduct() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow < 0) {
            showMessageDialog(null, FormStrings.NEEDS_TO_CHOOSE_ROW);
            return;
        }

        if (okcancel("Вы уверены?") == 2)
            return;

        int prodId = (int)productTable.getValueAt(selectedRow, 0);

        if (!bean.deleteProductFromDbByProdId(prodId)) {
            showMessageDialog(null, FormStrings.ERROR_WHILE_DELETE_RECORD);
            return;
        }
        repaintProdTable();
    }

    public void repaintProdTable() {
        this.remove(productScrollPane);
        productScrollPane = getProductScrollPaneWithTable();
        this.add(productScrollPane, 0);
        this.revalidate();
        this.repaint();
    }

    private void showAddProdFrame() {
        EventQueue.invokeLater(() -> {
            isFormEdit = false;
            AddEditProductFrame frame = new AddEditProductFrame(that);
        });
    }

    private void showEditProdFrame() {
        EventQueue.invokeLater(() -> {
            int selectedRow = productTable.getSelectedRow();
            if (selectedRow < 0) {
                showMessageDialog(null, FormStrings.NEEDS_TO_CHOOSE_ROW);
                return;
            }

            //TODO: обработка ошибок
            int prodId = (int)productTable.getValueAt(selectedRow, 0);
            String prodName = (String)productTable.getValueAt(selectedRow, 1);

            Product chosenProduct = new Product(prodId, prodName);

            isFormEdit = true;
            AddEditProductFrame frame = new AddEditProductFrame(that, chosenProduct);
        });
    }

    private JScrollPane getProductScrollPaneWithTable() {
        productTable = getFilledProdTable();
        productTable.setFillsViewportHeight(true);
        productScrollPane = new JScrollPane(productTable);
        return productScrollPane;
    }

    private JTable getFilledProdTable() {
        // get column's names
        String[] columnNames = {
            Products.ID,
            Products.NAME
        };

        // get data
        Object[][] data = null;

        List<Product> prodList = bean.getProdListFromDb();
        List<Object[]> rows = new ArrayList<>();

        for (Product prodItem : prodList) {
            Object[] row = prodItem.getFields();
            rows.add(row);
        }

        data = Utils.getFilledDataForTableFromRowsList(rows, columnNames);
        return new JTable(data, columnNames);
    }
}