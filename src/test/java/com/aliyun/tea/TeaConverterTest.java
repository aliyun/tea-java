package com.aliyun.tea;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;


public class TeaConverterTest {

    public static class MyObject {
        public String key;
    }

    public static class MyGenericObject<T> {
        public T data;
    }

    @Test
    public void newInstance() {
        new TeaConverter();
    }


    @Test
    public void buildMap() throws Exception {
        Map<String, Object> map = TeaConverter.buildMap(
                new TeaPair("key", "value"),
                new TeaPair("key2", "value2")
        );
        Assert.assertEquals(map.get("key"), "value");
        Assert.assertEquals(map.get("key2"), "value2");
        Assert.assertEquals(2, map.size());
    }

    @Test
    public void buildMapWithT() throws Exception {
        Map<String, String> map = TeaConverter.buildMap(
                new TeaPair("key", "value"),
                new TeaPair("key2", "value2")
        );
        Assert.assertEquals(map.get("key"), "value");
        Assert.assertEquals(map.get("key2"), "value2");
        Assert.assertEquals(2, map.size());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void merge() throws Exception {
        HashMap<String, Object> map1 = new HashMap<String, Object>();
        map1.put("key", "value");
        HashMap<String, Object> map2 = new HashMap<String, Object>();
        map1.put("key2", "value2");
        Map<String, Object> map = TeaConverter.merge(map1, map2);
        Assert.assertEquals(map.get("key"), "value");
        Assert.assertEquals(map.get("key2"), "value2");
        Assert.assertEquals(2, map.size());
    }
}