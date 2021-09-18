package com.aliyun.tea.interceptor;


import com.aliyun.tea.TeaRequest;
import com.aliyun.tea.TeaResponse;
import com.aliyun.tea.utils.AttributeMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InterceptorChain implements AutoCloseable {
    private List<RuntimeOptionsInterceptor> runtimeOptionsInterceptors = new ArrayList<>();
    private List<RequestInterceptor> requestInterceptors = new ArrayList<>();
    private List<ResponseInterceptor> responseInterceptors = new ArrayList<>();

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
        InterceptorContext result = context;
        for (RuntimeOptionsInterceptor interceptor : runtimeOptionsInterceptors) {
            Map<String, Object> interceptorResult = interceptor.modifyRuntimeOptions(result, attributes);
            result.setRuntimeOptions(interceptorResult);
        }
        return result;
    }

    public InterceptorContext modifyRequest(InterceptorContext context, AttributeMap attributes) {
        InterceptorContext result = context;
        for (RequestInterceptor interceptor : requestInterceptors) {
            TeaRequest interceptorResult = interceptor.modifyRequest(result, attributes);
            result.setTeaRequest(interceptorResult);
        }
        return result;
    }

    public InterceptorContext modifyResponse(InterceptorContext context, AttributeMap attributes) {
        InterceptorContext result = context;
        for (ResponseInterceptor interceptor : responseInterceptors) {
            TeaResponse interceptorResult = interceptor.modifyResponse(result, attributes);
            result.setTeaResponse(interceptorResult);
        }
        return result;
    }
}
