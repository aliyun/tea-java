package com.aliyun.tea;

import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TeaResponseTest {
    @Test
    public void teaResponseTest() throws IOException {
        TeaResponse response = new TeaResponse();
        Assert.assertEquals(0, response.headers.size());
        Assert.assertEquals(0, response.statusCode);
        Assert.assertNull(response.statusMessage);

        HttpURLConnection conn = mock(HttpURLConnection.class);
        when(conn.getResponseCode()).thenReturn(200);
        when(conn.getResponseMessage()).thenReturn("test");
        InputStream in = new BufferedInputStream(null);
        when(conn.getInputStream()).thenReturn(in);
        Map<String, List<String>> headers = new HashMap<>();
        List<String> list = new ArrayList<>();
        list.add("test-1");
        list.add("test-2");
        headers.put(null, null);
        headers.put("test", list);
        when(conn.getHeaderFields()).thenReturn(headers);
        response = new TeaResponse(conn);
        Assert.assertTrue(response.headers.size() > 0);
        Assert.assertEquals(200, response.statusCode);
        Assert.assertEquals("test", response.statusMessage);
        Assert.assertEquals(in, response.getResponse());
    }

    @Test
    public void getResponseBodyTest() throws Exception {
        HttpURLConnection conn = mock(HttpURLConnection.class);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream("test".getBytes("UTF-8"));
        when(conn.getInputStream()).thenThrow(new IOException());
        when(conn.getErrorStream()).thenReturn(byteArrayInputStream);
        when(conn.getResponseCode()).thenReturn(200);
        when(conn.getResponseMessage()).thenReturn("test");
        TeaResponse teaResponse = new TeaResponse(conn);
        String body = teaResponse.getResponseBody();
        Assert.assertEquals("test", body);

        teaResponse.body = null;
        body = teaResponse.getResponseBody();
        Assert.assertEquals("{\"message\":\"test\"}", body);
    }
}
