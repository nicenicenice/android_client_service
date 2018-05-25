package my.test.gui.jdbc.entities;

public class Slot {
    public Slot() {}

    public Slot(int id, String name, int prodId, int warehouseId) {
        this(id, name, prodId);
        this.warehouseId = warehouseId;
    }

    public Slot(int id, String name, int prodId) {
        this.id = id;
        this.name = name;
        this.prodId = prodId;
    }

    private int id;
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    private int prodId;
    public int getProdId() {
        return prodId;
    }
    public void setProdId(int prod_id) {
        this.prodId = prod_id;
    }

    private int warehouseId;
    public int getWarehouseId() {
        return warehouseId;
    }
    public void setWarehouseId(int warehouseId) {
        this.warehouseId = warehouseId;
    }

    private String name = null;
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    private String warehouseName = null;
    public String getWarehouseName() {
        return warehouseName;
    }
    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }

    private String prodName = null;
    public String getProdName() {
        return prodName;
    }
    public void setProdName(String prodName) {
        this.prodName = prodName;
    }

    public Object[] getFields() {
        return new Object[] {
            id,
            name,
            prodId,
            prodName,
            warehouseId,
            warehouseName
        };
    }

    public String toString() {
        return this.name;
    }
}
