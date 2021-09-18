package com.aliyun.tea.interceptor;

import com.aliyun.tea.TeaResponse;
import com.aliyun.tea.utils.AttributeMap;

public interface ResponseInterceptor {

    TeaResponse modifyResponse(InterceptorContext context, AttributeMap attributes);

}
