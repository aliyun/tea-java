package com.aliyun.darabonba;

import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class ResponseTest {

    @Test
    public void getResponseTest() throws Exception{
        Response response = new Response();
        response.body = new ByteArrayInputStream("test".getBytes("UTF-8"));
        InputStream inputStream = response.getResponse();
        Assert.assertTrue(inputStream instanceof  ByteArrayInputStream );
    }

    @Test
    public void getResponseBodyTest() throws Exception{
        Response response = new Response();
        response.statusMessage = "test";
        Assert.assertEquals("{\"message\":\"test\"}", response.getResponseBody());

        response.body = new ByteArrayInputStream("test".getBytes("UTF-8"));
        Assert.assertEquals("test", response.getResponseBody());
    }
}
