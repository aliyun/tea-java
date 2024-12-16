package com.aliyun.darabonba.utils;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import javax.net.ssl.X509TrustManager;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class X509TrustManagerImpTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void getSetIgnoreSSLCert() {
        X509TrustManagerImp trustManager = new X509TrustManagerImp(Collections.<X509TrustManager>emptyList());
        Assert.assertFalse(trustManager.isIgnoreSSLCert());
        trustManager = new X509TrustManagerImp(true);
        Assert.assertTrue(trustManager.isIgnoreSSLCert());
    }

    @Test
    public void testCheckClientTrusted() {
        try {
            X509TrustManagerImp trustManagerImp = new X509TrustManagerImp(Collections.<X509TrustManager>emptyList());
            trustManagerImp.checkClientTrusted(new X509Certificate[0], "authType");
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void testCheckServerTrustedAndIgnoreSSLCert() {
        try {
            X509TrustManagerImp trustManager = new X509TrustManagerImp(true);
            trustManager.checkServerTrusted(new X509Certificate[0], "authType");
        } catch (Exception e) {
            Assert.fail();
        }

        try {
            X509TrustManagerImp trustManagerImp = new X509TrustManagerImp(Collections.<X509TrustManager>emptyList());
            trustManagerImp.checkServerTrusted(new X509Certificate[0], "authType");
            Assert.fail();
        } catch (Exception e) {
            Assert.assertEquals("None of the TrustManagers trust this certificate chain", e.getMessage());
        }

    }

    @Test
    public void testCheckServerTrustedSucceedTwice() {
        try {
            final X509TrustManager trustManager0 = mock(X509TrustManager.class);
            doThrow(CertificateException.class).when(trustManager0).checkServerTrusted(any(X509Certificate[].class), anyString());
            final X509TrustManager trustManager1 = mock(X509TrustManager.class);
            doNothing().when(trustManager0).checkServerTrusted(any(X509Certificate[].class), anyString());
            List<X509TrustManager> trustManagerList = new ArrayList<X509TrustManager>();
            trustManagerList.add(trustManager0);
            trustManagerList.add(trustManager1);
            X509TrustManagerImp trustManager = new X509TrustManagerImp(trustManagerList);
            trustManager.checkServerTrusted(new X509Certificate[0], "authType");
        } catch (Exception e) {
            Assert.fail();
        }

    }

    @Test
    public void testCheckServerTrustedFailed() throws CertificateException {
        thrown.expect(CertificateException.class);
        final X509TrustManager trustManager0 = mock(X509TrustManager.class);
        doThrow(CertificateException.class).when(trustManager0).checkServerTrusted(any(X509Certificate[].class), anyString());
        List<X509TrustManager> trustManagerList = new ArrayList<X509TrustManager>();
        trustManagerList.add(trustManager0);
        X509TrustManagerImp trustManager = new X509TrustManagerImp(trustManagerList);
        trustManager.checkServerTrusted(new X509Certificate[0], "authType");
    }

    @Test
    public void testGetAcceptedIssuers() {
        X509TrustManagerImp trustManagerImp = new X509TrustManagerImp(Collections.<X509TrustManager>emptyList());
        trustManagerImp.getAcceptedIssuers();
        X509Certificate[] res = trustManagerImp.getAcceptedIssuers();
        Assert.assertEquals(0, res.length);
        final X509TrustManager trustManager0 = mock(X509TrustManager.class);
        X509Certificate certificate = mock(X509Certificate.class);
        when(trustManager0.getAcceptedIssuers()).thenReturn(new X509Certificate[]{certificate});
        List<X509TrustManager> trustManagerList = new ArrayList<X509TrustManager>();
        trustManagerList.add(trustManager0);
        X509TrustManagerImp trustManager = new X509TrustManagerImp(trustManagerList);
        res = trustManager.getAcceptedIssuers();
        Assert.assertEquals(1, res.length);
        Assert.assertEquals(certificate, res[0]);
    }
}
