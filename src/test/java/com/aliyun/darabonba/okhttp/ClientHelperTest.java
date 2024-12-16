package com.aliyun.darabonba.okhttp;

import okhttp3.OkHttpClient;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static com.aliyun.darabonba.okhttp.ClientHelper.clients;

public class ClientHelperTest {
    @Test
    public void getClientKeyTest() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        new ClientHelper();
        Method getClientKey = ClientHelper.class.getDeclaredMethod("getClientKey", String.class, int.class);
        getClientKey.setAccessible(true);
        String str = (String) getClientKey.invoke(ClientHelper.class, "0:0:0:0:0:0:0:1", 0);
        Assert.assertEquals("0:0:0:0:0:0:0:1:0", str);
        str = (String) getClientKey.invoke(ClientHelper.class, "0:0:0:0:0:0:0:1", 0);
        Assert.assertEquals("0:0:0:0:0:0:0:1:0", str);

        getClientKey = ClientHelper.class.getDeclaredMethod("getClientKey", String.class, int.class, String.class);
        getClientKey.setAccessible(true);
        str = (String) getClientKey.invoke(ClientHelper.class, "0:0:0:0:0:0:0:1", 0, "user:passwd");
        Assert.assertEquals("user:passwd@0:0:0:0:0:0:0:1:0", str);
        str = (String) getClientKey.invoke(ClientHelper.class, "0:0:0:0:0:0:0:1", 0, "user:passwd");
        Assert.assertEquals("user:passwd@0:0:0:0:0:0:0:1:0", str);

        getClientKey = ClientHelper.class.getDeclaredMethod("getClientKey", String.class, int.class, Map.class);
        getClientKey.setAccessible(true);
        str = (String) getClientKey.invoke(ClientHelper.class, "0:0:0:0:0:0:0:1", 0, new HashMap<String, Object>());
        Assert.assertEquals("0:0:0:0:0:0:0:1:0", str);
        str = (String) getClientKey.invoke(ClientHelper.class, "0:0:0:0:0:0:0:1", 0, new HashMap<String, Object>());
        Assert.assertEquals("0:0:0:0:0:0:0:1:0", str);

        Map<String, Object> map = new HashMap<>();
        map.put("httpProxy", "http://127.0.0.1:80");
        map.put("httpsProxy", "https://127.0.0.1:80");
        map.put("socks5Proxy", "socks5://user:password@127.0.0.1:1080");
        map.put("connectTimeout", 1000);
        map.put("readTimeout", 2000);
        map.put("ignoreSSL", false);
        str = (String) getClientKey.invoke(ClientHelper.class, "0:0:0:0:0:0:0:1", 0, map);
        Assert.assertEquals("0:0:0:0:0:0:0:1:0:http://127.0.0.1:80:https://127.0.0.1:80:socks5://user:password@127.0.0.1:1080:1000:2000:false", str);
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
        Assert.assertNotNull(clients.get("null:0:http://127.0.0.1:80"));
        Assert.assertNotNull(clients.get("null:0:https://127.0.0.1:80"));

        map.put("httpsProxy", "https://user:password@127.0.0.1:80");
        client = ClientHelper.getOkHttpClient(null, 0, map);
        Assert.assertNotNull(client);
        Assert.assertNotNull(clients.get("null:0:https://user:password@127.0.0.1:80"));
        Assert.assertNotSame(clients.get("null:0:http://127.0.0.1:80"), clients.get("null:0:https://127.0.0.1:80"));
        Assert.assertNotSame(clients.get("null:0:https://user:password@127.0.0.1:80"), clients.get("null:0:https://127.0.0.1:80"));


        map.put("httpsProxy", null);
        map.put("socks5Proxy", "socks5://user:password@127.0.0.1:1080");
        client = ClientHelper.getOkHttpClient(null, 0, map);
        Assert.assertNotNull(client);

        Assert.assertNotNull(clients.get("null:0:socks5://user:password@127.0.0.1:1080"));

        map.put("socks5Proxy", "socks5://user:passwd@127.0.0.1:1080");
        client = ClientHelper.getOkHttpClient(null, 0, map);
        Assert.assertNotNull(client);
        Assert.assertNotSame(clients.get("null:0:socks5://user:password@127.0.0.1:1080"), clients.get("null:0:socks5://user:passwd@127.0.0.1:1080"));
    }
}
