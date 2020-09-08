package com.aliyun.tea.okhttp;

import okhttp3.OkHttpClient;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class ClientHelperTest {
    @Test
    public void getClientKeyTest() {
        new ClientHelper();
        Assert.assertTrue("0:0:0:0:0:0:0:1:0".equals(ClientHelper.getClientKey("0:0:0:0:0:0:0:1", 0)));
    }

    @Test
    public void getOkHttpClientTest() throws Exception{
        Map<String, Object> map = new HashMap<>();
        map.put("httpProxy", "http://127.0.0.1:80");
        OkHttpClient client = ClientHelper.getOkHttpClient(null, 0, map);
        Assert.assertNotNull(client);

        map.put("httpProxy", null);
        map.put("httpsProxy", "https://127.0.0.1:80");
        client = ClientHelper.getOkHttpClient(null, 0, map);
        Assert.assertNotNull(client);
    }
}
