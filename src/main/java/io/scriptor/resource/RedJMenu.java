package io.scriptor.resource;

import java.util.Arrays;

public class RedJMenu {

    public String id;
    public String label;
    public RedJMenuItem[] items;

    @Override
    public String toString() {
        return "RedJMenu { id=%s, label=%s, items=%s }".formatted(id, label, Arrays.toString(items));
    }
}
