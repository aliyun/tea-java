package com.aliyun.tea.utils;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class AttributeMapTest {

    static class Option<T> extends AttributeMap.Key<T> {
        public static final Option<String> A = new Option<>(String.class);
        public static final Option<String> B = new Option<>(String.class);
        public static final Option<Map<String, String>> C = new Option<>(new UnsafeValueType(HashMap.class));

        protected Option(Class<T> valueType) {
            super(valueType);
        }

        protected Option(UnsafeValueType unsafeValueType) {
            super(unsafeValueType);
        }
    }

    @Test
    public void containsKeyTest() {
        AttributeMap attribute = AttributeMap.empty();
        attribute.put(Option.A, "test1");
        Assert.assertEquals(true, attribute.containsKey(Option.A));
    }

    @Test
    public void putIfAbsentTest() {
        AttributeMap attribute = AttributeMap.empty();
        attribute.putIfAbsent(Option.A, "test1");
        attribute.putIfAbsent(Option.A, "test2");
        Assert.assertEquals("test1", attribute.get(Option.A));
    }

    @Test
    public void copyTest() {
        AttributeMap attribute = AttributeMap.empty();
        attribute.put(Option.A, "test1");
        attribute.put(Option.B, "test2");
        Map<String, String> map = new HashMap<>();
        map.put("test", "test3");
        attribute.put(Option.C, map);
        AttributeMap copyMap = attribute.copy();
        Assert.assertNotNull(copyMap);
        Assert.assertEquals("test1", copyMap.get(Option.A));
        Assert.assertEquals("test2", copyMap.get(Option.B));
        Assert.assertEquals("test3", copyMap.get(Option.C).get("test"));
        Assert.assertEquals(3, copyMap.size());
    }

    @Test
    public void closeTest() {
        AttributeMap attribute = AttributeMap.empty();
        attribute.put(Option.A, "test1");
        attribute.put(Option.B, "test2");
        attribute.close();
        Assert.assertEquals(0, attribute.size());
    }
}
