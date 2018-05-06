package com.proj.mqliteclient.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by user on 28/04/2018
 */

// класс отвечает за создание и обновление БД
class DbOpenHelper extends SQLiteOpenHelper implements DbContract {

    private static final int DB_VERSION = 2;

    public DbOpenHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    // создание
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + GR_OVERLAYS + "(" +
                GroundOverlays.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                GroundOverlays.NAME + " TEXT, " +
                GroundOverlays.LAT_LNG_BOUND_NEN + " REAL, " +
                GroundOverlays.LAT_LNG_BOUND_NEE + " REAL, " +
                GroundOverlays.LAT_LNG_BOUND_SWN + " REAL, " +
                GroundOverlays.LAT_LNG_BOUND_SWE + " REAL, " +
                GroundOverlays.OVERLAY_PIC + " BLOB" +
                ")"
        );

    }

    // обновление
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE " + GR_OVERLAYS);
        onCreate(db);
    }
}
