package com.aliyun.tea.interceptor;

import com.aliyun.tea.TeaRequest;
import com.aliyun.tea.utils.AttributeMap;

public class MockRequestInterceptor implements RequestInterceptor {

    @Override
    public TeaRequest modifyRequest(InterceptorContext context, AttributeMap attributes) {
        TeaRequest request = context.teaRequest();
        request.pathname = "/test";
        request.method = "POST";
        return request;
    }

}
