package com.aliyun.tea.okhttp;

import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public class ServerAccount implements Authenticator {
    private String userName;
    private String password;

    public ServerAccount(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    @Nullable
    @Override
    public Request authenticate(@Nullable Route route, @NotNull Response response) throws IOException {
        String account = Credentials.basic(userName, password);
        return response.request().newBuilder().header("Proxy-Authorization", account).build();
    }
}
