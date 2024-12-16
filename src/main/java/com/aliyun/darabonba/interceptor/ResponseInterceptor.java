package com.aliyun.darabonba.interceptor;

import com.aliyun.darabonba.Response;
import com.aliyun.darabonba.utils.AttributeMap;

public interface ResponseInterceptor {

    Response modifyResponse(InterceptorContext context, AttributeMap attributes);

}
