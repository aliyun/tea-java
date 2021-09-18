package com.aliyun.tea.utils;

import org.junit.Assert;
import org.junit.Test;

public class AttributeMapTest {

    static class Option<T> extends AttributeMap.Key<T>{
        public static final Option<String> A = new Option<>(String.class);
        public static final Option<String> B = new Option<>(String.class);

        protected Option(Class<T> valueType) {
            super(valueType);
        }

        protected Option(UnsafeValueType unsafeValueType) {
            super(unsafeValueType);
        }
    }

    @Test
    public void copyTest() {
        AttributeMap attribute = AttributeMap.empty();
        attribute.put(Option.A, "test1");
        attribute.put(Option.B, "test2");
        AttributeMap copyMap = attribute.copy();
        Assert.assertNotNull(copyMap);
        Assert.assertEquals("test1", copyMap.get(Option.A));
        Assert.assertEquals("test2", copyMap.get(Option.B));
    }
}
