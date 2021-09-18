package com.aliyun.tea.interceptor;

import com.aliyun.tea.TeaRequest;
import com.aliyun.tea.TeaResponse;

import java.util.Map;

public class InterceptorContext {
    private TeaRequest teaRequest;
    private Map<String, Object> runtimeOptions;
    private TeaResponse teaResponse;

    private InterceptorContext() {
    }

    public static InterceptorContext create() {
        return new InterceptorContext();
    }

    public TeaRequest teaRequest() {
        return teaRequest;
    }

    public Map<String, Object> runtimeOptions() {
        return runtimeOptions;
    }

    public TeaResponse teaResponse() {
        return teaResponse;
    }

    public InterceptorContext setTeaRequest(TeaRequest teaRequest) {
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

    public InterceptorContext setTeaResponse(TeaResponse teaResponse) {
        this.teaResponse = teaResponse;
        return this;
    }

    public InterceptorContext copy() {
        return create().setTeaRequest(teaRequest)
                .setRuntimeOptions(runtimeOptions)
                .setTeaResponse(teaResponse);
    }
}
