package io.scriptor.model;

import org.yaml.snakeyaml.Yaml;

import java.io.IOException;

public class ResourceInfo {

    public static ResourceInfo read(final String name) throws IOException {
        try (final var stream = ClassLoader.getSystemResourceAsStream(name)) {
            return new Yaml().loadAs(stream, ResourceInfo.class);
        }
    }

    private final String[] resources;

    public ResourceInfo(final String[] resources) {
        this.resources = resources;
    }

    public Resource[] readAll() throws IOException {
        final var result = new Resource[resources.length];
        for (int i = 0; i < resources.length; ++i) {
            result[i] = Resource.read(resources[i]);
        }
        return result;
    }
}
