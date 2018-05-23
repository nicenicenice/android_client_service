package my.test.gui.jdbc.entities;

// A presentation of overlay's table data
public class Overlay {

    private int id_warehouse;
    private String latLngBoundNEN;
    private String latLngBoundNEE;
    private String latLngBoundSWN;
    private String latLngBoundSWE;
    private String decodedOverlayPic;
    private String warehouseName;

    public int getIdWarehouse() {return id_warehouse;}
    public String getWarehouseName() {return warehouseName;}
    public String getLatLngBoundNEN() {return latLngBoundNEN;}
    public String getLatLngBoundNEE() {return latLngBoundNEE;}
    public String getLatLngBoundSWN() {return latLngBoundSWN;}
    public String getLatLngBoundSWE() {return latLngBoundSWE;}
    public String getDecodedOverlayPic() {return decodedOverlayPic;}

    public Object[] getFields() {
        return new Object[] {
            id_warehouse,
            warehouseName,
            latLngBoundNEN,
            latLngBoundNEE,
            latLngBoundSWN,
            latLngBoundSWE,
            decodedOverlayPic
        };
    }
    public void setIdWarehouse(int id_warehouse) { this.id_warehouse = id_warehouse; }
    public void setWarehouseName(String warehouseName) { this.warehouseName = warehouseName; }
    public void setLatLngBoundNEN(String latLngBoundNEN) { this.latLngBoundNEN = latLngBoundNEN; }
    public void setLatLngBoundNEE(String latLngBoundNEE) { this.latLngBoundNEE = latLngBoundNEE; }
    public void setLatLngBoundSWN(String latLngBoundSWN) { this.latLngBoundSWN = latLngBoundSWN; }
    public void setLatLngBoundSWE(String latLngBoundSWE) { this.latLngBoundSWE = latLngBoundSWE; }
    public void setDecodedOverlayPic(String decodedOverlayPic) { this.decodedOverlayPic = decodedOverlayPic; }
}
