package my.android.service;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import my.android.service.contracts.*;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.*;
import my.android.service.contracts.ProductContract.Products;
import my.android.service.contracts.SlotContract.Slots;
import my.android.service.contracts.OverlayContract.GroundOverlays;
import my.android.service.contracts.WarehouseContract.Warehouses;
import my.android.service.contracts.WarehouseSlotContract.WarehouseSlots;

@WebServlet("/get_data")
public class OverlayService extends HttpServlet {

    private static final Logger LOG = Logger.getLogger(OverlayService.class);

    @Override
    // функция, выполняющаяся при GET запросе
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        try {
            JSONArray overlays = getOverlaysInJsonArrayFromDb();
            JSONArray slots = getSlotsInJsonArrayFromDb();
            JSONArray products = getProductsInJsonArrayFromDb();
            JSONArray warehouses = getWarehousesInJsonArrayFromDb();
            JSONArray warehouseSlots = getWarehouseSlotsInJsonArrayFromDb();

            JSONObject result = new JSONObject();
            result.put("overlays", overlays);
            result.put("slots", slots);
            result.put("products", products);
            result.put("warehouses", warehouses);
            result.put("warehouses_slots", warehouseSlots);

            sendResponse(response, result);
            //LOG.info("");
        } catch (Exception e) {
            LOG.error(e.getMessage());
            //sendResponse(response, "Opps.. something terrible happened! " + e.getMessage());
        }
    }

    /**
     * Connect to the test.db database
     * @return the Connection object
     */
    private Connection connect() {
        // SQLite connection string
        String url = "jdbc:sqlite:/Users/user/gr_overlays.db";
        Connection conn = null;
        try {
            try {
                Class.forName("org.sqlite.JDBC");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }

    /**
     * select all rows in the warehouses table
     */
    // подключаеся к БД и выбираем данные из нее
    private JSONArray getOverlaysInJsonArrayFromDb() {
        String sql = "SELECT * FROM " + OverlayContract.GR_OVERLAYS;

        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            JSONArray jsonResult = new JSONArray();

            while (rs.next()) {
                JSONObject jsonRow = new JSONObject();

                jsonRow.put(GroundOverlays.ID, rs.getInt(GroundOverlays.ID));
                jsonRow.put(GroundOverlays.ID_WAREHOUSE, rs.getInt(GroundOverlays.ID_WAREHOUSE));
                jsonRow.put(GroundOverlays.LAT_LNG_BOUND_NEN, rs.getDouble(GroundOverlays.LAT_LNG_BOUND_NEN));
                jsonRow.put(GroundOverlays.LAT_LNG_BOUND_NEE, rs.getDouble(GroundOverlays.LAT_LNG_BOUND_NEE));
                jsonRow.put(GroundOverlays.LAT_LNG_BOUND_SWN, rs.getDouble(GroundOverlays.LAT_LNG_BOUND_SWN));
                jsonRow.put(GroundOverlays.LAT_LNG_BOUND_SWE, rs.getDouble(GroundOverlays.LAT_LNG_BOUND_SWE));

                // здесь мы считываем картинку в формате BLOB в ByteArrayOutput поток (89 стр)
                // потом этот поток конвертируем в массив байтов (91 стр)
                // потом кодируем байтовый массив в строку, используя ISO-8859-1 кодировку.
                // для того, чтобы на строне android могли декодировать сроку, получить массив байтов, из него Bitmap, а из Bitmap - картинку
                byte[] bytes = null;
                ByteArrayOutputStream baos;
                String decodedBytes = null;
                try {
                    baos = new ByteArrayOutputStream();

                    InputStream input = rs.getBinaryStream(GroundOverlays.OVERLAY_PIC);
                    byte[] buffer = new byte[1024];
                    while (input.read(buffer) > 0) {
                        baos.write(buffer);
                    }
                    bytes = baos.toByteArray();
                    decodedBytes = new String(bytes, "ISO-8859-1");
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                jsonRow.put(GroundOverlays.OVERLAY_PIC, decodedBytes);

                jsonResult.put(jsonRow);
            }
            return jsonResult;
        } catch (SQLException e) {
            LOG.error(e.getMessage());
            return null;
        }
    }

    private JSONArray getSlotsInJsonArrayFromDb() {
        String sql = "SELECT * FROM " + SlotContract.SLOT;

        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            JSONArray jsonResult = new JSONArray();

            while (rs.next()) {
                JSONObject jsonRow = new JSONObject();
                jsonRow.put(Slots.ID, rs.getInt(Slots.ID));
                jsonRow.put(Slots.NAME, rs.getString(Slots.NAME));
                jsonRow.put(Slots.ID_PRODUCT, rs.getInt(Slots.ID_PRODUCT));
                jsonResult.put(jsonRow);
            }
            return jsonResult;
        } catch (SQLException e) {
            LOG.error(e.getMessage());
            return null;
        }
    }

    private JSONArray getProductsInJsonArrayFromDb() {
        String sql = "SELECT * FROM " + ProductContract.PRODUCT;

        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            JSONArray jsonResult = new JSONArray();

            while (rs.next()) {
                JSONObject jsonRow = new JSONObject();
                jsonRow.put(Products.ID, rs.getInt(Products.ID));
                jsonRow.put(Products.NAME, rs.getString(Products.NAME));
                jsonResult.put(jsonRow);
            }
            return jsonResult;
        } catch (SQLException e) {
            LOG.error(e.getMessage());
            return null;
        }
    }

    private JSONArray getWarehousesInJsonArrayFromDb() {
        String sql = "SELECT * FROM " + WarehouseContract.WAREHOUSE;

        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            JSONArray jsonResult = new JSONArray();

            while (rs.next()) {
                JSONObject jsonRow = new JSONObject();
                jsonRow.put(Warehouses.ID, rs.getInt(Warehouses.ID));
                jsonRow.put(Warehouses.NAME, rs.getString(Warehouses.NAME));
                jsonResult.put(jsonRow);
            }
            return jsonResult;
        } catch (SQLException e) {
            LOG.error(e.getMessage());
            return null;
        }
    }

    private JSONArray getWarehouseSlotsInJsonArrayFromDb() {
        String sql = "SELECT * FROM " + WarehouseSlotContract.WAREHOUSE_SLOT;

        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            JSONArray jsonResult = new JSONArray();

            while (rs.next()) {
                JSONObject jsonRow = new JSONObject();
                jsonRow.put(WarehouseSlots.ID, rs.getInt(WarehouseSlots.ID));
                jsonRow.put(WarehouseSlots.ID_WAREHOUSE, rs.getInt(WarehouseSlots.ID_WAREHOUSE));
                jsonRow.put(WarehouseSlots.ID_SLOT, rs.getInt(WarehouseSlots.ID_SLOT));
                jsonResult.put(jsonRow);
            }
            return jsonResult;
        } catch (SQLException e) {
            LOG.error(e.getMessage());
            return null;
        }
    }

    // отправляем ответ
    private void sendResponse(HttpServletResponse response, JSONObject jsonResult) {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = null;
        try {
            out = response.getWriter();
            out.print(jsonResult);
            //out.write(result);
        } catch (IOException e) {
            if (out != null)
                out.write("Error has occurred while forming the response.");
        } finally {
            if (out != null)
                out.close();
        }
    }
}
