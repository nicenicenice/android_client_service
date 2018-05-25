package my.test.gui.jdbc.views;

import my.test.gui.jdbc.Utils;
import my.test.gui.jdbc.contracts.ProductContract.Products;
import my.test.gui.jdbc.contracts.SlotContract.Slots;
import my.test.gui.jdbc.contracts.WarehouseContract.Warehouses;
import my.test.gui.jdbc.db.OverlayBean;
import my.test.gui.jdbc.entities.Slot;
import my.test.gui.jdbc.resources.Strings.FormStrings;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

import static javax.swing.JOptionPane.showMessageDialog;
import static my.test.gui.jdbc.Utils.okcancel;

public class SlotFrame extends JFrame {
    // controller for work with db
    private OverlayBean bean = new OverlayBean();;

    private JButton addSlotButton = new JButton("Добавить новый слот");
    private JButton editSlotButton = new JButton("Изменить выбранный слот");
    private JButton deleteSlotButton = new JButton("Удалить выбранный слот");

    private final int FRAME_WIDTH = 500;
    private final int FRAME_HEIGHT = 540;

    public boolean isFormEdit() {
        return isFormEdit;
    }
    private boolean isFormEdit = false;

    private JTable slotTable = new JTable();
    private JScrollPane slotScrollPane = new JScrollPane();

    JFrame that;

    SlotFrame() {}

    SlotFrame(JComponent component) {
        super("Форма работы со слотами");
        that = this;

        slotScrollPane = getSlotScrollPaneWithTable();
        add(slotScrollPane);

        add(editSlotButton);
        add(addSlotButton);
        add(deleteSlotButton);

        initButtons();

        setLayout(new FlowLayout());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(FRAME_WIDTH,FRAME_HEIGHT);
        setVisible(true);
        setResizable(false);
    }

    private void initButtons() {
        addSlotButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { showAddSlotFrame(); }
        });

        editSlotButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { showEditSlotFrame(); }
        });

        deleteSlotButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { deleteSelectedSlot(); }
        });
    }

    private void showAddSlotFrame() {
        EventQueue.invokeLater(() -> {
            isFormEdit = false;
            AddEditSlotFrame frame = new AddEditSlotFrame(that);
        });
    }

    private void showEditSlotFrame() {
        EventQueue.invokeLater(() -> {
            int selectedRow = slotTable.getSelectedRow();
            if (selectedRow < 0) {
                showMessageDialog(null, FormStrings.NEEDS_TO_CHOOSE_ROW);
                return;
            }

            //TODO: обработка ошибок
            int slotId = (int)slotTable.getValueAt(selectedRow, 0);
            String prodName = (String)slotTable.getValueAt(selectedRow, 1);
            int prodId = (int)slotTable.getValueAt(selectedRow, 2);
            int warehouseId = (int)slotTable.getValueAt(selectedRow, 4);

            Slot chosenSlot = new Slot(slotId, prodName, prodId, warehouseId);

            isFormEdit = true;
            AddEditSlotFrame frame = new AddEditSlotFrame(that, chosenSlot);
        });
    }

    private void deleteSelectedSlot() {
        int selectedRow = slotTable.getSelectedRow();
        if (selectedRow < 0) {
            showMessageDialog(null, FormStrings.NEEDS_TO_CHOOSE_ROW);
            return;
        }

        if (okcancel("Вы уверены?") == 2)
            return;

        int slotId = (int)slotTable.getValueAt(selectedRow, 0);

        if (!bean.deleteAllRecordsRelatedWithSlotInDb(slotId)) {
            showMessageDialog(null, FormStrings.ERROR_WHILE_DELETE_RECORD);
            return;
        }
        repaintSlotTable();
    }

    public void repaintSlotTable() {
        this.remove(slotScrollPane);
        slotScrollPane = getSlotScrollPaneWithTable();
        this.add(slotScrollPane, 0);
        this.revalidate();
        this.repaint();
    }

    private JScrollPane getSlotScrollPaneWithTable() {
        slotTable = getFilledSlotTable();
        slotTable.setFillsViewportHeight(true);
        slotScrollPane = new JScrollPane(slotTable);
        return slotScrollPane;
    }

    private JTable getFilledSlotTable() {
        // get column's names
        String[] columnNames = {
            Slots.ID,
            Slots.NAME,
            Slots.ID_PRODUCT,
            Products.NAME,
            Warehouses.ID,
            Warehouses.NAME
        };

        // get data
        Object[][] data = null;

        List<Slot> slotList = bean.getSlotListWithAllInfoFromDb();
        List<Object[]> rows = new ArrayList<>();

        for (Slot slotItem : slotList) {
            Object[] row = slotItem.getFields();
            rows.add(row);
        }

        data = Utils.getFilledDataForTableFromRowsList(rows, columnNames);
        return new JTable(data, columnNames);
    }
}