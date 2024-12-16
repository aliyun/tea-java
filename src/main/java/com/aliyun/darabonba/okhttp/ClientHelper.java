package com.aliyun.darabonba.okhttp;

import okhttp3.OkHttpClient;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ClientHelper {
    public static final ConcurrentHashMap<String, OkHttpClient> clients = new ConcurrentHashMap<String, OkHttpClient>();

    public static OkHttpClient getOkHttpClient(String host, int port, Map<String, Object> map) throws Exception {
        String key = getClientKey(host, port, map);
        OkHttpClient client = clients.get(key);
        if (null == client) {
            client = creatClient(map);
            clients.put(key, client);
        }
        return client;
    }

    private static OkHttpClient creatClient(Map<String, Object> map) {
        OkHttpClientBuilder builder = new OkHttpClientBuilder();
        builder = builder.protocols(map).connectTimeout(map).readTimeout(map).connectionPool(map).certificate(map).proxy(map).proxyAuthenticator(map);
        return builder.buildOkHttpClient();
    }

    private static String getClientKey(String host, int port) {
        return String.format("%s:%d", host, port);
    }

    private static String getClientKey(String host, int port, String userInfo) {
        return String.format("%s@%s:%d", userInfo, host, port);
    }

    private static String getClientKey(String host, int port, Map<String, Object> map) {
        return String.format("%s:%d", host, port) +
                (map.containsKey("httpProxy") && null != map.get("httpProxy") ? ":" + map.get("httpProxy") : "") +
                (map.containsKey("httpsProxy") && null != map.get("httpsProxy") ? ":" + map.get("httpsProxy") : "") +
                (map.containsKey("socks5Proxy") && null != map.get("socks5Proxy") ? ":" + map.get("socks5Proxy") : "") +
                (map.containsKey("connectTimeout") && null != map.get("connectTimeout") ? ":" + map.get("connectTimeout") : "") +
                (map.containsKey("readTimeout") && null != map.get("readTimeout") ? ":" + map.get("readTimeout") : "") +
                (map.containsKey("ignoreSSL") && null != map.get("ignoreSSL") ? ":" + map.get("ignoreSSL") : "");
    }
}
