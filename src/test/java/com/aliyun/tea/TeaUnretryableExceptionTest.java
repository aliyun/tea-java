package com.aliyun.tea;

import org.junit.Assert;
import org.junit.Test;

public class TeaUnretryableExceptionTest {
    @Test
    public void TestUnretryableException() {
        RuntimeException e = new RuntimeException("test");
        TeaUnretryableException exception = new TeaUnretryableException(TeaRequest.create(), e);
        Assert.assertEquals("test", exception.getMessage());
        Assert.assertEquals("Protocol: http\n" +
                "Port: null\n" +
                "GET null\n" +
                "Query:\n" +
                "Headers:\n", exception.getLastRequest().toString());
    }
}
