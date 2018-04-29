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
    public void refreshTestTableWithJsonData(JSONArray response) {
        trunkateTestTable();
        insertJsonArrayIntoTable(response);
    }

    // удаляем все данные
    private void trunkateTestTable() {
        SQLiteDatabase db = mDbOpenHelper.getWritableDatabase();
        db.delete(TEST, null, null);
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

                Double num1 = jsonRow.getDouble("num1");
                Double num2 = jsonRow.getDouble("num2");
                Double num3 = jsonRow.getDouble("num3");
                Double num4 = jsonRow.getDouble("num4");
                String picture = jsonRow.getString("picture");

                ContentValues values = new ContentValues();

                values.put(Test.NUM1, num1);
                values.put(Test.NUM2, num2);
                values.put(Test.NUM3, num3);
                values.put(Test.NUM4, num4);
                values.put(Test.PICTURE, picture);

                db.insert(TEST, null, values);
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
    public Cursor getAllDataFromTestTable() {
        SQLiteDatabase db = mDbOpenHelper.getWritableDatabase();
        String table =  TEST;

        Cursor c = db.query(table, null,
                null, null, null, null, null);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }

}
