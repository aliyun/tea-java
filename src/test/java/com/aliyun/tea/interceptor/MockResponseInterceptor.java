package com.aliyun.tea.interceptor;

import com.aliyun.tea.TeaResponse;
import com.aliyun.tea.utils.AttributeMap;

public class MockResponseInterceptor implements ResponseInterceptor {

    @Override
    public TeaResponse modifyResponse(InterceptorContext context, AttributeMap attributes) {
        TeaResponse response = context.teaResponse();
        if (response.statusCode == 200) {
            response.statusCode = 400;
        }
        return response;
    }

}
