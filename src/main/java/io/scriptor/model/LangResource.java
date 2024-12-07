package io.scriptor.model;

import io.scriptor.yaml.IYamlNode;

public class LangResource extends Resource {

    public static LangResource read(final String id, final IYamlNode yaml) {
    }

    public LangResource(final String id) {
        super(id, ResourceType.LANG);
    }
}
