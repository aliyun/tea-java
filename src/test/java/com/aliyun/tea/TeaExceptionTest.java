package com.aliyun.tea;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class TeaExceptionTest {
    private class TestClass {
        private String test;
    }

    @Test
    public void toTeaExceptionTest() {
        Map<String, Object> map = new HashMap<>();
        map.put("code", "test");
        map.put("message", "test");
        TeaException exception = new TeaException(map);
        Assert.assertEquals("test", exception.getCode());
        Assert.assertEquals("test", exception.getMessage());

        exception.setData(map);
        Assert.assertEquals(map, exception.getData());

        map.put("data", new HashMap<>());
        exception = new TeaException(map);
        Assert.assertEquals("test", exception.getCode());
        Assert.assertEquals("test", exception.getMessage());
        Assert.assertNotEquals(map, exception.getData());

        TestClass testClass = new TestClass();
        testClass.test = "test";
        map.put("data", testClass);
        exception = new TeaException(map);
        Assert.assertEquals("test", exception.getCode());
        Assert.assertEquals("test", exception.getMessage());
        Assert.assertEquals("test", exception.getData().get("test"));
    }


}
