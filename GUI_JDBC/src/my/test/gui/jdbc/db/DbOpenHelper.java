package my.test.gui.jdbc.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static my.test.gui.jdbc.db.OverlayBean.DB_URL;
import static my.test.gui.jdbc.db.OverlayBean.JDBC_DRIVER;

public class DbOpenHelper {

    public DbOpenHelper() {
        createOverlayTable();
        createProductTable();
        createSlotTable();
        createBindingWarehouseSlotTable();
        createWarehouseTable();
    }

    private Connection connect() {
        Connection conn = null;
        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return conn;
    }

    private void createOverlayTable() {
        String sql = "CREATE TABLE IF NOT EXISTS overlay (\n" +
                "\t\tid INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "\t\tid_warehouse INTEGER,\n" +
                "\t\tlatLngBoundNEN REAL, \n" +
                "\t\tlatLngBoundNEE REAL, \n" +
                "\t\tlatLngBoundSWN REAL, \n" +
                "\t\tlatLngBoundSWE REAL, \n" +
                "\t\toverlayPic BLOB,\n" +
                "\t\tFOREIGN KEY(id_warehouse) REFERENCES warehouse(id_warehouse)\n" +
                "\t);";
        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void createProductTable() {
        String sql = "CREATE TABLE IF NOT EXISTS product (\n" +
                "\t\tid_product INTEGER PRIMARY KEY AUTOINCREMENT, \n" +
                "\t\tproduct_name TEXT\n" +
                "\t);";
        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void createWarehouseTable() {
        String sql = "CREATE TABLE IF NOT EXISTS warehouse (\n" +
                "\t\tid_warehouse INTEGER PRIMARY KEY AUTOINCREMENT, \n" +
                "\t\twarehouse_name TEXT\n" +
                "\t);";
        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void createSlotTable() {
        String sql = "CREATE TABLE IF NOT EXISTS slot (\n" +
                "\t\tid_slot INTEGER PRIMARY KEY AUTOINCREMENT, \n" +
                "\t\tslot_name TEXT, \n" +
                "\t\tid_product INTEGER, \n" +
                "\t\tFOREIGN KEY(id_product) REFERENCES product(id_product)\n" +
                "\t);";
        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void createBindingWarehouseSlotTable() {
        String sql = "CREATE TABLE IF NOT EXISTS warehouse_slot (\n"+
                "\t\tid INTEGER PRIMARY KEY AUTOINCREMENT, \n"+
                "\t\tid_warehouse INTEGER, \n"+
                "\t\tid_slot INTEGER, \n"+
                "\t\tFOREIGN KEY(id_warehouse) REFERENCES warehouse(id_warehouse),\n"+
                "\t\tFOREIGN KEY(id_slot) REFERENCES slot(id_slot)\n"+
                "\t);";
        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}