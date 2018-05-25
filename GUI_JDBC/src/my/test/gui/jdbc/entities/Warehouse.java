package my.test.gui.jdbc.entities;

public class Warehouse {
    public Warehouse() {}
    public Warehouse (int id, String name) {
        this.id = id;
        this.name = name;
    }

    private int id;
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    private String name = null;
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public Object[] getFields() {
        return new Object[] {
                id,
                name
        };
    }

    public String toString() {
        return this.name;
    }
}
