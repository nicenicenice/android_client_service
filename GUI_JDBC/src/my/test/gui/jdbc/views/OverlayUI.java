package my.test.gui.jdbc.views;

import my.test.gui.jdbc.Utils;
import my.test.gui.jdbc.entities.Overlay;
import my.test.gui.jdbc.contracts.ProductContract;
import my.test.gui.jdbc.db.OverlayBean;
import my.test.gui.jdbc.contracts.OverlayContract.GroundOverlays;
import my.test.gui.jdbc.contracts.WarehouseContract.Warehouses;
import my.test.gui.jdbc.contracts.SlotContract;
import my.test.gui.jdbc.resources.Strings;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

import static javax.swing.JOptionPane.showMessageDialog;
import static my.test.gui.jdbc.Utils.okcancel;

public class OverlayUI extends JPanel {
    private static final int ADD_FORM = 1;
    private static final int EDIT_FORM = 2;

    // main form
    private JButton addWarehouseButton = new JButton("Добавить склад");
    private JButton editWarehouseButton = new JButton("Изменить склад");
    private JButton deleteWarehouseButton = new JButton("Удалить склад");
    private JButton slotButton = new JButton("Слоты");
    private JButton prodButton = new JButton("Продукты");
    private JTable overlayTable = new JTable();
    private JTable slotTable = new JTable();
    private JScrollPane overlayScrollPane;
    private JScrollPane slotScrollPane;

    // controller for work with db
    private OverlayBean bean = new OverlayBean();

    private boolean areWeOpeningEditOverlayForm = false;
    public boolean areWeOpeningEditOverlayForm() {
        return areWeOpeningEditOverlayForm;
    }
    public void setAreWeOpeningEditOverlayForm(boolean val) {
        areWeOpeningEditOverlayForm = val;
    }

