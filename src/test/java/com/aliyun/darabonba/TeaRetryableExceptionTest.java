package com.aliyun.darabonba;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class TeaRetryableExceptionTest {
    @Test
    public void teaRetryableException() {
        TeaRetryableException exception = new TeaRetryableException();
        Assert.assertNull(exception.getMessage());

        RuntimeException runtimeException = new RuntimeException("Throw TeaException");
        exception = new TeaRetryableException(runtimeException);
        Assert.assertEquals("Throw TeaException", exception.getMessage());

        Map<String, Object> map = new HashMap<>();
        map.put("code", "test");
        map.put("message", "test");

        exception = new TeaRetryableException(map);
        Assert.assertEquals("test", exception.getMessage());
    }
}
