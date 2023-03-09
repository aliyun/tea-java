package com.aliyun.tea.okhttp;

import okhttp3.OkHttpClient;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static com.aliyun.tea.okhttp.ClientHelper.clients;

public class ClientHelperTest {
    @Test
    public void getClientKeyTest() {
        new ClientHelper();
        Assert.assertTrue("0:0:0:0:0:0:0:1:0".equals(ClientHelper.getClientKey("0:0:0:0:0:0:0:1", 0)));
    }

    @Test
    public void getOkHttpClientTest() throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("httpProxy", "http://127.0.0.1:80");
        OkHttpClient client = ClientHelper.getOkHttpClient(null, 0, map);
        Assert.assertNotNull(client);

        map.put("httpProxy", null);
        map.put("httpsProxy", "https://127.0.0.1:80");
        client = ClientHelper.getOkHttpClient(null, 0, map);
        Assert.assertNotNull(client);
        Assert.assertNotNull(clients.get("127.0.0.1:80"));

        map.put("httpsProxy", "https://user:password@127.0.0.1:80");
        client = ClientHelper.getOkHttpClient(null, 0, map);
        Assert.assertNotNull(client);
        Assert.assertNotNull(clients.get("user:password@127.0.0.1:80"));
        Assert.assertNotSame(clients.get("user:password@127.0.0.1:80"), clients.get("127.0.0.1:80"));


        map.put("httpsProxy", null);
        map.put("socks5Proxy", "socks5://user:password@127.0.0.1:1080");
        client = ClientHelper.getOkHttpClient(null, 0, map);
        Assert.assertNotNull(client);

        Assert.assertNull(clients.get("127.0.0.1:1080"));
        Assert.assertNotNull(clients.get("user:password@127.0.0.1:1080"));

        map.put("socks5Proxy", "socks5://user:passwd@127.0.0.1:1080");
        client = ClientHelper.getOkHttpClient(null, 0, map);
        Assert.assertNotNull(client);
        Assert.assertNotSame(clients.get("user:password@127.0.0.1:1080"), clients.get("user:passwd@127.0.0.1:1080"));
    }
}
