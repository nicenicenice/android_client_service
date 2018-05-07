package test.assignment.vista;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.*;

@WebServlet("/get_data")
public class DoctorScheduler extends HttpServlet {

    private static final Logger LOG = Logger.getLogger(DoctorScheduler.class);

    @Override
    // функция, выполняющаяся при GET запросе
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        try {
            JSONArray jsonArray = getDbDataInJsonArrayFormat();
            sendResponse(response, jsonArray);
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
                System.out.println("2" + e.getMessage());
            }

            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println("1" + e.getMessage());
        }
        return conn;
    }

    /**
     * select all rows in the warehouses table
     */
    // подключаеся к БД и выбираем данные из нее
    private JSONArray getDbDataInJsonArrayFormat() {
        String sql = "SELECT * FROM gr_overlays";

        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            JSONArray jsonResult = new JSONArray();

            while (rs.next()) {
                JSONObject jsonRow = new JSONObject();

                jsonRow.accumulate("name", rs.getString("name"));
                jsonRow.accumulate("latLngBoundNEN", rs.getDouble("latLngBoundNEN"));
                jsonRow.accumulate("latLngBoundNEE", rs.getDouble("latLngBoundNEE"));
                jsonRow.accumulate("latLngBoundSWN", rs.getDouble("latLngBoundSWN"));
                jsonRow.accumulate("latLngBoundSWE", rs.getDouble("latLngBoundSWE"));

                // здесь мы считываем картинку в формате BLOB в ByteArrayOutput поток (89 стр)
                // потом этот поток конвертируем в массив байтов (91 стр)
                // потом кодируем байтовый массив в строку, используя ISO-8859-1 кодировку.
                // для того, чтобы на строне android могли декодировать сроку, получить массив байтов, из него Bitmap, а из Bitmap - картинку
                byte[] bytes = null;
                ByteArrayOutputStream baos;
                String decodedBytes = null;
                try {
                    baos = new ByteArrayOutputStream();

                    InputStream input = rs.getBinaryStream("overlayPic");
                    byte[] buffer = new byte[1024];
                    while (input.read(buffer) > 0) {
                        baos.write(buffer);
                    }
                    bytes = baos.toByteArray();
                    decodedBytes = new String(bytes, "ISO-8859-1");
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                jsonRow.accumulate("overlayPic", decodedBytes);

                jsonResult.put(jsonRow);
            }
            return jsonResult;
        } catch (SQLException e) {
            LOG.error(e.getMessage());
            return null;
        }
    }

    // отправляем ответ
    private void sendResponse(HttpServletResponse response, JSONArray jsonResult) {
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