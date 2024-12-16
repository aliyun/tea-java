package com.aliyun.darabonba.interceptor;

import com.aliyun.darabonba.Response;
import com.aliyun.darabonba.utils.AttributeMap;

public class MockResponseInterceptor implements ResponseInterceptor {

    @Override
    public Response modifyResponse(InterceptorContext context, AttributeMap attributes) {
        Response response = context.teaResponse();
        if (response.statusCode == 200) {
            response.statusCode = 400;
        }
        return response;
    }

}
