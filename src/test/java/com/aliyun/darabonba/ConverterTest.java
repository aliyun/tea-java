package com.aliyun.darabonba;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;


public class ConverterTest {

    public static class MyObject {
        public String key;
    }

    public static class MyGenericObject<T> {
        public T data;
    }

    @Test
    public void newInstance() {
        Assert.assertNotNull(new Converter());
    }


    @Test
    public void buildMap() {
        Map<String, Object> map = Converter.buildMap(
                new Pair("key", "value"),
                new Pair("key2", "value2")
        );
        Assert.assertEquals(map.get("key"), "value");
        Assert.assertEquals(map.get("key2"), "value2");
        Assert.assertEquals(2, map.size());
    }

    @Test
    public void buildMapWithT() {
        Map<String, String> map = Converter.buildMap(
                new Pair("key", "value"),
                new Pair("key2", "value2")
        );
        Assert.assertEquals(map.get("key"), "value");
        Assert.assertEquals(map.get("key2"), "value2");
        Assert.assertEquals(2, map.size());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void merge() {
        HashMap<String, Object> map1 = new HashMap<String, Object>();
        map1.put("key", "value");
        HashMap<String, Object> map2 = new HashMap<String, Object>();
        map1.put("key2", "value2");
        map1.put("key3", null);
        HashMap<String, Object> map3 = null;
        Map<String, Object> map = Converter.merge(Object.class, map1, map2, map3);
        Assert.assertEquals(map.get("key"), "value");
        Assert.assertEquals(map.get("key2"), "value2");
        Assert.assertEquals(2, map.size());

        Model teaModel = new Model();
        map = Converter.merge(Object.class, map1, map2, map3, teaModel);
        Assert.assertEquals(map.get("key"), "value");
        Assert.assertEquals(map.get("key2"), "value2");
        Assert.assertEquals(2, map.size());
    }
}