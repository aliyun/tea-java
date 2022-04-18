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
        TeaRequest teaRequest =  new TeaRequest();
        teaRequest.method = "GET";
        teaRequest.protocol = "https";
        teaRequest.port = 8000;
        exception = new TeaUnretryableException(teaRequest);
        Assert.assertEquals("Protocol: https\n" +
                "Port: 8000\n" +
                "GET null\n" +
                "Query:\n" +
                "Headers:\n", exception.getLastRequest().toString());
        exception = new TeaUnretryableException(e);
        Assert.assertEquals("java.lang.RuntimeException: test", exception.getMessage());
        exception = new TeaUnretryableException();
        Assert.assertNull(exception.getLastRequest());
        Assert.assertNull(exception.getMessage());
    }
}
