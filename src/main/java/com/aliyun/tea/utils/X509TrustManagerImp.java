package com.aliyun.tea.utils;

import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;

public class X509TrustManagerImp implements X509TrustManager {
    @Override
    public void checkClientTrusted(X509Certificate[] x509Certificates, String s) {
    }

    @Override
    public void checkServerTrusted(X509Certificate[] x509Certificates, String s) {
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0];
    }
}
