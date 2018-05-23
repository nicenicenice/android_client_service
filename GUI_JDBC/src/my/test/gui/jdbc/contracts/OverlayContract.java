package my.test.gui.jdbc.contracts;

public interface OverlayContract {
    String GR_OVERLAYS = "overlay";
    interface GroundOverlays {
        String ID = "id";
        String ID_WAREHOUSE = "id_warehouse";
        String LAT_LNG_BOUND_NEN = "latLngBoundNEN";
        String LAT_LNG_BOUND_NEE = "latLngBoundNEE";
        String LAT_LNG_BOUND_SWN = "latLngBoundSWN";
        String LAT_LNG_BOUND_SWE = "latLngBoundSWE";
        String OVERLAY_PIC = "overlayPic";
    }
}