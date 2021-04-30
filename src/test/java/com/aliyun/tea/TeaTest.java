package com.aliyun.tea;

import com.aliyun.tea.utils.X509TrustManagerImp;
import org.junit.Assert;
import org.junit.Test;
import org.powermock.reflect.Whitebox;

import javax.net.ssl.SSLSocketFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.mock;

public class TeaTest {
    @Test
    public void init() {
        Assert.assertNotNull(new Tea());
    }

    @Test
    public void composeUrlTest() throws NoSuchMethodException, InvocationTargetException,
            IllegalAccessException {
        Method composeUrl = Tea.class.getDeclaredMethod("composeUrl", TeaRequest.class);
        composeUrl.setAccessible(true);
        TeaRequest request = new TeaRequest();
        Map<String, String> map = new HashMap<>();
        map.put("test", null);
        map.put("host", "test");
        request.headers = map;
        request.pathname = "/test";
        request.protocol = null;
        request.query = map;
        String str = (String) composeUrl.invoke(Tea.class, request);
        Assert.assertEquals("http://test/test?host=test", str);

        request.query = new HashMap<>();
        request.pathname = null;
        str = (String) composeUrl.invoke(Tea.class, request);
        Assert.assertEquals("http://test", str);

        request.query = new HashMap<>();
        request.query.put("test", "and");
        request.pathname = "?test";
        request.protocol = "HTTP";
        str = (String) composeUrl.invoke(Tea.class, request);
        Assert.assertEquals("HTTP://test?test&test=and", str);

        request.query = null;
        String str2 = (String) composeUrl.invoke(Tea.class, request);
        Assert.assertEquals("HTTP://test?test", str2);
    }

    @Test
    public void doActionTest() {
        TeaRequest request = new TeaRequest();
        Map<String, String> map = new HashMap<>();
        map.put("host", "www.google.com.hk");
        request.protocol = "http";
        request.headers = map;
        request.method = "GET";
        Map<String, Object> runtimeOptions = new HashMap<>();
        runtimeOptions.put("readTimeout", "50000");
        runtimeOptions.put("connectTimeout", "50000");
        TeaResponse response = Tea.doAction(request, runtimeOptions);
        Assert.assertEquals(200, response.statusCode);
    }

    @Test
    public void getBackoffTimeTest() {
        Map<String, Object> map = new HashMap<>();
        map.put("policy", "");
        int number = Tea.getBackoffTime(map, 88);
        Assert.assertEquals(0, number);

        map.put("policy", "no");
        number = Tea.getBackoffTime(map, 88);
        Assert.assertEquals(0, number);

        map.put("policy", "one");
        map.put("period", null);
        number = Tea.getBackoffTime(map, 88);
        Assert.assertEquals(0, number);

        map.put("policy", "one");
        map.put("period", 0);
        number = Tea.getBackoffTime(map, 88);
        Assert.assertEquals(88, number);

        map.put("policy", "one");
        map.put("period", 66);
        number = Tea.getBackoffTime(map, 88);
        Assert.assertEquals(66, number);
    }

    @Test
    public void sleepTest() {
        long start = System.currentTimeMillis();
        Tea.sleep(100);
        long end = System.currentTimeMillis();
        Assert.assertTrue(end - start >= 100);
    }

    @Test
    public void isRetryableTest() {
        Assert.assertFalse(Tea.isRetryable(new RuntimeException()));
    }

    @Test
    public void allowRetryTest() {
        Map<String, Object> map = null;
        Assert.assertTrue(Tea.allowRetry(map, 0, 6L));

        Assert.assertFalse(Tea.allowRetry(map, 6, 6L));

        map = new HashMap<>();
        map.put("maxAttempts", null);
        map.put("retryable", true);
        Assert.assertFalse(Tea.allowRetry(map, 6, 6L));

        map.put("maxAttempts", 8);
        Assert.assertTrue(Tea.allowRetry(map, 6, 6L));

        map.put("retryable", false);
        Assert.assertFalse(Tea.allowRetry(map, 6, 6L));

        map.put("retryable", "111");
        Assert.assertFalse(Tea.allowRetry(map, 6, 6L));
    }

    @Test
    public void toReadableTest() throws IOException {
        String str = "readable test";
        InputStream inputStream = Tea.toReadable(str);
        byte[] bytes = new byte[1024];
        int index = inputStream.read(bytes);
        String result = new String(bytes, 0, index);
        Assert.assertTrue(str.equals(result));
    }

    @Test
    public void setProxyAuthorizationTest() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method setProxyAuthorization = Tea.class.getDeclaredMethod("setProxyAuthorization", Map.class, Object.class);
        setProxyAuthorization.setAccessible(true);
        Object httpsProxy = "http://user:password@127.0.0.1:8080";
        Map<String, String> result = (Map<String, String>) setProxyAuthorization.invoke(new Tea(), new HashMap<String, String>(), httpsProxy);
        assert "Basic dXNlcjpwYXNzd29yZA==".equals(result.get("Proxy-Authorization"));

        httpsProxy = "http://127.0.0.1:8080";
        result = (Map<String, String>) setProxyAuthorization.invoke(new Tea(), new HashMap<String, String>(), httpsProxy);
        assert null == result.get("Proxy-Authorization");

        httpsProxy = null;
        result = (Map<String, String>) setProxyAuthorization.invoke(new Tea(), new HashMap<String, String>(), httpsProxy);
        assert null == result.get("Proxy-Authorization");
    }
}
