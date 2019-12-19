package com.aliyun.tea;

import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
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

        when(conn.getInputStream()).thenThrow(new IOException());
        when(conn.getErrorStream()).thenReturn(in);
        response = new TeaResponse(conn);
        Assert.assertEquals("test", response.statusMessage);
        Assert.assertEquals(in, response.getResponse());
    }

    @Test
    public void getResponseBodyTest() throws Exception {
        TeaResponse response = new TeaResponse();
        Field httpURLConnection = response.getClass().getDeclaredField("conn");
        Method getResponseBody = response.getClass().getDeclaredMethod("getResponseBody");
        httpURLConnection.setAccessible(true);
        getResponseBody.setAccessible(true);
        URL url = new URL("http://www.aliyun.com");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.connect();
        httpURLConnection.set(response, conn);
        String content = (String) getResponseBody.invoke(response);
        Assert.assertNotNull(content);

        response.statusMessage = "test";
        conn = mock(HttpURLConnection.class);
        when(conn.getInputStream()).thenThrow(new IOException());
        when(conn.getErrorStream()).thenReturn(null);
        httpURLConnection.set(response, conn);
        content = (String) getResponseBody.invoke(response);
        Assert.assertEquals("{\"message\":\"test\"}", content);
    }


}
