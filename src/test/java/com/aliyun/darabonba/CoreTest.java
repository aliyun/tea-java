package com.aliyun.darabonba;

import com.aliyun.darabonba.interceptor.InterceptorChain;
import com.aliyun.darabonba.interceptor.MockRequestInterceptor;
import com.aliyun.darabonba.interceptor.MockResponseInterceptor;
import com.aliyun.darabonba.okhttp.ClientHelper;
import okhttp3.Protocol;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class CoreTest {
    @Test
    public void init() {
        Assert.assertNotNull(new Core());
    }

    @Test
    public void composeUrlTest() throws NoSuchMethodException, InvocationTargetException,
            IllegalAccessException {
        Method composeUrl = Core.class.getDeclaredMethod("composeUrl", Request.class);
        composeUrl.setAccessible(true);
        Request request = new Request();
        Map<String, String> map = new HashMap<>();
        map.put("test", null);
        map.put("host", "test");
        request.headers = map;
        request.pathname = "/test";
        request.protocol = null;
        request.query = map;
        String str = (String) composeUrl.invoke(Core.class, request);
        Assert.assertEquals("https://test/test?host=test", str);

        request.query = new HashMap<>();
        request.pathname = null;
        str = (String) composeUrl.invoke(Core.class, request);
        Assert.assertEquals("https://test", str);

        request.query = null;
        request.pathname = null;
        str = (String) composeUrl.invoke(Core.class, request);
        Assert.assertEquals("https://test", str);

        request.query = new HashMap<>();
        request.query.put("test", "and");
        request.pathname = "?test";
        request.protocol = "HTTP";
        str = (String) composeUrl.invoke(Core.class, request);
        Assert.assertEquals("HTTP://test?test&test=and", str);
        request.query.put("test", "null");
        str = (String) composeUrl.invoke(Core.class, request);
        Assert.assertEquals("HTTP://test?test&test=null", str);

        request.query = new HashMap<>();
        request.query.put("host", "test");
        request.pathname = "/test";
        request.protocol = "http";
        request.port = 80;
        str = (String) composeUrl.invoke(Core.class, request);
        Assert.assertEquals("http://test:80/test?host=test", str);
        request.port = 443;
        str = (String) composeUrl.invoke(Core.class, request);
        Assert.assertEquals("http://test:443/test?host=test", str);
        request.port = null;
        str = (String) composeUrl.invoke(Core.class, request);
        Assert.assertEquals("http://test/test?host=test", str);
    }

    @Test
    public void doActionTest() {
        ClientHelper.clients.clear();
        Request request = new Request();
        Map<String, String> map = new HashMap<>();
        map.put("host", "www.google.com.hk");
        request.protocol = "http";
        request.headers = map;
        request.method = "GET";
        Map<String, Object> runtimeOptions = new HashMap<>();
        runtimeOptions.put("readTimeout", "50000");
        runtimeOptions.put("connectTimeout", "50000");
        Response response = Core.doAction(request, runtimeOptions);
        Assert.assertEquals(200, response.statusCode);
        response = Core.doAction(request);
        Assert.assertEquals(200, response.statusCode);
        try {
            String body = response.getResponseBody();
            Assert.assertNotNull(body);
        } catch (TeaException e) {
            Assert.fail();
        }
    }

    @Test
    public void doActionProtocolsTest() {
        ClientHelper.clients.clear();
        Request request = new Request();
        Map<String, String> map = new HashMap<>();
        map.put("host", "github.com");
        request.protocol = "http";
        request.headers = map;
        request.method = "GET";
        Map<String, Object> runtimeOptions = new HashMap<>();
        Response response = Core.doAction(request, runtimeOptions);
        Assert.assertEquals(200, response.statusCode);
        Assert.assertEquals(Protocol.HTTP_2, response.response.protocol());

        ClientHelper.clients.clear();
        runtimeOptions.put("disableHttp2", "test");
        response = Core.doAction(request, runtimeOptions);
        Assert.assertEquals(200, response.statusCode);
        Assert.assertEquals(Protocol.HTTP_2, response.response.protocol());

        ClientHelper.clients.clear();
        runtimeOptions.put("disableHttp2", false);
        response = Core.doAction(request, runtimeOptions);
        Assert.assertEquals(200, response.statusCode);
        Assert.assertEquals(Protocol.HTTP_2, response.response.protocol());

        ClientHelper.clients.clear();
        runtimeOptions.put("disableHttp2", true);
        response = Core.doAction(request, runtimeOptions);
        Assert.assertEquals(200, response.statusCode);
        Assert.assertEquals(Protocol.HTTP_1_1, response.response.protocol());
    }

    @Test
    public void doActionWithProxyTest() {
        ClientHelper.clients.clear();
        Request request = new Request();
        Map<String, String> map = new HashMap<>();
        map.put("host", "www.google.com.hk");
        request.protocol = "http";
        request.headers = map;
        request.method = "GET";
        Map<String, Object> runtimeOptions = new HashMap<>();
        runtimeOptions.put("httpProxy", "https://user:password@127.0.0.1:8080");
        try {
            Core.doAction(request, runtimeOptions);
            Assert.fail();
        } catch (TeaRetryableException e) {
            Assert.assertEquals("Failed to connect to /127.0.0.1:8080", e.getMessage());
        }
        ClientHelper.clients.clear();
        runtimeOptions.clear();
        runtimeOptions.put("socks5Proxy", "socks5://user:password@127.0.0.1:1080");
        try {
            Core.doAction(request, runtimeOptions);
            Assert.fail();
        } catch (TeaRetryableException e) {
            Assert.assertTrue(e.getMessage().startsWith("Connection refused"));
        }
    }

    @Test
    public void doActionWithInterceptorTest() {
        ClientHelper.clients.clear();
        Request request = new Request();
        Map<String, String> map = new HashMap<>();
        map.put("host", "www.google.com.hk");
        request.protocol = "http";
        request.headers = map;
        request.method = "GET";
        Map<String, Object> runtimeOptions = new HashMap<>();
        runtimeOptions.put("readTimeout", "50000");
        runtimeOptions.put("connectTimeout", "50000");
        InterceptorChain chain = InterceptorChain.create();
        chain.addResponseInterceptor(new MockResponseInterceptor());
        Response response = Core.doAction(request, runtimeOptions, chain);
        Assert.assertEquals(400, response.statusCode);
        chain.addRequestInterceptor(new MockRequestInterceptor());
        response = Core.doAction(request, runtimeOptions, chain);
        Assert.assertEquals(404, response.statusCode);
        try {
            String body = response.getResponseBody();
            Assert.assertNotNull(body);
        } catch (TeaException e) {
            Assert.fail();
        }
    }

    @Test
    public void getBackoffTimeTest() {
        Map<String, Object> map = new HashMap<>();
        map.put("policy", "");
        int number = Core.getBackoffTime(map, 88);
        Assert.assertEquals(0, number);

        map.put("policy", "no");
        number = Core.getBackoffTime(map, 88);
        Assert.assertEquals(0, number);

        map.put("policy", "one");
        map.put("period", null);
        number = Core.getBackoffTime(map, 88);
        Assert.assertEquals(0, number);

        map.put("policy", "one");
        map.put("period", 0);
        number = Core.getBackoffTime(map, 88);
        Assert.assertEquals(88, number);

        map.put("policy", "one");
        map.put("period", 66);
        number = Core.getBackoffTime(map, 88);
        Assert.assertEquals(66, number);
    }

    @Test
    public void sleepTest() {
        long start = System.currentTimeMillis();
        Core.sleep(100);
        long end = System.currentTimeMillis();
        Assert.assertTrue(end - start >= 100);
    }

    @Test
    public void isRetryableTest() {
        Assert.assertFalse(Core.isRetryable(new RuntimeException()));
    }

    @Test
    public void allowRetryTest() {
        Map<String, Object> map = null;
        Assert.assertTrue(Core.allowRetry(map, 0, 6L));

        Assert.assertFalse(Core.allowRetry(map, 6, 6L));

        map = new HashMap<>();
        map.put("maxAttempts", null);
        map.put("retryable", true);
        Assert.assertFalse(Core.allowRetry(map, 6, 6L));

        map.put("maxAttempts", 8);
        Assert.assertTrue(Core.allowRetry(map, 6, 6L));

        map.put("retryable", false);
        Assert.assertFalse(Core.allowRetry(map, 6, 6L));

        map.put("retryable", "111");
        Assert.assertFalse(Core.allowRetry(map, 6, 6L));
    }

    @Test
    public void toReadableTest() throws IOException {
        String str = "readable test";
        InputStream inputStream = Core.toReadable(str);
        byte[] bytes = new byte[1024];
        int index = inputStream.read(bytes);
        String result = new String(bytes, 0, index);
        Assert.assertTrue(str.equals(result));
    }
}
