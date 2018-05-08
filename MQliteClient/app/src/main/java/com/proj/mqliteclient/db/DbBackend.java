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
    public void refreshTableWithJsonData(JSONArray response) {
        if (response == null)
            return;
        trunkateTable();
        insertJsonArrayIntoTable(response);
    }

    // удаляем все данные
    private void trunkateTable() {
        SQLiteDatabase db = mDbOpenHelper.getWritableDatabase();
        db.delete(GR_OVERLAYS, null, null);
    }

    // вставляем данные из json array в таблицу
    private void insertJsonArrayIntoTable(JSONArray response) {
        if (response == null || response.length() <= 0) {
            return;
        }

        SQLiteDatabase db = mDbOpenHelper.getWritableDatabase();

        try {
            db.beginTransaction();

            for (int i = 0; i < response.length(); i++) {
                JSONObject jsonRow = response.getJSONObject(i);

                String name = jsonRow.getString("name");
                Double latLngBoundNEN = jsonRow.getDouble("latLngBoundNEN");
                Double latLngBoundNEE = jsonRow.getDouble("latLngBoundNEE");
                Double latLngBoundSWN = jsonRow.getDouble("latLngBoundSWN");
                Double latLngBoundSWE = jsonRow.getDouble("latLngBoundSWE");
                String overlayPic = jsonRow.getString("overlayPic");

                ContentValues values = new ContentValues();

                values.put(GroundOverlays.NAME, name);
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

    // готовим запрос на выборку всез полей таблицы, (select * from test;)
    public Cursor getAllDataFromTable(String nameOfOverlay) {
        SQLiteDatabase db = mDbOpenHelper.getWritableDatabase();
        String table =  GR_OVERLAYS;

        String where = nameOfOverlay == null
                ? null : GroundOverlays.NAME + " LIKE ?";
        String[] whereArgs = nameOfOverlay == null
                ? null : new String[] {"%" + nameOfOverlay + "%"};

        Cursor c = db.query(table, null,
                where, whereArgs, null, null, null);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }

    public Cursor getNamesFromTable() {
        SQLiteDatabase db = mDbOpenHelper.getWritableDatabase();
        String table =  GR_OVERLAYS;
        String[] columns = new String[] {GR_OVERLAYS + "." + GroundOverlays.NAME};

        Cursor c = db.query(table, columns,
                null, null, null, null, null);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }

}
