package com.aliyun.tea.utils;

import javax.net.ssl.X509TrustManager;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class X509TrustManagerImp implements X509TrustManager {
    private List<X509TrustManager> trustManagers = new ArrayList<X509TrustManager>();

    private boolean ignoreSSLCert = false;

    public boolean isIgnoreSSLCert() {
        return ignoreSSLCert;
    }

    public X509TrustManagerImp(boolean ignoreSSLCert) {
        this.ignoreSSLCert = ignoreSSLCert;
    }

    public X509TrustManagerImp(List<X509TrustManager> trustManagers) {
        this.trustManagers = trustManagers;
    }

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) {
        // do nothing
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        if (this.ignoreSSLCert) {
            return;
        }
        for (X509TrustManager trustManager : this.trustManagers) {
            try {
                trustManager.checkServerTrusted(chain, authType);
                return; // someone trusts them. success!
            } catch (CertificateException e) {
                // maybe someone else will trust them
            }
        }
        throw new CertificateException("None of the TrustManagers trust this certificate chain");
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        List<X509Certificate> certificates = new ArrayList<X509Certificate>();
        for (X509TrustManager trustManager : this.trustManagers) {
            certificates.addAll(Arrays.asList(trustManager.getAcceptedIssuers()));
        }
        X509Certificate[] certificatesArray = new X509Certificate[certificates.size()];
        return certificates.toArray(certificatesArray);
    }
}
