package my.test.gui.jdbc.entities;

import java.util.ArrayList;
import java.util.List;

public class Slot {
    private List<String> products = new ArrayList<>();
    public List<String> getProducts() {
        return products;
    }
    public void setProducts(List<String> products) {
        this.products = products;
    }
    public void addProductName(String prodName) {
        products.add(prodName);
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
                products,
                name
        };
    }

}
