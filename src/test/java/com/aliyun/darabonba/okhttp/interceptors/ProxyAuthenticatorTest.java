package com.aliyun.darabonba.okhttp.interceptors;

import okhttp3.*;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.TimeUnit;


public class ProxyAuthenticatorTest {
    @Test
    public void proxyAuthTest() throws IOException {
        ThreadLocalProxyAuthenticator authenticator = ThreadLocalProxyAuthenticator.getInstance();
        authenticator.setCredentials("user", "password");
        Assert.assertEquals("user", authenticator.getPasswordAuthentication().getUserName());
        Assert.assertEquals("password", new String(authenticator.getPasswordAuthentication().getPassword()));
        ThreadLocalProxyAuthenticator.clearCredentials();
        Assert.assertNull(authenticator.getPasswordAuthentication());

        SocksProxyAuthInterceptor authInterceptor = new SocksProxyAuthInterceptor("user", "password");
        authInterceptor.intercept(new Interceptor.Chain() {
            @Override
            public Request request() {
                return null;
            }

            @Override
            public Response proceed(Request request) throws IOException {
                Assert.assertEquals("user", ThreadLocalProxyAuthenticator.getInstance().getPasswordAuthentication().getUserName());
                Assert.assertEquals("password", new String(ThreadLocalProxyAuthenticator.getInstance().getPasswordAuthentication().getPassword()));
                return null;
            }

            @Override
            public Connection connection() {
                return null;
            }

            @Override
            public Call call() {
                return null;
            }

            @Override
            public int connectTimeoutMillis() {
                return 0;
            }

            @Override
            public Interceptor.Chain withConnectTimeout(int timeout, TimeUnit unit) {
                return null;
            }

            @Override
            public int readTimeoutMillis() {
                return 0;
            }

            @Override
            public Interceptor.Chain withReadTimeout(int timeout, TimeUnit unit) {
                return null;
            }

            @Override
            public int writeTimeoutMillis() {
                return 0;
            }

            @Override
            public Interceptor.Chain withWriteTimeout(int timeout, TimeUnit unit) {
                return null;
            }
        });


        Assert.assertNull(authenticator.getPasswordAuthentication());
    }
}
