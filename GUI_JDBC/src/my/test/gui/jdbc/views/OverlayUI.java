package my.test.gui.jdbc.views;

import my.test.gui.jdbc.entities.Overlay;
import my.test.gui.jdbc.contracts.ProductContract;
import my.test.gui.jdbc.controller.OverlayBean;
import my.test.gui.jdbc.contracts.OverlayContract.GroundOverlays;
import my.test.gui.jdbc.contracts.SlotContract;
import my.test.gui.jdbc.entities.Slot;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

import static javax.swing.JOptionPane.showMessageDialog;

// TODO: cоздать вес необходимые таблица, если они не созданы
public class OverlayUI extends JPanel {

    // main form
    private JButton createButton = new JButton("Create overlay");
    private JButton editButton = new JButton("Edit overlay");
    private JButton deleteButton = new JButton("Delete overlay");

    // controller for work with db
    private OverlayBean bean = new OverlayBean();
    private JTable overlayTable = new JTable();
    private JTable slotTable = new JTable();
    private JScrollPane overlayScrollPane;
    private JScrollPane slotScrollPane;

    private boolean areWeOpeningEditOverlayForm = false;
    public boolean areWeOpeningEditOverlayForm() {
        return areWeOpeningEditOverlayForm;
    }
    public void setAreWeOpeningEditOverlayForm(boolean val) {
        areWeOpeningEditOverlayForm = val;
    }
    public OverlayUI() {
        setLayout(new GridLayout(2, 2, 15, 20));

        // add table with overlays
        overlayScrollPane = getOverlayScrollPaneWithTable();
        add(overlayScrollPane, 0);

        slotScrollPane = getSlotScrollPaneWithTable(-1);
        add(slotScrollPane, 1);

        // add buttons
        add(initButtons(), 2);

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

    // form to add overlay
    private void showCreateOverlayFrame()
    {
        JPanel panel = this;
        EventQueue.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                OverlayAddFrame frame = new OverlayAddFrame(panel);
            }
        });
    }

    private JPanel initButtons() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 3, 3));

        panel.add(createButton);
        createButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                showCreateOverlayFrame();
            }
        });
        panel.add(editButton);
        editButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) { editSelectedRow(); }
        });
        panel.add(deleteButton);
        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                deleteSelectedRow();
            }
        });
        return panel;
    }

    private void editSelectedRow() {
        int selectedRow = overlayTable.getSelectedRow();
        if (selectedRow < 0) {
            showMessageDialog(null, "для начала, нужно выбрать строку");
            return;
        }

        areWeOpeningEditOverlayForm = true;
        //TODO: обработка ошибок
        int idWarehouse = (int)overlayTable.getValueAt(selectedRow, 0);
        Overlay overlay = bean.getOverlayEqualTo(idWarehouse);
        OverlayAddFrame frame = new OverlayAddFrame(this, overlay);
    }

    private int okcancel(String theMessage) {
        int result = JOptionPane.showConfirmDialog((Component) null, theMessage,
                "alert", JOptionPane.OK_CANCEL_OPTION);
        return result;
    }

    private void deleteSelectedRow() {
        int selectedRow = overlayTable.getSelectedRow();
        if (selectedRow < 0) {
            showMessageDialog(null, "для начала, нужно выбрать строку");
            return;
        }

        if (okcancel("Вы уверены?") == 2)
            return;

        int id_warehouse = (int)overlayTable.getValueAt(selectedRow, 0);
        boolean isOverlayDeleted = bean.deleteOverlayFromDbByWarehouseId(id_warehouse);
        if (isOverlayDeleted) {
            repaintOverlayTable();
        } else {
            showMessageDialog(null, "ошибка при удалении записи");
        }
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
        return slotScrollPane;
    }

    public void repaintOverlayTable() {
        this.remove(overlayScrollPane);
        overlayScrollPane = getOverlayScrollPaneWithTable();
        this.add(overlayScrollPane, 0);
        this.revalidate();
        this.repaint();
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
            if (prodToSlot == null && prodToSlot.size() <= 0) {
                data = new Object[1][columnNames.length];
            } else {
                data = new Object[prodToSlot.size()][];
                Iterator it = prodToSlot.entrySet().iterator();
                int i = 0;
                while (it.hasNext()) {
                    data[i] = new Object[2];
                    Map.Entry pair = (Map.Entry)it.next();
                    String prodName = (String)pair.getKey();
                    String slotName = (String)pair.getValue();
                    data[i][0] = slotName;
                    data[i][1] = prodName;
                    i++;
                    //it.remove(); // avoids a ConcurrentModificationException
                }
            }
        }
        return new JTable(data, columnNames);
    }

    private Object[][] fillDataTableByRowsList(List<Object[]> rows) {
        Object[][] data = null;
        int numRows = rows.size();
        if (numRows <= 0)
            return null;

        data = new Object[numRows][];
        for (int i = 0; i < numRows; ++i) {
            Object[] row = rows.get(i);
            int rowSize = row.length;
            data[i] = new Object[rowSize];
            for (int j = 0; j < rowSize; ++j) {
                data[i][j] = row[j];
            }
        }
        return data;
    }

    private JTable getFilledOverlayTable() {
        // get column's names
        String[] columnNames = {
                GroundOverlays.ID_WAREHOUSE,
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

        // lay down data to two dimension array to show in th table
        int numRows = rows.size();
        if (numRows <= 0) {
            data = new Object[1][columnNames.length];
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
        return new JTable(data, columnNames);
    }
}