    public OverlayUI() {
        setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));

        // add tables
        overlayScrollPane = getOverlayScrollPaneWithTable();
        add(overlayScrollPane, 0);

        slotScrollPane = getSlotScrollPaneWithTable(-1);
        add(slotScrollPane, 1);

        // add buttons
        add(initButtons(), 2);

        // add click handler
        addRowClickListnerToOverlayTable();

        setSizeSlotScrollPane();
    }

    private void showAddWarehouseFrame()
    {
        JPanel that = this;
        EventQueue.invokeLater(() -> {
            that.putClientProperty("formKind", ADD_FORM);
            WarehouseFrame frame = new WarehouseFrame(that);
        });
    }

    private void showEditWarehouseFrame() {
        JPanel that = this;
        EventQueue.invokeLater(() -> {
            int selectedRow = overlayTable.getSelectedRow();
            if (selectedRow < 0) {
                showMessageDialog(null, "для начала, нужно выбрать строку");
                return;
            }

            areWeOpeningEditOverlayForm = true;
            //TODO: обработка ошибок
            int idWarehouse = (int)overlayTable.getValueAt(selectedRow, 0);
            Overlay overlay = bean.getOverlayEqualTo(idWarehouse);

            that.putClientProperty("formKind", EDIT_FORM);
            WarehouseFrame frame = new WarehouseFrame(that, overlay);
        });
    }

    private void addProduct() {
        JPanel that = this;

        EventQueue.invokeLater(() -> {
            that.putClientProperty("formKind", ADD_FORM);
            ProductFrame frame = new ProductFrame(that);
        });
    }

    private void addSlot() {
        JPanel that = this;

        EventQueue.invokeLater(() -> {
            SlotFrame frame = new SlotFrame(that);
        });
    }

    private JPanel initButtons() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 3, 3));

        panel.add(addWarehouseButton);
        addWarehouseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) { showAddWarehouseFrame(); }
        });

        panel.add(editWarehouseButton);
        editWarehouseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) { showEditWarehouseFrame(); }
        });

        panel.add(deleteWarehouseButton);
        deleteWarehouseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) { deleteSelectedRow(); }
        });

        panel.add(prodButton);
        prodButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                addProduct();
            }
        });

        panel.add(slotButton);
        slotButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                addSlot();
            }
        });

        return panel;
    }

    private void deleteSelectedRow() {
        //TODO: удалять нужно по-особому
        int selectedRow = overlayTable.getSelectedRow();
        if (selectedRow < 0) {
            showMessageDialog(null, Strings.FormStrings.NEEDS_TO_CHOOSE_ROW);
            return;
        }

        if (okcancel("Вы уверены?") == 2)
            return;

        int idWarehouse = (int)overlayTable.getValueAt(selectedRow, 0);
        boolean isOverlayDeleted = bean.deleteOverlayAndRelatedRecordsFromDb(idWarehouse);
        if (!isOverlayDeleted) {
            showMessageDialog(null, "ошибка при удалении записи");
            return;
        }

        repaintOverlayTable();
    }

    private JScrollPane getOverlayScrollPaneWithTable() {
        overlayTable = getFilledOverlayTable();
        overlayTable.setFillsViewportHeight(true);
        overlayScrollPane = new JScrollPane(overlayTable);
        return overlayScrollPane;
    }

    private JScrollPane getSlotScrollPaneWithTable(int warehouseId) {
        slotTable = getFilledSlotTable(warehouseId);
        slotTable.setFillsViewportHeight(true);
        slotScrollPane = new JScrollPane(slotTable);
        setSizeSlotScrollPane();
        return slotScrollPane;
    }

    private void setSizeSlotScrollPane() {
        slotScrollPane.setPreferredSize(new Dimension(300, 420));
    }

    public void repaintOverlayTable() {
        this.remove(overlayScrollPane);
        overlayScrollPane = getOverlayScrollPaneWithTable();
        this.add(overlayScrollPane, 0);
        this.revalidate();
        this.repaint();

        addRowClickListnerToOverlayTable();
    }

    private void addRowClickListnerToOverlayTable() {
        // add info to the slot table when clicked an overlay
        overlayTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {
                int selectedRow = overlayTable.getSelectedRow();
                int idWarehouse = (int)overlayTable.getValueAt(selectedRow, 0);
                if (idWarehouse > 0)
                    repaintSlotTable(idWarehouse);
            }
        });
    }

    public void repaintSlotTable(int warehouseId) {
        this.remove(slotScrollPane);
        slotScrollPane = getSlotScrollPaneWithTable(warehouseId);
        this.add(slotScrollPane, 1);
        this.revalidate();
        this.repaint();
    }

    private JTable getFilledSlotTable(int warehouseId) {
        // get column's names
        String[] columnNames = {
            SlotContract.Slots.NAME,
            ProductContract.Products.NAME
        };

        // get data
        Object[][] data = null;

        if (warehouseId <= 0) {
            data = new Object[1][columnNames.length];
        } else {
            Map<String, String> prodToSlot = bean.getSlotListFromDB(warehouseId);
            if (prodToSlot == null || prodToSlot.size() <= 0) {
                data = new Object[1][columnNames.length];
            } else {
                data = new Object[prodToSlot.size()][];
                Iterator it = prodToSlot.entrySet().iterator();
                int i = 0;
                while (it.hasNext()) {
                    data[i] = new Object[2];
                    Map.Entry pair = (Map.Entry)it.next();
                    String slotName = (String)pair.getKey();
                    String prodName = (String)pair.getValue();

                    data[i][0] = slotName;
                    data[i][1] = prodName;
                    i++;
                    //it.remove(); // avoids a ConcurrentModificationException
                }
            }
        }
        return new JTable(data, columnNames);
    }

    private JTable getFilledOverlayTable() {
        // get column's names
        String[] columnNames = {
            Warehouses.ID,
            Warehouses.NAME,
            GroundOverlays.LAT_LNG_BOUND_NEE,
            GroundOverlays.LAT_LNG_BOUND_NEN,
            GroundOverlays.LAT_LNG_BOUND_SWE,
            GroundOverlays.LAT_LNG_BOUND_SWN,
            GroundOverlays.OVERLAY_PIC
        };

        // get data
        Object[][] data = null;
        List<Object[]> rows = new ArrayList<>();
        List<Overlay> overlayList = bean.getOverlayListFromDB();

        for (Overlay overlayItem : overlayList) {
            Object[] row = overlayItem.getFields();
            rows.add(row);
        }

        data = Utils.getFilledDataForTableFromRowsList(rows, columnNames);
        return new JTable(data, columnNames);
    }
}