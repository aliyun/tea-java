package com.aliyun.tea.utils;

import org.junit.Assert;
import org.junit.Test;

import java.security.cert.X509Certificate;

public class X509TrustManagerImpTest {
    @Test
    public void  testCheckClientTrusted(){
        try {
            X509TrustManagerImp trustManagerImp = new X509TrustManagerImp();
            trustManagerImp.checkClientTrusted(new X509Certificate[0],"authType");
        }catch (Exception e){
            Assert.fail();
        }
    }
    @Test
    public void testCheckServerTrusted(){
        try {
            X509TrustManagerImp trustManagerImp = new X509TrustManagerImp();
            trustManagerImp.checkServerTrusted(new X509Certificate[0],"authType");
        }catch (Exception e){
            Assert.fail();
        }
    }
    @Test
    public void testGetAcceptedIssuers(){
        X509TrustManagerImp trustManagerImp = new X509TrustManagerImp();
        trustManagerImp.getAcceptedIssuers();
        X509Certificate[] res = trustManagerImp.getAcceptedIssuers();
        Assert.assertEquals(0, res.length);
    }
}
