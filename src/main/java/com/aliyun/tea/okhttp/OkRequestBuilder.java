package com.aliyun.tea.okhttp;

import com.aliyun.tea.Tea;
import com.aliyun.tea.TeaRequest;
import okhttp3.Request;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;

import static com.aliyun.tea.Tea.toUpperFirstChar;

public class OkRequestBuilder {
    private Request.Builder builder;

    public OkRequestBuilder(Request.Builder builder) {
        this.builder = builder;
    }

    public OkRequestBuilder url(TeaRequest request) throws UnsupportedEncodingException, MalformedURLException {
        String urlString = Tea.composeUrl(request);
        URL url = new URL(urlString);
        this.builder = this.builder.url(url);
        return this;
    }

    public OkRequestBuilder header(TeaRequest request) {
        for (String headerName : request.headers.keySet()) {
            this.builder = this.builder.header(toUpperFirstChar(headerName), request.headers.get(headerName));
        }
        return this;
    }

    public OkRequestBuilder httpMethod(TeaRequest request) {
        String method = request.method.toUpperCase();
        OkRequestBody requestBody;
        switch (method) {
            case "DELETE":
                this.builder = this.builder.delete();
                break;
            case "POST":
                requestBody = new OkRequestBody(request);
                this.builder = this.builder.post(requestBody);
                break;
            case "PUT":
                requestBody = new OkRequestBody(request);
                this.builder = this.builder.put(requestBody);
                break;
            case "PATCH":
                requestBody = new OkRequestBody(request);
                this.builder = this.builder.patch(requestBody);
                break;
            default:
                this.builder = this.builder.get();
                break;
        }
        return this;
    }


    public Request buildRequest() {
        return this.builder.build();
    }
}
