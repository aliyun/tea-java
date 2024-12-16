package com.aliyun.darabonba.interceptor;

import com.aliyun.darabonba.Request;
import com.aliyun.darabonba.utils.AttributeMap;

public class MockRequestInterceptor implements RequestInterceptor {

    @Override
    public Request modifyRequest(InterceptorContext context, AttributeMap attributes) {
        Request request = context.teaRequest();
        request.pathname = "/test";
        request.method = "POST";
        return request;
    }

}
