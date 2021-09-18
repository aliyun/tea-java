package com.aliyun.tea.interceptor;

import com.aliyun.tea.TeaRequest;
import com.aliyun.tea.TeaResponse;
import com.aliyun.tea.utils.AttributeMap;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class InterceptorChainTest {

    @Test
    public void modifyRuntimeOptions() {
        InterceptorChain chain = InterceptorChain.create();
        chain.addRuntimeOptionsInterceptor(new MockRuntimeOptionsInterceptor());
        Map<String, Object> runtimeOptions = new HashMap<>();
        InterceptorContext context = InterceptorContext.create().setRuntimeOptions(runtimeOptions);
        context = chain.modifyRuntimeOptions(context, AttributeMap.empty());
        Assert.assertEquals("test", context.runtimeOptions().get("test"));
    }

    @Test
    public void modifyRequest() {
        InterceptorChain chain = InterceptorChain.create();
        chain.addRequestInterceptor(new MockRequestInterceptor());
        TeaRequest request = new TeaRequest();
        InterceptorContext context = InterceptorContext.create().setTeaRequest(request);
        context = chain.modifyRequest(context, AttributeMap.empty());
        Assert.assertEquals("/test", context.teaRequest().pathname);
        Assert.assertEquals("POST", context.teaRequest().method);
    }

    @Test
    public void modifyResponse() {
        InterceptorChain chain = InterceptorChain.create();
        chain.addResponseInterceptor(new MockResponseInterceptor());
        TeaResponse response = new TeaResponse();
        response.statusCode = 200;
        InterceptorContext context = InterceptorContext.create().setTeaResponse(response);
        context = chain.modifyResponse(context, AttributeMap.empty());
        Assert.assertEquals(400, context.teaResponse().statusCode);
    }
}
