package com.proj.mqliteclient.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by user on 28/04/2018
 */

// класс отвечает за создание и обновление БД
class DbOpenHelper extends SQLiteOpenHelper implements DbContract {

    private static final int DB_VERSION = 1;

    public DbOpenHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    // создание
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TEST + "(" +
                Test.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                Test.NUM1 + " REAL, " +
                Test.NUM2 + " REAL, " +
                Test.NUM3 + " REAL, " +
                Test.NUM4 + " REAL, " +
                Test.PICTURE + " BLOB" +
                ")"
        );

    }

    // обновление
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE " + TEST);
        onCreate(db);
    }
}
