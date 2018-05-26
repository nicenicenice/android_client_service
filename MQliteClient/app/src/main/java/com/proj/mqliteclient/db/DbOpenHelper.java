package com.proj.mqliteclient.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by user on 28/04/2018
 */

// класс отвечает за создание и обновление БД
class DbOpenHelper extends SQLiteOpenHelper implements DbContract {

    private static final int DB_VERSION = 7;

    public DbOpenHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    // создание
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + GR_OVERLAYS + "(" +
                GroundOverlays.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                GroundOverlays.WAREHOUSE_ID + " INTEGER, " +
                GroundOverlays.NAME + " TEXT, " +
                GroundOverlays.LAT_LNG_BOUND_NEN + " REAL, " +
                GroundOverlays.LAT_LNG_BOUND_NEE + " REAL, " +
                GroundOverlays.LAT_LNG_BOUND_SWN + " REAL, " +
                GroundOverlays.LAT_LNG_BOUND_SWE + " REAL, " +
                GroundOverlays.OVERLAY_PIC + " BLOB," +
                "FOREIGN KEY(" + GroundOverlays.WAREHOUSE_ID + ") REFERENCES " + WAREHOUSE + "(" + Warehouses.ID + ")" +
                ")"
        );

        db.execSQL("CREATE TABLE " + PRODUCTS + "(" +
                Products.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                Products.NAME + " TEXT" +
                ")"
        );

        db.execSQL("CREATE TABLE " + SLOTS + "(" +
                Slots.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                Slots.NAME + " TEXT, " +
                Slots.PROD_ID + " INTEGER, " +
                "FOREIGN KEY(" + Slots.PROD_ID + ") REFERENCES " + PRODUCTS + "(" + Products.ID + ")" +
                ")"
        );


        db.execSQL("CREATE TABLE " + WAREHOUSE + "(" +
                Warehouses.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                Warehouses.NAME + " TEXT"
                + ")"
        );

        db.execSQL("CREATE TABLE " + WAREHOUSE_SLOT + "(" +
                WarehouseSlots.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                WarehouseSlots.ID_WAREHOUSE + " INTEGER, " +
                WarehouseSlots.ID_SLOT + " INTEGER, " +
                "FOREIGN KEY(" + WarehouseSlots.ID_WAREHOUSE + ") REFERENCES " + WAREHOUSE + "(" + Warehouses.ID + "), " +
                "FOREIGN KEY(" + WarehouseSlots.ID_SLOT + ") REFERENCES " + SLOTS + "(" + Slots.ID + ")"
                + ")"
        );
    }

    // обновление
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + GR_OVERLAYS);
        db.execSQL("DROP TABLE IF EXISTS " + PRODUCTS);
        db.execSQL("DROP TABLE IF EXISTS " + SLOTS);
        db.execSQL("DROP TABLE IF EXISTS " + WAREHOUSE);
        db.execSQL("DROP TABLE IF EXISTS " + WAREHOUSE_SLOT);
        onCreate(db);
    }
}
