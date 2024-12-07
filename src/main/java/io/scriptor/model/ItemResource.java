package io.scriptor.model;

import io.scriptor.yaml.IYamlNode;

public class ItemResource extends Resource {

    public static ItemResource read(final String id, final IYamlNode yaml) {
    }

    public ItemResource(final String id) {
        super(id, ResourceType.ITEM);
    }
}
