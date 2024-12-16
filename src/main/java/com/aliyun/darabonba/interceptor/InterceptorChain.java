package com.aliyun.darabonba.interceptor;


import com.aliyun.darabonba.Request;
import com.aliyun.darabonba.Response;
import com.aliyun.darabonba.utils.AttributeMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InterceptorChain implements AutoCloseable {
    private final List<RuntimeOptionsInterceptor> runtimeOptionsInterceptors = new ArrayList<>();
    private final List<RequestInterceptor> requestInterceptors = new ArrayList<>();
    private final List<ResponseInterceptor> responseInterceptors = new ArrayList<>();

    private InterceptorChain() {
    }

    public static InterceptorChain create() {
        return new InterceptorChain();
    }

    public void addRuntimeOptionsInterceptor(RuntimeOptionsInterceptor interceptor) {
        this.runtimeOptionsInterceptors.add(interceptor);
    }

    public void addRequestInterceptor(RequestInterceptor interceptor) {
        this.requestInterceptors.add(interceptor);
    }

    public void addResponseInterceptor(ResponseInterceptor interceptor) {
        this.responseInterceptors.add(interceptor);
    }

    @Override
    public void close() {
        runtimeOptionsInterceptors.clear();
        requestInterceptors.clear();
        responseInterceptors.clear();
    }

    public InterceptorContext modifyRuntimeOptions(InterceptorContext context, AttributeMap attributes) {
        for (RuntimeOptionsInterceptor interceptor : runtimeOptionsInterceptors) {
            Map<String, Object> interceptorResult = interceptor.modifyRuntimeOptions(context, attributes);
            context.setRuntimeOptions(interceptorResult);
        }
        return context;
    }

    public InterceptorContext modifyRequest(InterceptorContext context, AttributeMap attributes) {
        for (RequestInterceptor interceptor : requestInterceptors) {
            Request interceptorResult = interceptor.modifyRequest(context, attributes);
            context.setTeaRequest(interceptorResult);
        }
        return context;
    }

    public InterceptorContext modifyResponse(InterceptorContext context, AttributeMap attributes) {
        for (ResponseInterceptor interceptor : responseInterceptors) {
            Response interceptorResult = interceptor.modifyResponse(context, attributes);
            context.setTeaResponse(interceptorResult);
        }
        return context;
    }
}
