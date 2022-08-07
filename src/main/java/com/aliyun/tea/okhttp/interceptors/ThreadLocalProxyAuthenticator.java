package com.aliyun.tea.okhttp.interceptors;

import java.net.PasswordAuthentication;
import java.net.Authenticator;

public class ThreadLocalProxyAuthenticator extends Authenticator {
    private ThreadLocal<PasswordAuthentication> credentials = new ThreadLocal<>();

    private ThreadLocalProxyAuthenticator(){}

    private static class SingletonHolder {
        private static final ThreadLocalProxyAuthenticator instance = new ThreadLocalProxyAuthenticator();
    }

    public static final ThreadLocalProxyAuthenticator getInstance() {
        return SingletonHolder.instance;
    }

    public void setCredentials(String user, String password) {
        ThreadLocalProxyAuthenticator authenticator = ThreadLocalProxyAuthenticator.getInstance();
        Authenticator.setDefault(authenticator);
        authenticator.credentials.set(new PasswordAuthentication(user, password.toCharArray()));
    }

    public static void clearCredentials() {
        ThreadLocalProxyAuthenticator authenticator = ThreadLocalProxyAuthenticator.getInstance();
        Authenticator.setDefault(authenticator);
        authenticator.credentials.set(null);
    }

    @Override
    public PasswordAuthentication getPasswordAuthentication() {
        return credentials.get();
    }
}
