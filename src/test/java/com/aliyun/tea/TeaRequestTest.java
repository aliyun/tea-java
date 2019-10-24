package com.aliyun.tea;

import org.junit.Assert;
import org.junit.Test;

public class TeaRequestTest {
    @Test
    public void toStringTest() {
        TeaRequest request = new TeaRequest();
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
