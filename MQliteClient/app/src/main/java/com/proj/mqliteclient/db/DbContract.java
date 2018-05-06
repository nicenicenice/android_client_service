package com.proj.mqliteclient.db;

/**
 * Created by user on 28/04/2018.
 */

// просто набор констант, которые мы хотим использовать. (таблицы, поля и тп)
public interface DbContract {
    String DB_NAME = "main.sqlite";
    String GR_OVERLAYS = "gr_overlays";
    interface GroundOverlays {
        String ID = "rowid";
        String NAME = "name";
        String LAT_LNG_BOUND_NEN = "latLngBoundNEN";
        String LAT_LNG_BOUND_NEE = "latLngBoundNEE";
        String LAT_LNG_BOUND_SWN = "latLngBoundSWN";
        String LAT_LNG_BOUND_SWE = "latLngBoundSWE";
        String OVERLAY_PIC = "overlay_pic";
    }
}
