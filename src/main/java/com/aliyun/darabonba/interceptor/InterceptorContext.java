package com.aliyun.darabonba.interceptor;

import com.aliyun.darabonba.Request;
import com.aliyun.darabonba.Response;

import java.util.Map;

public class InterceptorContext {
    private Request teaRequest;
    private Map<String, Object> runtimeOptions;
    private Response teaResponse;

    private InterceptorContext() {
    }

    public static InterceptorContext create() {
        return new InterceptorContext();
    }

    public Request teaRequest() {
        return teaRequest;
    }

    public Map<String, Object> runtimeOptions() {
        return runtimeOptions;
    }

    public Response teaResponse() {
        return teaResponse;
    }

    public InterceptorContext setTeaRequest(Request teaRequest) {
        this.teaRequest = teaRequest;
        return this;
    }

    public InterceptorContext setRuntimeOptions(Map<String, Object> runtimeOptions) {
        this.runtimeOptions = runtimeOptions;
        return this;
    }

    public InterceptorContext addRuntimeOptions(String key, Object value) {
        this.runtimeOptions.put(key, value);
        return this;
    }

    public InterceptorContext setTeaResponse(Response teaResponse) {
        this.teaResponse = teaResponse;
        return this;
    }

    public InterceptorContext copy() {
        return create().setTeaRequest(teaRequest)
                .setRuntimeOptions(runtimeOptions)
                .setTeaResponse(teaResponse);
    }
}
