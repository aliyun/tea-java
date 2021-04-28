package com.aliyun.tea.okhttp;

import okhttp3.OkHttpClient;

import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ClientHelper {
    public static final ConcurrentHashMap<String, OkHttpClient> clients = new ConcurrentHashMap<String, OkHttpClient>();

    public static OkHttpClient getOkHttpClient(String host, int port, Map<String, Object> map) throws Exception {
        String key;
        if (null != map.get("httpProxy") || null != map.get("httpsProxy")) {
            Object urlString = null == map.get("httpProxy") ? map.get("httpsProxy") : map.get("httpProxy");
            URL url = new URL(String.valueOf(urlString));
            key = getClientKey(url.getHost(), url.getPort());
        } else {
            key = getClientKey(host, port);
        }
        OkHttpClient client = clients.get(key);
        if (null == client) {
            client = creatClient(map);
            clients.put(key, client);
        }
        return client;
    }

    public static OkHttpClient creatClient(Map<String, Object> map) {
        OkHttpClientBuilder builder = new OkHttpClientBuilder();
        builder = builder.connectTimeout(map).readTimeout(map).connectionPool(map).certificate(map).proxy(map).proxyAuthenticator(map);
        OkHttpClient client = builder.buildOkHttpClient();
        return client;
    }

    public static String getClientKey(String host, int port) {
        return String.format("%s:%d", host, port);
    }
}
