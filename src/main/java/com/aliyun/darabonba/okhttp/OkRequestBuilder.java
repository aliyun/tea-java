package com.aliyun.darabonba.okhttp;

import com.aliyun.darabonba.Request;

import java.net.URL;
import java.util.Map;


public class OkRequestBuilder {
    private final okhttp3.Request.Builder builder;

    public OkRequestBuilder(okhttp3.Request.Builder builder) {
        this.builder = builder;
    }

    public OkRequestBuilder url(URL url) {
        this.builder.url(url);
        return this;
    }

    public OkRequestBuilder header(Map<String, String> headers) {
        for (String headerName : headers.keySet()) {
            this.builder.header(headerName, headers.get(headerName));
        }
        return this;
    }

    public okhttp3.Request buildRequest(Request request) {
        String method = request.method.toUpperCase();
        OkRequestBody requestBody;
        switch (method) {
            case "DELETE":
                requestBody = new OkRequestBody(request);
                this.builder.delete(requestBody);
                break;
            case "POST":
                requestBody = new OkRequestBody(request);
                this.builder.post(requestBody);
                break;
            case "PUT":
                requestBody = new OkRequestBody(request);
                this.builder.put(requestBody);
                break;
            case "PATCH":
                requestBody = new OkRequestBody(request);
                this.builder.patch(requestBody);
                break;
            case "HEAD":
                this.builder.head();
                break;
            default:
                this.builder.get();
                break;
        }
        return this.builder.build();
    }
}
