package com.aliyun.darabonba.utils;

import org.junit.Assert;
import org.junit.Test;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class TrueHostnameVerifierTest {
    @Test
    public void trueHostnameVerifierTest() throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        HostnameVerifier trueHostnameVerifier = DefaultHostnameVerifier.getInstance(true);
        SSLSession sslSession = null;
        Assert.assertTrue(trueHostnameVerifier.verify("authType", sslSession));
        Assert.assertTrue(trueHostnameVerifier.verify(null, null));

        Constructor<DefaultHostnameVerifier> constructor = DefaultHostnameVerifier.class.getDeclaredConstructor(boolean.class);
        constructor.setAccessible(true);
        DefaultHostnameVerifier hostnameVerifier = constructor.newInstance(false);
        Assert.assertFalse(hostnameVerifier.verify(null, null));
    }
}
