package com.aliyun.darabonba.okhttp;

import com.aliyun.darabonba.TeaException;
import com.aliyun.darabonba.utils.DefaultHostnameVerifier;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class OkHttpClientBuilderTest {
    private Map<String, Object> map = new HashMap<>();

    @Test
    public void timeOutTest() {
        map.clear();
        OkHttpClientBuilder clientBuilder = new OkHttpClientBuilder();
        OkHttpClient client = clientBuilder.connectTimeout(map).readTimeout(map).buildOkHttpClient();
        Assert.assertEquals(10000, client.connectTimeoutMillis());
        Assert.assertEquals(10000, client.readTimeoutMillis());

        map.clear();
        clientBuilder = new OkHttpClientBuilder();
        map.put("connectTimeout", 666);
        map.put("readTimeout", 888);
        client = clientBuilder.connectTimeout(map).readTimeout(map).buildOkHttpClient();
        Assert.assertEquals(666, client.connectTimeoutMillis());
        Assert.assertEquals(888, client.readTimeoutMillis());
    }

    @Test
    public void connectionPoolTest() {
        map.clear();
        OkHttpClientBuilder clientBuilder = Mockito.spy(new OkHttpClientBuilder());
        clientBuilder.connectionPool(map);
        map.clear();
        map.put("maxIdleConns", 666);
        clientBuilder.connectionPool(map);
        Mockito.verify(clientBuilder, Mockito.times(2)).connectionPool(map);

        map.put("keepAliveDuration", null);
        clientBuilder.connectionPool(map);

        map.put("keepAliveDuration", "");
        try {
            clientBuilder.connectionPool(map);
            Assert.fail();
        } catch (NumberFormatException e) {
            Assert.assertTrue(e.getMessage().contains("For input string: \"\""));
        }

        map.put("keepAliveDuration", "str");
        try {
            clientBuilder.connectionPool(map);
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().contains("For input string: \"str\""));
        }

        map.put("keepAliveDuration", 0);
        try {
            clientBuilder.connectionPool(map);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().contains("keepAliveDuration <= 0: 0"));
        }

        map.put("keepAliveDuration", 100L);
        clientBuilder.connectionPool(map);
        Mockito.verify(clientBuilder, Mockito.times(7)).connectionPool(map);
    }

    @Test
    public void certificateTest() throws IOException {
        map.clear();
        OkHttpClientBuilder clientBuilder = Mockito.spy(new OkHttpClientBuilder());
        clientBuilder.certificate(map);
        map.clear();
        map.put("ignoreSSL", true);
        clientBuilder.certificate(map);
        Mockito.verify(clientBuilder, Mockito.times(2)).certificate(map);

        map.clear();
        map.put("ignoreSSL", false);
        map.put("ca", "-----BEGIN CERTIFICATE-----\nwrong ca-----END CERTIFICATE-----");
        try {
            new OkHttpClientBuilder().certificate(map);
            Assert.fail();
        } catch (TeaException e) {
            Assert.assertTrue(e.getMessage().contains("Unable to initialize"));
        }

        map.put("ca", null);
        new OkHttpClientBuilder().certificate(map);
        map.put("ca", "");
        new OkHttpClientBuilder().certificate(map);

        map.put("ca", System.getenv("CA"));
        OkHttpClientBuilder builder = new OkHttpClientBuilder().certificate(map);

        OkHttpClient client = builder.buildOkHttpClient();
        Assert.assertFalse(client.hostnameVerifier() instanceof DefaultHostnameVerifier);
        Assert.assertNotNull(client.sslSocketFactory());

        map.put("key", null);
        map.put("cert", null);
        new OkHttpClientBuilder().certificate(map);

        map.put("key", "");
        map.put("cert", "");
        new OkHttpClientBuilder().certificate(map);

        map.put("ca", "-----BEGIN CERTIFICATE-----\nwrong ca-----END CERTIFICATE-----");
        map.put("key", "wrong key");
        map.put("cert", "-----BEGIN CERTIFICATE-----\nwrong cert-----END CERTIFICATE-----");
        try {
            new OkHttpClientBuilder().certificate(map);
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue(e instanceof TeaException);
        }
    }

    @Test
    public void proxyTest() throws Exception {
        map.clear();
        OkHttpClientBuilder clientBuilder = Mockito.spy(new OkHttpClientBuilder());
        clientBuilder.proxy(map);
        map.clear();
        map.put("httpProxy", "http://127.0.0.1:8080");
        clientBuilder.proxy(map);
        map.clear();
        map.put("httpsProxy", "https://user:password@127.0.0.1:8080");
        clientBuilder.proxy(map);
        Mockito.verify(clientBuilder, Mockito.times(3)).proxy(map);
        map.put("socks5Proxy", "socks5://user:password@127.0.0.1:1080");
        clientBuilder.proxy(map);
        Mockito.verify(clientBuilder, Mockito.times(4)).proxy(map);
        map.clear();
        map.put("socks5Proxy", "socks5://user:password@127.0.0.1:1080");
        clientBuilder.proxy(map);
        Mockito.verify(clientBuilder, Mockito.times(5)).proxy(map);
    }

    @Test
    public void protocolsTest() {
        map.clear();
        OkHttpClientBuilder clientBuilder = new OkHttpClientBuilder();
        OkHttpClient client = clientBuilder.protocols(map).buildOkHttpClient();
        Assert.assertEquals(2, client.protocols().size());
        Assert.assertTrue(client.protocols().contains(Protocol.HTTP_2));
        Assert.assertTrue(client.protocols().contains(Protocol.HTTP_1_1));
        // HTTP_1_0 在 OkHttp 中已不支持
        Assert.assertFalse(client.protocols().contains(Protocol.HTTP_1_0));

        map.clear();
        clientBuilder = new OkHttpClientBuilder();
        map.put("disableHttp2", true);
        client = clientBuilder.protocols(map).buildOkHttpClient();
        Assert.assertEquals(1, client.protocols().size());
        Assert.assertFalse(client.protocols().contains(Protocol.HTTP_2));
        Assert.assertTrue(client.protocols().contains(Protocol.HTTP_1_1));
        // HTTP_1_0 在 OkHttp 中已不支持
        Assert.assertFalse(client.protocols().contains(Protocol.HTTP_1_0));
    }
}
