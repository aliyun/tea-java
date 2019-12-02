package com.aliyun.tea.utils;

import org.junit.Assert;
import org.junit.Test;

import javax.net.ssl.SSLSession;

public class TrueHostnameVerifierTest {
    @Test
    public void trueHostnameVerifierTest(){
        TrueHostnameVerifier trueHostnameVerifier = new TrueHostnameVerifier();
        SSLSession sslSession = null;
        trueHostnameVerifier.verify("authType",sslSession);
        Assert.assertTrue(trueHostnameVerifier.verify(null,null));

    }
}
