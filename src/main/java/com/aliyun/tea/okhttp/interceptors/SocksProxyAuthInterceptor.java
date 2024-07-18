package com.aliyun.tea.okhttp.interceptors;

import okhttp3.*;

import java.io.IOException;

public class SocksProxyAuthInterceptor implements Interceptor {
    private final String user;
    private final String password;

    public SocksProxyAuthInterceptor(String user, String password) {
        this.user = user;
        this.password = password;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        ThreadLocalProxyAuthenticator.getInstance().setCredentials(user, password);
        try {
            return chain.proceed(chain.request());
        } finally {
            ThreadLocalProxyAuthenticator.clearCredentials();
        }
    }
}
