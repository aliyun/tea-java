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

        map.put("description", "error description");
        Map<String, Object> detail = new HashMap<>();
        detail.put("AuthAction", "ram:ListUsers");
        detail.put("AuthPrincipalType", "SubUser");
        detail.put("PolicyType", "ResourceGroupLevelIdentityBassdPolicy");
        detail.put("NoPermissionType", "ImplicitDeny");
        map.put("accessDeniedDetail", detail);
        exception = new TeaException(map);
        Assert.assertEquals("error description", exception.getDescription());
        Assert.assertEquals("ImplicitDeny", exception.getAccessDeniedDetail().get("NoPermissionType"));

        map.put("accessDeniedDetail", "wrong type");
        exception = new TeaException(map);
        Assert.assertNull(exception.getAccessDeniedDetail());

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

    @Test
    public void baseTest() {
        TeaException exception = new TeaException("test massage", new RuntimeException("runtime exception"));
        Assert.assertNull(exception.getCode());
        Assert.assertNull(exception.getData());
        Assert.assertNull(exception.getStatusCode());
        Assert.assertEquals("test massage", exception.getMessage());
        Assert.assertEquals("test massage", exception.message);
        Assert.assertEquals(exception.getLocalizedMessage(), exception.getMessage());
        Assert.assertEquals("runtime exception", exception.getCause().getMessage());
    }


}
