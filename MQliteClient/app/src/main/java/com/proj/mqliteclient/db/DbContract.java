package com.proj.mqliteclient.db;

/**
 * Created by user on 28/04/2018.
 */

// просто набор констант, которые мы хотим использовать. (таблицы, поля и тп)
public interface DbContract {
    String DB_NAME = "main13.sqlite";

    String GR_OVERLAYS = "overlay";
    interface GroundOverlays {
        String ID = "id";
        String NAME = "name";
        String WAREHOUSE_ID = "id_warehouse";
        String LAT_LNG_BOUND_NEN = "latLngBoundNEN";
        String LAT_LNG_BOUND_NEE = "latLngBoundNEE";
        String LAT_LNG_BOUND_SWN = "latLngBoundSWN";
        String LAT_LNG_BOUND_SWE = "latLngBoundSWE";
        String OVERLAY_PIC = "overlayPic";
    }

    String SLOTS = "slot";
    interface Slots {
        String ID = "id_slot";
        String NAME = "slot_name";
        String PROD_ID = "id_product";
    }

    String PRODUCTS = "product";
    interface Products {
        String ID = "id_product";
        String NAME = "product_name";
    }

    String WAREHOUSE = "warehouse";
    interface Warehouses {
        String ID = "id_warehouse";
        String NAME = "warehouse_name";
    }

    String WAREHOUSE_SLOT = "warehouse_slot";
    interface WarehouseSlots {
        String ID = "id";
        String ID_WAREHOUSE = "id_warehouse";
        String ID_SLOT = "id_slot";
    }

    // имена json массивов
    String JSON_SLOTS_ARRAY = "slots";
    String JSON_OVERLAYS_ARRAY = "overlays";
    String JSON_PRODUCTS_ARRAY = "products";
    String JSON_WAREHOUSES_ARRAY = "warehouses";
    String JSON_WAREHOUSES_SLOT_ARRAY = "warehouses_slots";
}
