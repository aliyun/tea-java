package com.aliyun.tea.utils;

import java.util.HashMap;
import java.util.Map;

public class AttributeMap implements AutoCloseable {

    private final Map<Key<?>, Object> attributes;

    private AttributeMap(Map<? extends Key<?>, ?> attributes) {
        this.attributes = new HashMap<>(attributes);
    }

    public <T> boolean containsKey(Key<T> typedKey) {
        return attributes.containsKey(typedKey);
    }

    public <T> T get(Key<T> key) {
        Validate.notNull(key, "Key to retrieve must not be null.");
        return key.convertValue(attributes.get(key));
    }

    public <T> AttributeMap put(Key<T> key, T value) {
        Validate.notNull(key, "Key to set must not be null.");
        attributes.put(key, value);
        return this;
    }

    public <T> AttributeMap putIfAbsent(Key<T> key, T value) {
        Validate.notNull(key, "Key to set must not be null.");
        if (attributes.get(key) == null) {
            attributes.put(key, value);
        }
        return this;
    }

    public AttributeMap copy() {
        Map<Key<?>, Object> map = new HashMap<>();
        for (Map.Entry<Key<?>, Object> entry : attributes.entrySet()) {
            entry.getKey().validateValue(entry.getValue());
            map.put(entry.getKey(), entry.getValue());
        }
        return new AttributeMap(map);
    }

    public int size() {
        return attributes.size();
    }

    public static AttributeMap empty() {
        return new AttributeMap(new HashMap<Key<?>, Object>());
    }

    @Override
    public void close() {
        for (Object value : attributes.values()) {
            IOUtils.closeIfCloseable(value);
        }
        attributes.clear();
    }

    public abstract static class Key<T> {

        private final Class<?> valueType;

        protected Key(Class<T> valueType) {
            this.valueType = valueType;
        }

        protected Key(UnsafeValueType unsafeValueType) {
            this.valueType = unsafeValueType.valueType;
        }

        protected static class UnsafeValueType {
            private final Class<?> valueType;

            public UnsafeValueType(Class<?> valueType) {
                this.valueType = valueType;
            }
        }

        final void validateValue(Object value) {
            if (value != null) {
                Validate.isAssignableFrom(valueType, value.getClass(),
                        "Invalid option: %s. Required value of type %s, but was %s.",
                        this, valueType, value.getClass());
            }
        }

        public final T convertValue(Object value) {
            validateValue(value);

            T result = (T) valueType.cast(value);
            return result;
        }
    }
}
