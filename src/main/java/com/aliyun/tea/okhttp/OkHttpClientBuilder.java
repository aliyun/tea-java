package com.aliyun.tea.okhttp;


import com.aliyun.tea.TeaException;
import com.aliyun.tea.okhttp.interceptors.SocksProxyAuthInterceptor;
import com.aliyun.tea.utils.StringUtils;
import com.aliyun.tea.utils.DefaultHostnameVerifier;
import com.aliyun.tea.utils.X509TrustManagerImp;
import okhttp3.*;
import okhttp3.Authenticator;

import javax.net.ssl.*;
import java.io.*;
import java.net.*;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class OkHttpClientBuilder {
    private static final String charset = "UTF-8";
    private final OkHttpClient.Builder builder;
    public static final String PEM_BEGIN = "-----BEGIN CERTIFICATE-----";
    public static final String PEM_END = "-----END CERTIFICATE-----";

    public OkHttpClientBuilder() {
        builder = new OkHttpClient().newBuilder();
    }

    public OkHttpClientBuilder protocols(Map<String, Object> map) {
        if (null != map.get("disableHttp2")) {
            Object object = map.get("disableHttp2");
            boolean disableHttp2 = false;
            try {
                disableHttp2 = Boolean.parseBoolean(String.valueOf(object));
            } catch (Exception ignored) {
            }
            if (disableHttp2) {
                this.builder.protocols(Collections.singletonList(Protocol.HTTP_1_1));
            }
        }
        return this;
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
        long keepAliveDuration = 10000L;
        if (map.containsKey("keepAliveDuration") && null != map.get("keepAliveDuration")) {
            keepAliveDuration = Long.parseLong(String.valueOf(map.get("keepAliveDuration")));
        }
        ConnectionPool connectionPool = new ConnectionPool(maxIdleConnections, keepAliveDuration, TimeUnit.MILLISECONDS);
        this.builder.connectionPool(connectionPool);
        return this;
    }

    private List<String> splitPemCertificates(String pemData) throws Exception {
        List<String> certificates = new ArrayList<String>();

        if (null != pemData && pemData.contains(PEM_BEGIN) && pemData.contains(PEM_END)) {
            StringBuilder sb = null;
            BufferedReader reader = new BufferedReader(new StringReader(pemData));

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains(PEM_BEGIN)) {
                    sb = new StringBuilder();
                    sb.append(PEM_BEGIN).append('\n');
                } else if (null != sb && line.contains(PEM_END)) {
                    sb.append(PEM_END).append('\n');
                    certificates.add(sb.toString());
                    sb = null;
                } else if (null != sb) {
                    sb.append(line).append('\n');
                }
            }
        } else if (null != pemData) {
            certificates.add(pemData);
        }

        return certificates;
    }

    public OkHttpClientBuilder certificate(Map<String, Object> map) {
        try {
            if (null != map.get("ignoreSSL") && Boolean.parseBoolean(String.valueOf(map.get("ignoreSSL")))) {
                X509TrustManager compositeX509TrustManager = new X509TrustManagerImp(true);
                SSLContext sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null, new TrustManager[]{compositeX509TrustManager}, new java.security.SecureRandom());
                this.builder.sslSocketFactory(sslContext.getSocketFactory(), compositeX509TrustManager).
                        hostnameVerifier(DefaultHostnameVerifier.getInstance(true));
            } else if (!StringUtils.isEmpty(map.get("ca"))) {
                SSLContext sslContext = SSLContext.getInstance("TLS");
                KeyManagerFactory keyManagerFactory = null;
                if (!StringUtils.isEmpty(map.get("key")) && !StringUtils.isEmpty(map.get("cert"))) {
                    KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
                    String cert = String.valueOf(map.get("cert"));
                    try (InputStream is = new ByteArrayInputStream(cert.getBytes(charset))) {
                        keyStore.load(is, String.valueOf(map.get("key")).toCharArray());
                    }
                    keyManagerFactory = KeyManagerFactory.getInstance("X.509");
                    keyManagerFactory.init(keyStore, String.valueOf(map.get("key")).toCharArray());
                }
                KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
                trustStore.load(null);
                String ca = String.valueOf(map.get("ca"));
                List<String> pemCerts = splitPemCertificates(ca);
                CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
                int certIndex = 0;
                // Process each certificate and add to the keystore
                for (String pemCert : pemCerts) {
                    try (InputStream is = new ByteArrayInputStream(pemCert.getBytes(charset))) {
                        Certificate certificate = certFactory.generateCertificate(is);
                        trustStore.setCertificateEntry("ca" + certIndex++, certificate);
                    }
                }
                TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                trustManagerFactory.init(trustStore);
                X509TrustManager trustManager = (X509TrustManager) trustManagerFactory.getTrustManagers()[0];
                sslContext.init(keyManagerFactory != null ? keyManagerFactory.getKeyManagers() : null
                        , trustManagerFactory.getTrustManagers()
                        , new SecureRandom());
                this.builder.sslSocketFactory(sslContext.getSocketFactory(), trustManager);
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
