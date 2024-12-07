package io.scriptor.model;

import io.scriptor.yaml.IYamlNode;

import java.util.ArrayList;
import java.util.List;

public class MenuResource extends Resource {

    public static MenuResource read(final String id, final IYamlNode yaml) {
        final var itemsNode = yaml.get("items");
        if (itemsNode.isEmpty()) return null;
        final List<Resource> items = new ArrayList<>();
        for (final var itemNode : itemsNode.get()) {
        }
        return new MenuResource()
    }

    public final Resource[] items;

    public MenuResource(final String id, final Resource[] items) {
        super(id, ResourceType.MENU);
        this.items = items;
    }
}
