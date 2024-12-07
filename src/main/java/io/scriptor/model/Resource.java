package io.scriptor.model;

import io.scriptor.yaml.IYamlNode;

import java.io.File;
import java.io.IOException;

public abstract class Resource {

    public static Resource read(final String name) throws IOException {
        final var filename = new File(name).getName();
        final var id = filename.substring(0, filename.lastIndexOf('.'));
        try (final var stream = ClassLoader.getSystemResourceAsStream(name)) {
            final var yaml = IYamlNode.load(stream);

            final var typeNode = yaml.get("type");
            if (typeNode.isEmpty()) return null;

            final var type = typeNode.get().as(String.class).map(x -> ResourceType.valueOf(x.toUpperCase()));
            return type.map(x -> switch (x) {
                case MENU -> MenuResource.read(id, yaml);
                case ITEM -> ItemResource.read(id, yaml);
                case LANG -> LangResource.read(id, yaml);
            }).orElse(null);

        }
    }

    public enum ResourceType {
        MENU,
        ITEM,
        LANG,
    }

    public final String id;
    public final ResourceType type;

    protected Resource(final String id, final ResourceType type) {
        this.id = id;
        this.type = type;
    }
}
