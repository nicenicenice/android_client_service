package com.proj.mqliteclient.utils;

import android.support.annotation.Nullable;
import org.json.JSONArray;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * Created by user on 31/07/2016.
 */
public class Utils {
    private static String SERVICE_URL = "http://10.0.2.2:8080/overlay_service/get_data";

    // парсим json из полученного ответа сервиса
    @Nullable
    public static JSONArray getDataInJsonArrayFormat() throws Exception {
        String rawResponce = getRawDataFromService();

        JSONArray jsonResponce;
        jsonResponce = new JSONArray(rawResponce);
        return jsonResponce;
    }

    // подключаемся к сервису и получаем данные с него
    private static String getRawDataFromService() throws Exception {
        BufferedReader inputStream;
        HttpURLConnection connection = null;

        try {
            URL jsonUrl = new URL(SERVICE_URL);
            connection = (HttpURLConnection)jsonUrl.openConnection();

            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            inputStream = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));

            StringBuilder response = new StringBuilder();
            String line;
            while ((line = inputStream.readLine()) != null)
            {
                response.append(line);
            }
            return response.toString();
        } catch (Exception e) {
            throw new Exception(e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}
