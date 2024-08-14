package io.scriptor.resource;

import java.util.Arrays;

public class RedJMenuBar {

    public String id;
    public boolean isMain;
    public String[] menus;

    @Override
    public String toString() {
        return "RedJMenuBar { id=%s, isMain=%b, menus=%s }".formatted(id, isMain, Arrays.toString(menus));
    }
}
