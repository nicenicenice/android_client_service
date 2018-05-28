package my.test.gui.jdbc.views;

import my.test.gui.jdbc.Utils;
import my.test.gui.jdbc.db.OverlayBean;
import my.test.gui.jdbc.entities.Product;
import my.test.gui.jdbc.entities.Slot;
import my.test.gui.jdbc.entities.Warehouse;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.List;
import javax.swing.*;

import static javax.swing.JOptionPane.showMessageDialog;
import static my.test.gui.jdbc.resources.Strings.FormStrings;

public class AddEditSlotFrame extends JFrame {
    // controller for work with db
    private OverlayBean bean = new OverlayBean();;

    // frame for add overlay form
    private JTextField slotId = new JTextField(30);
    private JTextField slotName = new JTextField(30);
    private JTextField productId = new JTextField(30);
    private JTextField warehouseId = new JTextField(30);

    private JButton addSlotButton = new JButton("Добавить");
    private JButton editSlotButton = new JButton("Изменить");

    JComboBox<Product> productComboBox = new JComboBox<Product>();
    JComboBox<Warehouse> warehouseComboBox = new JComboBox<Warehouse>();

    private int FRAME_WIDTH = 450;
    private int FRAME_HEIGHT = 220;
    boolean isFormEdit = false;

    JFrame that;
    SlotFrame parentFrame;

    AddEditSlotFrame(JFrame frame, Slot slot) {
        this(frame);
        toFillFormBySlot(slot);
    }

    AddEditSlotFrame(JFrame frame) {
        super("Добавление слота");

        that = this;
        parentFrame = (SlotFrame)frame;
        isFormEdit = parentFrame.isFormEdit();

        int widthIndent = 0;
        int heightIndent = 7;
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setOpaque(true);

        if (parentFrame.isFormEdit()) {
            FRAME_HEIGHT = 270;
            this.setTitle("Изменение слота");

            JLabel lIdOfSlot = new JLabel("Id слота");
            mainPanel.add(lIdOfSlot);
            mainPanel.add(slotId);
            mainPanel.add(Box.createRigidArea(new Dimension(widthIndent, heightIndent)));
            slotId.setEnabled(false);
        }

        JLabel lNameOfSlot = new JLabel("Название слота");
        mainPanel.add(lNameOfSlot);
        mainPanel.add(slotName);
        mainPanel.add(Box.createRigidArea(new Dimension(widthIndent, heightIndent)));

        productComboBox = getFilledProductJComboBox();
        if (productComboBox == null || productComboBox.getItemCount() <= 0) {
            showMessageDialog(null, "для начала добавьте продукт в таблицу");
            return;
        }
        JLabel lproducts = new JLabel("Продукты");
        mainPanel.add(lproducts);
        mainPanel.add(productComboBox);
        mainPanel.add(Box.createRigidArea(new Dimension(widthIndent, heightIndent)));

        warehouseComboBox = getFilledWarehouseJComboBox();
        JLabel lWarehouses = new JLabel("Склады");
        mainPanel.add(lWarehouses);
        mainPanel.add(warehouseComboBox);
        mainPanel.add(Box.createRigidArea(new Dimension(widthIndent, heightIndent)));

        // handle if we should add a new record or edit an old one
        if (parentFrame.isFormEdit()) {
            mainPanel.add(editSlotButton);
        } else {
            mainPanel.add(addSlotButton);
        }
        mainPanel.add(Box.createRigidArea(new Dimension(widthIndent, heightIndent)));

        initButtons();

        add(BorderLayout.WEST, mainPanel);
        setLayout(new FlowLayout());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(FRAME_WIDTH,FRAME_HEIGHT);

        Utils.setFrameLocationToCenterOfScreen(this);
        setVisible(true);
        setResizable(false);
    }

    private JComboBox<Product> getFilledProductJComboBox() {
        JComboBox<Product> productComboBox = new JComboBox<Product>();
        java.util.List<Product> prodList = bean.getProdListFromDb();
        for (Product prod : prodList) {
            productComboBox.addItem(prod);
        }
        return productComboBox;
    }

