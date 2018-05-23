package my.test.gui.jdbc.entities;

import java.util.ArrayList;
import java.util.List;

public class Product {
    private List<String> slots = new ArrayList<>();
    public List<String> getSlots() {
        return slots;
    }
    public void setSlots(List<String> slots) {
        this.slots = slots;
    }
    public void addSlotName(String slotName) {
        slots.add(slotName);
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
                slots,
                name
        };
    }

}
