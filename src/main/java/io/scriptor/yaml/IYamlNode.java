package io.scriptor.yaml;

import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.*;

public interface IYamlNode extends Iterable<IYamlNode> {

    class YamlList implements IYamlNode {

        private final List<IYamlNode> nodes;

        public YamlList() {
            this(new ArrayList<>());
        }

        public YamlList(final List<IYamlNode> nodes) {
            this.nodes = nodes;
        }

        @Override
        public Optional<IYamlNode> get(final int index) {
            if (index < 0 || index >= nodes.size())
                return Optional.empty();
            return Optional.of(nodes.get(index));
        }

        @Override
        public void add(final IYamlNode node) {
            nodes.add(node);
        }

        @Override
        public void set(final int index, final IYamlNode node) {
            if (index < 0 || index >= nodes.size())
                return;
            nodes.set(index, node);
        }

        @Override
        public Iterator<IYamlNode> iterator() {
            return nodes.iterator();
        }
    }

    class YamlMap implements IYamlNode {

        private final Map<String, IYamlNode> map;

        public YamlMap() {
            this(new HashMap<>());
        }

        public YamlMap(final Map<String, IYamlNode> map) {
            this.map = map;
        }

        @Override
        public Optional<IYamlNode> get(final String key) {
            if (!map.containsKey(key))
                return Optional.empty();
            return Optional.of(map.get(key));
        }

        @Override
        public void put(final String key, final IYamlNode node) {
            map.put(key, node);
        }

        @Override
        public Iterator<IYamlNode> iterator() {
            return map.values().iterator();
        }
    }

    class YamlData implements IYamlNode {

        private final Object data;

        public YamlData(final Object data) {
            this.data = data;
        }

        @Override
        public <T> Optional<T> as(final Class<T> clazz) {
            if (!clazz.isInstance(data))
                return Optional.empty();
            return Optional.of(clazz.cast(data));
        }

        @Override
        public Iterator<IYamlNode> iterator() {
            return Collections.emptyIterator();
        }
    }

    static IYamlNode load(final InputStream stream) {
        final var yaml = new Yaml().loadAs(stream, Map.class);
        return load(yaml);
    }

    static IYamlNode load(final Object yaml) {
        if (yaml instanceof List<?> list)
            return load(list);

        if (yaml instanceof Map<?, ?> map)
            return load(map);

        return new YamlData(yaml);
    }

    static YamlList load(final List<?> yaml) {
        final var node = new YamlList();

        for (final var entry : yaml) {
            node.add(load(entry));
        }

        return node;
    }

    static YamlMap load(final Map<?, ?> yaml) {
        final var node = new YamlMap();

        for (final var entry : yaml.entrySet()) {
            node.put((String) entry.getKey(), load(entry.getValue()));
        }

        return node;
    }

    default <T> Optional<T> as(final Class<T> clazz) {
        return Optional.empty();
    }

    default Optional<IYamlNode> get(final int index) {
        return Optional.empty();
    }

    default Optional<IYamlNode> get(final String key) {
        return Optional.empty();
    }

    default void add(final IYamlNode node) {
    }

    default void set(final int index, final IYamlNode node) {
    }

    default void put(final String key, final IYamlNode node) {
    }
}
