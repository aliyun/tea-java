package com.aliyun.tea;

import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class TeaResponseTest {

    @Test
    public void getResponseTest() throws Exception{
        TeaResponse response = new TeaResponse();
        response.body = new ByteArrayInputStream("test".getBytes("UTF-8"));
        InputStream inputStream = response.getResponse();
        Assert.assertTrue(inputStream instanceof  ByteArrayInputStream );
    }

    @Test
    public void getResponseBodyTest() throws Exception{
        TeaResponse response = new TeaResponse();
        response.statusMessage = "test";
        Assert.assertEquals("{\"message\":\"test\"}", response.getResponseBody());

        response.body = new ByteArrayInputStream("test".getBytes("UTF-8"));
        Assert.assertEquals("test", response.getResponseBody());
    }
}
