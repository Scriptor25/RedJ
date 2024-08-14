package io.scriptor.resource;

public class RedJMenuItem {

    public String label;
    public String shortcut;
    public String action;

    @Override
    public String toString() {
        return "RedJMenuItem { label=%s, shortcut=%s, action=%s }".formatted(label, shortcut, action);
    }
}
