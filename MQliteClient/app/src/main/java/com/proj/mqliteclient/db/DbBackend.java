package com.proj.mqliteclient.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.support.annotation.VisibleForTesting;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by user on 28/04/2018.
 */

// главный класс для работы с БД
class DbBackend implements DbContract {

    private final DbOpenHelper mDbOpenHelper;

    DbBackend(Context context) {
        mDbOpenHelper = new DbOpenHelper(context);
    }

    @VisibleForTesting
    DbBackend(DbOpenHelper dbOpenHelper) {
        mDbOpenHelper = dbOpenHelper;
    }

    // очищаем таблицу и заполняем новыми данными из полученного json array
    public void refreshTablesWithJsonData(JSONObject response) {
        if (response == null)
            return;
        trunkateTable();

        try {
            JSONArray overlays = response.getJSONArray(JSON_OVERLAYS_ARRAY);
            insertJsonArrayIntoOverlayTable(overlays);

            JSONArray slots = response.getJSONArray(JSON_SLOTS_ARRAY);
            insertJsonArrayIntoSlotsTable(slots);

            JSONArray products = response.getJSONArray(JSON_PRODUCTS_ARRAY);
            insertJsonArrayIntoProductTable(products);

            JSONArray warehouses = response.getJSONArray(JSON_WAREHOUSES_ARRAY);
            insertJsonArrayIntoWarehouseTable(warehouses);

            JSONArray warehouseSlots = response.getJSONArray(JSON_WAREHOUSES_SLOT_ARRAY);
            insertJsonArrayIntoWarehouseSlotsTable(warehouseSlots);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // удаляем все данные
    private void trunkateTable() {
        SQLiteDatabase db = mDbOpenHelper.getWritableDatabase();
        db.delete(GR_OVERLAYS, null, null);
        db.delete(SLOTS, null, null);
        db.delete(PRODUCTS, null, null);
        db.delete(WAREHOUSE, null, null);
        db.delete(WAREHOUSE_SLOT, null, null);
    }

    // вставляем данные из json array в таблицу
    private void insertJsonArrayIntoOverlayTable(JSONArray overlays) {
        if (overlays == null || overlays.length() <= 0) {
            return;
        }

        SQLiteDatabase db = mDbOpenHelper.getWritableDatabase();

        try {
            db.beginTransaction();

            for (int i = 0; i < overlays.length(); i++) {
                JSONObject jsonRow = overlays.getJSONObject(i);

                int id = jsonRow.getInt(GroundOverlays.ID);
                int warehouseId = jsonRow.getInt(GroundOverlays.WAREHOUSE_ID);
                Double latLngBoundNEN = jsonRow.getDouble(GroundOverlays.LAT_LNG_BOUND_NEN);
                Double latLngBoundNEE = jsonRow.getDouble(GroundOverlays.LAT_LNG_BOUND_NEE);
                Double latLngBoundSWN = jsonRow.getDouble(GroundOverlays.LAT_LNG_BOUND_SWN);
                Double latLngBoundSWE = jsonRow.getDouble(GroundOverlays.LAT_LNG_BOUND_SWE);
                String overlayPic = jsonRow.getString(GroundOverlays.OVERLAY_PIC);

                ContentValues values = new ContentValues();

                values.put(GroundOverlays.ID, id);
                values.put(GroundOverlays.WAREHOUSE_ID, warehouseId);
                values.put(GroundOverlays.LAT_LNG_BOUND_NEN, latLngBoundNEN);
                values.put(GroundOverlays.LAT_LNG_BOUND_NEE, latLngBoundNEE);
                values.put(GroundOverlays.LAT_LNG_BOUND_SWN, latLngBoundSWN);
                values.put(GroundOverlays.LAT_LNG_BOUND_SWE, latLngBoundSWE);
                values.put(GroundOverlays.OVERLAY_PIC, overlayPic);

                db.insert(GR_OVERLAYS, null, values);
            }

            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.w("Error: ", e);
        } finally {
            db.endTransaction();
        }
        db.close();
    }

    private void insertJsonArrayIntoProductTable(JSONArray products) {
        if (products == null || products.length() <= 0) {
            return;
        }

        SQLiteDatabase db = mDbOpenHelper.getWritableDatabase();

        try {
            db.beginTransaction();

            for (int i = 0; i < products.length(); i++) {
                JSONObject jsonRow = products.getJSONObject(i);

                int id = jsonRow.getInt(Products.ID);
                String name = jsonRow.getString(Products.NAME);

                ContentValues values = new ContentValues();

                values.put(Products.ID, id);
                values.put(Products.NAME, name);

                db.insert(PRODUCTS, null, values);
            }

            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.w("Error: ", e);
        } finally {
            db.endTransaction();
        }
        db.close();
    }

    private void insertJsonArrayIntoWarehouseSlotsTable(JSONArray warehouseSlots) {
        if (warehouseSlots == null || warehouseSlots.length() <= 0) {
            return;
        }

        SQLiteDatabase db = mDbOpenHelper.getWritableDatabase();

        try {
            db.beginTransaction();

            for (int i = 0; i < warehouseSlots.length(); i++) {
                JSONObject jsonRow = warehouseSlots.getJSONObject(i);

                int id = jsonRow.getInt(WarehouseSlots.ID);
                int slotId = jsonRow.getInt(WarehouseSlots.ID_SLOT);
                int warehouseId = jsonRow.getInt(WarehouseSlots.ID_WAREHOUSE);

                ContentValues values = new ContentValues();

                values.put(WarehouseSlots.ID, id);
                values.put(WarehouseSlots.ID_SLOT, slotId);
                values.put(WarehouseSlots.ID_WAREHOUSE, warehouseId);

                db.insert(WAREHOUSE_SLOT, null, values);
            }

            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.w("Error: ", e);
        } finally {
            db.endTransaction();
        }
        db.close();
    }


    private void insertJsonArrayIntoWarehouseTable(JSONArray warehouses) {
        if (warehouses == null || warehouses.length() <= 0) {
            return;
        }

        SQLiteDatabase db = mDbOpenHelper.getWritableDatabase();

        try {
            db.beginTransaction();

            for (int i = 0; i < warehouses.length(); i++) {
                JSONObject jsonRow = warehouses.getJSONObject(i);

                int id = jsonRow.getInt(Warehouses.ID);
                String name = jsonRow.getString(Warehouses.NAME);

                ContentValues values = new ContentValues();

                values.put(Warehouses.ID, id);
                values.put(Warehouses.NAME, name);

                db.insert(WAREHOUSE, null, values);
            }

            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.w("Error: ", e);
        } finally {
            db.endTransaction();
        }
        db.close();
    }

    private void insertJsonArrayIntoSlotsTable(JSONArray slots) {
        if (slots == null || slots.length() <= 0) {
            return;
        }

        SQLiteDatabase db = mDbOpenHelper.getWritableDatabase();

        try {
            db.beginTransaction();

            for (int i = 0; i < slots.length(); i++) {
                JSONObject jsonRow = slots.getJSONObject(i);

                int id = jsonRow.getInt(Slots.ID);
                String name = jsonRow.getString(Slots.NAME);
                int prodId = jsonRow.getInt(Slots.PROD_ID);

                ContentValues values = new ContentValues();

                values.put(Slots.ID, id);
                values.put(Slots.NAME, name);
                values.put(Slots.PROD_ID, prodId);

                db.insert(SLOTS, null, values);
            }

            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.w("Error: ", e);
        } finally {
            db.endTransaction();
        }
        db.close();
    }

    // готовим запрос на выборку всез полей таблицы, (select * from test;)
    public Cursor getAllDataFromTable(String nameOfWarehouse) {
        SQLiteDatabase db = mDbOpenHelper.getWritableDatabase();
//        String table =  GR_OVERLAYS;

//        String where = nameOfOverlay == null
//                ? null : GroundOverlays.NAME + " = ?";
//        String[] whereArgs = nameOfOverlay == null
//                ? null : new String[] {nameOfOverlay};
//
//        Cursor c = db.query(table, null,
//                where, whereArgs, null, null, null);

        String sql = "SELECT * FROM " + GR_OVERLAYS + " INNER JOIN " + WAREHOUSE
                + " ON " + GR_OVERLAYS + "." + GroundOverlays.WAREHOUSE_ID + " = " + WAREHOUSE + "." + Warehouses.ID +
                " WHERE " + Warehouses.NAME + " = ?";

        Cursor c = db.rawQuery(sql, new String[] {nameOfWarehouse});

        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }

    public Cursor getNamesFromTable() {
        SQLiteDatabase db = mDbOpenHelper.getWritableDatabase();

        String sql = "SELECT * FROM " + GR_OVERLAYS + " INNER JOIN " + WAREHOUSE
                + " ON " + GR_OVERLAYS + "." + GroundOverlays.WAREHOUSE_ID + " = " + WAREHOUSE + "." + Warehouses.ID;

        Cursor c = db.rawQuery(sql, null);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }

    public Cursor getSlotsInfoFromTable() {
        SQLiteDatabase db = mDbOpenHelper.getWritableDatabase();

        String sql = "SELECT * FROM " + SLOTS + " INNER JOIN " + PRODUCTS
                + " ON " + SLOTS + "." + Slots.PROD_ID + " = " + PRODUCTS + "." + Products.ID;

        Cursor c = db.rawQuery(sql, null);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }

}
