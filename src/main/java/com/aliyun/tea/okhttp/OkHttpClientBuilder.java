package com.aliyun.tea.okhttp;


import com.aliyun.tea.TeaException;
import com.aliyun.tea.okhttp.interceptors.SocksProxyAuthInterceptor;
import com.aliyun.tea.utils.TrueHostnameVerifier;
import com.aliyun.tea.utils.X509TrustManagerImp;
import okhttp3.*;
import okhttp3.Authenticator;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.net.*;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class OkHttpClientBuilder {
    private OkHttpClient.Builder builder;

    public OkHttpClientBuilder() {
        builder = new OkHttpClient().newBuilder();
    }

    public OkHttpClientBuilder connectTimeout(Map<String, Object> map) {
        Object object = map.get("connectTimeout");
        long timeout;
        try {
            timeout = Long.parseLong(String.valueOf(object));
        } catch (Exception e) {
            return this;
        }
        this.builder.connectTimeout(timeout, TimeUnit.MILLISECONDS);
        return this;
    }

    public OkHttpClientBuilder readTimeout(Map<String, Object> map) {
        Object object = map.get("readTimeout");
        long timeout;
        try {
            timeout = Long.parseLong(String.valueOf(object));
        } catch (Exception e) {
            return this;
        }
        this.builder.readTimeout(timeout, TimeUnit.MILLISECONDS);
        return this;
    }

    public OkHttpClientBuilder connectionPool(Map<String, Object> map) {
        Object maxIdleConns = map.get("maxIdleConns");
        int maxIdleConnections;
        try {
            maxIdleConnections = Integer.parseInt(String.valueOf(maxIdleConns));
        } catch (Exception e) {
            maxIdleConnections = 5;
        }
        ConnectionPool connectionPool = new ConnectionPool(maxIdleConnections, 10000L, TimeUnit.MILLISECONDS);
        this.builder.connectionPool(connectionPool);
        return this;
    }

    public OkHttpClientBuilder certificate(Map<String, Object> map) {
        try {
            if (Boolean.parseBoolean(String.valueOf(map.get("ignoreSSL")))) {
                X509TrustManager compositeX509TrustManager = new X509TrustManagerImp();
                SSLContext sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null, new TrustManager[]{compositeX509TrustManager}, new java.security.SecureRandom());
                this.builder.sslSocketFactory(sslContext.getSocketFactory(), compositeX509TrustManager).
                        hostnameVerifier(new TrueHostnameVerifier());
            }
            return this;
        } catch (Exception e) {
            throw new TeaException(e.getMessage(), e);
        }

    }

    public OkHttpClientBuilder proxy(Map<String, Object> map) {
        try {
            if (null != map.get("httpProxy") || null != map.get("httpsProxy")) {
                Object urlString = null == map.get("httpProxy") ? map.get("httpsProxy") : map.get("httpProxy");
                URL url = new URL(String.valueOf(urlString));
                this.builder.proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(url.getHost(), url.getPort())));
            } else if (null != map.get("socks5Proxy")) {
                Object urlString = map.get("socks5Proxy");
                URI url = new URI(String.valueOf(urlString));
                this.builder.proxy(new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(url.getHost(), url.getPort())));
            }
            return this;
        } catch (Exception e) {
            throw new TeaException(e.getMessage(), e);
        }

    }

    public OkHttpClientBuilder proxyAuthenticator(Map<String, Object> map) {
        try {
            if (null != map.get("httpProxy") || null != map.get("httpsProxy")) {
                Object proxy = map.get("httpsProxy") != null ? map.get("httpsProxy") : map.get("httpProxy");
                URL proxyUrl = new URL(String.valueOf(proxy));
                String userInfo = proxyUrl.getUserInfo();
                if (null != userInfo) {
                    final String[] userMessage = userInfo.split(":");
                    final String credential = Credentials.basic(userMessage[0], userMessage[1]);
                    Authenticator authenticator = new Authenticator() {
                        @Override
                        public Request authenticate(Route route, Response response) throws IOException {
                            return response.request().newBuilder()
                                    .header("Proxy-Authorization", credential)
                                    .build();
                        }
                    };
                    this.builder.proxyAuthenticator(authenticator);
                }
            } else if (null != map.get("socks5Proxy")) {
                Object proxy = map.get("socks5Proxy");
                URI proxyUrl = new URI(String.valueOf(proxy));
                String userInfo = proxyUrl.getUserInfo();
                if (null != userInfo) {
                    final String[] userMessage = userInfo.split(":");
                    this.builder.addInterceptor(new SocksProxyAuthInterceptor(userMessage[0], userMessage[1]));
                }
            }
            return this;
        } catch (Exception e) {
            throw new TeaException(e.getMessage(), e);
        }
    }

    public OkHttpClient buildOkHttpClient() {
        return this.builder.build();
    }
}
