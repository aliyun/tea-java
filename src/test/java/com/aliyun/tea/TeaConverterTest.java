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
    public void toMap() {
        MyObject myObject = new MyObject();
        myObject.key = "myObject.key";
        Map<String, Object> map = TeaConverter.toMap(myObject);
        Assert.assertEquals("myObject.key", map.get("key"));
    }

    @Test
    public void toGenericMap() {
        MyObject myObject = new MyObject();
        myObject.key = "myObject.key";
        MyGenericObject<MyObject> myGenericObject = new MyGenericObject<MyObject>();
        myGenericObject.data = myObject;
        Map<String, Object> map = TeaConverter.toMap(myGenericObject);
        Assert.assertEquals("myObject.key", ((Map<?, ?>) map.get("data")).get("key"));
    }

    @Test
    public void toNestGenericMap() {
        MyGenericObject<String> childGenericObject = new MyGenericObject<String>();
        childGenericObject.data = "childGenericObject.data";
        MyGenericObject<MyGenericObject<?>> myGenericObject = new MyGenericObject<MyGenericObject<?>>();
        myGenericObject.data = childGenericObject;
        Map<String, Object> map = TeaConverter.toMap(myGenericObject);
        Assert.assertEquals("childGenericObject.data", ((Map<?, ?>) map.get("data")).get("data"));
    }

    @Test
    public void toObject() throws Exception {
        HashMap<String, String> map = new HashMap<String, String>() {{
            put("key", "value");
        }};
        MyObject obj = TeaConverter.toObject(map, new TeaObject<MyObject>() {
        });
        Assert.assertEquals("value", obj.key);
    }

    @Test
    public void toGenericObject() throws Exception {
        HashMap<String, String> map = new HashMap<String, String>() {{
            put("data", "value");
        }};
        MyGenericObject<String> obj = TeaConverter.toObject(map, new TeaObject<MyGenericObject<String>>() {
        });
        Assert.assertEquals("value", obj.data);
    }

    @Test
    public void toNestGenericObject() throws Exception {
        MyGenericObject<String> childGenericObject = new MyGenericObject<String>();
        childGenericObject.data = "childGenericObject.data";
        MyGenericObject<MyGenericObject<?>> myGenericObject = new MyGenericObject<MyGenericObject<?>>();
        myGenericObject.data = childGenericObject;
        Map<String, Object> map = TeaConverter.toMap(myGenericObject);
        MyGenericObject<MyGenericObject<String>> obj = TeaConverter.toObject(map, new TeaObject<MyGenericObject<MyGenericObject<String>>>() {
        });
        Assert.assertEquals(MyGenericObject.class, obj.data.getClass());
        Assert.assertEquals("childGenericObject.data", obj.data.data);
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
    public void merge() throws Exception {
        Map<String, Object> map = TeaConverter.merge(
                new HashMap<String, Object>() {{
                    put("key", "value");
                }},
                new HashMap<String, Object>() {{
                    put("key2", "value2");
                }}
        );
        Assert.assertEquals(map.get("key"), "value");
        Assert.assertEquals(map.get("key2"), "value2");
        Assert.assertEquals(2, map.size());
    }
}