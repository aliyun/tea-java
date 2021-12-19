package com.aliyun.tea.okhttp;

import okhttp3.OkHttpClient;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

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
    }

    @Test
    public void certificateTest() {
        map.clear();
        OkHttpClientBuilder clientBuilder = Mockito.spy(new OkHttpClientBuilder());
        clientBuilder.certificate(map);
        map.clear();
        map.put("ignoreSSL", true);
        clientBuilder.certificate(map);
        Mockito.verify(clientBuilder, Mockito.times(2)).certificate(map);
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
        map.put("socks5Proxy", "https://user:password@127.0.0.1:8081");
        clientBuilder.proxy(map);
        Mockito.verify(clientBuilder, Mockito.times(4)).proxy(map);
        map.clear();
        map.put("socks5Proxy", "https://user:password@127.0.0.1:8081");
        clientBuilder.proxy(map);
        Mockito.verify(clientBuilder, Mockito.times(5)).proxy(map);
    }
}
