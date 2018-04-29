package com.proj.mqliteclient.utils;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 31/07/2016.
 */
public class Utils {
    private static String SERVICE_URL = "http://10.0.2.2:8080/doctors_schedule/get_data";

    @Nullable
    public static JSONArray getDataInJsonArrayFormat() {
        String rawResponce = getRawDataFromService();
        if (rawResponce == null)
            return null;

        JSONArray jsonResponce;
        try {
            jsonResponce = new JSONArray(rawResponce);
            return jsonResponce;
        } catch (Exception e) {
            return null;
        }
    }

    private static String getRawDataFromService() {
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
            e.printStackTrace();
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}
