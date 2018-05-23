package my.test.gui.jdbc.controller;

import my.test.gui.jdbc.contracts.*;
import my.test.gui.jdbc.entities.Overlay;
import org.apache.log4j.Logger;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import my.test.gui.jdbc.contracts.WarehouseContract.Warehouses;
import my.test.gui.jdbc.contracts.OverlayContract.GroundOverlays;
import my.test.gui.jdbc.contracts.WarehouseSlotContract.WarehouseSlots;
import my.test.gui.jdbc.contracts.ProductContract.Products;
import my.test.gui.jdbc.contracts.SlotContract.Slots;

// Controller
public class OverlayBean {
    private static final Logger LOG = Logger.getLogger(OverlayBean.class);
    static final String JDBC_DRIVER = "org.sqlite.JDBC";
    static final String DB_URL = "jdbc:sqlite:/Users/user/gr_overlays.db";

    public OverlayBean() {}

    private Connection connect() {
        Connection conn = null;
        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL);
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    public List<Integer> getSlotIdsBy(int warehouseId) {
        String sql = "SELECT * FROM " + WarehouseSlotContract.WAREHOUSE_SLOT
                + " WHERE " + WarehouseSlots.ID_WAREHOUSE + " = ?";

        List<Integer> slotIds = new ArrayList<>();
        try (Connection conn = this.connect();
             PreparedStatement pstmt  = conn.prepareStatement(sql)) {

            pstmt.setInt(1, warehouseId);
            ResultSet rs  = pstmt.executeQuery();

            // loop through the result set
            while (rs.next()) {
                Integer slotId = rs.getInt(WarehouseSlots.ID_SLOT);
                slotIds.add(slotId);
            }
            return slotIds;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    public Map<String, String> getSlotListFromDB(int warehouseId) {
        List<Integer> slotIds = getSlotIdsBy(warehouseId);

        if (slotIds == null || slotIds.size() <= 0)
            return null;
        int numSlots = slotIds.size();

        String rawPlaceholdersStr = new String(new char[numSlots]).replace("\0", "?,");
        String placeholdersStr = rawPlaceholdersStr.substring(0, rawPlaceholdersStr.length() - 1);

        String sql = "SELECT * FROM " + SlotContract.SLOT + " INNER JOIN " + ProductContract.PRODUCT
                + " ON " + SlotContract.SLOT + "." + SlotContract.Slots.ID_PRODUCT + " = "
                + ProductContract.PRODUCT + "." + ProductContract.Products.ID
                + " WHERE " + SlotContract.Slots.ID
                + " IN (" + placeholdersStr + ")";

        Map<String, String> prodToSlot = new HashMap<>();
        try (Connection conn = this.connect();
             PreparedStatement pstmt  = conn.prepareStatement(sql)) {
            for (int i = 0; i < slotIds.size(); i++) {
                pstmt.setInt(i + 1, slotIds.get(i));
            }
            ResultSet rs  = pstmt.executeQuery();

            while (rs.next()) {
                prodToSlot.put(rs.getString(Products.NAME), rs.getString(Slots.NAME));
            }
        } catch (SQLException e) {
            LOG.error(e.getMessage());
        }
        return prodToSlot;
    }

    public List<Overlay> getOverlayListFromDB() {
        String sql = "SELECT * FROM " + OverlayContract.GR_OVERLAYS + " INNER JOIN " + WarehouseContract.WAREHOUSE
                + " ON " + OverlayContract.GR_OVERLAYS + "." + GroundOverlays.ID_WAREHOUSE + " = "
                + WarehouseContract.WAREHOUSE + "." + Warehouses.ID;

        List<Overlay> overlayList = new ArrayList<>();

        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Overlay overlay = new Overlay();
                overlay.setIdWarehouse(rs.getInt(Warehouses.ID));
                overlay.setWarehouseName(rs.getString(Warehouses.NAME));
                overlay.setLatLngBoundNEN(rs.getString(GroundOverlays.LAT_LNG_BOUND_NEN));
                overlay.setLatLngBoundNEE(rs.getString(GroundOverlays.LAT_LNG_BOUND_NEE));
                overlay.setLatLngBoundSWN(rs.getString(GroundOverlays.LAT_LNG_BOUND_SWN));
                overlay.setLatLngBoundSWE(rs.getString(GroundOverlays.LAT_LNG_BOUND_SWE));

                String decodedBytes = null;
                try {
                    decodedBytes = getDecodedStringFromBlob(rs.getBinaryStream(GroundOverlays.OVERLAY_PIC));
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
                overlay.setDecodedOverlayPic(decodedBytes);
                overlayList.add(overlay);
            }
        } catch (SQLException e) {
            LOG.error(e.getMessage());
        }
        return overlayList;
    }

    public boolean insertWarehouseToDb(String warehouseName) {
        if (warehouseName == null || warehouseName.isEmpty())
            return false;

        String sql = "INSERT INTO " + WarehouseContract.WAREHOUSE
                + "(" + Warehouses.NAME +")" + " VALUES(?)";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, warehouseName);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public int getWarehouseIdByNameFromDb(String warehouseName) {
        if (warehouseName == null || warehouseName.isEmpty())
            return -1;

        int warehouseId = -1;

        String sql = "SELECT * FROM " + WarehouseContract.WAREHOUSE
                + " WHERE " + Warehouses.NAME + " = ?";

        try (Connection conn = this.connect();
             PreparedStatement pstmt  = conn.prepareStatement(sql)) {

            pstmt.setString(1, warehouseName);
            ResultSet rs  = pstmt.executeQuery();

            // loop through the result set
            if (rs.next()) {
                warehouseId = rs.getInt(Warehouses.ID);
            }
            return warehouseId;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public boolean insertRowToDB(Overlay overlay) {
        if (overlay == null)
            return false;

        boolean succesResult = false;
        succesResult = insertWarehouseToDb(overlay.getWarehouseName());

        if (!succesResult)
            return false;

        int wakehouseId = getWarehouseIdByNameFromDb(overlay.getWarehouseName());
        if (wakehouseId <= 0)
            return false;

        String sql = "INSERT INTO " + OverlayContract.GR_OVERLAYS + "("
                + GroundOverlays.ID_WAREHOUSE + ","
                + GroundOverlays.LAT_LNG_BOUND_NEN + ","
                + GroundOverlays.LAT_LNG_BOUND_NEE + ","
                + GroundOverlays.LAT_LNG_BOUND_SWE + ","
                + GroundOverlays.LAT_LNG_BOUND_SWN + ","
                + GroundOverlays.OVERLAY_PIC + ")"
                + " VALUES(?,?,?,?,?,?)";

        try (Connection conn = this.connect();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setInt(1, wakehouseId);
                    pstmt.setString(2, overlay.getLatLngBoundNEN());
                    pstmt.setString(3, overlay.getLatLngBoundNEE());
                    pstmt.setString(4, overlay.getLatLngBoundSWE());
                    pstmt.setString(5, overlay.getLatLngBoundSWN());
                    String imageInString = overlay.getDecodedOverlayPic();
                    byte[] imageInBytes = null;
                    try {
                        // декодируем строку в массив битов
                        imageInBytes = imageInString.getBytes("ISO-8859-1");
                    } catch (Exception e) {}

                    pstmt.setBytes(6, imageInBytes);
                    pstmt.executeUpdate();
                    return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateWarehouseById(int idWarehouse, String warehouseName) {
        if (idWarehouse <= 0)
            return false;

        String sql = "UPDATE " + WarehouseContract.WAREHOUSE+ " SET "
                + Warehouses.NAME + " = ?"
                + " WHERE " + Warehouses.ID + " = ?";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, warehouseName);
            pstmt.setInt(2, idWarehouse);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateRowInDb(Overlay overlay) {
        if (overlay == null)
            return false;

        // сначала меняем имя wharehouse,
        // потом оверлей
        boolean successResult = updateWarehouseById(overlay.getIdWarehouse(), overlay.getWarehouseName());
        if (!successResult)
            return false;

        String sql = "UPDATE " + OverlayContract.GR_OVERLAYS + " SET "
                + GroundOverlays.LAT_LNG_BOUND_NEN + " = ?" + ","
                + GroundOverlays.LAT_LNG_BOUND_NEE + " = ?" + ","
                + GroundOverlays.LAT_LNG_BOUND_SWE + " = ?" + ","
                + GroundOverlays.LAT_LNG_BOUND_SWN + " = ?" + ","
                + GroundOverlays.OVERLAY_PIC + " = ?"
                + " WHERE " + GroundOverlays.ID_WAREHOUSE + " = ?";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, overlay.getLatLngBoundNEN());
            pstmt.setString(2, overlay.getLatLngBoundNEE());
            pstmt.setString(3, overlay.getLatLngBoundSWE());
            pstmt.setString(4, overlay.getLatLngBoundSWN());

            String imageString = overlay.getDecodedOverlayPic();
            byte[] pictureInBytes = null;
            try {
                // декодируем строку в массив битов
                pictureInBytes = imageString.getBytes("ISO-8859-1");
            } catch (Exception e) {}

            pstmt.setBytes(5, pictureInBytes);
            pstmt.setInt(6, overlay.getIdWarehouse());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteOverlayFromDbByWarehouseId(int idWarehouse) {
        if (idWarehouse <= 0)
            return false;

        String sql = "DELETE FROM " + OverlayContract.GR_OVERLAYS
                + " WHERE " + GroundOverlays.ID_WAREHOUSE + " = ?";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // set the corresponding param
            pstmt.setInt(1, idWarehouse);
            // execute the delete statement
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Overlay getOverlayEqualTo(int idWarehouse) {
        String sql = "SELECT * FROM " + OverlayContract.GR_OVERLAYS + " INNER JOIN " + WarehouseContract.WAREHOUSE
                + " ON " + OverlayContract.GR_OVERLAYS + "." + GroundOverlays.ID_WAREHOUSE + " = "
                + WarehouseContract.WAREHOUSE + "." + Warehouses.ID
                + " WHERE " + OverlayContract.GR_OVERLAYS + "." + GroundOverlays.ID_WAREHOUSE + " = ?";;

        try (Connection conn = this.connect();
             PreparedStatement pstmt  = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idWarehouse);
            ResultSet rs  = pstmt.executeQuery();

            // loop through the result set
            Overlay overlay = null;
            if (rs.next()) {
                overlay = new Overlay();
                overlay.setIdWarehouse(rs.getInt(Warehouses.ID));
                overlay.setWarehouseName(rs.getString(Warehouses.NAME));
                overlay.setLatLngBoundNEN(rs.getString(GroundOverlays.LAT_LNG_BOUND_NEN));
                overlay.setLatLngBoundNEE(rs.getString(GroundOverlays.LAT_LNG_BOUND_NEE));
                overlay.setLatLngBoundSWN(rs.getString(GroundOverlays.LAT_LNG_BOUND_SWN));
                overlay.setLatLngBoundSWE(rs.getString(GroundOverlays.LAT_LNG_BOUND_SWE));

                String decodedBytes = null;
                try {
                    decodedBytes = getDecodedStringFromBlob(rs.getBinaryStream(GroundOverlays.OVERLAY_PIC));
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
                overlay.setDecodedOverlayPic(decodedBytes);
            }
            return overlay;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getDecodedStringFromBlob(InputStream input) throws IOException {
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
}