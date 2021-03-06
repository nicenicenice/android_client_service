package com.proj.mqliteclient.db;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by user on 28/04/2018.
 */

// класс-помощьник для работы с БД
public class DbUtils {

    // получаем данные из БД и закрываем курсор
    public static List<ContentValues> getResultStringListAndClose(Cursor c) {
        final List<ContentValues> resultStringList = getAllDataFromDB(c);
        closeCursor(c);
        return resultStringList;
    }

    public static List<String> getStringListWithNamesAndClose(Cursor c) {
        final List<String> resultStringList = getNamesFromDB(c);
        closeCursor(c);
        return resultStringList;
    }

    public static Map<String, String> getMapWithSlotsProdsAndClose(Cursor c) {
        final Map<String, String> resultSlotsInfoMap = getSlotsInfoFromDB(c);
        closeCursor(c);
        return resultSlotsInfoMap;
    }

    private static Map<String, String> getSlotsInfoFromDB(Cursor c) {
        Map<String, String> slotToProdMap = new HashMap<>();

        if (c != null && (c.isFirst() || c.moveToFirst())) {
            do {
                String slotName = null;
                String prodName = null;

                int slotNameIdx = c.getColumnIndex(DbContract.Slots.NAME);
                if (!c.isNull(slotNameIdx)) {
                    slotName = c.getString(slotNameIdx);
                }

                int prodNameIdx = c.getColumnIndex(DbContract.Products.NAME);
                if (!c.isNull(prodNameIdx)) {
                    prodName = c.getString(prodNameIdx);
                }
                slotToProdMap.put(slotName, prodName);
            }
            while (c.moveToNext());
        }

        return slotToProdMap;

    }

    // с помощью курсора вынимаем данные из таблицы в наш список
    // фактически, здесь происхоит выполнение запроса
    private static List<ContentValues> getAllDataFromDB(Cursor c) {
        List<ContentValues> resList = new ArrayList<>();
        if (c != null && (c.isFirst() || c.moveToFirst())) {
            do {
                ContentValues dBValues = new ContentValues();

                int id = c.getColumnIndex(DbContract.GroundOverlays.WAREHOUSE_ID);
                if (!c.isNull(id)) {
                    dBValues.put(DbContract.GroundOverlays.WAREHOUSE_ID, c.getInt(id));
                }

                int latLngBoundNEN = c.getColumnIndex(DbContract.GroundOverlays.LAT_LNG_BOUND_NEN);
                if (!c.isNull(latLngBoundNEN)) {
                    dBValues.put(DbContract.GroundOverlays.LAT_LNG_BOUND_NEN, c.getDouble(latLngBoundNEN));
                }

                int latLngBoundNEE = c.getColumnIndex(DbContract.GroundOverlays.LAT_LNG_BOUND_NEE);
                if (!c.isNull(latLngBoundNEE)) {
                    dBValues.put(DbContract.GroundOverlays.LAT_LNG_BOUND_NEE, c.getDouble(latLngBoundNEE));
                }

                int latLngBoundSWN = c.getColumnIndex(DbContract.GroundOverlays.LAT_LNG_BOUND_SWN);
                if (!c.isNull(latLngBoundSWN)) {
                    dBValues.put(DbContract.GroundOverlays.LAT_LNG_BOUND_SWN, c.getDouble(latLngBoundSWN));
                }

                int latLngBoundSWE = c.getColumnIndex(DbContract.GroundOverlays.LAT_LNG_BOUND_SWE);
                if (!c.isNull(latLngBoundSWE)) {
                    dBValues.put(DbContract.GroundOverlays.LAT_LNG_BOUND_SWE, c.getDouble(latLngBoundSWE));
                }

                int overlayPic = c.getColumnIndex(DbContract.GroundOverlays.OVERLAY_PIC);
                if (!c.isNull(overlayPic)) {
                    dBValues.put(DbContract.GroundOverlays.OVERLAY_PIC, c.getString(overlayPic));
                }
                resList.add(dBValues);

                System.out.println(dBValues.toString());
            }
            while (c.moveToNext());
        }

        return resList;

    }

    private static List<String> getNamesFromDB(Cursor c) {
        List<String> resList = new ArrayList<>();
        if (c != null && (c.isFirst() || c.moveToFirst())) {
            do {
                String overlayName = null;

                int nameIdx = c.getColumnIndex(DbContract.Warehouses.NAME);
                if (!c.isNull(nameIdx)) {
                    overlayName = c.getString(nameIdx);
                }
                resList.add(overlayName);
            }
            while (c.moveToNext());
        }

        return resList;

    }

    // закрываем курсор
    public static void closeCursor(Cursor c) {
        if (c != null && !c.isClosed()) {
            c.close();
        }
    }
}
