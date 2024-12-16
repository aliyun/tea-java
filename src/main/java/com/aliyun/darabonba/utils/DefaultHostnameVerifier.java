package com.aliyun.darabonba.utils;

import okhttp3.internal.tls.OkHostnameVerifier;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

public class DefaultHostnameVerifier implements HostnameVerifier {
    private final boolean ignoreSSLCert;
    private static final HostnameVerifier NOOP_INSTANCE = new DefaultHostnameVerifier(true);
    private static final HostnameVerifier DEFAULT_INSTANCE = OkHostnameVerifier.INSTANCE;

    private DefaultHostnameVerifier(boolean ignoreSSLCert) {
        this.ignoreSSLCert = ignoreSSLCert;
    }

    public static HostnameVerifier getInstance(boolean ignoreSSLCert) {
        if (ignoreSSLCert) {
            return NOOP_INSTANCE;
        } else {
            return DEFAULT_INSTANCE;
        }
    }

    @Override
    public boolean verify(String s, SSLSession sslSession) {
        return ignoreSSLCert;
    }
}
