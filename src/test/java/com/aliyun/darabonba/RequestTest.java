package com.aliyun.darabonba;

import org.junit.Assert;
import org.junit.Test;

public class RequestTest {
    @Test
    public void toStringTest() {
        Request request = new Request();
        request.query.put("query", "test");
        request.headers.put("host", "www.baidu.com");
        Assert.assertEquals("Protocol: http\n" +
                "Port: null\n" +
                "GET null\n" +
                "Query:\n" +
                "    query: test\n" +
                "Headers:\n" +
                "    host: www.baidu.com\n", request.toString());
    }
}
