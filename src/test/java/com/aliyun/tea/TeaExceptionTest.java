package com.aliyun.tea;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class TeaExceptionTest {
    private class TestClass {
        private String test;
        private int statusCode;
    }

    @Test
    public void toTeaExceptionTest() {
        Map<String, Object> map = new HashMap<>();
        map.put("code", "test");
        map.put("message", "test");
        TeaException exception = new TeaException(map);
        Assert.assertEquals("test", exception.getCode());
        Assert.assertEquals("test", exception.getMessage());
        Assert.assertNull(exception.getStatusCode());

        exception.setData(map);
        Assert.assertEquals(map, exception.getData());

        map.put("data", new HashMap<>());
        exception = new TeaException(map);
        Assert.assertEquals("test", exception.getCode());
        Assert.assertEquals("test", exception.getMessage());
        Assert.assertNotEquals(map, exception.getData());
        Assert.assertNull(exception.getStatusCode());
        Assert.assertNull(exception.getData().get("statusCode"));

        Map<String, Object> data = new HashMap<>();
        data.put("statusCode", 200);
        map.put("data", data);
        exception = new TeaException(map);
        Assert.assertEquals("test", exception.getCode());
        Assert.assertEquals("test", exception.getMessage());
        Assert.assertEquals((Integer) 200, exception.getStatusCode());
        Assert.assertEquals(200, exception.getData().get("statusCode"));

        data.put("statusCode", 200L);
        map.put("data", data);
        exception = new TeaException(map);
        Assert.assertEquals((Integer) 200, exception.getStatusCode());
        Assert.assertEquals(200L, exception.getData().get("statusCode"));

        TestClass testClass = new TestClass();
        testClass.test = "test";
        testClass.statusCode = 200;
        map.put("data", testClass);
        exception = new TeaException(map);
        Assert.assertEquals("test", exception.getCode());
        Assert.assertEquals("test", exception.getMessage());
        Assert.assertEquals("test", exception.getData().get("test"));
        Assert.assertEquals(200, exception.getData().get("statusCode"));
        Assert.assertNull(exception.getStatusCode());
    }


}