    private JComboBox<Warehouse> getFilledWarehouseJComboBox() {
        JComboBox<Warehouse> warehouseComboBox = new JComboBox<Warehouse>();
        List<Warehouse> warehouseList = bean.getWarehouseListFromDb();
        for (Warehouse warehouse : warehouseList) {
            warehouseComboBox.addItem(warehouse);
        }
        return warehouseComboBox;
    }

    private void initButtons() {
        addSlotButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { addSlot(); }
        });

        editSlotButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { editSlot(); }
        });
    }

    private void addSlot()
    {
        EventQueue.invokeLater(() -> {
            // check if a form complety filled
            if (!checkIfFormCompletyFilled()) {
                showMessageDialog(null, FormStrings.FILL_THE_FORM);
                return;
            }
            // forming data to insert to DB
            Slot slot = getAllDataFromForm();

            // insert data to DB
            if (new OverlayBean().insertSlotToDBWithBindings(slot)) {
                clearForm();
                parentFrame.repaintSlotTable();
            }
        });
    }

    private void editSlot()
    {
        EventQueue.invokeLater(() -> {
            // check is a form complety filled
            if (!checkIfFormCompletyFilled()) {
                showMessageDialog(null, FormStrings.FILL_THE_FORM);
                return;
            }

            // get all form's data to update db
            Slot slot = getAllDataFromForm();

            // update data in a row
            if (new OverlayBean().updateBindingOfWarehouseAndSlot(slot)) {
                parentFrame.repaintSlotTable();
                that.dispatchEvent(new WindowEvent(that, WindowEvent.WINDOW_CLOSING));
            }
        });
    }

    private int getProdIdxInComboBoxByProdId(int prodId) {
        for (int i = 0; i < productComboBox.getItemCount(); ++i) {
            Product prod = productComboBox.getItemAt(i);
            if (prodId == prod.getId())
                return i;
        }
        return -1;
    }

    private int getWarehouseIdxInComboBoxByWarehouseId(int warehouseId) {
        for (int i = 0; i < warehouseComboBox.getItemCount(); ++i) {
            Warehouse warehouse = warehouseComboBox.getItemAt(i);
            if (warehouseId == warehouse.getId())
                return i;
        }
        return -1;
    }

    private void toFillFormBySlot(Slot slot) {
        slotId.setText(String.valueOf(slot.getId()));
        slotName.setText(slot.getName());

        int productIdx = getProdIdxInComboBoxByProdId(slot.getProdId());
        if (productIdx >= 0)
            productComboBox.setSelectedIndex(productIdx);

        int warehouseIdx = getWarehouseIdxInComboBoxByWarehouseId(slot.getWarehouseId());
        if (warehouseIdx >= 0)
            warehouseComboBox.setSelectedIndex(warehouseIdx);
    }

    private void clearForm() {
        slotName.setText("");
        if (warehouseComboBox.getItemCount() > 0)
            warehouseComboBox.setSelectedIndex(0);
        if (productComboBox.getItemCount() > 0)
            productComboBox.setSelectedIndex(0);
    }

    private Slot getAllDataFromForm() {
        Slot slot = new Slot();

        if (!slotId.getText().isEmpty() && isFormEdit)
            slot.setId(Integer.valueOf(slotId.getText().trim()));
        slot.setName(slotName.getText());

        // взять склад id и продукт id из ComboBox
        Product selectedProd = (Product)productComboBox.getSelectedItem();
        slot.setProdId(selectedProd.getId());

        Warehouse warehouse = (Warehouse)warehouseComboBox.getSelectedItem();
        slot.setWarehouseId(warehouse.getId());
        return slot;
    }

    private boolean checkIfFormCompletyFilled() {
        return !slotName.getText().isEmpty() &&
                !(slotId.getText().isEmpty() & isFormEdit);

    }
}